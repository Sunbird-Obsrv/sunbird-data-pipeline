package org.ekstep.ep.samza.task;

import org.apache.samza.Partition;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Metric;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.TaskContext;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.fixtures.MetricsFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

		jobMetricsMock = new JobMetrics(contextMock, null);

		Set<SystemStreamPartition> systemStreamPartitions = new HashSet<>();
		SystemStreamPartition systemStreamPartition =
				new SystemStreamPartition("kafka", "inputtopic1", new Partition(1));
		SystemStreamPartition systemStreamPartition1 =
				new SystemStreamPartition("kafka", "inputtopic2", new Partition(1));
		systemStreamPartitions.add(systemStreamPartition);
		systemStreamPartitions.add(systemStreamPartition1);
		jobMetricsMock.setOffset(systemStreamPartition, "2");
		jobMetricsMock.setOffset(systemStreamPartition1, "3");

		Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
				MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM);
		when(contextMock.getSystemStreamPartitions()).thenReturn(systemStreamPartitions);
		long consumer_lag = jobMetricsMock.consumerLag(concurrentHashMap);
		Assert.assertEquals(4, consumer_lag);

	}

	@Test
	public void shouldReturnConsumerLagWhenNoEvents() {

		jobMetricsMock = new JobMetrics(contextMock, null);

		Set<SystemStreamPartition> systemStreamPartitions = new HashSet<>();
		SystemStreamPartition systemStreamPartition =
				new SystemStreamPartition("kafka", "inputtopic1", new Partition(1));
		systemStreamPartitions.add(systemStreamPartition);
		Map<String, ConcurrentHashMap<String, Metric>> concurrentHashMap =
				MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT);
		when(contextMock.getSystemStreamPartitions()).thenReturn(systemStreamPartitions);
		long consumer_lag = jobMetricsMock.consumerLag(concurrentHashMap);
		Assert.assertEquals(0, consumer_lag);

	}
}
