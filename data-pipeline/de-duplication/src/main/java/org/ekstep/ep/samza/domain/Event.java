package org.ekstep.ep.samza.domain;


import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.DeDuplicationConfig;

import java.util.HashMap;
import java.util.Map;

public class Event {
    private final Telemetry telemetry;

    public Event(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
    }

    public Map<String, Object> getMap() {
        return telemetry.getMap();
    }

    public String getJson() {
        Gson gson = new Gson();
        String json = gson.toJson(getMap());
        return json;
    }

    public String getChecksum() {

        String checksum = id();
        if (checksum != null)
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
    
    public String did() {
        NullableValue<String> checksum = telemetry.read("context.did");
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

    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dd_processed", false);
        telemetry.add("flags.dd_checksum_present", false);
    }

    public void markDuplicate() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dd_processed", false);
        telemetry.add("flags.dd_duplicate_event", true);
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dd_processed", true);
        telemetry.add("type", "events");
    }

    public void markRedisFailure() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dd_processed", false);
        telemetry.add("flags.dd_redis_failure", true);
        telemetry.add("type", "events");
    }

    public void markFailure(String error, DeDuplicationConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dd_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.dd_error", error);
        telemetry.add("metadata.src", config.jobName());
    }

    public void updateDefaults(DeDuplicationConfig config) {
        String channelString = telemetry.<String>read("context.channel").value();
        String channel = StringUtils.deleteWhitespace(channelString);
        if(channel == null || channel.isEmpty()) {
            telemetry.addFieldIfAbsent("context", new HashMap<String, Object>());
            telemetry.add("context.channel", config.defaultChannel());
        }
    }
}

