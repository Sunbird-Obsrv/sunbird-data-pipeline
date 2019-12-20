package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.Arrays;
import java.util.List;

public class TelemetryRouterConfig {

    private final String JOB_NAME = "TelemetryRouter";

    private String failedTopic;
    private String primaryRouteTopic;
    private String secondaryRouteTopic;
    private String secondaryRouteEvents;
    private String malformedTopic;
    private String auditRouteTopic;

    public TelemetryRouterConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        primaryRouteTopic = config.get("router.events.primary.route.topic", "telemetry.sink");
        secondaryRouteEvents = config.get("router.events.secondary.route.events", "LOG,ERROR");
        secondaryRouteTopic = config.get("router.events.secondary.route.topic", "telemetry.log");
        auditRouteTopic = config.get("router.events.audit.route.topic", "telemetry.audit");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");

    }

    public String getPrimaryRouteTopic() {
        return primaryRouteTopic;
    }


    public String getSecondaryRouteTopic() {
        return secondaryRouteTopic;
    }


    public List<String> getSecondaryRouteEvents() {
        String[] events = this.secondaryRouteEvents.split(",");
        return Arrays.asList(events);
    }

    public String getAuditRouteTopic() {
        return auditRouteTopic;
    }


    public String failedTopic() {
        return failedTopic;
    }

    public String malformedTopic() {
        return malformedTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }
}