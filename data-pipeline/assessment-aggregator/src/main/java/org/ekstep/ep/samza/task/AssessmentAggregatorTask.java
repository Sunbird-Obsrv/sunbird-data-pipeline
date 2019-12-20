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
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.AssessmentAggregatorService;
import org.ekstep.ep.samza.util.CassandraConnect;

public class AssessmentAggregatorTask extends BaseSamzaTask {

    private JobMetrics metrics;
    private AssessmentAggregatorService service;
    private AssessmentAggregatorConfig config;

    public AssessmentAggregatorTask(Config config, TaskContext taskContext, CassandraConnect cassandraConnect) {
        init(config, taskContext, cassandraConnect);
    }

    public AssessmentAggregatorTask() {

    }

    @Override
    public void init(Config config, TaskContext context) {
        init(config, context, null);
    }

    private void init(Config config, TaskContext context, CassandraConnect cassandraConnect) {

        this.config = new AssessmentAggregatorConfig(config);
        this.metrics = new JobMetrics(context, this.config.jobName());
        cassandraConnect = null != cassandraConnect ? cassandraConnect : new CassandraConnect(this.config.getCassandraHost(), this.config.getCassandraPort());
        this.service = new AssessmentAggregatorService(cassandraConnect, this.config);
        this.initTask(config, this.metrics);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        AssessmentAggregatorSource source = new AssessmentAggregatorSource(envelope);
        AssessmentAggregatorSink sink = new AssessmentAggregatorSink(collector, metrics, config);
        service.process(source, sink);
    }

    public JobMetrics getJobMetrics() {
    	return this.metrics;
    }
}
