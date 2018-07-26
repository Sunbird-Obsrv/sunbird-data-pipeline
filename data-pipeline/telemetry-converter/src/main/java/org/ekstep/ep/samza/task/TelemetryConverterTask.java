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

import java.util.Map;

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
import org.ekstep.ep.samza.converter.converters.TelemetryV3Converter;
import org.ekstep.ep.samza.converter.domain.TelemetryV3;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.google.gson.Gson;

public class TelemetryConverterTask implements StreamTask, InitableTask, WindowableTask {

	static Logger LOGGER = new Logger(TelemetryConverterTask.class);
	private TelemetryConverterConfig config;
	private JobMetrics metrics;

	public TelemetryConverterTask(Config config, TaskContext context) {
		init(config, context);
	}

	public TelemetryConverterTask() {

	}

	@Override
	public void init(Config config, TaskContext context) {
		this.config = new TelemetryConverterConfig(config);
		metrics = new JobMetrics(context, this.config.jobName());
	}

	
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator)
			throws Exception {

		TelemetryConverterSink sink = new TelemetryConverterSink(collector, metrics, config);
		String message = (String) envelope.getMessage();
		@SuppressWarnings("unchecked")
		Map<String, Object> map = (Map<String, Object>) new Gson().fromJson(message, Map.class);
		try {
			if (!map.containsKey("@timestamp")) {
				long ets = ((Number) map.get("ets")).longValue();
				String timestamp = new DateTime(ets).withZone(DateTimeZone.UTC).toString();
				map.put("@timestamp", timestamp);
			}
			if ("3.0".equals(map.get("ver"))) {
				// It is already a V3 events. Skipping and let it pass through
				sink.toSuccessTopic(map.get("mid") != null ? map.get("mid").toString() : null, new Gson().toJson(map));
			} else {
				TelemetryV3Converter converter = new TelemetryV3Converter(map);
				TelemetryV3[] v3Events = converter.convert();
				for (TelemetryV3 telemetryV3 : v3Events) {
					sink.toSuccessTopic(telemetryV3, map);
					LOGGER.info(telemetryV3.getEid(), "Converted to V3. EVENT: {}", telemetryV3.toMap());
				}
			}
		} catch (Exception ex) {
			LOGGER.error("", "Failed to convert event to telemetry v3", ex);
			sink.toFailedTopic(map, ex);
		}
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String mEvent = metrics.collect();
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
		metrics.clear();
	}

}
