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
import org.apache.samza.metrics.Counter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.rule.*;
import org.ekstep.ep.samza.api.GoogleGeoLocationAPI;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.service.DeviceService;
import org.ekstep.ep.samza.service.GoogleReverseSearchService;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.*;
import org.ekstep.ep.samza.util.Configuration;

import java.text.MessageFormat;
import java.util.*;

public class ReverseSearchStreamTask implements StreamTask, InitableTask, WindowableTask {

    static Logger LOGGER = new Logger(ReverseSearchStreamTask.class);
    private KeyValueStore<String, Object> deviceStore;
    private String bypass;
    private LocationService locationService;
    private Counter messageCount;
    private DeviceService deviceService;
    private List<Rule> locationRules;
    private Configuration configuration;

    @Override
    public void init(Config config, TaskContext context) {
        this.configuration = new Configuration(config);
        bypass = configuration.getByPass();
        KeyValueStore<String, Object> reverseSearchStore = (KeyValueStore<String, Object>) context.getStore("reverse-search");
        this.deviceStore = (KeyValueStore<String, Object>) context.getStore("device");
        GoogleReverseSearchService googleReverseSearch = new GoogleReverseSearchService(new GoogleGeoLocationAPI(configuration.getApiKey()));

        locationRules = Arrays.asList(new LocationPresent(), new LocationEmpty(), new LocationAbsent());

        locationService = new LocationService(reverseSearchStore,
                googleReverseSearch,
                configuration.getReverseSearchCacheAreaSizeInMeters()
                );
        deviceService = new DeviceService(deviceStore);
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");
    }

    public ReverseSearchStreamTask() {
    }

    //For testing only
    ReverseSearchStreamTask(KeyValueStore<String, Object> deviceStore,
                            String bypass, LocationService locationService, DeviceService deviceService, List<Rule> locationRules, Config config) {
        this.deviceStore = deviceStore;
        this.bypass = bypass;
        this.locationService = locationService;
        this.deviceService =  deviceService;
        this.locationRules = locationRules;
        this.configuration = new Configuration(config);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        Map<String, Object> jsonObject = null;
        try {
            jsonObject = (Map<String, Object>) envelope.getMessage();
            Object ets1 = jsonObject.get("ets");
            Event event = new Event(jsonObject);
            if(ets1 != null)
                LOGGER.info("", MessageFormat.format("Inside Task. ETS:{0}, type: {1}", ets1, ets1.getClass()));
            processEvent(event, collector);
            messageCount.inc();
        } catch (Exception e) {
            LOGGER.error(null, "PROCESSING FAILED: " + jsonObject, e);
        }
    }

    public void processEvent(Event event, MessageCollector collector) {
        event.setTimestamp();
        Location location = null;

        if (bypass.equals("true")) {
            LOGGER.info(event.id(), "BYPASSING: {}", event);
        } else {
            try {
                String did = event.getDid();
                for (Rule rule : locationRules) {
                    if(rule.isApplicableTo(event)){
                        rule.apply(event, locationService, deviceService);
                    }
                }
                location = deviceService.getLocation(did,event.id());
            } catch (Exception e) {
                LOGGER.error(null, "REVERSE SEARCH FAILED: " + event, e);
            }
        }

        try {
            if (location != null) {
                event.AddLocation(location);
                event.setFlag("ldata_obtained", true);
            } else {
                event.setFlag("ldata_obtained", false);
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", configuration.getFailedTopic()), event.getMap()));
            }
            event.updateDefaults(configuration);
            event.setFlag("ldata_processed", true);
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", configuration.getSuccessTopic()), event.getMap()));
        } catch (Exception e) {
            LOGGER.error(null, "ERROR WHEN ROUTING EVENT: {}" + event, e);
        }
    }

    @Override
    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        messageCount.clear();
    }
}
