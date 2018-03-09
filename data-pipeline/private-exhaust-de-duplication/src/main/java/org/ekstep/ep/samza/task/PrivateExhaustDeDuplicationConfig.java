package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class PrivateExhaustDeDuplicationConfig {

    private final String jobName;
    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;
    private String metricsTopic;

    public PrivateExhaustDeDuplicationConfig(Config config) {
        jobName = config.get("output.metrics.job.name", "PrivateExhaustDeDuplication");
        successTopic = config.get("output.success.topic.name", "telemetry.private_exhaust");
        failedTopic = config.get("output.failed.topic.name", "telemetry.private_exhaust.fail");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.private_exhaust.duplicate");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String duplicateTopic() {
        return duplicateTopic;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String jobName() {
        return jobName;
    }
}