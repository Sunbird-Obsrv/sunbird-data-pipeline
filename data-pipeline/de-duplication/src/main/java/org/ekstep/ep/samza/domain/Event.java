package org.ekstep.ep.samza.domain;


import com.google.gson.Gson;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Telemetry telemetry;

    public Event(Map<String,Object> map) {
        this.telemetry = new Telemetry(map);
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String getChecksum(){

        String checksum = id();
        if(checksum != null)
            return checksum;

       return mid();
    }

    public String id() {
        NullableValue<String> checksum = telemetry.read("metadata.checksum");
        return checksum.value();
    }

    public String mid() {
        NullableValue<String> checksum = telemetry.read("mid");
        return checksum.value();
    }

    public void addEventType() {
        telemetry.add("type", "events");
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }

    public void updateMetadata(String value) {
        telemetry.add("metadata.de_duplication_error",value);
    }
}

