package org.ekstep.ep.samza.task;

import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.Metric;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.metrics.MetricsRegistryMap;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.fixtures.MetricsFixture;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.Mockito.*;

public class JobMetricsTest {

	private TaskContext contextMock;
	private MetricsRegistry metricsRegistry;
	private Counter counter;
	private TaskCoordinator coordinatorMock;
	private IncomingMessageEnvelope envelopeMock;
	private Config configMock;
	private JobMetrics jobMetricsMock;
    private MetricsRegistryMap metricsRegistryMap;

	@Before
	public void setUp() {
		contextMock = Mockito.mock(TaskContext.class);
		metricsRegistry = Mockito.mock(MetricsRegistry.class);
		counter = Mockito.mock(Counter.class);
		coordinatorMock = mock(TaskCoordinator.class);
		configMock = Mockito.mock(Config.class);
		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);


	}

	@Test
	public void shouldReturnConsumerLag() {

		jobMetricsMock= new JobMetrics(contextMock,null);

		Set<SystemStreamPartition> systemStreamPartitions = new HashSet<>();
		SystemStreamPartition systemStreamPartition= new SystemStreamPartition("kafka","telemetry.denorm1",new Partition(1));
		SystemStreamPartition systemStreamPartition1= new SystemStreamPartition("kafka","telemetry.denorm",new Partition(1));
		systemStreamPartitions.add(systemStreamPartition);
		systemStreamPartitions.add(systemStreamPartition1);
		jobMetricsMock.setOffset(systemStreamPartition,"2");
		jobMetricsMock.setOffset(systemStreamPartition1,"3");

		Map<String, ConcurrentHashMap<String, Metric>>  concurrentHashMap=  MetricsFixture.getMetricMap(MetricsFixture.METRIC_EVENT_STREAM);
		when(contextMock.getSystemStreamPartitions()).thenReturn(systemStreamPartitions);
		long consumer_lag= jobMetricsMock.getConsumerLag(concurrentHashMap);
		Assert.assertEquals(4,consumer_lag);

	}
}
