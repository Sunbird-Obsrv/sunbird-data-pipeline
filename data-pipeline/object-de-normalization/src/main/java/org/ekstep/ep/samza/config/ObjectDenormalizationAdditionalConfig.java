package org.ekstep.ep.samza.config;

import java.util.List;

public class ObjectDenormalizationAdditionalConfig {
    private List<EventDenormalizationConfig> eventConfigs;

    public ObjectDenormalizationAdditionalConfig(List<EventDenormalizationConfig> eventConfigs) {
        this.eventConfigs = eventConfigs;
    }

    public List<EventDenormalizationConfig> eventConfigs() {
        return eventConfigs;
    }

    @Override
    public String toString() {
        return "ObjectDenormalizationAdditionalConfig{" +
                "eventConfigs=" + eventConfigs +
                '}';
    }
}
