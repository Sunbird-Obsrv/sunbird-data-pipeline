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
import org.ekstep.ep.samza.esclient.ElasticSearchClient;
import org.ekstep.ep.samza.esclient.ElasticSearchService;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.metrics.JobMetrics;
import org.ekstep.ep.samza.service.EsIndexerPrimaryService;

import java.net.UnknownHostException;

public class EsIndexerPrimaryTask implements StreamTask, InitableTask, WindowableTask {
    static Logger LOGGER = new Logger(EsIndexerPrimaryTask.class);
    private EsIndexerPrimaryConfig config;
    private JobMetrics metrics;
    private EsIndexerPrimaryService service;

    public EsIndexerPrimaryTask(Config config, TaskContext context, ElasticSearchService elasticSearchService) throws Exception {
        init(config, context, elasticSearchService);
    }

    public EsIndexerPrimaryTask(){

    }

    @Override
    public void init(Config config, TaskContext context) throws Exception{
        init(config, context, null);
    }

    private void init(Config config, TaskContext context, ElasticSearchService elasticSearchService) throws UnknownHostException {
        this.config = new EsIndexerPrimaryConfig(config);
        metrics = new JobMetrics(context);

        elasticSearchService =
                elasticSearchService == null ?
                        new ElasticSearchClient(this.config.esHost(),this.config.esPort()) :
                        elasticSearchService;

        service = new EsIndexerPrimaryService(elasticSearchService);
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
                        TaskCoordinator taskCoordinator) throws Exception {
        EsIndexerPrimarySource source = new EsIndexerPrimarySource(envelope);
        EsIndexerPrimarySink sink = new EsIndexerPrimarySink(collector, metrics, config);
        service.process(source, sink);
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        metrics.clear();
    }
}
