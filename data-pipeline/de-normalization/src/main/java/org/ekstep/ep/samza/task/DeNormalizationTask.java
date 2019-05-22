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
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.EventUpdaterFactory;
import org.ekstep.ep.samza.service.DeNormalizationService;
import org.ekstep.ep.samza.util.*;

import java.util.Arrays;
import java.util.List;

public class DeNormalizationTask implements StreamTask, InitableTask, WindowableTask {

    private static Logger LOGGER = new Logger(DeNormalizationTask.class);
    private DeNormalizationConfig config;
    private DeviceDataCache deviceCache;
    private UserDataCache userCache;
    private ContentDataCache contentCache;
    private DialCodeDataCache dialcodeCache;
    private JobMetrics metrics;
    private DeNormalizationService service;

    public DeNormalizationTask(Config config, TaskContext context, DeviceDataCache deviceCache, UserDataCache userCache,
                               ContentDataCache contentCache, DialCodeDataCache dialcodeCache, JobMetrics jobMetrics)  {
        init(config, context, deviceCache, userCache, contentCache, dialcodeCache, jobMetrics);
    }

    public DeNormalizationTask() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public void init(Config config, TaskContext context) {
        init(config, context, deviceCache, userCache, contentCache, dialcodeCache, metrics);
    }


    public void init(Config config, TaskContext context, DeviceDataCache deviceCache, UserDataCache userCache, ContentDataCache contentCache, DialCodeDataCache dialcodeCache, JobMetrics jobMetrics) {

        this.config = new DeNormalizationConfig(config);
        this.metrics = jobMetrics == null ? new JobMetrics(context, this.config.jobName()) : jobMetrics;
        this.deviceCache = deviceCache == null ? new DeviceDataCache(config, metrics): deviceCache;
        this.userCache = userCache == null ? new UserDataCache(config, metrics): userCache;
        this.contentCache = contentCache == null ? new ContentDataCache(config, metrics) : contentCache;
        this.dialcodeCache = dialcodeCache == null ? new DialCodeDataCache(config, metrics) : dialcodeCache;

        service = new DeNormalizationService(this.config, new EventUpdaterFactory(this.contentCache, this.userCache, this.deviceCache, this.dialcodeCache));
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator) {
        DeNormalizationSource source = new DeNormalizationSource(envelope);
        DeNormalizationSink sink = new DeNormalizationSink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
        metrics.clear();
    }
}
