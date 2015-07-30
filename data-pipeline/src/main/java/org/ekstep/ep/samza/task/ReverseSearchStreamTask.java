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
import org.ekstep.ep.samza.system.Location;

import java.util.HashMap;
import java.util.Map;

public class ReverseSearchStreamTask implements StreamTask, InitableTask, ClosableTask {

  private KeyValueStore<String, Object> reverseSearchStore;
  private KeyValueStore<String, Object> deviceStore;
  private GoogleReverseSearch googleReverseSearch;

  public void init(Config config, TaskContext context) {
    String apiKey = config.get("google.api.key","");
    this.reverseSearchStore = (KeyValueStore<String, Object>) context.getStore("reverse-search");
    this.deviceStore = (KeyValueStore<String, Object>) context.getStore("device");
    googleReverseSearch = new GoogleReverseSearch(new GoogleGeoLocationAPI(apiKey));
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
    Map<String, Object> jsonObject;
    Location location = null;
    Device device = null;
    try {

      jsonObject = (Map<String, Object>) envelope.getMessage();
      Map<String, Object> edata = (Map<String, Object>) jsonObject.get("edata");
      Map<String, Object> eks = (Map<String, Object>) edata.get("eks");
      String loc = (String)eks.get("loc");
      String did = (String)jsonObject.get("did");

      System.out.println("loc= "+loc);

      if(loc!=null && loc!=""){
        String stored_location = (String)reverseSearchStore.get(loc);
        System.out.println("stored_location= "+stored_location);

        if(stored_location == null){
          // do reverse search
          System.out.println("Performing reverse search");

          location = googleReverseSearch.getLocation(loc);

          String json = JsonWriter.objectToJson(location);
          reverseSearchStore.put(loc, json);

          System.out.println("Setting device loc");
          device = new Device(did);
          device.setLocation(location);
          String djson = JsonWriter.objectToJson(device);
          deviceStore.put(did, djson);
        } else {
          System.out.println("Picking store data for reverse search");
          location = (Location)JsonReader.jsonToJava(stored_location);
        }
      } else {
        System.out.println("Trying to pick from device");
        String stored_device = (String)deviceStore.get(did);
        device = (Device)JsonReader.jsonToJava(stored_device);
        location = device.getLocation();
      }
      } catch (Exception e) {
        System.err.println("unable to parse");
        jsonObject = null;
      }
    // WikipediaFeedEvent event = new WikipediaFeedEvent(jsonObject);
    System.out.println("ok");
    try {

      if(location!=null){
        System.out.println("Location available");
        Map<String, String> ldata = new HashMap<String, String>();
        ldata.put("locality",location.getCity());
        ldata.put("district",location.getDistrict());
        ldata.put("state",location.getState());
        ldata.put("country",location.getCountry());
        jsonObject.put("ldata",ldata);
      } else {
        System.out.println("location NOT available");
      }

      collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "events_with_location"), jsonObject));
    } catch (Exception e) {
      System.err.println("!");
    }
  }

  public static void main(String[] args) {
    // String[] lines = new String[] { "[[Wikipedia talk:Articles for creation/Lords of War]]  http://en.wikipedia.org/w/index.php?diff=562991653&oldid=562991567 * BBGLordsofWar * (+95) /* Lords of War: Elves versus Lizardmen */]", "[[David Shepard (surgeon)]] M http://en.wikipedia.org/w/index.php?diff=562993463&oldid=562989820 * Jacobsievers * (+115) /* American Revolution (1775�1783) */  Added to note regarding David Shepard's brothers" };

    // for (String line : lines) {
    //   System.out.println(parse(line));
    // }
  }

  @Override
  public void close() throws Exception {

  }
}
