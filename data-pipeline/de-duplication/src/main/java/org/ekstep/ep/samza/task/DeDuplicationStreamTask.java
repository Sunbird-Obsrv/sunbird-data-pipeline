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

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.system.Device;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Location;

import java.util.Map;

public class DeDuplicationStreamTask implements StreamTask, InitableTask {

    private KeyValueStore<String, Object> deDuplicationStore;
    private KeyValueStore<String, Object> deviceStore;
    private String successTopic;
    private String failedTopic;

    @Override
    public void init(Config config, TaskContext context) {
        String apiKey = config.get("google.api.key", "");

        successTopic = config.get("output.success.topic.name", "unique_events");
        failedTopic = config.get("output.failed.topic.name", "duplicate_events");

        this.deDuplicationStore = (KeyValueStore<String, Object>) context.getStore("de-duplication");
        this.deviceStore = (KeyValueStore<String, Object>) context.getStore("device");
    }

    public DeDuplicationStreamTask() {

    }

    public DeDuplicationStreamTask(KeyValueStore<String, Object> deDuplicationStore, KeyValueStore<String, Object> deviceStore) {
        this.deDuplicationStore = deDuplicationStore;
        this.deviceStore = deviceStore;
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        Map<String, Object> jsonObject;
        Location location = null;
        Device device = null;
        try {
            jsonObject = (Map<String, Object>) envelope.getMessage();
            processEvent(new Event(jsonObject), collector);
        } catch (Exception e) {
            System.err.println("Error while getting message");
        }
    }

    public void processEvent(Event event, MessageCollector collector) {
        deviceStore.put(event.getDid(),event);
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getDid()));
    }
}
