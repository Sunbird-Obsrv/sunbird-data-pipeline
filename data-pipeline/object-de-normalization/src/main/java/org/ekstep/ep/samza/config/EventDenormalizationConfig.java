package org.ekstep.ep.samza.config;

import java.util.List;

public class EventDenormalizationConfig {
    private String name;
    private String eidPattern;
    private List<DataDenormalizationConfig> denormalizationConfigs;

    public EventDenormalizationConfig(String name, String eidPattern, List<DataDenormalizationConfig> denormalizationConfigs) {
        this.name = name;
        this.eidPattern = eidPattern;
        this.denormalizationConfigs = denormalizationConfigs;
    }

    public String eidPattern() {
        return eidPattern;
    }

    public List<DataDenormalizationConfig> denormalizationConfigs() {
        return denormalizationConfigs;
    }

    @Override
    public String toString() {
        return "EventDenormalizationConfig{" +
                "name='" + name + '\'' +
                ", eidPattern='" + eidPattern + '\'' +
                ", dataDenormalizationConfigs=" + denormalizationConfigs +
                '}';
    }
}
