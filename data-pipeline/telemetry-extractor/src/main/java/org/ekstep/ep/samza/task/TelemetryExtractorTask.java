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

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.domain.Telemetry;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.core.JobMetrics;

import java.util.List;
import java.util.Map;

public class TelemetryExtractorTask implements StreamTask, InitableTask, WindowableTask {

    static Logger LOGGER = new Logger(TelemetryExtractorTask.class);
    private TelemetryExtractorConfig config;
    private JobMetrics metrics;

    public TelemetryExtractorTask(Config config, TaskContext context) {
        init(config, context);
    }

    public TelemetryExtractorTask() {

    }

    @Override
    public void init(Config config, TaskContext context) {
        this.config = new TelemetryExtractorConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator) throws Exception {
        try {
            String message = (String) envelope.getMessage();
            Map<String, Object> eventSpec = (Map<String, Object>) new Gson().fromJson(message, Map.class);
            long syncts = ((Number)eventSpec.get("syncts")).longValue();
            List<Map<String, Object>> events = (List<Map<String, Object>>) eventSpec.get("events");
            processEvents(events, syncts, collector);
            generateLOGEvent(eventSpec, collector);

        } catch (Exception e){
            LOGGER.info("","Failed to process events: "+ e.getMessage());
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
        metrics.clear();
    }

    private void processEvents(List<Map<String, Object>> events, long syncts, MessageCollector collector){
        for(Map<String, Object> event: events){
            try {
                event.put("syncts",syncts);
                String json = new Gson().toJson(event);
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.successTopic()), json));
            }catch (Exception e){
                LOGGER.info((String)event.get("eid"),"Failed to process the event: "+ e.getMessage() + " mid: "+(String)event.get("mid"));
            }

        }
    }
    private void generateLOGEvent(Map<String, Object> eventSpec, MessageCollector collector) {
        try {
            // Creating LOG event
            Telemetry v3spec = new Telemetry(eventSpec);
            String auditEvent =  v3spec.toJson();
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.successTopic()), auditEvent));
        }catch (Exception e){
            e.printStackTrace();
            LOGGER.info("","Failed to generate LOG event: "+ e.getMessage());
        }
    }
}