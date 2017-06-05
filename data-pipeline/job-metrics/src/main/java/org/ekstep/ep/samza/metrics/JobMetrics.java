package org.ekstep.ep.samza.metrics;

import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.task.TaskContext;

public class JobMetrics {
    private final Counter successMessageCount;
    private final Counter failedMessageCount;
    private final Counter retryMessageCount;
    private final Counter skippedMessageCount;
    private final Counter cacheHitCount;
    private final Counter cacheMissCount;
    private final Counter cacheExpiredCount;

    public JobMetrics(TaskContext context) {
        MetricsRegistry metricsRegistry = context.getMetricsRegistry();
        successMessageCount = metricsRegistry.newCounter(getClass().getName(), "success-message-count");
        failedMessageCount = metricsRegistry.newCounter(getClass().getName(), "failed-message-count");
        retryMessageCount = metricsRegistry.newCounter(getClass().getName(), "retry-message-count");
        skippedMessageCount = metricsRegistry.newCounter(getClass().getName(), "skipped-message-count");
        cacheHitCount = metricsRegistry.newCounter(getClass().getName(), "cache-hit-count");
        cacheMissCount = metricsRegistry.newCounter(getClass().getName(), "cache-miss-count");
        cacheExpiredCount = metricsRegistry.newCounter(getClass().getName(), "cache-expired-count");
    }

    public void clear() {
        successMessageCount.clear();
        failedMessageCount.clear();
        skippedMessageCount.clear();
        cacheHitCount.clear();
        cacheMissCount.clear();
    }

    public void incSuccessCounter() {
        successMessageCount.inc();
    }

    public void incFailedCounter() {
        failedMessageCount.inc();
    }

    public void incRetryCounter(){
        retryMessageCount.inc();
    }

    public void incSkippedCounter() {
        skippedMessageCount.inc();
    }

    public void incCacheHitCounter() {
        cacheHitCount.inc();
    }

    public void incCacheMissCounter() {
        cacheMissCount.inc();
    }

    public void incCacheExpiredCounter() {
        cacheExpiredCount.inc();
    }
}
