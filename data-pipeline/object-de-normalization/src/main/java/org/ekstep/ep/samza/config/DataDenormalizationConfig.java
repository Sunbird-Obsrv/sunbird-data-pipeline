package org.ekstep.ep.samza.config;

public class DataDenormalizationConfig {
    private String idFieldPath;
    private String denormalizedFieldPath;

    public DataDenormalizationConfig(String idFieldPath, String denormalizedFieldPath) {
        this.idFieldPath = idFieldPath;
        this.denormalizedFieldPath = denormalizedFieldPath;
    }

    public String idFieldPath() {
        return idFieldPath;
    }

    public String denormalizedFieldPath() {
        return denormalizedFieldPath;
    }

    @Override
    public String toString() {
        return "DataDenormalizationConfig{" +
                "idFieldPath='" + idFieldPath + '\'' +
                ", denormalizedFieldPath='" + denormalizedFieldPath + '\'' +
                '}';
    }
}
