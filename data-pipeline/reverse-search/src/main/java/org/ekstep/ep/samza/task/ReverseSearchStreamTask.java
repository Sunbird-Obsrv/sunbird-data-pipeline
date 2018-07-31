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

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import org.ekstep.ep.samza.api.GoogleGeoLocationAPI;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.rule.LocationAbsent;
import org.ekstep.ep.samza.rule.LocationEmpty;
import org.ekstep.ep.samza.rule.LocationPresent;
import org.ekstep.ep.samza.rule.Rule;
import org.ekstep.ep.samza.service.DeviceService;
import org.ekstep.ep.samza.service.GoogleReverseSearchService;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Location;
import org.ekstep.ep.samza.util.Configuration;

import com.google.gson.Gson;

public class ReverseSearchStreamTask implements StreamTask, InitableTask, WindowableTask {

	static Logger LOGGER = new Logger(ReverseSearchStreamTask.class);
	private KeyValueStore<String, Object> deviceStore;
	private String bypass;
	private LocationService locationService;
	private DeviceService deviceService;
	private List<Rule> locationRules;
	private Configuration configuration;
	private JobMetrics metrics;

	@SuppressWarnings("unchecked")
	@Override
	public void init(Config config, TaskContext context) {
		this.configuration = new Configuration(config);
		bypass = configuration.getByPass();
		KeyValueStore<String, Object> reverseSearchStore = (KeyValueStore<String, Object>) context
				.getStore("reverse-search");
		this.deviceStore = (KeyValueStore<String, Object>) context.getStore("device");
		GoogleReverseSearchService googleReverseSearch = new GoogleReverseSearchService(
				new GoogleGeoLocationAPI(configuration.getApiKey()));

		locationRules = Arrays.asList(new LocationPresent(), new LocationEmpty(), new LocationAbsent());

		locationService = new LocationService(reverseSearchStore, googleReverseSearch,
				configuration.getReverseSearchCacheAreaSizeInMeters());
		deviceService = new DeviceService(deviceStore);
		metrics = new JobMetrics(context, this.configuration.jobName());
	}

	public ReverseSearchStreamTask() {
	}

	// For testing only
	ReverseSearchStreamTask(KeyValueStore<String, Object> deviceStore, String bypass, LocationService locationService,
			DeviceService deviceService, List<Rule> locationRules, Config config, TaskContext context) {
		this.deviceStore = deviceStore;
		this.bypass = bypass;
		this.locationService = locationService;
		this.deviceService = deviceService;
		this.locationRules = locationRules;
		this.configuration = new Configuration(config);
		this.metrics = new JobMetrics(context, this.configuration.jobName());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {

		ReverseSearchSink sink = new ReverseSearchSink(collector, metrics, configuration);
		Map<String, Object> jsonObject = null;
		try {
			jsonObject = (Map<String, Object>) envelope.getMessage();
			Object ets1 = jsonObject.get("ets");
			Event event = new Event(jsonObject);
			if (ets1 != null)
				LOGGER.info("", MessageFormat.format("Inside Task. ETS:{0}, type: {1}", ets1, ets1.getClass()));
			processEvent(event, collector);
		} catch (Exception e) {
			LOGGER.error(null, "PROCESSING FAILED: " + jsonObject, e);
			if (null != jsonObject)
				sink.sendToErrorTopic(jsonObject);
		}
	}

	public void processEvent(Event event, MessageCollector collector) {

		ReverseSearchSink sink = new ReverseSearchSink(collector, metrics, configuration);
		event.setTimestamp();
		Location location = null;

		if (bypass.equals("true")) {
			LOGGER.info(event.id(), "BYPASSING: {}", event);
		} else {
			try {
				String did = event.getDid();
				for (Rule rule : locationRules) {
					if (rule.isApplicableTo(event)) {
						rule.apply(event, locationService, deviceService);
					}
				}
				location = deviceService.getLocation(did, event.id());
			} catch (Exception e) {
				LOGGER.error(null, "REVERSE SEARCH FAILED: " + event, e);
				metrics.incErrorCounter();
			}
		}

		if (location != null) {
			event.AddLocation(location);
			event.setFlag("ldata_obtained", true);
		} else {
			event.setFlag("ldata_obtained", false);
		}

		event.updateDefaults(configuration);
		event.setFlag("ldata_processed", true);
		sink.sendToSuccessTopic(event);

	}

	@Override
	public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String mEvent = metrics.collect();
		@SuppressWarnings("unchecked")
		Map<String, Object> mEventMap = new Gson().fromJson(mEvent, Map.class);
		collector.send(
				new OutgoingMessageEnvelope(new SystemStream("kafka", configuration.getMetricsTopic()), mEventMap));
		metrics.clear();
	}
}
