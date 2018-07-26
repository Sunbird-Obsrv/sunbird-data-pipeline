package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class EsIndexerPrimarySink extends BaseSink {

    private JobMetrics metrics;
    private EsIndexerPrimaryConfig config;

    public EsIndexerPrimarySink(MessageCollector collector, JobMetrics metrics, EsIndexerPrimaryConfig config) {
    	
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
}
