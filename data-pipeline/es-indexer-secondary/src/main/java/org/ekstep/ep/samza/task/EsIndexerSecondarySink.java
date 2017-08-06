package org.ekstep.ep.samza.task;

import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.metrics.JobMetrics;

public class EsIndexerSecondarySink {

    private MessageCollector collector;
    private JobMetrics metrics;
    private EsIndexerSecondaryConfig config;

    public EsIndexerSecondarySink(MessageCollector collector, JobMetrics metrics, EsIndexerSecondaryConfig config) {
        this.collector = collector;
        this.metrics = metrics;
        this.config = config;
    }

    public void toFailedTopic(Event event) {
        collector.send(new OutgoingMessageEnvelope(
                new SystemStream("kafka", config.failedTopic()), event.getMap()));
        metrics.incFailedCounter();
    }
}
