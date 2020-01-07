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
import org.ekstep.ep.samza.service.EventsFlattenService;

public class EventsFlattenTask extends BaseSamzaTask {

    private EventsFlattenConfig config;
    private JobMetrics metrics;
    private EventsFlattenService service;

    public EventsFlattenTask(Config config, TaskContext context) {
        init(config, context);
    }

    public EventsFlattenTask() { }

    @Override
    public void init(Config config, TaskContext context) {
        this.config = new EventsFlattenConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
        service = new EventsFlattenService(this.config);
        this.initTask(config, metrics);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator) {
        EventsFlattenSink sink = new EventsFlattenSink(collector, metrics, config);
        EventsFlattenSource source = new EventsFlattenSource(envelope);
        service.process(source, sink);
    }

}
