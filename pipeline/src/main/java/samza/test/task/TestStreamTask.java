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

package samza.test.task;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.TaskContext;

import samza.test.system.Location;
import samza.test.system.Device;
import com.cedarsoftware.util.io.JsonWriter;
import com.cedarsoftware.util.io.JsonReader;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;

public class TestStreamTask implements StreamTask, InitableTask {

  private KeyValueStore<String, Object> reverseSearchStore;
  private KeyValueStore<String, Object> deviceStore;

  public void init(Config config, TaskContext context) {
    this.reverseSearchStore = (KeyValueStore<String, Object>) context.getStore("reverse-search");
    this.deviceStore = (KeyValueStore<String, Object>) context.getStore("device");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) {
    System.out.println("starting processing");
    Map<String, Object> jsonObject;
    Location location = null;
    Device device = null;
    try {
      jsonObject = (Map<String, Object>) envelope.getMessage();
      // {
      //   "did":"465e943d-38a1-41f3-8042-66b8453a9631",
      //   "edata":{
      //     "eks":{
      //       "loc":"22.568774,88.4331994",
      //       "ueksid":"child1"
      //     }
      //   },
      //   "eid":"GE_SESSION_START",
      //   "gdata":{
      //     "id":"genie.android",
      //     "ver":"2.2.16"
      //   },
      //   "sid":"3b68ca5b-6ee2-4e11-8763-ff1a0005ba29",
      //   "ts":"2015-07-06T11:37:32+05:30",
      //   "uid":"821c07a03badb405e8ccaea45217e56e69e4f6e0",
      //   "ver":"1.0",
      //   "@version":"1",
      //   "@timestamp":"2015-07-06T06:08:46.678Z",
      //   "uuid":"f015ae9b-1be2-431a-9a28-6eddc8a496685",
      //   "type":"events"
      // }

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

          location = new Location();

          String[] latlong = loc.split(",");
          Double _lat = Double.parseDouble(latlong[0]);
          Double _long = Double.parseDouble(latlong[1]);

          GeoApiContext context = new GeoApiContext().setApiKey("AIzaSyDd1SVvNpqDYQKAghY1-aY2EtdBoPI94l4");
          GeocodingResult[] results = GeocodingApi.newRequest(context).latlng(new LatLng(_lat,_long)).await();
          for (GeocodingResult r: results) {
            if(location.isReverseSearched()){
              break;
            }
            System.out.println(JsonWriter.objectToJson(r));
            System.out.println(JsonWriter.objectToJson(r.addressComponents));
            for(AddressComponent a:r.addressComponents){
              if(location.isReverseSearched()){
                break;
              }
              for (AddressComponentType t: a.types) {
                if(location.isReverseSearched()){
                  break;
                }
                System.out.println(JsonWriter.objectToJson(t));
                System.out.println(JsonWriter.objectToJson(t.name()));
                switch(t.ordinal()){
                  case 11:  location.setCity(a.longName);
                            break;
                  case 6:  location.setDistrict(a.longName);
                            break;
                  case 5:  location.setState(a.longName);
                            break;
                  case 4:  location.setCountry(a.longName);
                            break;
                  default: break;
                }
              }
            }
          }

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
        // System.out.println(jsonObject);
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

      collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", "test13"), jsonObject));
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
}
