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
import org.ekstep.ep.samza.service.TelemetryLocationUpdaterService;
import org.ekstep.ep.samza.util.DeviceProfileCache;
import org.ekstep.ep.samza.util.RedisConnect;

public class TelemetryLocationUpdaterTask extends BaseSamzaTask {

	private TelemetryLocationUpdaterConfig config;
	private JobMetrics metrics;
	private TelemetryLocationUpdaterService service;
	private DeviceProfileCache deviceProfileCache;
	private RedisConnect redisConnect;

	public TelemetryLocationUpdaterTask(Config config, TaskContext context, DeviceProfileCache deviceProfileCache, RedisConnect redisConnect) {
		init(config, context, deviceProfileCache, redisConnect);
	}

	public TelemetryLocationUpdaterTask() {
	}

	@Override
	public void init(Config config, TaskContext context) {
		init(config, context, deviceProfileCache, redisConnect);
	}

	public void init(Config config, TaskContext context, DeviceProfileCache deviceProfileCache, RedisConnect redisConnect) {
		
		this.config = new TelemetryLocationUpdaterConfig(config);
		this.metrics = new JobMetrics(context, this.config.jobName());
		this.redisConnect = redisConnect == null ? new RedisConnect(config) : redisConnect;
		this.deviceProfileCache = deviceProfileCache == null ? new DeviceProfileCache(config, metrics, this.redisConnect) : deviceProfileCache;
		this.service = new TelemetryLocationUpdaterService(this.deviceProfileCache, metrics, this.redisConnect, config);
		this.initTask(config, metrics);
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector,
						TaskCoordinator taskCoordinator) throws Exception {

		TelemetryLocationUpdaterSink sink = new TelemetryLocationUpdaterSink(collector, metrics, config);
		TelemetryLocationUpdaterSource source = new TelemetryLocationUpdaterSource(envelope);
		service.process(source, sink);
	}

}
