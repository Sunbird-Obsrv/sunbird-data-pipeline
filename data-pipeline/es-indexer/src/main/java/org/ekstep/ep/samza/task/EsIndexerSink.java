package org.ekstep.ep.samza.task;

import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class EsIndexerSink extends BaseSink {

    private JobMetrics metrics;
    private EsIndexerConfig config;

    public EsIndexerSink(MessageCollector collector, JobMetrics metrics, EsIndexerConfig config) {
    	
    	super(collector);
        this.metrics = metrics;
        this.config = config;
    }

    public void toFailedTopic(Event event) {
        toTopic(config.failedTopic(), event.id(), event.getMap());
        metrics.incFailedCounter();
    }

    public void toErrorTopic(Event event) {
    	toTopic(config.failedTopic(), event.id(), event.getMap());
        metrics.incErrorCounter();
    }

    public void markSuccess() {
        metrics.incSuccessCounter();
    }

    public void setMetricsOffset(SystemStreamPartition systemStreamPartition, String offset) {
        metrics.setOffset(systemStreamPartition, offset);
    }
}
