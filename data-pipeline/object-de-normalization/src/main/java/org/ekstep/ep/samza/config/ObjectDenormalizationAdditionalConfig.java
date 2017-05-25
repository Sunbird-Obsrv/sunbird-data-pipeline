package org.ekstep.ep.samza.config;

import java.util.List;

public class ObjectDenormalizationAdditionalConfig {
    private String desc;
    private List<EventDenormalizationConfig> eventConfigs;

    @Override
    public String toString() {
        return "ObjectDenormalizationAdditionalConfig{" +
                "desc='" + desc + '\'' +
                ", eventConfigs=" + eventConfigs +
                '}';
    }

    class EventDenormalizationConfig {
        private String name;
        private String eidPattern;
        private List<DataDenormalizationConfig> denormalizationConfigs;

        @Override
        public String toString() {
            return "EventDenormalizationConfig{" +
                    "name='" + name + '\'' +
                    ", eidPattern='" + eidPattern + '\'' +
                    ", dataDenormalizationConfigs=" + denormalizationConfigs +
                    '}';
        }
    }

    class DataDenormalizationConfig {
        private String idFieldPath;
        private String denormalizedFieldPath;

        @Override
        public String toString() {
            return "DataDenormalizationConfig{" +
                    "idFieldPath='" + idFieldPath + '\'' +
                    ", denormalizedFieldPath='" + denormalizedFieldPath + '\'' +
                    '}';
        }
    }
}
