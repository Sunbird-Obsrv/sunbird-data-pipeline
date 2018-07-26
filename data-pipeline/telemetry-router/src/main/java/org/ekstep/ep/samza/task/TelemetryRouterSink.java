package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class TelemetryRouterSink extends BaseSink {
    
    private JobMetrics metrics;
    private TelemetryRouterConfig config;

    public TelemetryRouterSink(MessageCollector collector, JobMetrics metrics,
                                  TelemetryRouterConfig config) {
        
    	super(collector);
        this.metrics = metrics;
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        toTopic(config.successTopic(), event.mid(), event.getJson());
        metrics.incSuccessCounter();
    }

    public void toFailedTopic(Event event) {
        toTopic(config.failedTopic(), event.mid(), event.getJson());
        metrics.incFailedCounter();
    }

    public void toErrorTopic(Event event) {
        toTopic(config.failedTopic(), event.mid(), event.getJson());
        metrics.incErrorCounter();
    }

    public void toMalformedEventsTopic(String message) {
        toTopic(config.malformedTopic(), null, message);
        metrics.incFailedCounter();
    }
}
