package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.task.DruidEventsValidatorConfig;

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

    public String version() {
        return (String) telemetry.read("ver").value();
    }

    @Override
    public String toString() {
        return "Event{" + "telemetry=" + telemetry + '}';
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dv_processed", true);
        telemetry.add("type", "events");
    }

    public void markFailure(String error, DruidEventsValidatorConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dv_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        if (null != error) {
            telemetry.add("metadata.dv_error", error);
            telemetry.add("metadata.src", config.jobName());
        }

    }

    public boolean isSummaryEvent() {
        return eid() != null && eid().equals("ME_WORKFLOW_SUMMARY");
    }

    public boolean isSearchEvent() {
        return "SEARCH".equalsIgnoreCase(eid());
    }

    public boolean isLogEvent() {
        return "LOG".equalsIgnoreCase(eid());
    }
}
