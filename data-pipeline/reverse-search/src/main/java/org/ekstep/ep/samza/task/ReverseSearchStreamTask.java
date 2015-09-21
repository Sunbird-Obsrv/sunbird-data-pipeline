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

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.actions.GoogleReverseSearch;
import org.ekstep.ep.samza.api.GoogleGeoLocationAPI;
import org.ekstep.ep.samza.system.Device;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Location;

import java.util.Map;

public class ReverseSearchStreamTask implements StreamTask, InitableTask {

    private KeyValueStore<String, Object> reverseSearchStore;
    private KeyValueStore<String, Object> deviceStore;
    private GoogleReverseSearch googleReverseSearch;
    private String successTopic;
    private String failedTopic;

    @Override
    public void init(Config config, TaskContext context) {
        String apiKey = config.get("google.api.key", "");

        successTopic = config.get("output.success.topic.name", "events_with_location");
        failedTopic = config.get("output.failed.topic.name", "events_failed_location");

        this.reverseSearchStore = (KeyValueStore<String, Object>) context.getStore("reverse-search");
        this.deviceStore = (KeyValueStore<String, Object>) context.getStore("device");
        googleReverseSearch = new GoogleReverseSearch(new GoogleGeoLocationAPI(apiKey));
    }

    public ReverseSearchStreamTask() {
    }

    public ReverseSearchStreamTask(KeyValueStore<String, Object> reverseSearchStore, KeyValueStore<String, Object> deviceStore, GoogleReverseSearch googleReverseSearch) {
        this.reverseSearchStore = reverseSearchStore;
        this.deviceStore = deviceStore;
        this.googleReverseSearch = googleReverseSearch;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
        Map<String, Object> jsonObject;
        Location location = null;
        Device device = null;
        try {
            jsonObject = (Map<String, Object>) envelope.getMessage();
            processEvent(new Event(jsonObject), collector);
        } catch (Exception e) {
            System.err.println("Error while getting message");
        }
    }

    public void processEvent(Event event, MessageCollector collector) {
        Location location = null;
        Device device = null;
        try {

            String loc = event.getGPSCoordinates();
            String did = event.getDid();

            if (loc != null && !loc.isEmpty()) {
                String stored_location = (String) reverseSearchStore.get(loc);
                if (stored_location == null) {
                    // do reverse search
                    System.out.println("Performing reverse search");
                    location = googleReverseSearch.getLocation(loc);
                    String json = JsonWriter.objectToJson(location);

                    System.out.println("Setting device loc in stores");
                    reverseSearchStore.put(loc, json);
                    device = new Device(did);
                    device.setLocation(location);
                    String djson = JsonWriter.objectToJson(device);
                    deviceStore.put(did, djson);
                } else {
                    System.out.println("Picking store data for reverse search");
                    location = (Location) JsonReader.jsonToJava(stored_location);
                    device = new Device(did);
                    device.setLocation(location);
                    String djson = JsonWriter.objectToJson(device);
                    deviceStore.put(did, djson);
                }
            } else {
                System.out.println("Trying to pick from device");
                String stored_device = (String) deviceStore.get(did);
                device = (Device) JsonReader.jsonToJava(stored_device);
                location = device.getLocation();
            }
        } catch (Exception e) {
            System.out.println(e);
            System.err.println("unable to parse");
        }
        System.out.println("ok");
        try {
            if (location != null) {
                event.AddLocation(location);
                event.setFlag("ldata_obtained",true);
            } else {
                event.setFlag("ldata_obtained", false);
                collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
            }
            event.setFlag("ldata_processed",true);
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
        } catch (Exception e) {
            System.out.println("ok");
            e.printStackTrace();
            System.err.println("!");
        }

    }

}
