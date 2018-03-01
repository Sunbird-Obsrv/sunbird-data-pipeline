package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PublicExhaustDeDuplicationConfig {
    private final String JOB_NAME = "PublicExhaustDeDuplication";
    private final String metricsTopic;
    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;

    public PublicExhaustDeDuplicationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.public_exhaust");
        failedTopic = config.get("output.failed.topic.name", "telemetry.public_exhaust.fail");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.public_exhaust.duplicate");
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
        return JOB_NAME;
    }
}