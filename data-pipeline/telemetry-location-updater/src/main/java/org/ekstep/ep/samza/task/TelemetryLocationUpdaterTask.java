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

import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.ekstep.ep.samza.cache.CacheEntry;
import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.engine.LocationEngine;
import org.ekstep.ep.samza.service.TelemetryLocationUpdaterService;
import org.ekstep.ep.samza.util.LocationCache;
import org.ekstep.ep.samza.util.LocationSearchServiceClient;

public class TelemetryLocationUpdaterTask implements StreamTask, InitableTask, WindowableTask {

	private static Logger LOGGER = new Logger(TelemetryLocationUpdaterTask.class);
	private TelemetryLocationUpdaterConfig config;
	private JobMetrics metrics;
	private TelemetryLocationUpdaterService service;
	private LocationEngine locationEngine;

	public TelemetryLocationUpdaterTask(Config config, TaskContext context,
										KeyValueStore<Object, Object> locationStore, LocationEngine locationEngine) {
		init(config, context, locationStore, locationEngine);
	}

	public TelemetryLocationUpdaterTask() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(Config config, TaskContext context) {
		init(config, context, (KeyValueStore<Object, Object>) context.getStore("location-store"), locationEngine);
	}

	@SuppressWarnings("unchecked")
	public void init(Config config, TaskContext context,
					 KeyValueStore<Object, Object> locationCacheStore, LocationEngine locationEngine) {

		this.config = new TelemetryLocationUpdaterConfig(config);
		LocationSearchServiceClient searchServiceClient =
				new LocationSearchServiceClient(config.get("channel.search.service.endpoint"),
						config.get("location.search.service.endpoint"),
						config.get("search.service.authorization.token"));

		metrics = new JobMetrics(context, this.config.jobName());
		CacheService<String, Location> locationStore = locationCacheStore != null
				? new CacheService<>(locationCacheStore, new TypeToken<CacheEntry<Location>>() {
		}.getType(), metrics)
				: new CacheService<>(context, "location-store", CacheEntry.class, metrics);

		locationEngine =
				locationEngine == null ?
				new LocationEngine(locationStore,
						searchServiceClient, new LocationCache(config))
				: locationEngine;
		metrics = new JobMetrics(context, this.config.jobName());
		service = new TelemetryLocationUpdaterService(this.config, locationEngine);
	}

	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator taskCoordinator)
			throws Exception {

		TelemetryLocationUpdaterSink sink = new TelemetryLocationUpdaterSink(collector, metrics, config);
		TelemetryLocationUpdaterSource source = new TelemetryLocationUpdaterSource(envelope);
		service.process(source, sink);
	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

		String mEvent = metrics.collect();
		collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", config.metricsTopic()), mEvent));
		metrics.clear();
	}
}
