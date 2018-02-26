package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryConverterConfig {
    private final String JOB_NAME = "TelemetryConverter";
    private final String metricsTopic;
    private String successTopic;
    private String failedTopic;

    public TelemetryConverterConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.v3");
        failedTopic = config.get("output.failed.topic.name", "telemetry.v3.fail");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }
}