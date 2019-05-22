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

import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.StreamTask;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.service.DruidEventsValidatorService;
import org.ekstep.ep.samza.util.SchemaValidator;

public class DruidEventsValidatorTask implements StreamTask, InitableTask, WindowableTask {

    static Logger LOGGER = new Logger(DruidEventsValidatorTask.class);
    private DruidEventsValidatorConfig config;
    private JobMetrics metrics;
    private DruidEventsValidatorService service;
    private JsonSchemaFactory jsonSchemaFactory;
    private SchemaValidator schemaValidator;

    public DruidEventsValidatorTask(Config config, TaskContext context) throws Exception {
        init(config, context);
    }

    public DruidEventsValidatorTask() {

    }

    @Override
    public void init(Config config, TaskContext context) throws Exception {
        this.config = new DruidEventsValidatorConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
        this.schemaValidator = schemaValidator == null ? new SchemaValidator(this.config) : schemaValidator;
        service = new DruidEventsValidatorService(this.config, this.schemaValidator);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        DruidEventsValidatorSource source = new DruidEventsValidatorSource(envelope);
        DruidEventsValidatorSink sink = new DruidEventsValidatorSink(collector, metrics, config);
        jsonSchemaFactory = JsonSchemaFactory.byDefault();
        service.process(source, sink, jsonSchemaFactory);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
        metrics.clear();
    }
}
