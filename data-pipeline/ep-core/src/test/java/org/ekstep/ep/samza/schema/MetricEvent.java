package org.ekstep.ep.samza.schema;

import java.util.List;

public class MetricEvent {

    private List<Element> metrics;
    private List<Element> dimensions;

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
