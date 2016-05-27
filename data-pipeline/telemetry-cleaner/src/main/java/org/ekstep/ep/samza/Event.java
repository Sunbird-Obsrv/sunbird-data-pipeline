package org.ekstep.ep.samza;

import java.util.Map;


public class Event {
    private final Map<String, Object> map;

    public Map<String, Object> getMap() {
        return map;
    }

    public Event(Map<String, Object> map) {
        this.map = map;
    }

    public String eid() {
        return map != null && map.containsKey("eid") ? (String) map.get("eid") : null;
    }
}
