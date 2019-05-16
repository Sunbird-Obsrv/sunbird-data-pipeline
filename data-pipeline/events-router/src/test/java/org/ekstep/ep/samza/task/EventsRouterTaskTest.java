package org.ekstep.ep.samza.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

import com.fiftyonred.mock_jedis.MockJedis;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.domain.DeDupEngine;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;

public class EventsRouterTaskTest {

	private static final String TELEMETRY_EVENTS_TOPIC = "events.telemetry";
	private static final String SUMMARY_EVENTS_TOPIC = "events.summary";
	private static final String FAILED_TOPIC = "telemetry.failed";
	private static final String MALFORMED_TOPIC = "telemetry.malformed";
	private static final String LOG_EVENTS_TOPIC = "events.log";
	private static final String DUPLICATE_TOPIC = "telemetry.duplicate";
	
	private MessageCollector collectorMock;
	private TaskContext contextMock;
	private MetricsRegistry metricsRegistry;
	private Counter counter;
	private TaskCoordinator coordinatorMock;
	private IncomingMessageEnvelope envelopeMock;
	private Config configMock;
	private EventsRouterTask eventsRouterTask;
	private DeDupEngine deDupEngineMock;
	private Jedis jedisMock = new MockJedis("duplicationtest");
	private int dupStoreId = 1;

	@Before
	public void setUp() {
		collectorMock = mock(MessageCollector.class);
		contextMock = Mockito.mock(TaskContext.class);
		metricsRegistry = Mockito.mock(MetricsRegistry.class);
		counter = Mockito.mock(Counter.class);
		coordinatorMock = mock(TaskCoordinator.class);
		envelopeMock = mock(IncomingMessageEnvelope.class);
		configMock = Mockito.mock(Config.class);

		RedisConnect redisConnectMock = mock(RedisConnect.class);
		deDupEngineMock = mock(DeDupEngine.class);
		stub(redisConnectMock.getConnection()).toReturn(jedisMock);
		stub(configMock.get("router.events.telemetry.route.topic", TELEMETRY_EVENTS_TOPIC)).toReturn(TELEMETRY_EVENTS_TOPIC);
		stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
		stub(configMock.get("router.events.summary.route.topic", SUMMARY_EVENTS_TOPIC)).toReturn(SUMMARY_EVENTS_TOPIC);
		stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);
		stub(configMock.get("router.events.log.route.topic", LOG_EVENTS_TOPIC)).toReturn(LOG_EVENTS_TOPIC);
		stub(configMock.get("output.duplicate.topic.name", DUPLICATE_TOPIC)).toReturn(DUPLICATE_TOPIC);
		stub(configMock.getBoolean("dedup.enabled", true)).toReturn(true);

		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
		stub(envelopeMock.getOffset()).toReturn("2");
		stub(envelopeMock.getSystemStreamPartition())
				.toReturn(new SystemStreamPartition("kafka", "telemetry.denorm.valid", new Partition(1)));

	}

	@Test
	public void shouldRouteSummaryEventsToSummaryTopic() throws Exception {

		stub(configMock.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY")).toReturn("ME_WORKFLOW_SUMMARY");
		when(deDupEngineMock.isUniqueEvent(anyString(), anyInt())).thenReturn(true);
		eventsRouterTask = new EventsRouterTask(deDupEngineMock, configMock, contextMock);
		stub(envelopeMock.getMessage()).toReturn(EventFixture.WORKFLOW_SUMMARY_EVENT);
		eventsRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUMMARY_EVENTS_TOPIC)));
	}
	
	@Test
	public void shouldRouteTelemetryEventsToTelemetryTopic() throws Exception {

		stub(configMock.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY")).toReturn("ME_WORKFLOW_SUMMARY");
		when(deDupEngineMock.isUniqueEvent(anyString(), anyInt())).thenReturn(true);
		eventsRouterTask = new EventsRouterTask(deDupEngineMock, configMock, contextMock);
		stub(envelopeMock.getMessage()).toReturn(EventFixture.START_EVENT);
		eventsRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), TELEMETRY_EVENTS_TOPIC)));
		
	}

	@Test
	public void shouldSendEventToFailedTopicIfEventIsNotParseable() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.UNPARSABLE_START_EVENT);
		when(deDupEngineMock.isUniqueEvent(anyString(), anyInt())).thenReturn(true);
		eventsRouterTask = new EventsRouterTask(deDupEngineMock, configMock, contextMock);
		eventsRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
	}

	@Test
	public void shouldSendEventToMalformedTopicIfEventIsAnyRandomString() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.ANY_STRING);
		when(deDupEngineMock.isUniqueEvent(anyString(), anyInt())).thenReturn(true);
		eventsRouterTask = new EventsRouterTask(deDupEngineMock, configMock, contextMock);
		eventsRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
	}

	@Test
	public void shouldSendEventToDuplicateTopic() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.START_EVENT);
		when(deDupEngineMock.isUniqueEvent(anyString(), anyInt())).thenReturn(false);
		eventsRouterTask = new EventsRouterTask(deDupEngineMock, configMock, contextMock);
		eventsRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), DUPLICATE_TOPIC)));
	}

	public ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
		return new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
				assertEquals("kafka", systemStream.getSystem());
				assertEquals(stream, systemStream.getStream());
				return true;
			}
		};
	}

	@Test
	public void shouldRouteLogEventsToLogEventsTopic() throws Exception {

		stub(configMock.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY")).toReturn("ME_WORKFLOW_SUMMARY");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.LOG_EVENT);
		when(deDupEngineMock.isUniqueEvent(anyString(), anyInt())).thenReturn(true);
		eventsRouterTask = new EventsRouterTask(deDupEngineMock, configMock, contextMock);
		eventsRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), LOG_EVENTS_TOPIC)));
	}
}
