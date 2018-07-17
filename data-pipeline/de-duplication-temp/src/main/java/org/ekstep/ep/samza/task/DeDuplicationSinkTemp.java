package org.ekstep.ep.samza.task;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.metrics.JobMetrics;

public class DeDuplicationSinkTemp {
    private MessageCollector collector;
    private JobMetrics metrics;
    private DeDuplicationConfigTemp config;

    public DeDuplicationSinkTemp(MessageCollector collector, JobMetrics metrics,
                                 DeDuplicationConfigTemp config) {
        this.collector = collector;
        this.metrics = metrics;
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.successTopic()), event.getJson()));
        metrics.incSuccessCounter();;
    }

    public void toDuplicateTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.duplicateTopic()), event.getJson()));
        metrics.incFailedCounter();
    }

    public void toMalformedEventsTopic(String message) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.malformedTopic()), message));
        metrics.incFailedCounter();
    }

    public void toErrorTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.failedTopic()), event.getJson()));
        metrics.incErrorCounter();
    }
}
