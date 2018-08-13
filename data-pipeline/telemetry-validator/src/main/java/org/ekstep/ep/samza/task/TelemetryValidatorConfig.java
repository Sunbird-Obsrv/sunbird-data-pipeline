package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryValidatorConfig {

    private final String JOB_NAME = "TelemetryValidator";
    private String schemaPath;
    private String successTopic;
    private String failedTopic;
    private String malformedTopic;
    private String defaultChannel;
    private String metricsTopic;

    public TelemetryValidatorConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.valid");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
        defaultChannel = config.get("default.channel", "org.sunbird");
        schemaPath = config.get("telemetry.schema.path", "/etc/samza-jobs/schemas");
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

    public String metricsTopic() {
        return metricsTopic;
    }

    public String defaultChannel() {
        return defaultChannel;
    }

    public String schemaPath() { return schemaPath; }

    public String jobName() {
        return JOB_NAME;
    }
}