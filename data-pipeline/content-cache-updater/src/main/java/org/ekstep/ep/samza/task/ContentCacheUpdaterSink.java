package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;

public class ContentCacheUpdaterSink extends BaseSink {

    public ContentCacheUpdaterSink(MessageCollector collector, JobMetrics metrics) {
        super(collector, metrics);
    }

    public void success() {
        metrics.incSuccessCounter();
    }

    public void markSkipped() { metrics.incSkippedCounter(); }
}
