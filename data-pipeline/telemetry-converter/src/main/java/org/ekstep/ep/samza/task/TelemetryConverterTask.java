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
import org.ekstep.ep.samza.converter.converters.TelemetryV3Converter;
import org.ekstep.ep.samza.converter.domain.TelemetryV3;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

public class TelemetryConverterTask implements StreamTask, InitableTask, WindowableTask {

    static Logger LOGGER = new Logger(TelemetryConverterTask.class);
    private TelemetryConverterConfig config;
    private JobMetrics metrics;

    public TelemetryConverterTask(Config config, TaskContext context) {
        init(config, context);
    }

    public TelemetryConverterTask() {

    }

    @Override
    public void init(Config config, TaskContext context) {
        this.config = new TelemetryConverterConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        String message = (String) envelope.getMessage();
        Map<String, Object> map = (Map<String, Object>) new Gson().fromJson(message, Map.class);
        try {
        	if(!map.containsKey("@timestamp")){
        		long ets = ((Number)map.get("ets")).longValue();
        		String timestamp = new DateTime(ets).toString();
        		map.put("@timestamp", timestamp);
        	}
            if ("3.0".equals(map.get("ver"))) {
                // It is already a V3 events. Skipping and let it pass through
            	toSuccessTopic(collector, new Gson().toJson(map));
            } else {
                TelemetryV3Converter converter = new TelemetryV3Converter(map);
                TelemetryV3[] v3Events = converter.convert();
                for (TelemetryV3 telemetryV3 : v3Events) {
                    toSuccessTopic(collector, telemetryV3, map);
                    LOGGER.info(telemetryV3.getEid(), "Converted to V3. EVENT: {}", telemetryV3.toMap());
                }
            }
        } catch (Exception ex) {
            LOGGER.error("", "Failed to convert event to telemetry v3", ex);
            toFailedTopic(collector, map, ex);
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
        metrics.clear();
    }

    private void toSuccessTopic(MessageCollector collector, String v2Event) {
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.successTopic()), v2Event));
        metrics.incSkippedCounter();
    }

    private void toSuccessTopic(MessageCollector collector, TelemetryV3 v3, Map<String, Object> v2) {
        Map<String, Object> flags = getFlags(v2);
        flags.put("v2_converted", true);

        Map<String, Object> metadata = getMetadata(v2);
        metadata.put("source_eid", v2.getOrDefault("eid", ""));
        metadata.put("source_mid", v2.getOrDefault("mid", ""));
        metadata.put("checksum", v3.getMid());

        Map<String, Object> event = v3.toMap();
        event.put("flags", flags);
        event.put("metadata", metadata);

        if(v2.containsKey("pump")){
            event.put("pump", "true");
        }

        String json = new Gson().toJson(event);
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.successTopic()), json));
        metrics.incSuccessCounter();
    }

    private void toFailedTopic(MessageCollector collector, Map<String, Object> event, Exception ex) {
        Map<String, Object> flags = getFlags(event);
        flags.put("v2_converted", false);
        flags.put("error", ex.getMessage());
        flags.put("stack", stacktraceToString(ex.getStackTrace()));

        Map<String, Object> payload = event;
        payload.put("flags", flags);
        payload.put("metadata", getMetadata(event));

        String json = new Gson().toJson(payload);

        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.failedTopic()), json));
        metrics.incErrorCounter();
    }

    private String stacktraceToString(StackTraceElement[] stackTrace) {
        String stack = "";
        for (StackTraceElement trace : stackTrace) {
            stack += trace.toString() + "\n";
        }
        return stack;
    }

    private Map<String, Object> getFlags(Map<String, Object> event) {
        Map<String, Object> flags;
        if (event.containsKey("flags") && (event.get("flags") instanceof Map)) {
            flags = (Map<String, Object>) event.get("flags");
        } else {
            flags = new HashMap<>();
        }

        return flags;
    }

    private Map<String, Object> getMetadata(Map<String, Object> event) {
        Map<String, Object> metadata;
        if (event.containsKey("metadata") && (event.get("metadata") instanceof Map)) {
            metadata = (Map<String, Object>) event.get("metadata");
        } else {
            metadata = new HashMap<>();
        }

        return metadata;
    }
}
