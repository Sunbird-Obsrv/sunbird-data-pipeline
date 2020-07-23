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
import org.ekstep.ep.samza.schema.Element;
import org.ekstep.ep.samza.schema.MetricEvent;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.assertEquals;
import static junit.framework.TestCase.assertTrue;
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
                new SystemStreamPartition("kafka", "inputtopic", new Partition(0));
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
                new SystemStreamPartition("kafka", "inputtopic", new Partition(0));
        systemStreamPartitions.add(systemStreamPartition);

        Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
                MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM2);
        when(contextMock.getSystemStreamPartitions()).thenReturn(systemStreamPartitions);
        long consumer_lag = jobMetricsMock.consumerLag(concurrentHashMap);
        Assert.assertEquals(0, consumer_lag);
    }

    /*
    @Test
    public void shouldCollectTheMetrics() {
        Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
                MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM1);
        SystemStreamPartition systemStreamPartition = new SystemStreamPartition("kafka", "inputtopic", new Partition(0));
        Set<SystemStreamPartition> partitionsSet = new HashSet<>();
        partitionsSet.add(systemStreamPartition);
        MetricsRegistryMap metricsRegistryMap = new MetricsRegistryMap("id");
        metricsRegistryMap.metrics().put("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics", concurrentHashMap.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics"));
        metricsRegistryMap.metrics().put("org.apache.samza.checkpoint.OffsetManagerMetrics", concurrentHashMap.get("org.apache.samza.checkpoint.OffsetManagerMetrics"));

        TaskName taskName = new TaskName("task");
        ArrayList<TaskName> list = new ArrayList<TaskName>();
        list.add(taskName);
        SamzaContainerContext samzaContainerContext = new SamzaContainerContext("id", mock(Config.class), list, metricsRegistryMap);
        when(contextMock.getSamzaContainerContext()).thenReturn(samzaContainerContext);
        when(contextMock.getSystemStreamPartitions()).thenReturn(partitionsSet);
        when(contextMock.getMetricsRegistry()).thenReturn(samzaContainerContext.metricsRegistry);

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
        jobMetricsMock.incCacheMissCounter();
        jobMetricsMock.incCacheErrorCounter();
        jobMetricsMock.incEmptyCacheValueCounter();
        jobMetricsMock.incProcessedMessageCount();
        jobMetricsMock.incUnprocessedMessageCount();
        jobMetricsMock.incDBHitCount();
        jobMetricsMock.incUserCacheHitCount();
        jobMetricsMock.incExpiredEventCount();
        jobMetricsMock.incUserDeclaredHitCount();
        jobMetricsMock.incIpLocationHitCount();
        jobMetricsMock.incDialCodesCount();
        jobMetricsMock.incDialCodesFromApiCount();
        jobMetricsMock.incDialCodesFromCacheCount();

        String metricsJson = jobMetricsMock.collect();
        System.out.println(metricsJson);
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        Map<String, Object> metrics = (Map<String, Object>) gson.fromJson(metricsJson, Map.class);
        assertEquals("test-job", metrics.get("job-name"));
        assertEquals(1.0, metrics.get("cache-hit-count"));
        assertEquals(1.0, metrics.get("db-hit-count"));
        assertEquals(1.0, metrics.get("batch-error-count"));
        assertEquals(1.0, metrics.get("success-message-count"));
        assertEquals(1.0, metrics.get("cache-empty-values-count"));
        assertEquals(1.0, metrics.get("failed-message-count"));
        assertEquals(1.0, metrics.get("unprocessed-message-count"));
        assertEquals(1.0, metrics.get("expired-event-count"));
        assertEquals(1.0, metrics.get("duplicate-event-count"));
        assertEquals(1.0, metrics.get("processed-message-count"));
        assertEquals(1.0, metrics.get("primary-route-success-count"));
        assertEquals(1.0, metrics.get("batch-success-count"));
        assertEquals(1.0, metrics.get("secondary-route-success-count"));
        assertEquals(1.0, metrics.get("device-cache-update-count"));
        assertEquals(1.0, metrics.get("cache-error-count"));
        assertEquals(1.0, metrics.get("error-message-count"));
        assertEquals(1.0, metrics.get("dialcodes-count"));
        assertEquals(1.0, metrics.get("dialcodes-api-hit"));
        assertEquals(1.0, metrics.get("dialcodes-cache-hit"));
        assertEquals(0.0, metrics.get("partition"));
        assertEquals(800.0, metrics.get("consumer-lag"));
    }
    */


    @Test
    public void validateCollectedMetrics() {
        Map<String, ConcurrentHashMap<String, Metric>> metricsMap =
                MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM1);
        MetricsRegistryMap metricsRegistryMap = new MetricsRegistryMap("id");
        SystemStreamPartition systemStreamPartition = new SystemStreamPartition("kafka", "inputtopic", new Partition(0));
        Set<SystemStreamPartition> partitionsSet = new HashSet<>();
        partitionsSet.add(systemStreamPartition);
        metricsRegistryMap.metrics().put("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics",
                metricsMap.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics"));
        metricsRegistryMap.metrics().put("org.apache.samza.checkpoint.OffsetManagerMetrics",
                metricsMap.get("org.apache.samza.checkpoint.OffsetManagerMetrics"));
        TaskName taskName = new TaskName("task");
        ArrayList<TaskName> taskList = new ArrayList<>();
        taskList.add(taskName);
        SamzaContainerContext samzaContainerContext = new SamzaContainerContext("id", mock(Config.class), taskList, metricsRegistryMap);
        when(contextMock.getSamzaContainerContext()).thenReturn(samzaContainerContext);
        when(contextMock.getSystemStreamPartitions()).thenReturn(partitionsSet);
        when(contextMock.getMetricsRegistry()).thenReturn(samzaContainerContext.metricsRegistry);

        jobMetricsMock = new JobMetrics(contextMock, "test-job");
        List<String> jobMetricList = new ArrayList<>(Arrays.asList("success-message-count","skipped-message-count","error-message-count",
                "batch-success-count","batch-error-count","duplicate-event-count"));
        jobMetricsMock.incSuccessCounter();
        jobMetricsMock.incSkippedCounter();
        jobMetricsMock.incErrorCounter();
        jobMetricsMock.incBatchSuccessCounter();
        jobMetricsMock.incBatchErrorCounter();
        jobMetricsMock.incDuplicateCounter();
        String metricsOutput = jobMetricsMock.collect(jobMetricList);
        System.out.println(metricsOutput);
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        Map<String, Object> metrics = (Map<String, Object>) gson.fromJson(metricsOutput, Map.class);
        assertEquals("test-job", metrics.get("job-name"));
        assertEquals(1.0, metrics.get("success-message-count"));
        assertEquals(1.0, metrics.get("skipped-message-count"));
        assertEquals(1.0, metrics.get("error-message-count"));
        assertEquals(1.0, metrics.get("batch-success-count"));
        assertEquals(1.0, metrics.get("batch-error-count"));
        assertEquals(1.0, metrics.get("duplicate-event-count"));
        assertEquals(0.0, metrics.get("partition"));
        assertEquals(800.0, metrics.get("consumer-lag"));
        assertTrue(metrics.containsKey("metricts") && metrics.get("metricts") != null);
    }

    @Test
    public void generatePrometheusMetricStructure() {
        Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
                MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM1);

        MetricsRegistryMap metricsRegistryMap = new MetricsRegistryMap("metric-registry");
        metricsRegistryMap.metrics().put("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics",
                concurrentHashMap.get("org.apache.samza.system.kafka.KafkaSystemConsumerMetrics"));
        metricsRegistryMap.metrics().put("org.apache.samza.checkpoint.OffsetManagerMetrics",
                concurrentHashMap.get("org.apache.samza.checkpoint.OffsetManagerMetrics"));

        SystemStreamPartition systemStreamPartition = new SystemStreamPartition("kafka", "inputtopic", new Partition(0));
        Set<SystemStreamPartition> partitionsSet = new HashSet<>();
        partitionsSet.add(systemStreamPartition);

        TaskName taskName = new TaskName("generate-prometheus-metrics-task");
        ArrayList<TaskName> taskList = new ArrayList<>(Collections.singletonList(taskName));
        SamzaContainerContext samzaContainerContext = new SamzaContainerContext("id",
                mock(Config.class), taskList, metricsRegistryMap);
        when(contextMock.getSamzaContainerContext()).thenReturn(samzaContainerContext);
        when(contextMock.getSystemStreamPartitions()).thenReturn(partitionsSet);
        when(contextMock.getMetricsRegistry()).thenReturn(samzaContainerContext.metricsRegistry);

        jobMetricsMock = new JobMetrics(contextMock, "test-job");

        List<String> jobMetricList = new ArrayList<>(Arrays.asList("success-message-count","skipped-message-count","error-message-count",
                "batch-success-count","batch-error-count","duplicate-event-count"));
        jobMetricsMock.incSuccessCounter();
        jobMetricsMock.incSkippedCounter();
        jobMetricsMock.incErrorCounter();
        jobMetricsMock.incBatchSuccessCounter();
        jobMetricsMock.incBatchErrorCounter();
        jobMetricsMock.incDuplicateCounter();
        String metricsOutput = jobMetricsMock.generateMetrics(jobMetricList);

        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
        Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        MetricEvent metrics = gson.fromJson(metricsOutput, MetricEvent.class);
        assertEquals("samza", metrics.getSystem());
        assertEquals("pipeline-metrics", metrics.getSubsystem());
        assertEquals(dt.format(new Date()), dt.format(new Date(metrics.getMetricts())));
        assertEquals(7, metrics.getMetrics().size());
        assertEquals(2, metrics.getDimensions().size());


        List<Element> metricList = metrics.getMetrics();
        assertTrue(metricList.contains(new Element("success-message-count", 1.0)));
        assertTrue(metricList.contains(new Element("skipped-message-count", 1.0)));
        assertTrue(metricList.contains(new Element("error-message-count", 1.0)));
        assertTrue(metricList.contains(new Element("batch-success-count", 1.0)));
        assertTrue(metricList.contains(new Element("batch-error-count", 1.0)));
        assertTrue(metricList.contains(new Element("duplicate-event-count", 1.0)));
        assertTrue(metricList.contains(new Element("consumer-lag", 800.0)));

        List<Element> dimsList = metrics.getDimensions();
        assertTrue(dimsList.contains(new Element("job-name", "test-job")));
        assertTrue(dimsList.contains(new Element("partition", 0.0)));
    }
}
