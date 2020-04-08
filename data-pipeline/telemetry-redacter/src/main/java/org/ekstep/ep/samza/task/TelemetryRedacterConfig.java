package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;


public class TelemetryRedacterConfig {
  
    private final String JOB_NAME = "TelemetryRedacter";

    private String redactedRouteTopic;
    private String failedTopic;

    public TelemetryRedacterConfig(Config config) {
        redactedRouteTopic = config.get("redacted.route.topic", "telemetry.raw");
        failedTopic = config.get("failed.topic.name", "telemetry.failed");
    }

    public String getRedactedRouteTopic() {
        return redactedRouteTopic;
    }
    
    public String jobName() {
        return JOB_NAME;
    }
    
    public String failedTopic() {
        return failedTopic;
    }

}