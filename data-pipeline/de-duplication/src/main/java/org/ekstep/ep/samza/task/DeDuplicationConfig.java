package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class DeDuplicationConfig {

    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;
    private String malformedTopic;
    private String defaultChannelId;


    public DeDuplicationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.unique");
        failedTopic = config.get("output.failed.topic.name", "telemetry.unique.fail");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.duplicate");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        defaultChannelId = config.get("default.channel.id", "in.ekstep");
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

    public String defaultChannelId() {
        return defaultChannelId;
    }
}