package org.ekstep.ep.samza.task;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class TelemetryValidatorSink {
    private MessageCollector collector;
    private JobMetrics metrics;
    private TelemetryValidatorConfig config;

    public TelemetryValidatorSink(MessageCollector collector, JobMetrics metrics,
                                  TelemetryValidatorConfig config) {
        this.collector = collector;
        this.metrics = metrics;
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.successTopic()), event.getJson()));
        metrics.incSuccessCounter();
    }

    public void toFailedTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.failedTopic()), event.getJson()));
        metrics.incFailedCounter();
    }

    public void toErrorTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.failedTopic()), event.getJson()));
        metrics.incErrorCounter();
    }

    public void toMalformedEventsTopic(String message) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.malformedTopic()), message));
        metrics.incFailedCounter();
    }
}
