package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.List;

public class DeDuplicationConfig {

    private final String JOB_NAME = "DeDuplication";
    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;
    private String malformedTopic;
    private String defaultChannel;
    private final int dupStore;
    private int expirySeconds;
    private List<String> includedProducerIds = new ArrayList<>();


    public DeDuplicationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.valid");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.duplicate");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        defaultChannel = config.get("default.channel", "org.sunbird");
        dupStore = config.getInt("redis.database.duplicationstore.id", 7);
        expirySeconds = config.getInt("redis.database.key.expiry.seconds", 86400);
        if (!config.get("dedup.producer.include.ids", "").isEmpty()) {
            includedProducerIds = config.getList("dedup.producer.include.ids", new ArrayList<>());
        }

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

    public String jobName() {
        return JOB_NAME;
    }

    public int dupStore() {
        return dupStore;
    }

    public int expirySeconds() {
        return expirySeconds;
    }

    public List<String> inclusiveProducerIds() {
        return includedProducerIds;
    }

}