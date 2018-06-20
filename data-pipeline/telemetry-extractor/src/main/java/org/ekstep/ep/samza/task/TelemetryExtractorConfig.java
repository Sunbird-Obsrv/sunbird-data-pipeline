package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class TelemetryExtractorConfig {
    private final String JOB_NAME = "TelemetryExtractor";
    private final String metricsTopic;
    private String successTopic;
    private String failedTopic;

    public TelemetryExtractorConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.extracted");
        failedTopic = config.get("output.failed.topic.name", "telemetry.extracted.fail");
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