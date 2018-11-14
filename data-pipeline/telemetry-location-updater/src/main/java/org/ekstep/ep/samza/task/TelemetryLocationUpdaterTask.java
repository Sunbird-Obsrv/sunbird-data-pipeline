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
	// private LocationCache cache;
	// private KeyValueStore<String, Location> locationStore;
	// private LocationSearchServiceClient searchService;

	public TelemetryLocationUpdaterTask(Config config, TaskContext context, KeyValueStore<String,
			Location> locationStore, LocationEngine locationEngine) {
		// this.cache = cache;
		// this.locationStore = locationStore;
		// this.searchService = searchService;
		init(config, context, locationStore, locationEngine);
	}

	public TelemetryLocationUpdaterTask() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(Config config, TaskContext context) {
		init(config, context, (KeyValueStore<String, Location>) context.getStore("location-store"), null);
	}

	@SuppressWarnings("unchecked")
	public void init(Config config, TaskContext context,
					 KeyValueStore<String, Location> locationCacheStore, LocationEngine locationEngine) {

		this.config = new TelemetryLocationUpdaterConfig(config);
		// if (cache == null) cache = new LocationCache(config);
		// if (locationStore == null) locationStore = (KeyValueStore<String, Location>) context.getStore("location-store");
		LocationSearchServiceClient searchServiceClient =
				new LocationSearchServiceClient(config.get("channel.search.service.endpoint"),
						config.get("location.search.service.endpoint"),
						config.get("search.service.authorization.token"));
		LocationCache locationCache = new LocationCache(config);
		locationEngine =
				locationEngine == null ?
				new LocationEngine(locationCacheStore,
						searchServiceClient, locationCache)
				: locationEngine;
		metrics = new JobMetrics(context, this.config.jobName());

		/*
		LocationSearchServiceClient searchServiceClient = searchService == null
				? new LocationSearchServiceClient(config.get("channel.search.service.endpoint") ,config.get("location.search.service.endpoint"), config.get("search.service.authorization.token"))
				: searchService;
				*/

		// service = new TelemetryLocationUpdaterService(this.config, cache, locationStore, searchServiceClient);
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
