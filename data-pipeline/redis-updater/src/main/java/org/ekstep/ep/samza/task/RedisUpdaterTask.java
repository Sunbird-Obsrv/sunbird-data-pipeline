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

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.StreamTask;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.service.RedisUpdaterService;
import org.ekstep.ep.samza.util.RedisConnect;

public class RedisUpdaterTask implements StreamTask, InitableTask, WindowableTask {

    private final String JOB_NAME = "redis-updater";
    static Logger LOGGER = new Logger(RedisUpdaterTask.class);
    private JobMetrics metrics;
    private RedisUpdaterService service;
    private String metricsTopic;
    private RedisConnect redisConnect;

    public RedisUpdaterTask(Config config, TaskContext context) {
        init(config, context);
    }

    public RedisUpdaterTask() {}

    @Override
    public void init(Config config, TaskContext context) {
        metrics = new JobMetrics(context,JOB_NAME);
        redisConnect = new RedisConnect(config);
        service = new RedisUpdaterService(config, redisConnect);
        metricsTopic = config.get("output.metrics.topic.name");
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        RedisUpdaterSource source = new RedisUpdaterSource(envelope);
        RedisUpdaterSink sink = new RedisUpdaterSink(collector, metrics);

        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", metricsTopic), mEvent));
        metrics.clear();
    }
}
