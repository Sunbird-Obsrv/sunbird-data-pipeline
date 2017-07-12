package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;

public class Configuration {
    private Config config;

    public Configuration(Config config) {
        this.config = config;
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
}
