package org.ekstep.ep.samza;


import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.List;

public class ContentDeNormalizationConfig {

    private final long cacheTTL;
    private final String searchServiceEndpoint;
    private String successTopic;
    private String failedTopic;
    private List<String> eventsToSkip;
    private List<String> eventsToAllow;

    public ContentDeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.content.de_normalized");
        failedTopic = config.get("output.failed.topic.name", "telemetry.content.de_normalized.fail");
        searchServiceEndpoint = config.get("search.service.endpoint");
        eventsToSkip = commaSeparatedStringToList(config, "events.to.skip");
        eventsToAllow = commaSeparatedStringToList(config, "events.to.allow");
        cacheTTL = Long.parseLong(config.get("content.store.ttl", "60000"));
    }

    private List<String> commaSeparatedStringToList(Config config, String key) {
        String[] split = config.get(key, "").split(",");
        List<String> list = new ArrayList<String>();
        for (String event : split) {
            list.add(event.trim().toUpperCase());
        }
        return list;
    }

    public String successTopic() {
        return successTopic;
    }

    public String failedTopic() {
        return failedTopic;
    }

    public List<String> eventsToSkip() {
        return eventsToSkip;
    }

    public List<String> eventsToAllow() {
        return eventsToAllow;
    }

    public long cacheTTL() {
        return cacheTTL;
    }

    public String searchServiceEndpoint() {
        return searchServiceEndpoint;
    }
}