package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryValidatorConfig {

    private String schemaPath;
    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;
    private String malformedTopic;
    private String defaultChannel;

    public TelemetryValidatorConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.unique");
        failedTopic = config.get("output.failed.topic.name", "telemetry.unique.fail");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.duplicate");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        defaultChannel = config.get("default.channel", "in.ekstep");
        schemaPath = config.get("telemetry.schema.path", "/schemas/");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String malformedTopic() {
        return malformedTopic;
    }

    public String defaultChannel() {
        return defaultChannel;
    }

    public String schemaPath() { return schemaPath; }
}