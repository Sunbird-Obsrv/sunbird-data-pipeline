package org.ekstep.ep.samza.task;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.commons.lang.StringUtils;
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
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import redis.clients.jedis.Jedis;
import com.fiftyonred.mock_jedis.MockJedis;
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
	private Jedis jedisMock = new MockJedis("test");

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
		RedisConnect redisConnectMock = mock(RedisConnect.class);

		deviceProfileCacheMock = mock(DeviceProfileCache.class);

		stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
		stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
		stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);
		stub(configMock.getInt("redis.database.userStore.id", 4)).toReturn(4);

		stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
		stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
		stub(envelopeMock.getOffset()).toReturn("2");
		stub(envelopeMock.getSystemStreamPartition())
				.toReturn(new SystemStreamPartition("kafka", "input.topic", new Partition(1)));
		stub(redisConnectMock.getConnection(4)).toReturn(jedisMock);

		telemetryLocationUpdaterTask = new TelemetryLocationUpdaterTask(configMock, contextMock, deviceProfileCacheMock, redisConnectMock);
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

	@Test
	public void shouldSendEventsToSuccessTopicWithUserProfileLocationAsDerived() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		// Map<String, String> uaspec = new HashMap<>();
		Map<String, String> device_spec = new HashMap<>();
		Long first_access = 1559484698000L;
		device_spec.put("os", "Android 6.0");
		device_spec.put("make", "Motorola XT1706");
		/*
		uaspec.put("agent", "Mozilla");
		uaspec.put("ver", "5.0");
		uaspec.put("system", "iPad");
		uaspec.put("platform", "AppleWebKit/531.21.10");
		uaspec.put("raw", "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)");
		*/
		DeviceProfile deviceProfile = new DeviceProfile
				("IN", "India", "KA", "Karnataka",
						"Bangalore", "Banglore-Custom", "Karnatak-Custom",
						"KA-Custom", device_spec, first_access,"Bangalore",
						"Karnataka");
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(deviceProfile);

		jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"type\":\"Registered\",\"state\":\"Karnataka\"}");
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() { }.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				Map<String, Object> devicedata = new Gson().fromJson(new Gson().toJson(outputEvent.get("devicedata")), mapType);

				assertEquals("Karnataka", devicedata.get("state"));
				assertEquals("Banglore-Custom", devicedata.get("districtcustom"));
				assertEquals("IN", devicedata.get("countrycode"));
				assertEquals("India", devicedata.get("country"));
				assertEquals("KA", devicedata.get("statecode"));
				assertEquals("Bangalore", devicedata.get("city"));
				assertEquals("KA-Custom", devicedata.get("statecustomcode"));
				assertEquals("Karnatak-Custom", devicedata.get("statecustomname"));
				assertEquals("IN-KA", devicedata.get("iso3166statecode"));
