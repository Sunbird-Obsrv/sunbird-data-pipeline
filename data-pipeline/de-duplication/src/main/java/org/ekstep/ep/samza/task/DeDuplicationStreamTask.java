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
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.system.Event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DeDuplicationStreamTask implements StreamTask, InitableTask, WindowableTask {

    private KeyValueStore<String, Object> deDuplicationStore;
    static Logger LOGGER = new Logger(DeDuplicationStreamTask.class);

    private String successTopic;
    private String failedTopic;

    private Counter messageCount;
    @Override
    public void init(Config config, TaskContext context) {
        String apiKey = config.get("google.api.key", "");

        successTopic = config.get("output.success.topic.name", "unique_events");
        failedTopic = config.get("output.failed.topic.name", "duplicate_events");

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

        String message = null;
        try {
            message = (String) envelope.getMessage();
            jsonObject = validateJson(collector, message, gson, jsonObject);
            processEvent(new Event(jsonObject), collector);
            messageCount.inc();
        }
        catch(JsonSyntaxException e){
            LOGGER.error(null, "INVALID EVENT: " + message, e);
        }
        catch (Exception e) {
            LOGGER.error(null, "EVENT FAILED: " + message, e);
        }
    }

    public Map<String,Object> validateJson(MessageCollector collector, String message, Gson gson,
                                           Map<String, Object> jsonObject) throws JsonSyntaxException {
        Map<String,Object> validJson = new HashMap<String,Object>();
        validJson =  (Map<String,Object>) gson.fromJson(message, jsonObject.getClass());
        return validJson;
    }

    public void processEvent(Event event, MessageCollector collector) throws Exception {
        String checkSum = event.getChecksum();
        if(deDuplicationStore.get(checkSum) == null){
            deDuplicationStore.put(checkSum, new Date().toString());
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getJson()));
        }
        else {
            LOGGER.info(event.id(), "DUPLICATE EVENT, CHECKSUM: ", checkSum);
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getJson()));
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
