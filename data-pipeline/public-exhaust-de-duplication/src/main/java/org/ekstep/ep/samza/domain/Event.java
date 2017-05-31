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

    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.public_de_dup_processed", false);
        telemetry.add("flags.public_de_dup_checksum_present", false);
    }

    public void markDuplicate() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.public_de_dup_processed", false);
        telemetry.add("flags.public_de_dup_duplicate_event", true);
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.public_de_dup_processed", true);
    }

    public void markFailure(String error) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.public_de_dup_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.public_de_dup_error", error);
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }


}


