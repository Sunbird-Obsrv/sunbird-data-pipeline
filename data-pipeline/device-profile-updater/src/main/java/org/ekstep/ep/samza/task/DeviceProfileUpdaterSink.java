package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;

public class DeviceProfileUpdaterSink extends BaseSink {
    public DeviceProfileUpdaterSink(MessageCollector collector, JobMetrics metrics) {
        super(collector, metrics);
    }

    public void deviceDBUpdateSuccess() { metrics.deviceDBUpdateSuccess(); }

    public void deviceCacheUpdateSuccess() { metrics.deviceCacheUpdateSuccess(); }

    public void success() {
        metrics.incSuccessCounter();
    }

    public void failed() {
        metrics.incFailedCounter();
    }

    public void error() {
        metrics.incErrorCounter();
    }
}
