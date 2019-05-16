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
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.service.EventsRouterService;
import org.ekstep.ep.samza.domain.DeDupEngine;
import org.ekstep.ep.samza.util.RedisConnect;

public class EventsRouterTask implements StreamTask, InitableTask, WindowableTask {

	static Logger LOGGER = new Logger(EventsRouterTask.class);
	private EventsRouterConfig config;
	private JobMetrics metrics;
	private EventsRouterService service;

	public EventsRouterTask(DeDupEngine deDupEngine, Config config, TaskContext context) {
		init(config, context, deDupEngine);
	}

	public EventsRouterTask() {

	}

	@Override
	public void init(Config config, TaskContext context) {
		
		this.config = new EventsRouterConfig(config);
		metrics = new JobMetrics(context, this.config.jobName());
		init(config, context, null);
	}

	private void init(Config config, TaskContext context, DeDupEngine deDupEngine) {
		this.config = new EventsRouterConfig(config);
		metrics = new JobMetrics(context, this.config.jobName());
		deDupEngine = deDupEngine == null ? new DeDupEngine(new RedisConnect(config)) : deDupEngine;
		service = new EventsRouterService(deDupEngine, this.config);

	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator)
			throws Exception {

		EventsRouterSink sink = new EventsRouterSink(collector, metrics, config);
		EventsRouterSource source = new EventsRouterSource(envelope);
		service.process(source, sink);
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		String mEvent = metrics.collect();
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
		metrics.clear();
	}
}
