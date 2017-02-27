package org.ekstep.ep.samza;

import org.apache.samza.metrics.Counter;
import org.apache.samza.task.TaskContext;

public class ContentDeNormalizationMetrics {
    private final Counter messageCount;

    public ContentDeNormalizationMetrics(TaskContext context) {
        messageCount = context
                .getMetricsRegistry()
                .newCounter(getClass().getName(), "message-count");

    }

    public void clear() {
        messageCount.clear();
    }

    public void incCounter() {
        messageCount.inc();
    }
}
