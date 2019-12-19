package org.ekstep.ep.samza.task;

import org.apache.samza.task.MessageCollector;
import org.ekstep.ep.samza.core.BaseSink;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.domain.BatchEvent;

public class AssessmentAggregatorSink extends BaseSink {

    private AssessmentAggregatorConfig config;

    public AssessmentAggregatorSink(MessageCollector collector, JobMetrics metrics,
                                    AssessmentAggregatorConfig config) {
        super(collector, metrics);
        this.config = config;
    }

    public void batchSuccess() {
        metrics.incBatchSuccessCounter();
    }

    public void success() {
        metrics.incSuccessCounter();
    }

    public void skip(BatchEvent batchEvent) {
        batchEvent.markSkipped(batchEvent);
        toTopic(config.failedTopic(), batchEvent.attemptId(), batchEvent.getJson());
        metrics.incSkippedCounter();
    }

    public void fail(String message) {
        toTopic(config.failedTopic(), null, message);
        metrics.incFailedCounter();
    }

    public void incDBHits() {
        metrics.incDBHitCount();
    }
    
    public void incDBInsertCount() {
    	metrics.incDBInsertCount();
    }
    
    public void incDBUpdateCount() {
    	metrics.incDBUpdateCount();
    }
}

