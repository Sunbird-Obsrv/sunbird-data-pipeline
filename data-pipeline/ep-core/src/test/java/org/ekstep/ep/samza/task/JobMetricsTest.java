package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.container.SamzaContainerContext;
import org.apache.samza.container.TaskName;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Metric;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.metrics.MetricsRegistryMap;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.fixtures.MetricsFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

public class JobMetricsTest {

    private TaskContext contextMock;
    private JobMetrics jobMetricsMock;

    @Before
    public void setUp() {
        contextMock = mock(TaskContext.class);
        MetricsRegistry metricsRegistry = mock(MetricsRegistry.class);
        Counter counter = mock(Counter.class);
        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
    }

    @Test
    public void shouldReturnConsumerLag() {

        jobMetricsMock = new JobMetrics(contextMock, "test-job");
        jobMetricsMock.clear();
        Set<SystemStreamPartition> systemStreamPartitions = new HashSet<>();
        SystemStreamPartition systemStreamPartition =
                new SystemStreamPartition("kafka", "inputtopic1", new Partition(0));
        systemStreamPartitions.add(systemStreamPartition);

        Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
                MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM1);
        when(contextMock.getSystemStreamPartitions()).thenReturn(systemStreamPartitions);
        long consumer_lag = jobMetricsMock.consumerLag(concurrentHashMap);
        Assert.assertEquals(800, consumer_lag);

    }

    @Test
    public void shouldReturnZeroConsumerLagWhenAllMessagesAreProcessed() {

        jobMetricsMock = new JobMetrics(contextMock, null);

        Set<SystemStreamPartition> systemStreamPartitions = new HashSet<>();
        SystemStreamPartition systemStreamPartition =
                new SystemStreamPartition("kafka", "inputtopic1", new Partition(0));
        systemStreamPartitions.add(systemStreamPartition);

        Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
                MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM2);
        when(contextMock.getSystemStreamPartitions()).thenReturn(systemStreamPartitions);
        long consumer_lag = jobMetricsMock.consumerLag(concurrentHashMap);
        Assert.assertEquals(0, consumer_lag);
    }

    @Test
    public void shouldCollectTheMetrics() {
        Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
                MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM2);
        MetricsRegistryMap metricsRegistryMap = new MetricsRegistryMap("id");
        TaskName taskName = new TaskName("task");
        ArrayList<TaskName> list = new ArrayList<TaskName>();
        list.add(taskName);
        SamzaContainerContext samzaContainerContext = new SamzaContainerContext("id", mock(Config.class), list, metricsRegistryMap);
        when(contextMock.getSamzaContainerContext()).thenReturn(samzaContainerContext);
        when(contextMock.getMetricsRegistry()).thenReturn(samzaContainerContext.metricsRegistry);
        metricsRegistryMap.metrics().put("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics", concurrentHashMap.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics"));
        metricsRegistryMap.metrics().put("org.apache.samza.checkpoint.OffsetManagerMetrics", concurrentHashMap.get("org.apache.samza.checkpoint.OffsetManagerMetrics"));
        jobMetricsMock = new JobMetrics(contextMock, "test-job");
        jobMetricsMock.incSuccessCounter();
        jobMetricsMock.deviceDBUpdateSuccess();
        jobMetricsMock.deviceCacheUpdateSuccess();
        jobMetricsMock.incFailedCounter();
        jobMetricsMock.incSkippedCounter();
        jobMetricsMock.incErrorCounter();
        jobMetricsMock.incBatchSuccessCounter();
        jobMetricsMock.incBatchErrorCounter();
        jobMetricsMock.incPrimaryRouteSuccessCounter();
        jobMetricsMock.incSecondaryRouteSuccessCounter();
        jobMetricsMock.incDuplicateCounter();
        jobMetricsMock.incCacheHitCounter();
        jobMetricsMock.incCacheErrorCounter();
        jobMetricsMock.incNoDataCount();
        jobMetricsMock.incProcessedMessageCount();
        jobMetricsMock.incUnprocessedMessageCount();
        jobMetricsMock.incDBHitCount();
        jobMetricsMock.incUserCacheHitCount();
        jobMetricsMock.incExpiredEventCount();
        jobMetricsMock.incUserDeclaredHitCount();
        jobMetricsMock.incIpLocationHitCount();
        jobMetricsMock.incNoCacheHitCount();
        System.out.println(jobMetricsMock.collect());
        Gson g = new Gson();
        Map<String, Object> metrics = g.fromJson(jobMetricsMock.collect(), Map.class);
        assert (metrics.get("job-name")).equals("test-job");
        assert (metrics.get("cache-hit-count")).equals(1.0);
        assert (metrics.get("db-hit-count")).equals(1.0);
        assert (metrics.get("batch-error-count")).equals(1.0);
        assert (metrics.get("success-message-count")).equals(1.0);
        assert (metrics.get("user-db-hit-count")).equals(0.0);
        assert (metrics.get("cache-empty-values-count")).equals(1.0);
        assert (metrics.get("failed-message-count")).equals(1.0);
        assert (metrics.get("unprocessed-message-count")).equals(1.0);
        assert (metrics.get("expired-event-count")).equals(1.0);
        assert (metrics.get("duplicate-event-count")).equals(1.0);
        assert (metrics.get("processed-message-count")).equals(1.0);
        assert (metrics.get("primary-route-success-count")).equals(1.0);
        assert (metrics.get("batch-success-count")).equals(1.0);
        assert (metrics.get("secondary-route-success-count")).equals(1.0);
        assert (metrics.get("device-cache-update-count")).equals(1.0);
        assert (metrics.get("cache-error-count")).equals(1.0);
        assert (metrics.get("error-message-count")).equals(1.0);
        assert (metrics.get("partition")).equals(0.0);
    }
}
