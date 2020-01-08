package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class EventsFlattenSink extends BaseSink {

    // private JobMetrics metrics;
    private EventsFlattenConfig config;

    public EventsFlattenSink(MessageCollector collector, JobMetrics metrics, EventsFlattenConfig config) {
        super(collector, metrics);
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        event.markSuccess();
        toTopic(config.getSuccessTopic(), event.did(), event.getJson());
        metrics.incSuccessCounter();
    }

    public void toErrorTopic(Event event, String errorMessage) {
        event.markFailure(errorMessage, config);
        toTopic(config.getFailedTopic(), event.did(), event.getJson());
        metrics.incErrorCounter();
    }


    public void toMalformedTopic(String message) {
        toTopic(config.getMalformedTopic(), null, message);
        metrics.incErrorCounter();
    }

    public void incrementSkippedCount(Event event) {
        metrics.incSkippedCounter();
    }


    public void toErrorEventsTopic(Event event) {
        toTopic(config.getFailedTopic(), event.did(), event.getJson());
        metrics.incSuccessCounter();
    }

}
