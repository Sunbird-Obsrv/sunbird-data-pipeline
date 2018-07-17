package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class DeDuplicationConfigTemp {

    private final String JOB_NAME = "DeDuplicationTemp";
    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;
    private String malformedTopic;
    private String defaultChannel;
    private final String metricsTopic;


    public DeDuplicationConfigTemp(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.valid.temp");
        failedTopic = config.get("output.failed.topic.name", "telemetry.unique.fail.temp");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.duplicate.temp");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed.temp");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics.temp");
        defaultChannel = config.get("default.channel", "in.ekstep");
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

    public String malformedTopic() {
        return malformedTopic;
    }

    public String defaultChannel() {
        return defaultChannel;
    }

    public String metricsTopic() {
        return metricsTopic;
    }

    public String jobName() {
        return JOB_NAME;
    }
}