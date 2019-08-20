package org.ekstep.ep.samza.task;

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
import org.ekstep.ep.samza.domain.DeviceProfile;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.DeviceProfileCache;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class TelemetryLocationUpdaterTaskTest {

	private static final String SUCCESS_TOPIC = "telemetry.with_location";
	private static final String FAILED_TOPIC = "telemetry.failed";
	private static final String MALFORMED_TOPIC = "telemetry.malformed";

	private MessageCollector collectorMock;
	private TaskCoordinator coordinatorMock;
	private IncomingMessageEnvelope envelopeMock;
	private DeviceProfileCache deviceProfileCacheMock;
	private TelemetryLocationUpdaterTask telemetryLocationUpdaterTask;

	@SuppressWarnings("unchecked")
	@Before
	public void setUp() {
		collectorMock = mock(MessageCollector.class);
		TaskContext contextMock = mock(TaskContext.class);
		MetricsRegistry metricsRegistry = mock(MetricsRegistry.class);
		Counter counter = mock(Counter.class);
		coordinatorMock = mock(TaskCoordinator.class);
		envelopeMock = mock(IncomingMessageEnvelope.class);
		Config configMock = mock(Config.class);

		deviceProfileCacheMock = mock(DeviceProfileCache.class);

		stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
		stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
		stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);

		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
		stub(envelopeMock.getOffset()).toReturn("2");
		stub(envelopeMock.getSystemStreamPartition())
				.toReturn(new SystemStreamPartition("kafka", "input.topic", new Partition(1)));

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, deviceProfileCacheMock);
	}

	@Test
	public void shouldSendEventsToSuccessTopicIfDidIsNull() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() {
		}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				assertFalse(outputEvent.containsKey("devicedata"));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(false, flags.get("device_profile_retrieved"));
				return true;
			}
		}));

	}

	/*
	@Test
	public void shouldSendEventsToSuccessTopicWithLocationFromDB() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		Map<String, String> resultFromCache = new HashMap<>();

		stub(configMock.get("cassandra.keyspace")).toReturn("device_db");
		stub(configMock.get("cassandra.device_profile_table")).toReturn("device_profile");
		stub(configMock.get("redis.database.deviceLocationStore.id")).toReturn("1");
		stub(configMock.get("location.db.redis.key.expiry.seconds")).toReturn("86400");
		stub(configMock.get("cache.unresolved.location.key.expiry.seconds")).toReturn("3600");
		// RedisConnect redisConnectMock = Mockito.mock(RedisConnect.class);
		// CassandraConnect cassandraConnectMock = Mockito.mock(CassandraConnect.class);

		DeviceLocationCache cache = Mockito.spy(new DeviceLocationCache(configMock, mock(JobMetrics.class)));
		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, cache);
		stub(deviceLocationCacheMock.getLocationFromCache("68dfc64a7751ad47617ac1a4e0531fb761ebea6f"))
				.toReturn(resultFromCache);
		DeviceProfile location = new DeviceProfile("IN", "India", "KA", "Karnataka",
				"Bangalore","Banglore-Custom","Karnatak-Custom","KA-Custom");
		stub(deviceLocationCacheMock.getLocationFromDeviceProfileDB("68dfc64a7751ad47617ac1a4e0531fb761ebea6f"))
				.toReturn(location);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> context = new Gson().fromJson(outputEvent.get("devicedata").toString(), mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				assertEquals("IN", context.get("countrycode"));
				assertEquals("India", context.get("country"));
				assertEquals("KA", context.get("statecode"));
				assertEquals("Karnataka", context.get("state"));
				assertEquals("Bangalore", context.get("city"));
				assertEquals("KA-Custom", context.get("statecustomcode"));
				assertEquals("Banglore-Custom", context.get("districtcustom"));
				assertEquals("Karnatak-Custom", context.get("statecustomname"));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(true, flags.get("device_location_retrieved"));
				return true;
			}
		}));
		
	}

	*/

	/*
	@Test
	public void shouldSendEventsToSuccessTopicWithStampingLocationFromChannelAPI() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(deviceLocationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		DeviceProfile loc = new DeviceProfile();
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


	/*
	@Test
	@Ignore
	public void shouldSendEventsToSuccessTopicWithStampingLocationFromLocalStore() throws Exception {

		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		stub(deviceLocationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(null);
		DeviceProfile loc = new DeviceProfile("IN", "India", "KA", "Karnataka", "Bangalore");
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
				assertEquals("3.0", outputEvent.get("ver"));
				assertEquals("IN", context.get("countrycode"));
				assertEquals("India", context.get("country"));
				assertEquals("KA", context.get("statecode"));
				assertEquals("Karnataka", context.get("state"));
				assertEquals("Bangalore", context.get("city"));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(true, flags.get("device_location_retrieved"));
				Map<String, Object> edata = new Gson().fromJson(outputEvent.get("edata").toString(), mapType);
				assertNull(edata.get("loc"));
				return true;
			}
		}));

	}
	*/

	@Test
	public void shouldSendEventsToSuccessTopicIfFoundInCache() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		Map uaspec = new HashMap();
		Map device_spec = new HashMap();
		Long first_access = 1559484698000L;
		device_spec.put("os", "Android 6.0");
		device_spec.put("make", "Motorola XT1706");
		uaspec.put("agent", "Mozilla");
		uaspec.put("ver", "5.0");
		uaspec.put("system", "iPad");
		uaspec.put("platform", "AppleWebKit/531.21.10");
		uaspec.put("raw", "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)");
		DeviceProfile deviceProfile = new DeviceProfile
				("IN", "India", "KA", "Karnataka",
						"Bangalore", "Banglore-Custom", "Karnatak-Custom",
						"KA-Custom", uaspec, device_spec, first_access);
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(deviceProfile);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() {
		}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				assertTrue(outputMessage.contains("\"countrycode\":\"IN\""));
				assertTrue(outputMessage.contains("\"country\":\"India\""));
				assertTrue(outputMessage.contains("\"statecode\":\"KA\""));
				assertTrue(outputMessage.contains("\"state\":\"Karnataka\""));
				assertTrue(outputMessage.contains("\"city\":\"Bangalore\""));
				assertTrue(outputMessage.contains("\"statecustomcode\":\"KA-Custom\""));
				assertTrue(outputMessage.contains("\"districtcustom\":\"Banglore-Custom\""));
				assertTrue(outputMessage.contains("\"statecustomname\":\"Karnatak-Custom\""));
				assertTrue(outputMessage.contains("\"iso3166statecode\":\"IN-KA\""));
				assertTrue(outputMessage.contains("\"agent\":\"Mozilla\""));
				assertTrue(outputMessage.contains("\"ver\":\"5.0\""));
				assertTrue(outputMessage.contains("\"system\":\"iPad\""));
				assertTrue(outputMessage.contains("\"os\":\"Android 6.0\""));
				assertTrue(outputMessage.contains("\"make\":\"Motorola XT1706\""));
				assertTrue(outputMessage.contains("\"platform\":\"AppleWebKit/531.21.10\""));
				assertTrue(outputMessage.contains("\"raw\":\"Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)\""));
				assertTrue(outputMessage.contains("\"firstaccess\":1559484698000"));
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(true, flags.get("device_profile_retrieved"));
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

	/*
	@Test
	public void shouldSendEventsToSuccessTopicWithUserLocation() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		DeviceProfile loc1 = new DeviceProfile("IN", "India", "KA", "Karnataka", "Bangalore");
		stub(deviceLocationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(loc1);
		DeviceProfile loc2 = new DeviceProfile(null, null, null, "Tamil Nadu", null, "Chennai");
		String userId = "393407b1-66b1-4c86-9080-b2bce9842886";
		stub(userLocationCacheMock.getLocationByUser(userId)).toReturn(loc2);
		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationEngine);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> userloc = new Gson().fromJson(new Gson().toJson(outputEvent.get("userdata")), mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				assertEquals("Tamil Nadu", userloc.get("state"));
				assertEquals("Chennai", userloc.get("district"));
				return true;
			}
		}));
	}
	*/


    /*
	@Test
	public void shouldNotAddUserLocationIfActorTypeIsNotUser() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_ACTOR_AS_SYSTEM);
		DeviceProfile loc1 = new DeviceProfile("IN", "India", "KA", "Karnataka", "Bangalore");
		stub(deviceLocationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(loc1);
		DeviceProfile loc2 = new DeviceProfile(null, null, null, "Tamil Nadu", null, "Chennai");
		String userId = "393407b1-66b1-4c86-9080-b2bce9842886";
		stub(userLocationCacheMock.getLocationByUser(userId)).toReturn(loc2);
		// stub(locationEngineMock.getLocation("0123221617357783046602")).toReturn(loc2);

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationEngineMock);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Object userlocdata = outputEvent.get("userdata");
				assertNull(userlocdata);
				return true;
			}
		}));
	}
	*/

	/*
	@Test
	public void shouldFallbackToIPLocationIfUserLocationIsNotResolved() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		DeviceProfile loc1 = new DeviceProfile("IN", "India", "KA", "Karnataka", null, "Mysore");
		stub(deviceLocationCacheMock.getLocationForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
				"0123221617357783046602")).toReturn(null);
		String userId = "393407b1-66b1-4c86-9080-b2bce9842886";
		stub(userLocationCacheMock.getLocationByUser(userId)).toReturn(null);
		// stub(locationEngineMock.getLocation("0123221617357783046602")).toReturn(loc1);
		stub(locationEngineMock.deviceLocationCache()).toReturn(deviceLocationCacheMock);

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, locationEngineMock);
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> userloc = new Gson().fromJson(new Gson().toJson(outputEvent.get("userdata")), mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				assertEquals("Karnataka", userloc.get("state"));
				assertEquals("Mysore", userloc.get("district"));
				return true;
			}
		}));
	}
	*/


}
