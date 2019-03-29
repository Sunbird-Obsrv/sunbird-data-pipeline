package org.ekstep.ep.samza.task;

import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class TelemetryValidatorSink extends BaseSink {
    
    private JobMetrics metrics;
    private TelemetryValidatorConfig config;

    public TelemetryValidatorSink(MessageCollector collector, JobMetrics metrics,
                                  TelemetryValidatorConfig config) {
        
    	super(collector);
        this.metrics = metrics;
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        toTopic(config.successTopic(), event.mid(), event.getJson());
        metrics.incSuccessCounter();
    }

    public void toFailedTopic(Event event, String failedMessage) {
    	event.markFailure(failedMessage, config);
    	toTopic(config.failedTopic(), event.mid(), event.getJson());
        metrics.incFailedCounter();
    }

    public void toErrorTopic(Event event, String errorMessage) {
    	event.markFailure(errorMessage, config);
    	toTopic(config.failedTopic(), event.mid(), event.getJson());
        metrics.incErrorCounter();
    }

    public void toMalformedEventsTopic(String message) {
        toTopic(config.malformedTopic(), null, message);
        metrics.incFailedCounter();
    }

    public void setMetricsOffset(SystemStreamPartition systemStreamPartition, String offset) {
        metrics.setOffset(systemStreamPartition, offset);
    }

}
