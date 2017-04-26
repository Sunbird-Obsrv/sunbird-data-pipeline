package org.ekstep.ep.samza.task;


import org.apache.samza.config.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemDeNormalizationConfig {

    private final long cacheTTL;
    private final String searchServiceEndpoint;
    private final HashMap<String, Object> itemTaxonomy;
    private String successTopic;
    private String failedTopic;

    public ItemDeNormalizationConfig(Config config) {
        successTopic = config.get("output.success.topic.name", "telemetry.item.de_normalized");
        failedTopic = config.get("output.failed.topic.name", "telemetry.item.de_normalized.fail");
        searchServiceEndpoint = config.get("search.service.endpoint");
        cacheTTL = Long.parseLong(config.get("item.store.ttl", "60000"));
        itemTaxonomy = getItemTaxonomy(config);
    }

    private HashMap<String, Object> getItemTaxonomy(Config config) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<String> eventFields = commaSeparatedStringToList(config, "item_id.overridden.events");
        for (String eventField : eventFields) {
            String field = eventField.toLowerCase();
            String eventType = field.split("\\.")[0];
            if (config.containsKey(field)) {
                List<String> gidPath = getItemIdField(config, field);
                map.put(eventType.toUpperCase(), gidPath);
            }
        }
        return map;
    }

    private List<String> getItemIdField(Config config, String key) {
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

    public String searchServiceEndpoint() {
        return searchServiceEndpoint;
    }

    public long cacheTTL() {
        return cacheTTL;
    }

    public HashMap<String, Object> itemTaxonomy() {
        return itemTaxonomy;
    }
}