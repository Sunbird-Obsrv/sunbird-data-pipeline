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
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.esclient.ElasticSearchClient;
import org.ekstep.ep.samza.esclient.ElasticSearchService;
import org.ekstep.ep.samza.service.EsIndexerService;

import java.net.UnknownHostException;
import java.util.Map;

public class EsIndexerTask implements StreamTask, InitableTask, WindowableTask {
	static Logger LOGGER = new Logger(EsIndexerTask.class);
	private EsIndexerConfig config;
	private JobMetrics metrics;
	private EsIndexerService service;

	public EsIndexerTask(Config config, TaskContext context, ElasticSearchService elasticSearchService)
			throws Exception {
		init(config, context, elasticSearchService);
	}

	public EsIndexerTask() {

	}

	@Override
	public void init(Config config, TaskContext context) throws Exception {
		init(config, context, null);
	}

	private void init(Config config, TaskContext context, ElasticSearchService elasticSearchService)
			throws UnknownHostException {
		this.config = new EsIndexerConfig(config);
		metrics = new JobMetrics(context, this.config.jobName());

		elasticSearchService = elasticSearchService == null
				? new ElasticSearchClient(this.config.esPort(), this.config.esHosts())
				: elasticSearchService;

		service = new EsIndexerService(elasticSearchService, this.config);
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator)
			throws Exception {
		
		EsIndexerSource source = new EsIndexerSource(envelope);
		EsIndexerSink sink = new EsIndexerSink(collector, metrics, config);
		service.process(source, sink);
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String mEvent = metrics.collect();
		@SuppressWarnings("unchecked")
		Map<String, Object> mEventMap = new Gson().fromJson(mEvent, Map.class);
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEventMap));
		metrics.clear();
	}
}
