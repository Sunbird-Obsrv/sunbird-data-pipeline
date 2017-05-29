package org.ekstep.ep.samza.task;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.metrics.JobMetrics;

public class DeDuplicationSink {
    private MessageCollector collector;
    private JobMetrics metrics;
    private DeDuplicationConfig config;

    public DeDuplicationSink(MessageCollector collector, JobMetrics metrics,
                             DeDuplicationConfig config) {
        this.collector = collector;
        this.metrics = metrics;
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.successTopic()), event.getMap()));
        metrics.incSuccessCounter();
    }

    public void toFailedTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.failedTopic()), event.getMap()));
        metrics.incFailedCounter();
    }


    public void toDuplicateTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.duplicateTopic()), event.getMap()));
        metrics.incFailedCounter();
    }

    public void toMalformedEventsTopic(String message) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.malformedTopic()), message));
        metrics.incFailedCounter();
    }

}
