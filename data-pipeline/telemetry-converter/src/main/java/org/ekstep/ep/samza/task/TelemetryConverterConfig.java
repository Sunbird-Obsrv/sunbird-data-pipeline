package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryConverterConfig {
    private String successTopic;
    private String failedTopic;

    public TelemetryConverterConfig(Config config) {
        // TODO: decide on the output topics
        successTopic = config.get("output.success.topic.name", "telemetry.v3");
        failedTopic = config.get("output.failed.topic.name", "telemetry.v3.fail");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }
}