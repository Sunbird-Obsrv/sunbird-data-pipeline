package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Telemetry telemetry;

    public Event(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
    }

    public String id() {
        return telemetry.<String>read("metadata.checksum").value();
    }

    public Map<String, Boolean> flags() {
        return telemetry.<Map<String, Boolean>>read("flags").value();
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }

    public Map<String, Object> map() {
        return telemetry.getMap();
    }

    public String eid() {
        return telemetry.<String>read("eid").value();
    }

    public void markSkipped() {
        telemetry.add("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.portal_profile_manage_skipped", true);
    }
}