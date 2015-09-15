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

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.*;
import org.ekstep.ep.samza.api.TaxonomyApi;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Taxonomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaxonomyStreamTask implements StreamTask, InitableTask {

    private KeyValueStore<String, Object> taxonomyStore;

    private String successTopic;
    private String failedTopic;
    private String apiHost;

    @Override
    public void init(Config config, TaskContext context) {
        String apiKey = config.get("google.api.key", "");

        successTopic = config.get("output.success.topic.name", "unique_events");
        failedTopic = config.get("output.failed.topic.name", "failed_taxonomy_events");
        apiHost = config.get("api.host");
        this.taxonomyStore = (KeyValueStore<String, Object>) context.getStore("taxonomy");
    }

    public TaxonomyStreamTask() {

    }

    public TaxonomyStreamTask(KeyValueStore<String, Object> taxonomyStore) {
        this.taxonomyStore = taxonomyStore;
    }

    @Override
    public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator){
        Map<String, Object> jsonObject;
        try {
            jsonObject =  (Map<String, Object>) envelope.getMessage();
            processEvent(new Event(jsonObject), collector);
        }
        catch (Exception e) {
            System.err.println("Error while getting message"+e);
        }
    }

    public void processEvent(Event event, MessageCollector collector) throws Exception {
        String cid = (String) event.getCid();
        Map<String, Object> taxonomyLibrary = null;

//        Map<String,Object> txStore = new HashMap<String, Object>();

        try {
            if (taxonomyStore.get(cid) == null) {
                System.out.println("if taxonomy store returns null");
                taxonomyLibrary = (Map<String, Object>) new TaxonomyApi(apiHost).getTaxonomyLibrary();
                createTaxonomyHash(taxonomyLibrary, taxonomyStore);
                Taxonomy taxonomy = new Taxonomy(cid, taxonomyStore);
                event.addTaxonomyData(taxonomy.getTaxonomyData(cid));
            }
            else {
                System.out.println("if taxonomy store has value");
                Taxonomy taxonomy = new Taxonomy(cid,taxonomyStore);
                event.addTaxonomyData(taxonomy.getTaxonomyData(cid));
            }
            System.out.println("Output to Success Topic ");
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", successTopic), event.getMap()));
        }
        catch(Exception e){
            System.out.println("Output to Failed Topic ");
            collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", failedTopic), event.getMap()));
        }
    }

    public void createTaxonomyHash(Map<String, Object> jsonObject,KeyValueStore<String,Object> taxonomyStore) throws Exception {

        Map<String,Object> map =  new HashMap<String, Object>();
        Gson gson = new Gson();


        Map<String, Object> result;
        result = ( Map<String, Object>) jsonObject.get("result");

        Map<String, Object> taxonomy_hierarchy;
        taxonomy_hierarchy = (Map<String, Object>) result.get("taxonomy_hierarchy");

        ArrayList<Map<String,Object>> childElements;
        childElements = (ArrayList<Map<String,Object>>) taxonomy_hierarchy.get("children");

        String parent = null;

        processChildData(map, parent, childElements);

        for (Map.Entry<String, Object> entry : map.entrySet())
        {
            taxonomyStore.put(entry.getKey(),entry.getValue());
        }

    }

    private void processChildData(Map<String, Object> map,  String parent, ArrayList<Map<String,Object>> childElements) throws Exception{

        for (Map<String, Object> element : childElements) {

            Map<String, Object> childData = new HashMap<String, Object>();
            Map<String, Object> metadata;

            String id = (String) element.get("identifier");
            String type = (String) element.get("type");
            metadata = (Map<String, Object>) element.get("metadata");
            String name = (String) metadata.get("name");

            childData.put("id",id);
            childData.put("type",type);
            childData.put("name", name);
            childData.put("parent", parent);
            map.put(id, childData);

            ArrayList<Map<String, Object>> childrens;
            childrens = (ArrayList<Map<String, Object>>) element.get("children");

            String newParent = id ;

            if (childrens == null)
                break;
            else {
                processChildData(map, newParent, childrens);;
            }
        }
    }
}
