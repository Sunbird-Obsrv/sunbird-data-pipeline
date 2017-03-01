package org.ekstep.ep.samza.task;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.domain.Event;

public class ContentDeNormalizationSink {
    private MessageCollector collector;
    private ContentDeNormalizationMetrics metrics;
    private ContentDeNormalizationConfig config;

    public ContentDeNormalizationSink(MessageCollector collector, ContentDeNormalizationMetrics metrics,
                                      ContentDeNormalizationConfig config) {
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

}
