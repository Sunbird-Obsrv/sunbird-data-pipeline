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
    static Logger LOGGER = new Logger(JobMetrics.class);
    private final String jobName;
    private final Counter successMessageCount;
    private final Counter failedMessageCount;
    private final Counter skippedMessageCount;
    private final Counter errorMessageCount;
    private final Counter cacheHitCount;
    private final Counter cacheMissCount;
    private final Counter cacheExpiredCount;
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
        cacheExpiredCount = metricsRegistry.newCounter(getClass().getName(), "cache-expired-count");
        jobName = jName;
        this.context = context;
    }

    public void clear() {
        successMessageCount.clear();
        failedMessageCount.clear();
        skippedMessageCount.clear();
        errorMessageCount.clear();
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

    public void incCacheMissCounter() { cacheMissCount.inc();}

    public void setOffset(SystemStreamPartition systemStreamPartition, String offset) {
        offsetMap.put(systemStreamPartition.getStream() + systemStreamPartition.getPartition().getPartitionId(),
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
                        sysPartition.getPartition().getPartitionId(), 0L) + 1L;
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
        return new Gson().toJson(metricsEvent);
    }
}
