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
import org.ekstep.ep.samza.converters.TelemetryV3Converter;
import org.ekstep.ep.samza.domain.TelemetryV3;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;

import java.util.Map;

public class TelemetryConverterTask implements StreamTask, InitableTask, WindowableTask {


    static Logger LOGGER = new Logger(TelemetryConverterTask.class);
    private TelemetryConverterConfig config;
    private JobMetrics metrics;

//    public TelemetryConverterTask(Config config, TaskContext context,
//                                  KeyValueStore<Object, Object> deDuplicationStore, DeDupEngine deDupEngine) {
//        init(config, context, deDuplicationStore, deDupEngine);
//    }

    public TelemetryConverterTask() {

    }

    @Override
    public void init(Config config, TaskContext context) {
//        init(config, context,
//                (KeyValueStore<Object, Object>) context.getStore("de-duplication"), null);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        //TODO: Logger
        try {
            String message = (String) envelope.getMessage();
            Map<String, Object> map = (Map<String, Object>) new Gson().fromJson(message, Map.class);
            TelemetryV3Converter converter = new TelemetryV3Converter(map);

            TelemetryV3 telemetryV3 = converter.convert();
            toSuccessTopic(collector, telemetryV3);
        }
        catch(Exception ex) {
            toFailedTopic(collector, envelope);
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        metrics.clear();
    }

    private void toSuccessTopic(MessageCollector collector, TelemetryV3 v3) {
        // TODO: v3.toJson()
        String json = "";
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.successTopic()), json));
        metrics.incFailedCounter();;
    }

    private void toFailedTopic(MessageCollector collector, IncomingMessageEnvelope envelope) {
        String json = "";
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.failedTopic()), json));
        metrics.incFailedCounter();
    }
}
