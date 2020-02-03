package org.ekstep.ep.samza.domain;


import org.apache.commons.lang.StringUtils;
import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.task.DeDuplicationConfig;
import java.util.HashMap;
import java.util.Map;

public class Event extends Events {

    public Event(Map<String, Object> map) {
        super(map);
    }


    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.derived_dd_processed", false);
        telemetry.add("flags.derived_dd_checksum_present", false);
    }

    public void markDuplicate() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.derived_dd_processed", false);
        telemetry.add("flags.derived_dd_duplicate_event", true);
    }

    public void markSuccess() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.derived_dd_processed", true);
        telemetry.add("type", "events");
    }

    public void markRedisFailure() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.derived_dd_processed", false);
        telemetry.add("flags.derived_dd_redis_failure", true);
        telemetry.add("type", "events");
    }

    public void markFailure(String error, DeDuplicationConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.derived_dd_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.derived_dd_error", error);
        telemetry.add("metadata.src", config.jobName());
    }

}

