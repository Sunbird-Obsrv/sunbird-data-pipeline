package org.ekstep.ep.samza.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.samza.config.Config;

public class Configuration {
	
    private Config config;
    private final HashMap<String,Object> contentTaxonomy;
    private String JOB_NAME = "ReverseSearch";

    public Configuration(Config config) {
        this.config = config;
        this.contentTaxonomy = getContentTaxonomy(config);
    }

    public String getSuccessTopic(){
        return config.get("output.success.topic.name", "events_with_location");
    }

    public String getFailedTopic(){
        return config.get("output.failed.topic.name", "events_failed_location");
    }

    public Double getReverseSearchCacheAreaSizeInMeters(){
        return Double.parseDouble(config.get("reverse.search.cache.area.size.in.meters",
                "200"));
    }

    public String getByPass(){
        return config.get("bypass", "true");
    }

    public String getApiKey(){
        return config.get("google.api.key", "");
    }

    public String getDefaultChannel(){
        return config.get("default.channel", "ekstep.in");
    }

    public String jobName() {
        return JOB_NAME;
    }

    public String getMetricsTopic() {
        return config.get("output.metrics.topic.name", "pipeline_metrics");
    }
    
    public HashMap<String, Object> objectTaxonomy() {
        return contentTaxonomy;
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
    
    private List<String> commaSeparatedStringToList(Config config, String key) {
        String[] split = config.get(key, "").split(",");
        List<String> list = new ArrayList<String>();
        for (String event : split) {
            list.add(event.trim().toUpperCase());
        }
        return list;
    }
    
    private List<String> getGidField(Config config, String key) {
        List<String> list = new ArrayList<String>();
            String[] split = config.get(key, "").split("\\.");
            for (String e : split) {
                list.add(e.trim());
            }
        return list;
    }
}
