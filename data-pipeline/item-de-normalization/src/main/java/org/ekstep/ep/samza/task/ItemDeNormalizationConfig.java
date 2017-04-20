package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

public class ItemDeNormalizationConfig {

    private String successTopic;
    private String failedTopic;
    private final String searchServiceEndpoint;
    private final long cacheTTL;

    public ItemDeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.item.de_normalized");
        failedTopic = config.get("output.failed.topic.name", "telemetry.item.de_normalized.fail");
        searchServiceEndpoint = config.get("search.service.endpoint");
        cacheTTL = Long.parseLong(config.get("item.store.ttl", "60000"));
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public String searchServiceEndpoint() {
        return searchServiceEndpoint;
    }

    public long cacheTTL() {
        return cacheTTL;
    }
}