package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.Arrays;
import java.util.List;

public class DeDuplicationConfig {

    private final String JOB_NAME = "DeDuplication";
    private String successTopic;
    private String failedTopic;
    private String duplicateTopic;
    private String malformedTopic;
    private String defaultChannel;
    private final String metricsTopic;
    private final int dupStore;
    private int expirySeconds;
    private List<String> serverEventEid;
    private List<String> producerId;


    public DeDuplicationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.valid");
        failedTopic = config.get("output.failed.topic.name", "telemetry.failed");
        duplicateTopic = config.get("output.duplicate.topic.name", "telemetry.duplicate");
        malformedTopic = config.get("output.malformed.topic.name", "telemetry.malformed");
        metricsTopic = config.get("output.metrics.topic.name", "pipeline_metrics");
        defaultChannel = config.get("default.channel", "org.sunbird");
        dupStore = config.getInt("redis.database.duplicationstore.id", 7);
        expirySeconds = config.getInt("redis.database.key.expiry.seconds", 432000);
        serverEventEid = config.getList("server.events.skip.eid", Arrays.asList("AUDIT","SEARCH","ERROR","LOG"));
        producerId = config.getList("producer.id", Arrays.asList("prod.diksha.app"));
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

    public int dupStore() {
        return dupStore;
    }

    public int expirySeconds() {
        return expirySeconds;
    }

    public List<String> serverEventEid() {
        return serverEventEid;
    }

    public List<String> producerId() {
        return producerId;
    }
}