//				assertEquals(Long.valueOf("1559484698000"), Long.valueOf(devicedata.get("firstaccess").toString()));

				Map<String, Object> deviceSpec = new Gson().fromJson(new Gson().toJson(devicedata.get("devicespec")), mapType);
				assertEquals("Android 6.0", deviceSpec.get("os"));
				assertEquals("Motorola XT1706", deviceSpec.get("make"));

				/*
				Map<String, Object> uaSpec = new Gson().fromJson(new Gson().toJson(devicedata.get("uaspec")), mapType);
				assertEquals("Mozilla", uaSpec.get("agent"));
				assertEquals("5.0", uaSpec.get("ver"));
				assertEquals("iPad", uaSpec.get("system"));
				assertEquals("AppleWebKit/531.21.10", uaSpec.get("platform"));
				assertEquals("Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)", uaSpec.get("raw"));
				*/

				Map<String, Object> userDeclared = new Gson().fromJson(new Gson().toJson(devicedata.get("userdeclared")), mapType);
				assertEquals("Karnataka", userDeclared.get("state"));
				assertEquals("Bangalore", userDeclared.get("district"));

				Map<String, Object> derivedLocationData = new Gson().fromJson(new Gson().toJson(outputEvent.get("derivedlocationdata")), mapType);
				assertEquals("Karnataka", derivedLocationData.get("state"));
				assertEquals("Bengaluru", derivedLocationData.get("district"));
				assertEquals("user-profile", derivedLocationData.get("from"));

				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(true, flags.get("device_profile_retrieved"));
				assertEquals(true, flags.get("derived_location_retrieved"));
				return true;
			}
		}));
	}

	@Test
	public void shouldSendEventsToSuccessTopicWithUserDeclaredLocationAsDerived() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		DeviceProfile deviceProfile = new DeviceProfile
				("IN", "India", "KA", "Karnataka",
						"Bangalore", "Banglore-Custom", "Karnatak-Custom",
						"KA-Custom", new HashMap<>(), 0L,"Bangalore",
						"Karnataka");
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(deviceProfile);

		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() { }.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				Map<String, Object> devicedata = new Gson().fromJson(new Gson().toJson(outputEvent.get("devicedata")), mapType);

				Map<String, Object> deviceSpec = new Gson().fromJson(new Gson().toJson(devicedata.get("devicespec")), mapType);
				assertTrue(deviceSpec.isEmpty());

				// Map<String, Object> uaSpec = new Gson().fromJson(new Gson().toJson(devicedata.get("uaspec")), mapType);
				// assertEquals(true, uaSpec.isEmpty());

				Map<String, Object> userDeclared = new Gson().fromJson(new Gson().toJson(devicedata.get("userdeclared")), mapType);
				assertEquals("Karnataka", userDeclared.get("state"));
				assertEquals("Bangalore", userDeclared.get("district"));

				Map<String, Object> derivedLocationData = new Gson().fromJson(new Gson().toJson(outputEvent.get("derivedlocationdata")), mapType);
				assertEquals("Karnataka", derivedLocationData.get("state"));
				assertEquals("Bangalore", derivedLocationData.get("district"));
				assertEquals("user-declared", derivedLocationData.get("from"));

				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(true, flags.get("device_profile_retrieved"));
				assertEquals(true, flags.get("derived_location_retrieved"));
				return true;
			}
		}));
	}

	@Test
	public void shouldSendEventsToSuccessTopicWithIpLocationAsDerived() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		DeviceProfile deviceProfile = new DeviceProfile
				("IN", "India", "KA", "Karnataka",
						"Bangalore", "Banglore-Custom", "Karnatak-Custom",
						"KA-Custom", new HashMap<>(), 0L,"",
						"");
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(deviceProfile);

		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() { }.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				assertEquals("3.0", outputEvent.get("ver"));
				Map<String, Object> devicedata = new Gson().fromJson(new Gson().toJson(outputEvent.get("devicedata")), mapType);

				Map<String, Object> deviceSpec = new Gson().fromJson(new Gson().toJson(devicedata.get("devicespec")), mapType);
				assertTrue(deviceSpec.isEmpty());

				// Map<String, Object> uaSpec = new Gson().fromJson(new Gson().toJson(devicedata.get("uaspec")), mapType);
				// assertEquals(true, uaSpec.isEmpty());

				Map<String, Object> userDeclared = new Gson().fromJson(new Gson().toJson(devicedata.get("userdeclared")), mapType);
				assertEquals("", userDeclared.get("state"));
				assertEquals("", userDeclared.get("district"));

				Map<String, Object> derivedLocationData = new Gson().fromJson(new Gson().toJson(outputEvent.get("derivedlocationdata")), mapType);
				assertEquals("Karnataka", derivedLocationData.get("state"));
				assertEquals("Banglore-Custom", derivedLocationData.get("district"));
				assertEquals("ip-resolved", derivedLocationData.get("from"));

				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(true, flags.get("device_profile_retrieved"));
				assertEquals(true, flags.get("derived_location_retrieved"));
				return true;
			}
		}));
	}

	@Test
	public void shouldSendEventsToSuccessTopicWithOutDeviceProfile() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		DeviceProfile deviceProfile = null;
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(deviceProfile);

		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() { }.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(false, flags.get("device_profile_retrieved"));
				assertEquals(false, flags.get("derived_location_retrieved"));
				return true;
			}
		}));
	}

	@Test
	public void shouldSendEventsToSuccessTopicWithOutDeviceProfileAndWithUserProfileLocationAsDerived() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
		DeviceProfile deviceProfile = null;
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f")).toReturn(deviceProfile);

		jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"type\":\"Registered\",\"state\":\"Karnataka\"}");
		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() { }.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);

				Map<String, Object> derivedLocationData = new Gson().fromJson(new Gson().toJson(outputEvent.get("derivedlocationdata")), mapType);
				assertEquals("Karnataka", derivedLocationData.get("state"));
				assertEquals("Bengaluru", derivedLocationData.get("district"));
				assertEquals("user-profile", derivedLocationData.get("from"));

				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(false, flags.get("device_profile_retrieved"));
				assertEquals(true, flags.get("derived_location_retrieved"));
				return true;
			}
		}));
	}

	@Test
	public void deviceProfileFromMapShouldNotFailForEmptyValues() {
		Map<String, String> deviceProfileMap = new HashMap<>();
		deviceProfileMap.put("user_declared_state", "Tamil Nadu");
		deviceProfileMap.put("user_declared_district", "Tiruchirappalli");

		DeviceProfile deviceProfile = new DeviceProfile().fromMap(deviceProfileMap);
		assertEquals("Tamil Nadu", deviceProfile.getUserDeclaredState());
		assertEquals("Tiruchirappalli", deviceProfile.getUserDeclaredDistrict());

		assertEquals(0, deviceProfile.getDevicespec().size());
		assertTrue(StringUtils.isEmpty(deviceProfile.getState()));
		assertTrue(StringUtils.isEmpty(deviceProfile.getCity()));
		assertTrue(StringUtils.isEmpty(deviceProfile.getDistrictCustom()));
		assertTrue(StringUtils.isEmpty(deviceProfile.getstateCodeCustom()));
		assertTrue(StringUtils.isEmpty(deviceProfile.getstateCustomName()));
		assertEquals(0L, deviceProfile.getFirstaccess().longValue());

		assertFalse(deviceProfile.isLocationResolved());
		assertFalse(deviceProfile.isDeviceProfileResolved());
	}

	@Test
	public void shouldNotFailIfUserIdIsMissing() throws Exception {
		stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_SUMMARY);
		DeviceProfile deviceProfile = null;
		stub(deviceProfileCacheMock.getDeviceProfileForDeviceId("099988ce86c4dbb9a4057ff611d38427")).toReturn(deviceProfile);

		telemetryLocationUpdaterTask.process(envelopeMock, collectorMock, coordinatorMock);
		Type mapType = new TypeToken<Map<String, Object>>() { }.getType();
		verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
			@Override
			public boolean matches(Object o) {
				OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
				String outputMessage = (String) outgoingMessageEnvelope.getMessage();
				Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
				Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
				assertEquals(false, flags.get("device_profile_retrieved"));
				assertEquals(false, flags.get("derived_location_retrieved"));
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
}
