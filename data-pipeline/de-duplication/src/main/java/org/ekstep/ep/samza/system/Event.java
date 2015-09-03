package org.ekstep.ep.samza.system;


import com.google.gson.Gson;

import java.util.Map;

public class Event {
    private final Map<String, Object> map;
    private Map<String, Object> jsonObject;

    public Event(Map<String,Object> map) {
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return map;
    }

    public String getChecksum(){
        Map<String, Object> metadata = (Map<String, Object>) map.get("metadata");
        return (String)metadata.get("checksum");
    }

    public String getJson(){
        Gson gson=new Gson();
        String json = gson.toJson(map);
        return json;
    }
}

