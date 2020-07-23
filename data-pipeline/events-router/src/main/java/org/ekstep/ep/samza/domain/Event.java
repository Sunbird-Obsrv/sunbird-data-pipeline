package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.task.EventsRouterConfig;

import java.util.HashMap;
import java.util.Map;

public class Event extends Events {

    public Event(Map<String, Object> map) {
        super(map);
    }

    public void markFailure(String error, EventsRouterConfig config) {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.tr_processed", false);

        telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
        telemetry.add("metadata.tr_error", error);
        telemetry.add("metadata.src", config.jobName());
    }

    public void markSkipped() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dd_dr_processed", false);
        telemetry.add("flags.dd_dr_checksum_present", false);
    }

    public void markDuplicate() {
        telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
        telemetry.add("flags.dd_dr_processed", false);
        telemetry.add("flags.dd_dr_duplicate_event", true);
    }

}
