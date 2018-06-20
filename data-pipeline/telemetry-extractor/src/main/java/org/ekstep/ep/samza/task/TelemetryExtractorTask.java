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

import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.util.ExtractorUtils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
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
            byte[] message = (byte[]) envelope.getMessage();
            byte[] decompressedData = ExtractorUtils.decompress(message);
            String jsonString = new String(decompressedData);
            Map map = (Map)new Gson().fromJson(jsonString, Map.class);
            String rawDataStr = getRawDataStr(map);
            if(rawDataStr!=null){
                String ts = (String) map.get("Timestamp");
                processEvents(rawDataStr, ts, collector);
            }
        }catch (Exception e){
            e.printStackTrace();
            //TODO add log statement
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

    private void toSuccessTopic(MessageCollector collector, Map<String, Object> event) {
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

    private void processEvents(String rawDataStr, String ts, MessageCollector collector){
        Map<String, Object> eventMap = (Map<String, Object>)new Gson().fromJson(rawDataStr, Map.class);
        List<Map<String, Object>> events = (List<Map<String, Object>>) eventMap.get("events");
        for(Map<String, Object> event: events){
            try {
                event.put("@timestamp",ts);
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.successTopic()), event));
            }catch (Exception e){
                e.printStackTrace();
                //TODO add log statement
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.failedTopic()), event));
            }
        }
    }
    private String getRawDataStr(Map map) {
        try {
            String datatype = (String) map.get("DataType");
            String encodedString = (String) map.get("RawData");
            byte[] decoded = Base64.getDecoder().decode(encodedString);

            if(StringUtils.equals("gzip", datatype)) {
                byte[] decompresedRawData = ExtractorUtils.decompress(decoded);
                String rawDataStr = new String(decompresedRawData);
                return rawDataStr;
            }else {
                String rawDataStr = new String(decoded, StandardCharsets.UTF_8);
                return rawDataStr;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
            //TODO add log statement
        }
    }
}