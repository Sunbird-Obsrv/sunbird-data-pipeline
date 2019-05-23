package org.ekstep.ep.samza.core;

import com.google.gson.Gson;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Metric;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.metrics.MetricsRegistryMap;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.TaskContext;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JobMetrics {
    private static Logger LOGGER = new Logger(JobMetrics.class);
    private final String jobName;
    private final Counter successMessageCount;
    private final Counter failedMessageCount;
    private final Counter skippedMessageCount;
    private final Counter errorMessageCount;
    private final Counter cacheHitCount;
    private final Counter cacheMissCount;
    private final Counter cacheExpiredCount;
    private final Counter cacheErrorCount;
    private final Counter cacheEmptyValuesCount;
    private final Counter processedMessageCount;
    private final Counter unprocessedMessageCount;
    private final Counter dbHitCount;
    private final Counter dbErrorCount;
    private final Counter deviceDbHitCount;
    private final Counter deviceCacheHitCount;
    private final Counter userDbHitCount;
    private final Counter userCacheHitCount;
    private final Counter deviceDbErrorCount;
    private final Counter userDbErrorCount;
    private final Counter expiredEventCount;
    private TaskContext context;
    private int partition;
    private HashMap<String,Long> offsetMap = new HashMap<>();
    public JobMetrics(TaskContext context) {
        this(context,null);
    }

    public JobMetrics(TaskContext context, String jName) {
        MetricsRegistry metricsRegistry = context.getMetricsRegistry();
        successMessageCount = metricsRegistry.newCounter(getClass().getName(), "success-message-count");
        failedMessageCount = metricsRegistry.newCounter(getClass().getName(), "failed-message-count");
        skippedMessageCount = metricsRegistry.newCounter(getClass().getName(), "skipped-message-count");
        errorMessageCount = metricsRegistry.newCounter(getClass().getName(), "error-message-count");
        cacheHitCount = metricsRegistry.newCounter(getClass().getName(), "cache-hit-count");
        cacheMissCount = metricsRegistry.newCounter(getClass().getName(), "cache-miss-count");
        cacheEmptyValuesCount = metricsRegistry.newCounter(getClass().getName(), "cache-empty-values-count");
        cacheExpiredCount = metricsRegistry.newCounter(getClass().getName(), "cache-expired-count");
        cacheErrorCount = metricsRegistry.newCounter(getClass().getName(), "cache-error-count");
        processedMessageCount = metricsRegistry.newCounter(getClass().getName(), "processed-message-count");
        unprocessedMessageCount = metricsRegistry.newCounter(getClass().getName(), "unprocessed-message-count");
        dbHitCount = metricsRegistry.newCounter(getClass().getName(), "db-hit-count");
        dbErrorCount = metricsRegistry.newCounter(getClass().getName(), "db-error-count");
        deviceCacheHitCount = metricsRegistry.newCounter(getClass().getName(), "device-cache-hit-count");
        userCacheHitCount = metricsRegistry.newCounter(getClass().getName(), "user-cache-hit-count");
        deviceDbHitCount = metricsRegistry.newCounter(getClass().getName(), "device-db-hit-count");
        userDbHitCount = metricsRegistry.newCounter(getClass().getName(), "user-db-hit-count");
        deviceDbErrorCount = metricsRegistry.newCounter(getClass().getName(), "device-db-error-count");
        userDbErrorCount = metricsRegistry.newCounter(getClass().getName(), "user-db-error-count");
        expiredEventCount = metricsRegistry.newCounter(getClass().getName(), "expired-event-count");
        jobName = jName;
        this.context = context;
    }

    public void clear() {
        successMessageCount.clear();
        failedMessageCount.clear();
        skippedMessageCount.clear();
        errorMessageCount.clear();
        cacheEmptyValuesCount.clear();
        cacheHitCount.clear();
        cacheMissCount.clear();
        cacheExpiredCount.clear();
        cacheErrorCount.clear();
        processedMessageCount.clear();
        unprocessedMessageCount.clear();
        dbHitCount.clear();
        dbErrorCount.clear();
        deviceDbHitCount.clear();
        userDbHitCount.clear();
        deviceCacheHitCount.clear();
        userCacheHitCount.clear();
        deviceDbErrorCount.clear();
        userDbErrorCount.clear();
        expiredEventCount.clear();
    }

    public void incSuccessCounter() {
        successMessageCount.inc();
    }

    public void incFailedCounter() {
        failedMessageCount.inc();
    }

    public void incSkippedCounter() {
        skippedMessageCount.inc();
    }

    public void incErrorCounter() {
        errorMessageCount.inc();
    }

    public void incCacheHitCounter() { cacheHitCount.inc(); }

    public void incCacheExpiredCounter() { cacheExpiredCount.inc();}

    public void incCacheErrorCounter() { cacheErrorCount.inc(); }

    public void incCacheMissCounter() { cacheMissCount.inc();}

    public void incNoDataCount() { cacheEmptyValuesCount.inc(); }

    public void incProcessedMessageCount() { processedMessageCount.inc(); }

    public void incUnprocessedMessageCount() { unprocessedMessageCount.inc(); }

    public void incDBHitCount() {
        dbHitCount.inc();
    }

    public void incDBErrorCount() { dbErrorCount.inc(); }

    public void incDeviceDBErrorCount() { deviceDbErrorCount.inc(); }

    public void incUserDBErrorCount() { userDbErrorCount.inc(); }

    public void incDeviceCacheHitCount() {
        deviceCacheHitCount.inc();
    }

    public void incUserCacheHitCount() {
        userCacheHitCount.inc();
    }

    public void incDeviceDbHitCount() {
        deviceDbHitCount.inc();
    }

    public void incUserDbHitCount() {
        userDbHitCount.inc();
    }

    public void incExpiredEventCount() { expiredEventCount.inc(); }

    public void setOffset(SystemStreamPartition systemStreamPartition, String offset) {
        String offsetMapKey = String.format("%s%s", systemStreamPartition.getStream(),
                systemStreamPartition.getPartition().getPartitionId());
        offsetMap.put(offsetMapKey,
                Long.valueOf(offset));
    }

    public long consumerLag(Map<String, ConcurrentHashMap<String, Metric>> containerMetricsRegistry) {
        long consumerLag = 0;
        try {
            for (SystemStreamPartition sysPartition : context.getSystemStreamPartitions()) {
                String offsetChangeKey = String.format("%s-%s-%s-offset-change",
                        sysPartition.getSystem(), sysPartition.getStream(), sysPartition.getPartition().getPartitionId());
                long logEndOffset =
                        Long.valueOf(containerMetricsRegistry.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics")
                                .get(offsetChangeKey).toString());
                long offset = offsetMap.getOrDefault(sysPartition.getStream() +
                        sysPartition.getPartition().getPartitionId(), -1L) + 1L;
                consumerLag += logEndOffset - offset;
                partition = sysPartition.getPartition().getPartitionId();
            }

        } catch (Exception e) {
            LOGGER.error(null, "EXCEPTION. WHEN COMPUTING CONSUMER LAG METRIC", e);
        }
        return consumerLag;
    }

    public String collect() {
        Map<String,Object> metricsEvent = new HashMap<>();
        metricsEvent.put("job-name", jobName);
        metricsEvent.put("success-message-count", successMessageCount.getCount());
        metricsEvent.put("failed-message-count", failedMessageCount.getCount());
        metricsEvent.put("error-message-count", errorMessageCount.getCount());
        metricsEvent.put("skipped-message-count", skippedMessageCount.getCount());
        metricsEvent.put("consumer-lag",
                consumerLag(((MetricsRegistryMap) context.getSamzaContainerContext().metricsRegistry).metrics()));
        metricsEvent.put("partition",partition);
        metricsEvent.put("cache-hit-count", cacheHitCount.getCount());
        metricsEvent.put("cache-miss-count", cacheMissCount.getCount());
        metricsEvent.put("cache-expired-count", cacheExpiredCount.getCount());
        metricsEvent.put("cache-error-count", cacheErrorCount.getCount());
        metricsEvent.put("cache-empty-values-count", cacheEmptyValuesCount.getCount());
        metricsEvent.put("processed-message-count", processedMessageCount.getCount());
        metricsEvent.put("unprocessed-message-count", unprocessedMessageCount.getCount());
        metricsEvent.put("db-hit-count", dbHitCount.getCount());
        metricsEvent.put("db-error-count", dbErrorCount.getCount());
        metricsEvent.put("device-cache-hit-count", deviceCacheHitCount.getCount());
        metricsEvent.put("user-cache-hit-count", userCacheHitCount.getCount());
        metricsEvent.put("device-db-hit-count", deviceDbHitCount.getCount());
        metricsEvent.put("user-db-hit-count", userDbHitCount.getCount());
        metricsEvent.put("device-db-error-count", deviceDbErrorCount.getCount());
        metricsEvent.put("user-db-error-count", userDbErrorCount.getCount());
        metricsEvent.put("expired-event-count", expiredEventCount.getCount());

        return new Gson().toJson(metricsEvent);
    }
}
