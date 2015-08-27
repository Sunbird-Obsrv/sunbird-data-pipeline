package org.ekstep.ep.samza.system;


import java.util.Map;

public class Event {
    private final Map<String, Object> map;

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
}

