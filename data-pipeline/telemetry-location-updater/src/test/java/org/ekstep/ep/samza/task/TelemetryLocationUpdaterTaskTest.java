package org.ekstep.ep.samza.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.LocationCache;
import org.ekstep.ep.samza.util.LocationSearchServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.Map;

public class TelemetryLocationUpdaterTaskTest {

	private static final String SUCCESS_TOPIC = "telemetry.with_location";
	private static final String FAILED_TOPIC = "telemetry.failed";
	private static final String MALFORMED_TOPIC = "telemetry.malformed";
	
	private MessageCollector collectorMock;
	private TaskContext contextMock;
	private MetricsRegistry metricsRegistry;
	private Counter counter;
	private TaskCoordinator coordinatorMock;
	private IncomingMessageEnvelope envelopeMock;
	private Config configMock;
	private LocationCache locationCacheMock;
	private TelemetryLocationUpdaterTask telemetryLocationUpdaterTask;
	private KeyValueStore<String, Location> locationStore;
	private LocationSearchServiceClient searchService;

	@Before
	public void setUp() {
		collectorMock = mock(MessageCollector.class);
		contextMock = mock(TaskContext.class);
		metricsRegistry = Mockito.mock(MetricsRegistry.class);
		counter = Mockito.mock(Counter.class);
		coordinatorMock = mock(TaskCoordinator.class);
		envelopeMock = mock(IncomingMessageEnvelope.class);
		configMock = Mockito.mock(Config.class);
		locationCacheMock = Mockito.mock(LocationCache.class);
		locationStore = mock(KeyValueStore.class);
		searchService = mock(LocationSearchServiceClient.class);

		stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
		stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
		stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);

		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
	}

	@Test
	public void shouldSendEventsToSuccessTopicIfDidIsNull() throws Exception {

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationCacheMock,
				locationStore, searchService);
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
		// stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		Location loc = new Location();
		loc.setDistrict("");
		loc.setState("Karnataka");
		stub(locationStore.get("0123221617357783046602")).toReturn(null);
		stub(searchService.searchChannelLocationId("0123221617357783046602")).toReturn("loc1");
		stub(searchService.searchLocation("loc1")).toReturn(loc);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertTrue(outputMessage.contains("\"state\":\"Karnataka\""));
				assertTrue(outputMessage.contains("\"district\":\"\""));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("ldata_obtained"), true);
				return true;
			}
		}));

	}
	
	@Test
	public void shouldSendEventsToSuccessTopicWithoutStampingLocation() throws Exception {

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationCacheMock, locationStore, searchService);
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertTrue(outputMessage.contains("\"state\":\"\""));
				assertTrue(outputMessage.contains("\"district\":\"\""));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("ldata_obtained"), false);
				return true;
			}
		}));
		
	}

	@Test
	public void shouldSendEventsToSuccessTopicWithStampingLocationFromChannelAPI() throws Exception {

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationCacheMock, locationStore, searchService);
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		Location loc = new Location();
		loc.setDistrict("");
		loc.setState("Karnataka");
		stub(locationStore.get("0123221617357783046602")).toReturn(null);
		stub(searchService.searchChannelLocationId("0123221617357783046602")).toReturn("loc1");
		stub(searchService.searchLocation("loc1")).toReturn(loc);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertEquals(outputMessage.contains("\"state\":\"Karnataka\""), true);
				assertEquals(outputMessage.contains("\"district\":\"\""), true);
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("ldata_obtained"), true);
				return true;
			}
		}));

	}

	@Test
	public void shouldSendEventsToSuccessTopicWithStampingLocationFromLocalStore() throws Exception {

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationCacheMock, locationStore, searchService);
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		Location loc = new Location();
		loc.setDistrict("Kodagu");
		loc.setState("Karnataka");
		stub(locationStore.get("0123221617357783046602")).toReturn(loc);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> context = new Gson().fromJson(outputEvent.get("ldata").toString(), mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertEquals(context.get("state"), "Karnataka");
				assertEquals(context.get("district"), "Kodagu");
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("ldata_obtained"), true);
				Map<String, Object> edata = new Gson().fromJson(outputEvent.get("edata").toString(), mapType);
				assertNull(edata.get("loc"));
				return true;
			}
		}));

	}

	@Test
	public void shouldSendEventsToSuccessTopicWithLocation() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		Location loc = new Location();
		loc.setDistrict("Bangalore");
		loc.setState("Karnataka");
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(loc);
		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationCacheMock, locationStore, searchService);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> context = new Gson().fromJson(outputEvent.get("ldata").toString(), mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertEquals(context.get("state"), "Karnataka");
				assertEquals(context.get("district"), "Bangalore");
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("ldata_obtained"), true);
				return true;
			}
		}));
	}

	@Test
	public void shouldSendEventToFailedTopicIfEventIsNotParseable() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.UNPARSABLE_START_EVENT);
		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationCacheMock, locationStore, searchService);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
	}

	@Test
	public void shouldSendEventToMalformedTopicIfEventIsAnyRandomString() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.ANY_STRING);
		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationCacheMock, locationStore, searchService);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
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
