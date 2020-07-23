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
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.TelemetryValidatorService;
import org.ekstep.ep.samza.util.TelemetrySchemaValidator;

public class TelemetryValidatorTask extends BaseSamzaTask {

    private TelemetryValidatorConfig config;
    private JobMetrics metrics;
    private TelemetryValidatorService service;

    public TelemetryValidatorTask(Config config, TaskContext context, TelemetrySchemaValidator telemetrySchemaValidator) throws Exception {
        init(config, context, telemetrySchemaValidator);
    }

    public TelemetryValidatorTask() {

    }


    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context, null);
    }


    public void init(Config config, TaskContext context, TelemetrySchemaValidator schemaValidator) throws Exception {
        this.config = new TelemetryValidatorConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
        TelemetrySchemaValidator telemetrySchemaValidator = schemaValidator == null ? new TelemetrySchemaValidator(this.config) : schemaValidator;
        service = new TelemetryValidatorService(this.config, telemetrySchemaValidator);
        this.initTask(config, metrics);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        TelemetryValidatorSource source = new TelemetryValidatorSource(envelope);
        TelemetryValidatorSink sink = new TelemetryValidatorSink(collector, metrics, config);

        service.process(source, sink);
    }
   
}
