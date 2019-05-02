package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class DruidEventsValidatorConfig {

    private final String JOB_NAME = "DruidEventsValidator";
    private String telemetrySchemaPath;
    private String summarySchemaPath;
    private String defaultSchemafile;
    private String successTopic;
    private String failedTopic;
    private String malformedTopic;
    private String metricsTopic;

    public DruidEventsValidatorConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.denorm.valid");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        metricsTopic = config.get("output.metrics.topic.name", "telemetry.pipeline_metrics");
        telemetrySchemaPath = config.get("telemetry.schema.path", "schemas/telemetry");
        summarySchemaPath = config.get("summary.schema.path", "schemas/summary");
        defaultSchemafile = config.get("event.schema.file","envelope.json");
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

    public String telemetrySchemaPath() { return telemetrySchemaPath; }

    public String summarySchemaPath() { return summarySchemaPath; }

    public String defaultSchemafile(){return defaultSchemafile;}

    public String jobName() {
        return JOB_NAME;
    }
}