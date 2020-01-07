package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;

public class EventsFlattenConfig {

    private final String JOB_NAME = "Events Flatten";

    private String failedTopic;
    private String successTopic;
    private String malformedTopic;
    private String flattenEventName;

    public EventsFlattenConfig(Config config) {
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        successTopic = config.get("output.success.topic.name", "telemetry.flatten");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        flattenEventName = config.get("flatten.telemetry.event.name", "SHARE");
    }

    public String getSuccessTopic() {
        return successTopic;
    }


    public String getFailedTopic() {
        return failedTopic;
    }


    public String getMalformedTopic() {
        return malformedTopic;

    }

    public String jobName() {
        return JOB_NAME;
    }

    public String getFlattenEventName() {
        return flattenEventName;
    }

}