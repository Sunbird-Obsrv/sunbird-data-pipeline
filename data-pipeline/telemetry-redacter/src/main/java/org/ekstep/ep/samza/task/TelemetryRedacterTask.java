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
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.service.TelemetryRedacterService;
import org.ekstep.ep.samza.util.QuestionDataCache;
import org.ekstep.ep.samza.util.RedisConnect;

public class TelemetryRedacterTask extends BaseSamzaTask {

	static Logger LOGGER = new Logger(TelemetryRedacterTask.class);
	private TelemetryRedacterConfig config;
	private JobMetrics metrics;
	private TelemetryRedacterService service;
	private RedisConnect redisConnect;

	public TelemetryRedacterTask(Config config, TaskContext context) {
		init(config, context);
	}

	public TelemetryRedacterTask() {

	}

	@Override
	public void init(Config config, TaskContext context) {
		
	  this.redisConnect = new RedisConnect(config);
		this.config = new TelemetryRedacterConfig(config);
		metrics = new JobMetrics(context, this.config.jobName());
		service = new TelemetryRedacterService(new QuestionDataCache(config, redisConnect), metrics);
		this.initTask(config, metrics);
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator)
			throws Exception {

		TelemetryRedacterSink sink = new TelemetryRedacterSink(collector, metrics, config);
		TelemetryRedacterSource source = new TelemetryRedacterSource(envelope);
		service.process(source, sink);
	}

}
