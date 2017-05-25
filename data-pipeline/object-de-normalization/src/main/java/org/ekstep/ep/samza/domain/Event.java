package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Telemetry telemetry;

    public Event(Telemetry telemetry) {
        this.telemetry = telemetry;
    }

    public String id() {
        NullableValue<String> checksum = telemetry.read("metadata.checksum");
        return checksum.value();
    }

    public String eid() {
        NullableValue<String> checksum = telemetry.read("eid");
        return checksum.value();
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

    public NullableValue<String> read(String path) {
        return telemetry.read(path);
    }

    public void update(String path, Map<String, String> data) {
        telemetry.add(path, data);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        return telemetry != null ? telemetry.equals(event.telemetry) : event.telemetry == null;
    }

    @Override
    public int hashCode() {
        return telemetry != null ? telemetry.hashCode() : 0;
    }
}

