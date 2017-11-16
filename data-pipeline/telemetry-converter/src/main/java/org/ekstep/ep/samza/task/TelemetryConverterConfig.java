package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryConverterConfig {
    private String successTopic;
    private String failedTopic;

    public TelemetryConverterConfig(Config config) {
        // TODO: decide on the output topics
        successTopic = config.get("output.success.topic.name", "telemetry.valid");
        failedTopic = config.get("output.failed.topic.name", "telemetry.unique.fail");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }
}