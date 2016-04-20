/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.system.Event;
import org.apache.samza.metrics.Counter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeDuplicationStreamTask implements StreamTask, InitableTask, WindowableTask {

    private KeyValueStore<String, Object> deDuplicationStore;

    private String successTopic;
    private String failedTopic;

    private Counter messageCount;
    @Override
    public void init(Config config, TaskContext context) {
        String apiKey = config.get("google.api.key", "");

        successTopic = config.get("output.success.topic.name", "unique_events");
        failedTopic = config.get("output.failed.topic.name", "duplicate_events");
        suppressDedupe = config.get("output.failed.topic.name", "duplicate_events");

        this.deDuplicationStore = (KeyValueStore<String, Object>) context.getStore("de-duplication");
        this.messageCount = context
            .getMetricsRegistry()
            .newCounter(getClass().getName(), "message-count");
    }

    public DeDuplicationStreamTask() {

    }

    public DeDuplicationStreamTask(KeyValueStore<String, Object> deDuplicationStore) {
        this.deDuplicationStore = deDuplicationStore;
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator){
        Gson gson=new Gson();
        Map<String,Object> jsonObject = new HashMap<String,Object>();

        try {
            String message = (String) envelope.getMessage();
            jsonObject = validateJson(collector, message, gson, jsonObject);
            processEvent(new Event(jsonObject), collector);
            messageCount.inc();
        }
        catch(JsonSyntaxException e){
            e.printStackTrace();
            System.err.println("Invalid Json Input"+e);
        }
        catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error while getting message"+e);
        }
    }

    public Map<String,Object> validateJson(MessageCollector collector, String message, Gson gson, Map<String, Object> jsonObject) throws JsonSyntaxException {
        Map<String,Object> validJson = new HashMap<String,Object>();
        validJson =  (Map<String,Object>) gson.fromJson(message, jsonObject.getClass());
        return validJson;
    }

    public void processEvent(Event event, MessageCollector collector) throws Exception {
        String checkSum = event.getChecksum();
        // if(deDuplicationStore.get(checkSum) == null)
        if(true)
        {
            System.out.println("create new checksum if it is not present in Store");

            Date date = new Date();
            deDuplicationStore.put(checkSum, date.toString());

            System.out.println("duplicationStore"+deDuplicationStore);
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getJson()));
        }
        else {
            System.out.println("Output to Failed Topic if the checksum already present in store");
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getJson()));
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
