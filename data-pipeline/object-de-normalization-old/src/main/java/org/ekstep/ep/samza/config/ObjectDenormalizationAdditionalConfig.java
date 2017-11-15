package org.ekstep.ep.samza.config;

import org.ekstep.ep.samza.domain.Event;

import java.util.ArrayList;
import java.util.List;

public class ObjectDenormalizationAdditionalConfig {
    private List<EventDenormalizationConfig> eventConfigs;

    public ObjectDenormalizationAdditionalConfig(List<EventDenormalizationConfig> eventConfigs) {
        this.eventConfigs = eventConfigs;
    }

    public List<EventDenormalizationConfig> eventConfigs() {
        return eventConfigs;
    }

    public List<EventDenormalizationConfig> filter(Event event){
        ArrayList<EventDenormalizationConfig> filteredEventConfig = new ArrayList<EventDenormalizationConfig>();
        for (EventDenormalizationConfig config : eventConfigs) {
            if (config.eidCompiledPattern().matcher(event.eid()).matches()) {
                filteredEventConfig.add(config);
            }
        }
        return filteredEventConfig;
    }

    @Override
    public String toString() {
        return "ObjectDenormalizationAdditionalConfig{" +
                "eventConfigs=" + eventConfigs +
                '}';
    }
}
