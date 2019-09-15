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
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.AssessmentAggregatorService;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.DBUtil;

public class AssessmentAggregatorTask implements StreamTask, InitableTask, WindowableTask {

    private JobMetrics metrics;
    private AssessmentAggregatorService service;
    private AssessmentAggregatorConfig config;

    @Override
    public void init(Config config, TaskContext context) {
        this.config = new AssessmentAggregatorConfig(config);
        metrics = new JobMetrics(context, this.config.jobName());
        CassandraConnect cassandraConnect = new CassandraConnect(this.config.getCassandraHost(),
                this.config.getCassandraPort());
        DBUtil dbUtil = new DBUtil(cassandraConnect, this.config);
        this.service = new AssessmentAggregatorService(dbUtil);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        AssessmentAggregatorSource source = new AssessmentAggregatorSource(envelope);
        AssessmentAggregatorSink sink = new AssessmentAggregatorSink(collector, metrics, config);
        service.process(source, sink, config);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
        metrics.clear();
    }
}
