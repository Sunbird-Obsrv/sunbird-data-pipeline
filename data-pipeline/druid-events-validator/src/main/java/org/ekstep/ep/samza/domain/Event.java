package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.events.domain.Events;
import org.ekstep.ep.samza.task.DruidEventsValidatorConfig;

import java.util.HashMap;
import java.util.Map;

public class Event extends Events {


    public Event(Map<String, Object> map) {
        super(map);
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
