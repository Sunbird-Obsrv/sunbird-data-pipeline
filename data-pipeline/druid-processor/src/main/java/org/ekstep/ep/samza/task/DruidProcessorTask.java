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

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.EventUpdaterFactory;
import org.ekstep.ep.samza.service.DruidProcessorService;
import org.ekstep.ep.samza.util.*;

import java.io.IOException;

public class DruidProcessorTask implements StreamTask, InitableTask, WindowableTask {

    static Logger LOGGER = new Logger(DruidProcessorTask.class);
    private DruidProcessorConfig config;
    private DeviceDataCache deviceCache;
    private UserDataCache userCache;
    private ContentDataCache contentCache;
    private DialCodeDataCache dialcodeCache;
    private RedisConnect redisConnect;
    private CassandraConnect cassandraConnect;
    private JobMetrics metrics;
    private DruidProcessorService service;
    private SchemaValidator schemaValidator;

    public DruidProcessorTask(Config config, TaskContext context, DeviceDataCache deviceCache,
                              UserDataCache userCache, ContentDataCache contentCache, CassandraConnect cassandraConnect,
                              DialCodeDataCache dialcodeCache, SchemaValidator schemaValidator, JobMetrics jobMetrics)
            throws Exception {
        init(config, context, deviceCache, userCache, contentCache, cassandraConnect, dialcodeCache,
                schemaValidator, jobMetrics);
    }

    public DruidProcessorTask() {}

    @SuppressWarnings("unchecked")
    @Override
    public void init(Config config, TaskContext context) throws Exception {
        init(config, context, deviceCache, userCache, contentCache, cassandraConnect, dialcodeCache,
                schemaValidator, metrics);
    }


    public void init(Config config, TaskContext context, DeviceDataCache deviceCache,
                     UserDataCache userCache, ContentDataCache contentCache, CassandraConnect cassandraConnect,
                     DialCodeDataCache dialcodeCache, SchemaValidator schemaValidator, JobMetrics jobMetrics)
            throws IOException, ProcessingException {
        this.config = new DruidProcessorConfig(config);
        this.metrics = jobMetrics == null ? new JobMetrics(context, this.config.jobName()) : jobMetrics;
        this.redisConnect = new RedisConnect(config);

        this.cassandraConnect =
                cassandraConnect == null ?
                        new CassandraConnect(config)
                        : cassandraConnect;

        this.deviceCache =
                deviceCache == null ?
                        new DeviceDataCache(config, this.redisConnect, this.cassandraConnect)
                        : deviceCache;

        this.userCache =
                userCache == null ?
                        new UserDataCache(config, this.redisConnect)
                        : userCache;

        this.contentCache =
                contentCache == null ?
                        new ContentDataCache(config, this.redisConnect)
                        : contentCache;

        this.dialcodeCache =
                dialcodeCache == null ?
                        new DialCodeDataCache(config, this.redisConnect)
                        : dialcodeCache;

        this.schemaValidator = schemaValidator == null ? new SchemaValidator(this.config) : schemaValidator;
        service = new DruidProcessorService(this.config, new EventUpdaterFactory(this.contentCache,
                this.userCache, this.deviceCache, this.dialcodeCache), this.schemaValidator);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator) {
        try {
            DruidProcessorSource source = new DruidProcessorSource(envelope);
            DruidProcessorSink sink = new DruidProcessorSink(collector, metrics, config);

            service.process(source, sink);
        } catch (Exception ex) {
            LOGGER.error("", "DeNormalization failed: " + ex.getMessage());
            Object event = envelope.getMessage();
            if (event != null) {
                LOGGER.info("", "FAILED_EVENT: " + event);
            }
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        String mEvent = metrics.collect();
        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
        metrics.clear();
    }
}
