package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class EventsFlattenerConfig {

    private final String JOB_NAME = "Events Flattener";

    private String failedTopic;
    private String successTopic;
    private String malformedTopic;
    private String flattenEventName;

    public EventsFlattenerConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        successTopic = config.get("output.success.topic.name", "telemetry.flattener");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        flattenEventName = config.get("flatten.telemetry.event.name", "SHARE");
    }

    public String getSuccessTopic() {
        return successTopic;
    }


    public String getFailedTopic() {
        return failedTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }

    public String getFlattenEventName() {
        return flattenEventName;
    }

}