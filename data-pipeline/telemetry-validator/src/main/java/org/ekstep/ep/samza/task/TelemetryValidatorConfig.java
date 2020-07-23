package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryValidatorConfig {

    private final String JOB_NAME = "TelemetryValidator";
    private String schemaPath;
    private String successTopic;
    private String failedTopic;
    private String malformedTopic;
    private String defaultChannel;
    private String schema_version;

    public TelemetryValidatorConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.valid");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        defaultChannel = config.get("default.channel", "org.sunbird");
        schemaPath = config.get("telemetry.schema.path", "/etc/samza-jobs/schemas");
        schema_version = config.get("telemetry.schema.version", "3.0");

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

    public String schemaPath() {
        return schemaPath;
    }

    public String schemaVersion() {
        return schema_version;
    }

    public String jobName() {
        return JOB_NAME;
    }
}