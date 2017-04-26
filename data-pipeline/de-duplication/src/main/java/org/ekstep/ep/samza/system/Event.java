package org.ekstep.ep.samza.system;


import com.google.gson.Gson;

import java.util.HashMap;
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
        if(map != null && map.containsKey("metadata") && (((Map<String, Object>) map.get("metadata")).containsKey("checksum")))
            return (String) ((Map<String, Object>) map.get("metadata")).get("checksum");

        if( map != null && map.containsKey("mid"))
            return (String) map.get("mid");

        return null;
    }

    public String getJson(){
        Gson gson=new Gson();
        String json = gson.toJson(map);
        return json;
    }

    public String id() {
        return map != null && map.containsKey("metadata") &&
            (((Map<String, Object>) map.get("metadata")).containsKey("checksum"))
            ? (String) ((Map<String, Object>) map.get("metadata")).get("checksum")
            : null;
    }

    public void addEventType() {
        map.put("type","events");
    }

    @Override
    public String toString() {
        return "Event{" +
            "map=" + getJson() +
            '}';
    }
}

