package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;

import java.util.HashMap;
import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private final Telemetry telemetry;
    private final Gson gson = new Gson();

    public Event(Map<String, Object> map) {
        this.telemetry = new Telemetry(map);
    }

    public String id() {
        return telemetry.<String>read("metadata.checksum").value();
    }

    public Map<String, Boolean> flags() {
        return telemetry.<Map<String, Boolean>>read("flags").value();
    }

    public Map<String, Object> metadata() {
        return telemetry.<Map<String, Object>>read("metadata").value();
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
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.portal_profile_manage_skipped", true);
    }

    public void markProcessed() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.portal_profile_manage_processed", true);
    }

    public NullableValue<String> uid() {
        return telemetry.read("uid");
    }

    public String userDetails() {
        NullableValue<Map<String, Object>> detailsMap = telemetry.read("edata.eks");
        if (detailsMap.isNull()) {
            return null;
        }
        return gson.toJson(detailsMap.value());
    }

    public void markFailed(Object err, Object errmsg) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.portal_profile_manage_failed", true);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.portal_profile_manage_err", err);
        telemetry.add("metadata.portal_profile_manage_errmsg", errmsg);
    }

}