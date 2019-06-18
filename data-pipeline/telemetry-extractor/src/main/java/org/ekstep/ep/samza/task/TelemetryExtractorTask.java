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
import org.ekstep.ep.samza.service.TelemetryExtractorService;
import org.ekstep.ep.samza.util.DeDupEngine;
import org.ekstep.ep.samza.util.RedisConnect;

public class TelemetryExtractorTask implements StreamTask, InitableTask, WindowableTask {

	static Logger LOGGER = new Logger(TelemetryExtractorTask.class);
	private TelemetryExtractorConfig config;
	private TelemetryExtractorService service;
	private JobMetrics metrics;

	public TelemetryExtractorTask(Config config, TaskContext context, DeDupEngine deDupEngine) {
		init(config, context, deDupEngine);
	}

	public TelemetryExtractorTask() {

	}

	@Override
	public void init(Config config, TaskContext context) {
		init(config, context, null);
	}


	public void init(Config config, TaskContext context, DeDupEngine deDupEngine) {
		this.config = new TelemetryExtractorConfig(config);
		this.metrics = new JobMetrics(context, this.config.jobName());
		deDupEngine = deDupEngine == null ?
				new DeDupEngine(new RedisConnect(config).getConnection(), this.config.dupStore(),
						this.config.expirySeconds()) : deDupEngine;
		this.service = new TelemetryExtractorService(this.config, this.metrics,deDupEngine);
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator)
			throws Exception {

		String message = (String) envelope.getMessage();
		TelemetryExtractorSink sink = new TelemetryExtractorSink(collector, metrics, config);
		sink.setMetricsOffset(envelope.getSystemStreamPartition(), envelope.getOffset());
		// metrics.setOffset(envelope.getSystemStreamPartition(), envelope.getOffset());
		try {
			service.process(message, sink);
		} catch (Exception e) {
			LOGGER.info("", "Failed to process events: " + e.getMessage());
			sink.toErrorTopic(message);
		}
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String mEvent = metrics.collect();
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
		metrics.clear();
	}

}