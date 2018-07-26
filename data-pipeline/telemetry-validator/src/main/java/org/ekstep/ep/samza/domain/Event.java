package org.ekstep.ep.samza.domain;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.TelemetryValidatorConfig;

import com.google.gson.Gson;

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

    public String eid() {
        NullableValue<String> eid = telemetry.read("eid");
        return eid.value();
    }

    public String schemaName(){
        String eid = eid();
        if (eid != null){
            return MessageFormat.format("{0}.json",eid.toLowerCase());
        }
        return null;
    }

    public String version(){
        return (String) telemetry.read("ver").value();
    }

    @Override
    public String toString() {
        return "Event{" +
                "telemetry=" + telemetry +
                '}';
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.tv_processed", true);
        telemetry.add("type", "events");
    }

    public void markFailure(String error) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.tv_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.tv_error", error);
    }

    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.tv_skipped", true);
    }
    
    public void updateDefaults(TelemetryValidatorConfig config) {
        String channelString = telemetry.<String>read("context.channel").value();
        String channel = StringUtils.deleteWhitespace(channelString);
        if(channel == null || channel.isEmpty()) {
            telemetry.addFieldIfAbsent("context", new HashMap<String, Object>());
            telemetry.add("context.channel", config.defaultChannel());
        }
    }
}

