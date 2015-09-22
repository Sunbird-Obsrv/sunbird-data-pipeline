package org.ekstep.ep.samza.system;


import com.google.gson.Gson;
import org.ekstep.ep.samza.system.ChecksumGenerator;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Map<String, Object> map;

    private Gson gson = new Gson();

    public Event(Map<String,Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public String getCid() {
        return (String) map.get("cid");
    }

    public String getCType() {
        return (String) map.get("ctype");
    }

    public void addTaxonomyData(Map<String, Object> taxonomyMap){
        map.put("taxonomy", taxonomyMap);
    }

    public void addCheksum(){
        String newMap = gson.toJson(map);
        String checksum = new ChecksumGenerator(newMap).generateCheksum();
        Map<String,Object> metadata = new HashMap<String,Object>();
        metadata.put("checksum",checksum);
        map.put("metadata", metadata);
    }

    public Boolean isChecksumPresent(){
        return map.containsKey("metadata");
    }
}

