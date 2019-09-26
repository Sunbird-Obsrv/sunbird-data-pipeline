package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;

public class DeviceProfileUpdaterSink extends BaseSink {
    private DeviceProfileUpdaterConfig config;

    public DeviceProfileUpdaterSink(MessageCollector collector, JobMetrics metrics, DeviceProfileUpdaterConfig config) {
        super(collector, metrics);
        this.config = config;
    }

    public void deviceDBUpdateSuccess() { metrics.deviceDBUpdateSuccess(); }

    public void deviceCacheUpdateSuccess() { metrics.deviceCacheUpdateSuccess(); }

    public void success() {
        metrics.incSuccessCounter();
    }

    public void failed() {
        metrics.incFailedCounter();
    }

    public void toMalformedTopic(String message) {
        toTopic(config.malformedTopic(), null, message);
        metrics.incErrorCounter();
    }
}
