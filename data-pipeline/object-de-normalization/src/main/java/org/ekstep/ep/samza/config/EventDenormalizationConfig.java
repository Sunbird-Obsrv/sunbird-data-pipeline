package org.ekstep.ep.samza.config;

import java.util.List;
import java.util.regex.Pattern;

public class EventDenormalizationConfig {
    private String name;
    private String eidPattern;
    private Pattern eidCompiledPattern;
    private List<DataDenormalizationConfig> denormalizationConfigs;

    public EventDenormalizationConfig(String name, String eidPattern, List<DataDenormalizationConfig> denormalizationConfigs) {
        this.name = name;
        this.eidPattern = eidPattern;
        this.denormalizationConfigs = denormalizationConfigs;
        this.eidCompiledPattern = Pattern.compile(eidPattern);
    }

    public String eidPattern() {
        return eidPattern;
    }

    public List<DataDenormalizationConfig> denormalizationConfigs() {
        return denormalizationConfigs;
    }

    public Pattern eidCompiledPattern() {
        return eidCompiledPattern;
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
