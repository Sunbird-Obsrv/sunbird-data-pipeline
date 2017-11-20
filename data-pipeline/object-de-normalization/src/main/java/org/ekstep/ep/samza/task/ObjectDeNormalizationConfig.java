package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.*;

public class ObjectDeNormalizationConfig {

    private final long cacheTTL;
    private final String searchServiceEndpoint;
    private final HashMap<String,Object> contentTaxonomy;
    private String successTopic;
    private String failedTopic;
    private List<String> eventsToSkip;
    private List<String> eventsToAllow;

    public ObjectDeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.content.de_normalized");
        failedTopic = config.get("output.failed.topic.name", "telemetry.content.de_normalized.fail");
        searchServiceEndpoint = config.get("search.service.endpoint");
        contentTaxonomy = getContentTaxonomy(config);
        cacheTTL = Long.parseLong(config.get("object.store.ttl", "60000"));
    }

    private HashMap<String, Object> getContentTaxonomy(Config config) {
        HashMap<String, Object> map = new HashMap<String,Object>();
        List<String> eventFields = commaSeparatedStringToList(config,"gid.overridden.events");
        for (String eventField : eventFields) {
            String field = eventField.toLowerCase();
            String eventType = field.split("\\.")[0];
            if (config.containsKey(field)){
                List<String> gidPath = getGidField(config, field);
                map.put(eventType.toUpperCase(), gidPath);
            }
        }
        return map;
    }

    private List<String> getGidField(Config config, String key) {
        List<String> list = new ArrayList<String>();
            String[] split = config.get(key, "").split("\\.");
            for (String e : split) {
                list.add(e.trim());
            }
        return list;
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

    public HashMap<String, Object> objectTaxonomy() {
        return contentTaxonomy;
    }

    public long cacheTTL() {
        return cacheTTL;
    }

    public String searchServiceEndpoint() {
        return searchServiceEndpoint;
    }
}