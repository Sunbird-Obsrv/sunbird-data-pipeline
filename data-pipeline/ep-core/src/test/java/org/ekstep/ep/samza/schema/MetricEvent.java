package org.ekstep.ep.samza.schema;

import java.util.List;

public class MetricEvent {

    private String system;
    private String subsystem;
    private List<Element> metrics;
    private List<Element> dimensions;

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(String subsystem) {
        this.subsystem = subsystem;
    }

    public List<Element> getMetrics() {
        return metrics;
    }

    public void setMetrics(List<Element> metrics) {
        this.metrics = metrics;
    }

    public List<Element> getDimensions() {
        return dimensions;
    }

    public void setDimensions(List<Element> dimensions) {
        this.dimensions = dimensions;
    }
}
