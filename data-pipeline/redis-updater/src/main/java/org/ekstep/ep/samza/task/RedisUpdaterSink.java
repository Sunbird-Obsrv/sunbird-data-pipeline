package org.ekstep.ep.samza.task;

import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.JobMetrics;

public class RedisUpdaterSink {

    private JobMetrics metrics;

    public RedisUpdaterSink(MessageCollector collector, JobMetrics metrics) {
        this.metrics = metrics;
    }

    public void success() {
        metrics.incSuccessCounter();
    }

    public void failed() {
        metrics.incFailedCounter();
    }

    public void error() {
        metrics.incErrorCounter();
    }

    public void setMetricsOffset(SystemStreamPartition systemStreamPartition, String offset) {
        metrics.setOffset(systemStreamPartition, offset);
    }
}

