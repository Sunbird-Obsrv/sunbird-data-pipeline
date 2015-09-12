package org.ekstep.ep.samza.system;


import com.google.gson.Gson;

import java.util.Map;

public class Event {
    private final Map<String, Object> map;

    public Event(Map<String,Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public String getJson(){
        Gson gson=new Gson();
        String json = gson.toJson(map);
        return json;
    }

    public String getCid() {
        return (String) map.get("cid");
    }

    public void addTaxonomyData(Map<String, Object> taxonomyMap){
        map.put("taxonomy", taxonomyMap);
    }
}

