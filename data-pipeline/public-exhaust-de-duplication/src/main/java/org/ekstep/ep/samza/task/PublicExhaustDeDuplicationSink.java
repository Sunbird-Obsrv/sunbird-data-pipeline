package org.ekstep.ep.samza.task;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.metrics.JobMetrics;

public class PublicExhaustDeDuplicationSink {
    private MessageCollector collector;
    private JobMetrics metrics;
    private PublicExhaustDeDuplicationConfig config;

    public PublicExhaustDeDuplicationSink(MessageCollector collector, JobMetrics metrics,
                                          PublicExhaustDeDuplicationConfig config) {
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

    public void toErrorTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.duplicateTopic()), event.getMap()));
        metrics.incErrorCounter();
    }

}
