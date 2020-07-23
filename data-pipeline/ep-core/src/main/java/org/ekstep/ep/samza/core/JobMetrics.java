package org.ekstep.ep.samza.core;

import com.google.gson.Gson;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Metric;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.metrics.MetricsRegistryMap;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.TaskContext;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class JobMetrics {
    private static Logger LOGGER = new Logger(JobMetrics.class);
    private final String jobName;
    private final Counter successMessageCount;
    private final Counter failedMessageCount;
    private final Counter skippedMessageCount;
    private final Counter errorMessageCount;
    private final Counter batchSuccessCount;
    private final Counter batchErrorCount;
    private final Counter primaryRouteSuccessCount;
    private final Counter secondaryRouteSuccessCount;
    private final Counter logRouteSuccessCount;
    private final Counter errorRouteSuccessCount;
    private final Counter auditRouteSuccessCount;
    private final Counter assessRouteSuccessCount;
    private final Counter shareEventRouteSuccessCount;
    private final Counter cacheHitCount;
    private final Counter cacheMissCount;
    private final Counter cacheErrorCount;
    private final Counter cacheEmptyValuesCount;
    private final Counter processedMessageCount;
    private final Counter unprocessedMessageCount;
    private final Counter dbHitCount;
    private final Counter userCacheHitCount;
    private final Counter expiredEventCount;
    private final Counter duplicateEventCount;
    private final Counter deviceDBUpdateCount;
    private final Counter deviceCacheUpdateCount;
    private final Counter userDeclaredHitCount;
    private final Counter ipLocationHitCount;
    
    private final Counter dbInsertCount;
    private final Counter dbUpdateCount;
    private final Counter dialCodesCount;
    private final Counter dialCodesFromApiCount;
    private final Counter dialCodesFromCacheCount;

    private TaskContext context;
    private int partition;

    private Map<String, Counter> metricCounterMap = new HashMap<>();

    public JobMetrics(TaskContext context) {
        this(context, null);
    }

    public JobMetrics(TaskContext context, String jName) {
        MetricsRegistry metricsRegistry = context.getMetricsRegistry();
        successMessageCount = metricsRegistry.newCounter(getClass().getName(), "success-message-count");
        failedMessageCount = metricsRegistry.newCounter(getClass().getName(), "failed-message-count");
        skippedMessageCount = metricsRegistry.newCounter(getClass().getName(), "skipped-message-count");
        errorMessageCount = metricsRegistry.newCounter(getClass().getName(), "error-message-count");
        batchSuccessCount = metricsRegistry.newCounter(getClass().getName(), "batch-success-count");
        batchErrorCount = metricsRegistry.newCounter(getClass().getName(), "batch-error-count");
        primaryRouteSuccessCount = metricsRegistry.newCounter(getClass().getName(), "primary-route-success-count");
        secondaryRouteSuccessCount = metricsRegistry.newCounter(getClass().getName(), "secondary-route-success-count");
        logRouteSuccessCount = metricsRegistry.newCounter(getClass().getName(), "log-route-success-count");
        errorRouteSuccessCount = metricsRegistry.newCounter(getClass().getName(), "error-route-success-count");;
        auditRouteSuccessCount = metricsRegistry.newCounter(getClass().getName(), "audit-route-success-count");
        assessRouteSuccessCount = metricsRegistry.newCounter(getClass().getName(), "assess-route-success-count");
        shareEventRouteSuccessCount = metricsRegistry.newCounter(getClass().getName(), "share-route-success-count");
        cacheHitCount = metricsRegistry.newCounter(getClass().getName(), "cache-hit-count");
        cacheMissCount = metricsRegistry.newCounter(getClass().getName(), "cache-miss-count");
        cacheEmptyValuesCount = metricsRegistry.newCounter(getClass().getName(), "cache-empty-values-count");
        cacheErrorCount = metricsRegistry.newCounter(getClass().getName(), "cache-error-count");
        processedMessageCount = metricsRegistry.newCounter(getClass().getName(), "processed-message-count");
        unprocessedMessageCount = metricsRegistry.newCounter(getClass().getName(), "unprocessed-message-count");
        dbHitCount = metricsRegistry.newCounter(getClass().getName(), "db-hit-count");
        userCacheHitCount = metricsRegistry.newCounter(getClass().getName(), "user-cache-hit-count");
        expiredEventCount = metricsRegistry.newCounter(getClass().getName(), "expired-event-count");
        duplicateEventCount = metricsRegistry.newCounter(getClass().getName(), "duplicate-event-count");
        deviceDBUpdateCount = metricsRegistry.newCounter(getClass().getName(), "device-db-update-count");
        deviceCacheUpdateCount = metricsRegistry.newCounter(getClass().getName(), "device-cache-update-count");
        userDeclaredHitCount = metricsRegistry.newCounter(getClass().getName(), "user-declared-hit-count");
        ipLocationHitCount = metricsRegistry.newCounter(getClass().getName(), "ip-location-hit-count");
        dbInsertCount = metricsRegistry.newCounter(getClass().getName(), "db-insert-count");
        dbUpdateCount = metricsRegistry.newCounter(getClass().getName(), "db-update-count");
        dialCodesCount = metricsRegistry.newCounter(getClass().getName(),"dial-codes-count");
        dialCodesFromApiCount = metricsRegistry.newCounter(getClass().getName(),"dial-codes-from-api-count");
        dialCodesFromCacheCount = metricsRegistry.newCounter(getClass().getName(),"dial-codes-from-cache-count");

        metricCounterMap.put("success-message-count", successMessageCount);
        metricCounterMap.put("failed-message-count", failedMessageCount);
        metricCounterMap.put("skipped-message-count", skippedMessageCount);
        metricCounterMap.put("error-message-count", errorMessageCount);
        metricCounterMap.put("batch-success-count", batchSuccessCount);
        metricCounterMap.put("batch-error-count", batchErrorCount);
        metricCounterMap.put("primary-route-success-count", primaryRouteSuccessCount);
        metricCounterMap.put("secondary-route-success-count", secondaryRouteSuccessCount);
        metricCounterMap.put("log-route-success-count", logRouteSuccessCount);
        metricCounterMap.put("error-route-success-count", errorRouteSuccessCount);
        metricCounterMap.put("audit-route-success-count", auditRouteSuccessCount);
        metricCounterMap.put("assess-route-success-count", assessRouteSuccessCount);
        metricCounterMap.put("cache-hit-count", cacheHitCount);
        metricCounterMap.put("cache-miss-count", cacheMissCount);
        metricCounterMap.put("cache-empty-values-count", cacheEmptyValuesCount);
        metricCounterMap.put("cache-error-count", cacheErrorCount);
        metricCounterMap.put("processed-message-count", processedMessageCount);
        metricCounterMap.put("unprocessed-message-count", unprocessedMessageCount);
        metricCounterMap.put("user-cache-hit-count", userCacheHitCount);
        metricCounterMap.put("db-hit-count", dbHitCount);
        metricCounterMap.put("expired-event-count", expiredEventCount);
        metricCounterMap.put("duplicate-event-count", duplicateEventCount);
        metricCounterMap.put("device-db-update-count", deviceDBUpdateCount);
        metricCounterMap.put("device-cache-update-count", deviceCacheUpdateCount);
        metricCounterMap.put("user-declared-hit-count", userDeclaredHitCount);
        metricCounterMap.put("ip-location-hit-count", ipLocationHitCount);
        metricCounterMap.put("db-insert-count", dbInsertCount);
        metricCounterMap.put("db-update-count", dbUpdateCount);
        metricCounterMap.put("dialcodes-count", dialCodesCount);
        metricCounterMap.put("dialcodes-api-hit", dialCodesFromApiCount);
        metricCounterMap.put("dialcodes-cache-hit", dialCodesFromCacheCount);
        jobName = jName;
        this.context = context;
    }

    public void clear() {
        successMessageCount.clear();
        failedMessageCount.clear();
        skippedMessageCount.clear();
        errorMessageCount.clear();
        batchSuccessCount.clear();
        batchErrorCount.clear();
        cacheEmptyValuesCount.clear();
        cacheHitCount.clear();
        cacheMissCount.clear();
        cacheErrorCount.clear();
        processedMessageCount.clear();
        unprocessedMessageCount.clear();
        dbHitCount.clear();
        userCacheHitCount.clear();
        expiredEventCount.clear();
        duplicateEventCount.clear();
        deviceDBUpdateCount.clear();
        deviceCacheUpdateCount.clear();
        userDeclaredHitCount.clear();
        ipLocationHitCount.clear();
        primaryRouteSuccessCount.clear();
        secondaryRouteSuccessCount.clear();
        logRouteSuccessCount.clear();
        errorRouteSuccessCount.clear();
        auditRouteSuccessCount.clear();
        assessRouteSuccessCount.clear();
        shareEventRouteSuccessCount.clear();
        dbInsertCount.clear();
        dbUpdateCount.clear();
        dialCodesCount.clear();
        dialCodesFromApiCount.clear();
        dialCodesFromCacheCount.clear();
    }

    public void incSuccessCounter() {
        successMessageCount.inc();
    }

    public void deviceDBUpdateSuccess() {
        deviceDBUpdateCount.inc();
    }

    public void deviceCacheUpdateSuccess() {
        deviceCacheUpdateCount.inc();
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

    public void incBatchSuccessCounter() {
        batchSuccessCount.inc();
    }

    public void incBatchErrorCounter() {
        batchErrorCount.inc();
    }

    public void incPrimaryRouteSuccessCounter() {
        primaryRouteSuccessCount.inc();
    }

    public void incSecondaryRouteSuccessCounter() {
        secondaryRouteSuccessCount.inc();
    }

    public void incLogRouteSuccessCounter() {
        logRouteSuccessCount.inc();
    }

    public void incErrorRouteSuccessCounter() {
        errorRouteSuccessCount.inc();
    }
    
    public void incAuditRouteSuccessCounter() {
        auditRouteSuccessCount.inc();
    }
    
    public void incAssessRouteSuccessCounter() {
      assessRouteSuccessCount.inc();
  }

    public void incShareEventRouteSuccessCounter() {
        shareEventRouteSuccessCount.inc();
    }

    public void incDuplicateCounter() {
        duplicateEventCount.inc();
    }

    public void incCacheHitCounter() {
        cacheHitCount.inc();
    }

    public void incCacheMissCounter() {
        cacheMissCount.inc();
    }

    public void incCacheErrorCounter() {
        cacheErrorCount.inc();
    }

    public void incEmptyCacheValueCounter() {
        cacheEmptyValuesCount.inc();
    }

    public void incProcessedMessageCount() {
        processedMessageCount.inc();
    }

    public void incUnprocessedMessageCount() {
        unprocessedMessageCount.inc();
    }

    public void incDBHitCount() {
        dbHitCount.inc();
    }

    public void incUserCacheHitCount() {
        userCacheHitCount.inc();
    }

    public void incExpiredEventCount() {
        expiredEventCount.inc();
    }

    public void incUserDeclaredHitCount() {
        userDeclaredHitCount.inc();
    }

    public void incIpLocationHitCount() {
        ipLocationHitCount.inc();
    }

    public void incDBInsertCount() {
        dbInsertCount.inc();
    }
    
    public void incDBUpdateCount() {
        dbUpdateCount.inc();
    }

    public void incDialCodesCount() { dialCodesCount.inc(); }

    public void incDialCodesFromApiCount() { dialCodesFromApiCount.inc(); }

    public void incDialCodesFromCacheCount() { dialCodesFromCacheCount.inc(); }

    public long consumerLag(Map<String, ConcurrentHashMap<String, Metric>> containerMetricsRegistry) {
        long consumerLag = 0;
        try {
            for (SystemStreamPartition sysPartition : context.getSystemStreamPartitions()) {
                long highWatermarkOffsetForPartition =
                        Long.valueOf(containerMetricsRegistry.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics")
                                .get(getSamzaMetricKey(sysPartition, "high-watermark")).toString());
                long checkpointedOffsetForPartition = Long.valueOf(containerMetricsRegistry.get("org.apache.samza.checkpoint.OffsetManagerMetrics")
                        .get(getSamzaMetricKey(sysPartition, "checkpointed-offset")).toString());
                consumerLag += highWatermarkOffsetForPartition - checkpointedOffsetForPartition;
                this.partition = sysPartition.getPartition().getPartitionId();
            }

        } catch (Exception e) {
            LOGGER.error(null, "EXCEPTION. WHEN COMPUTING CONSUMER LAG METRIC", e);
        }
        return consumerLag;
    }

    private String getSamzaMetricKey(SystemStreamPartition partition, String samzaMetricName) {
        return String.format("%s-%s-%s-%s",
                partition.getSystem(), partition.getStream(), partition.getPartition().getPartitionId(), samzaMetricName);
    }

    public String collect() {
        Map<String, Object> metricsEvent = new HashMap<>();
        metricsEvent.put("job-name", jobName);
        metricsEvent.put("partition", partition);
        metricsEvent.put("success-message-count", successMessageCount.getCount());
        metricsEvent.put("failed-message-count", failedMessageCount.getCount());
        metricsEvent.put("error-message-count", errorMessageCount.getCount());
        metricsEvent.put("batch-success-count", batchSuccessCount.getCount());
        metricsEvent.put("batch-error-count", batchErrorCount.getCount());
        metricsEvent.put("primary-route-success-count", primaryRouteSuccessCount.getCount());
        metricsEvent.put("secondary-route-success-count", secondaryRouteSuccessCount.getCount());
        metricsEvent.put("log-route-success-count", logRouteSuccessCount.getCount());
        metricsEvent.put("error-route-success-count", errorRouteSuccessCount.getCount());
        metricsEvent.put("skipped-message-count", skippedMessageCount.getCount());
        metricsEvent.put("cache-hit-count", cacheHitCount.getCount());
        metricsEvent.put("cache-miss-count", cacheMissCount.getCount());
        metricsEvent.put("cache-error-count", cacheErrorCount.getCount());
        metricsEvent.put("cache-empty-values-count", cacheEmptyValuesCount.getCount());
        metricsEvent.put("processed-message-count", processedMessageCount.getCount());
        metricsEvent.put("unprocessed-message-count", unprocessedMessageCount.getCount());
        metricsEvent.put("db-hit-count", dbHitCount.getCount());
        metricsEvent.put("user-cache-hit-count", userCacheHitCount.getCount());
        metricsEvent.put("expired-event-count", expiredEventCount.getCount());
        metricsEvent.put("duplicate-event-count", duplicateEventCount.getCount());

        metricsEvent.put("device-db-update-count", deviceDBUpdateCount.getCount());
        metricsEvent.put("device-cache-update-count", deviceCacheUpdateCount.getCount());
        metricsEvent.put("user-declared-hit-count", userDeclaredHitCount.getCount());
        metricsEvent.put("ip-location-hit-count", ipLocationHitCount.getCount());
        metricsEvent.put("audit-route-success-count", auditRouteSuccessCount.getCount());
        metricsEvent.put("share-route-success-count", shareEventRouteSuccessCount.getCount());
        metricsEvent.put("db-insert-count", dbInsertCount.getCount());
        metricsEvent.put("db-update-count", dbUpdateCount.getCount());
        metricsEvent.put("dialcodes-count", dialCodesCount.getCount());
        metricsEvent.put("dialcodes-api-hit", dialCodesCount.getCount());
        metricsEvent.put("dialcodes-cache-hit", dialCodesCount.getCount());

        metricsEvent.put("consumer-lag",
                consumerLag(((MetricsRegistryMap) context.getSamzaContainerContext().metricsRegistry).metrics()));
        metricsEvent.put("metricts", new DateTime().getMillis());
        return new Gson().toJson(metricsEvent);
    }

    public String collect(List<String> metrics) {
        Map<String, Object> metricsEvent = new HashMap<>();
        metricsEvent.put("job-name", jobName);
        metricsEvent.put("partition", partition);
        metricsEvent.put("consumer-lag",
                consumerLag(((MetricsRegistryMap) context.getSamzaContainerContext().metricsRegistry).metrics()));
        metricsEvent.put("metricts", new DateTime().getMillis());
        for (String metric: metrics) {
            metricsEvent.put(metric, metricCounterMap.get(metric).getCount());
        }
        return new Gson().toJson(metricsEvent);
    }

    public String generateMetrics(List<String> metrics) {
        Map<String, Object> metricsEvent = new HashMap<>();
        metricsEvent.put("system", "samza");
        metricsEvent.put("subsystem", "pipeline-metrics");
        metricsEvent.put("metricts", new DateTime().getMillis());
        List<Map<String, Object>> dimsList = new ArrayList<>();
        dimsList.add(createMap("job-name", jobName));
        dimsList.add(createMap("partition", partition));

        List<Map<String, Object>> metricsList = new ArrayList<>();
        for (String metric: metrics) {
            Map<String, Object> mMap = new HashMap<>();
            mMap.put("id", metric);
            mMap.put("value", metricCounterMap.get(metric).getCount());
            metricsList.add(mMap);
        }

        metricsList.add(createMap("consumer-lag",
                consumerLag(((MetricsRegistryMap) context.getSamzaContainerContext().metricsRegistry).metrics())));

        metricsEvent.put("dimensions", dimsList);
        metricsEvent.put("metrics", metricsList);
        return new Gson().toJson(metricsEvent);
    }

    private Map<String, Object> createMap(String id, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("value", value);
        return map;
    }
}
