package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.logger.Logger;

import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Map<String, Object> map;

    public Event(Map<String, Object> map) {
        this.map = map;
    }

    public String id() {
        return map != null && map.containsKey("metadata") &&
                (((Map<String, Object>) map.get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) map.get("metadata")).get("checksum")
                : null;
    }

    @Override
    public String toString() {
        return "Event{" +
                "map=" + map +
                '}';
    }

    public Map<String, Object> map() {
        return map;
    }
}

