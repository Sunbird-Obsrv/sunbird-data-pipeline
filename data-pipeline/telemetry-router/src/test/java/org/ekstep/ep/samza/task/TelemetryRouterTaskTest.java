package org.ekstep.ep.samza.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
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
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.Map;

public class TelemetryRouterTaskTest {

	private static final String PRIMARY_TOPIC = "telemetry.sink";
	private static final String FAILED_TOPIC = "telemetry.failed";
	private static final String SECONDARY_TOPIC = "telemetry.log";
	private static final String MALFORMED_TOPIC = "telemetry.malformed";
	
	private MessageCollector collectorMock;
	private TaskContext contextMock;
	private MetricsRegistry metricsRegistry;
	private Counter counter;
	private TaskCoordinator coordinatorMock;
	private IncomingMessageEnvelope envelopeMock;
	private Config configMock;
	private TelemetryRouterTask telemetryRouterTask;

	@Before
	public void setUp() {
		collectorMock = mock(MessageCollector.class);
		contextMock = Mockito.mock(TaskContext.class);
		metricsRegistry = Mockito.mock(MetricsRegistry.class);
		counter = Mockito.mock(Counter.class);
		coordinatorMock = mock(TaskCoordinator.class);
		envelopeMock = mock(IncomingMessageEnvelope.class);
		configMock = Mockito.mock(Config.class);

		stub(configMock.get("router.events.primary.route.topic", PRIMARY_TOPIC)).toReturn(PRIMARY_TOPIC);
		stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
		stub(configMock.get("router.events.secondary.route.topic", SECONDARY_TOPIC)).toReturn(SECONDARY_TOPIC);
		stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);

		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
		stub(envelopeMock.getOffset()).toReturn("2");
		stub(envelopeMock.getSystemStreamPartition())
				.toReturn( new SystemStreamPartition("kafka","input.topic",new Partition(1)));

//		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
	}

	@Test
	public void shouldSendLOGEventToSecondaryRoute() throws Exception {

		stub(configMock.get("router.events.secondary.route.events", "LOG,ERROR")).toReturn("LOG");
		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
		
		stub(envelopeMock.getMessage()).toReturn(EventFixture.LOG_EVENT);
		telemetryRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SECONDARY_TOPIC)));
	}
	
	@Test
	public void shouldSendERROREventToPrimaryRoute() throws Exception {

		stub(configMock.get("router.events.secondary.route.events", "LOG,ERROR")).toReturn("LOG");
		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
		
		stub(envelopeMock.getMessage()).toReturn(EventFixture.ERROR_EVENT);
		telemetryRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), PRIMARY_TOPIC)));
		
	}
	
	@Test
	public void shouldSendERROREventToSecondaryRoute() throws Exception {

		stub(configMock.get("router.events.secondary.route.events", "LOG,ERROR")).toReturn("LOG,ERROR");
		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
		
		stub(envelopeMock.getMessage()).toReturn(EventFixture.ERROR_EVENT);
		telemetryRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SECONDARY_TOPIC)));
		
	}
	
	@Test
	public void shouldSendSTARTEventToPrimaryRoute() throws Exception {

		stub(configMock.get("router.events.secondary.route.events", "LOG,ERROR")).toReturn("LOG");
		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
		
		stub(envelopeMock.getMessage()).toReturn(EventFixture.START_EVENT);
		telemetryRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), PRIMARY_TOPIC)));
		
	}

	@Test
	public void shouldStampTSForPrimaryEvent() throws Exception {
		stub(configMock.get("router.events.secondary.route.events", "LOG,ERROR")).toReturn("LOG");
		stub(envelopeMock.getMessage()).toReturn(EventFixture.START_EVENT);
		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
		telemetryRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertNotNull(outputEvent.get("ts"));
				return true;
			}
		}));
	}

	@Test
	public void shouldSendEventToFailedTopicIfEventIsNotParseable() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.UNPARSABLE_START_EVENT);
		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
		telemetryRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
	}

	@Test
	public void shouldSendEventToMalformedTopicIfEventIsAnyRandomString() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.ANY_STRING);
		telemetryRouterTask = new TelemetryRouterTask(configMock, contextMock);
		telemetryRouterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
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
}
