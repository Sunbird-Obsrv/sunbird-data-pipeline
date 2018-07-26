package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryExtractorConfig {
    private final String JOB_NAME = "TelemetryExtractor";
    private final String metricsTopic;
    private String successTopic;

    public TelemetryExtractorConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.raw");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
    }

    public String successTopic() {
        return successTopic;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }
}