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
    private long consumer_lag;
    private HashMap<String,Long> offset_map= new HashMap<>();
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
        this.context=context;
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

    public void setOffset(SystemStreamPartition systemStreamPartition,String offset)
    {
       offset_map.put(systemStreamPartition.getStream()+systemStreamPartition.getPartition().getPartitionId(), Long.valueOf(offset));
    }

    public long getConsumerLag(Map<String, ConcurrentHashMap<String, Metric>> container_registry) {
        try {
            consumer_lag = 0;
            for (SystemStreamPartition s : context.getSystemStreamPartitions()) {
                long log_end_offset = Long.valueOf(container_registry.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics").
                        get(s.getSystem() + "-" + s.getStream() + "-" + s.getPartition().getPartitionId() + "-offset-change").toString());
                long offset= offset_map.containsKey(s.getStream() + s.getPartition().getPartitionId()) ? offset_map.get(s.getStream() + s.getPartition().getPartitionId()) + 1 : 0;
                consumer_lag = consumer_lag + (log_end_offset - offset);
                partition = s.getPartition().getPartitionId();
            }
            return consumer_lag;
        }catch (Exception e)
        {
            LOGGER.error(null,
                    "EXCEPTION. WHEN COMPUTING CONSUMER LAG METRIC",
                    e);
            return 0;
        }
    }

    public String collect() {

        Map<String,Object> metricsEvent = new HashMap<>();
        metricsEvent.put("job-name", jobName);
        metricsEvent.put("success-message-count", successMessageCount.getCount());
        metricsEvent.put("failed-message-count", failedMessageCount.getCount());
        metricsEvent.put("error-message-count", errorMessageCount.getCount());
        metricsEvent.put("skipped-message-count", skippedMessageCount.getCount());
        metricsEvent.put("consumer_lag", getConsumerLag(((MetricsRegistryMap) context.getSamzaContainerContext().metricsRegistry).metrics()));
        metricsEvent.put("partition",partition);
        return new Gson().toJson(metricsEvent);
    }
}
