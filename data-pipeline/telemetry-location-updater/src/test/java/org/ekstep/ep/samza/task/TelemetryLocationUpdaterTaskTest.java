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
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.engine.LocationEngine;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.LocationCache;
import org.ekstep.ep.samza.util.LocationSearchServiceClient;
import org.ekstep.ep.samza.util.UserLocationCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
	private LocationEngine locationEngine;
	private LocationEngine locationEngineMock;
	private LocationCache locationCacheMock;
	private TelemetryLocationUpdaterTask telemetryLocationUpdaterTask;
	private CacheService<String, Location> locationStoreCache;
	private KeyValueStore<Object, Object> locationStoreMock;
	private LocationSearchServiceClient searchService;
	private UserLocationCache userLocationCacheMock;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		collectorMock = mock(MessageCollector.class);
		contextMock = mock(TaskContext.class);
		metricsRegistry = Mockito.mock(MetricsRegistry.class);
		counter = Mockito.mock(Counter.class);
		coordinatorMock = mock(TaskCoordinator.class);
		envelopeMock = mock(IncomingMessageEnvelope.class);
		configMock = Mockito.mock(Config.class);
		locationEngineMock = mock(LocationEngine.class);

		locationCacheMock = Mockito.mock(LocationCache.class);
		locationStoreMock = Mockito.mock(KeyValueStore.class);
		locationStoreCache = mock(CacheService.class);
		searchService = mock(LocationSearchServiceClient.class);
		userLocationCacheMock = mock(UserLocationCache.class);

		stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
		stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
		stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);

		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
		stub(envelopeMock.getOffset()).toReturn("2");
		stub(envelopeMock.getSystemStreamPartition())
				.toReturn( new SystemStreamPartition("kafka","input.topic",new Partition(1)));


		locationEngine = new LocationEngine(locationStoreCache, searchService, locationCacheMock, userLocationCacheMock);
		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationStoreMock, locationEngine);
	}

	@Test
	public void shouldSendEventsToSuccessTopicIfDidIsNull() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(null);
		// Location loc = new Location("", "", "", "", "");
		stub(locationStoreCache.get("0123221617357783046602")).toReturn(null);
		List<String> locationIds = new ArrayList<String>();
		locationIds.add("loc1");
		stub(searchService.searchChannelLocationId("0123221617357783046602")).toReturn(locationIds);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertTrue(outputMessage.contains("\"countrycode\":\"\""));
				assertTrue(outputMessage.contains("\"country\":\"\""));
				assertTrue(outputMessage.contains("\"statecode\":\"\""));
				assertTrue(outputMessage.contains("\"state\":\"\""));
				assertTrue(outputMessage.contains("\"city\":\"\""));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("device_location_retrieved"), false);
				return true;
			}
		}));

	}
	
	@Test
	public void shouldSendEventsToSuccessTopicWithoutStampingLocation() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(null);
		stub(searchService.searchChannelLocationId("0123221617357783046602")).toReturn(null);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertTrue(outputMessage.contains("\"countrycode\":\"\""));
				assertTrue(outputMessage.contains("\"country\":\"\""));
				assertTrue(outputMessage.contains("\"statecode\":\"\""));
				assertTrue(outputMessage.contains("\"state\":\"\""));
				assertTrue(outputMessage.contains("\"city\":\"\""));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("device_location_retrieved"), false);
				return true;
			}
		}));
		
	}

	/*
	@Test
	public void shouldSendEventsToSuccessTopicWithStampingLocationFromChannelAPI() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		Location loc = new Location();
		loc.setCity("");
		loc.setState("Karnataka");
		stub(locationStoreCache.get("0123221617357783046602")).toReturn(null);
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
				assertEquals(flags.get("device_location_retrieved"), true);
				return true;
			}
		}));

	}
	*/

	@Test
	@Ignore
	public void shouldSendEventsToSuccessTopicWithStampingLocationFromLocalStore() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(null);
		Location loc = new Location("IN", "India", "KA", "Karnataka", "Bangalore");
		stub(locationStoreCache.get("0123221617357783046602")).toReturn(loc);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> context = new Gson().fromJson(new Gson().toJson(outputEvent.get("devicedata")), mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertEquals(context.get("countrycode"), "IN");
				assertEquals(context.get("country"), "India");
				assertEquals(context.get("statecode"), "KA");
				assertEquals(context.get("state"), "Karnataka");
				assertEquals(context.get("city"), "Bangalore");
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("device_location_retrieved"), true);
				Map<String, Object> edata = new Gson().fromJson(outputEvent.get("edata").toString(), mapType);
				assertNull(edata.get("loc"));
				return true;
			}
		}));

	}

	@Test
	public void shouldSendEventsToSuccessTopicWithLocation() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		Location loc = new Location("IN", "India", "KA", "Karnataka", "Bangalore","Banglore-Custom","Karnatak-Custom","KA-Custom");
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(loc);
		stub(locationEngineMock.locationCache()).toReturn(locationCacheMock);

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationStoreMock, locationEngineMock);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> context = new Gson().fromJson(outputEvent.get("devicedata").toString(), mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertEquals(context.get("countrycode"), "IN");
				assertEquals(context.get("country"), "India");
				assertEquals(context.get("statecode"), "KA");
				assertEquals(context.get("state"), "Karnataka");
				assertEquals(context.get("city"), "Bangalore");
				assertEquals(context.get("statecustomcode"), "KA-Custom");
				assertEquals(context.get("districtcustom"), "Banglore-Custom");
				assertEquals(context.get("statecustomname"), "Karnatak-Custom");
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(flags.get("device_location_retrieved"), true);
				return true;
			}
		}));
	}

	@Test
	public void shouldSendEventToFailedTopicIfEventIsNotParseable() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.UNPARSABLE_START_EVENT);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
	}

	@Test
	public void shouldSendEventToMalformedTopicIfEventIsAnyRandomString() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.ANY_STRING);
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

	@Test
	public void shouldSendEventsToSuccessTopicWithUserLocation() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		Location loc1 = new Location("IN", "India", "KA", "Karnataka", "Bangalore");
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(loc1);
		Location loc2 = new Location(null, null, null, "Tamil Nadu", null, "Chennai");
		String userId = "393407b1-66b1-4c86-9080-b2bce9842886";
		stub(userLocationCacheMock.getLocationByUser(userId)).toReturn(loc2);
		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationStoreMock, locationEngine);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> userloc = new Gson().fromJson(new Gson().toJson(outputEvent.get("userdata")), mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertEquals(userloc.get("state"), "Tamil Nadu");
				assertEquals(userloc.get("district"), "Chennai");
				return true;
			}
		}));
	}

	@Test
	public void shouldNotAddUserLocationIfActorTypeIsNotUser() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_ACTOR_AS_SYSTEM);
		Location loc1 = new Location("IN", "India", "KA", "Karnataka", "Bangalore");
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(loc1);
		Location loc2 = new Location(null, null, null, "Tamil Nadu", null, "Chennai");
		String userId = "393407b1-66b1-4c86-9080-b2bce9842886";
		stub(userLocationCacheMock.getLocationByUser(userId)).toReturn(loc2);
		stub(locationEngineMock.getLocation("0123221617357783046602")).toReturn(loc2);

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationStoreMock, locationEngineMock);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Object userloc = outputEvent.get("userdata");
				assertEquals(userloc, null);
				return true;
			}
		}));
	}

	@Test
	public void shouldFallbackToIPLocationIfUserLocationIsNotResolved() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		Location loc1 = new Location("IN", "India", "KA", "Karnataka", null, "Mysore");
		stub(locationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(null);
		String userId = "393407b1-66b1-4c86-9080-b2bce9842886";
		stub(userLocationCacheMock.getLocationByUser(userId)).toReturn(null);
		stub(locationEngineMock.getLocation("0123221617357783046602")).toReturn(loc1);
		stub(locationEngineMock.locationCache()).toReturn(locationCacheMock);

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationStoreMock, locationEngineMock);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> userloc = new Gson().fromJson(new Gson().toJson(outputEvent.get("userdata")), mapType);
				assertEquals(outputEvent.get("ver"), "3.0");
				assertEquals(userloc.get("state"), "Karnataka");
				assertEquals(userloc.get("district"), "Mysore");
				return true;
			}
		}));
	}


}
