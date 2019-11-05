package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.Event;

public class DeDuplicationSink extends BaseSink {

    private DeDuplicationConfig config;

    public DeDuplicationSink(MessageCollector collector, JobMetrics metrics, DeDuplicationConfig config) {
        super(collector, metrics);
        this.config = config;
    }

    public void toSuccessTopic(Event event) {
        toTopic(config.successTopic(), event.did(), event.getJson());
        metrics.incSuccessCounter();
    }

    public void toSuccessTopicIfRedisException(Event event) {
        toTopic(config.successTopic(), event.did(), event.getJson());
        metrics.incCacheErrorCounter();
    }

    public void toDuplicateTopic(Event event) {
        toTopic(config.duplicateTopic(), event.did(), event.getJson());
        metrics.incDuplicateCounter();
    }

    public void toMalformedEventsTopic(String message) {
        toTopic(config.malformedTopic(), null, message);
        metrics.incFailedCounter();
    }

    public void toErrorTopic(Event event) {
        toTopic(config.failedTopic(), event.did(), event.getJson());
        metrics.incErrorCounter();
    }

}
