package org.ekstep.ep.samza.service;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.service.Fixtures.EventFixture;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterTask;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSource;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSink;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class DeviceProfileServiceTest {

    private RedisConnect redisConnectMock;
    private CassandraConnect cassandraConnectMock;
    private IncomingMessageEnvelope envelopeMock;
    private Jedis jedisMock = new MockJedis("test");
    private Jedis deviceJedisMock = new MockJedis("device");
    private DeviceProfileUpdaterService deviceProfileUpdaterService;
    private DeviceProfileUpdaterSink deviceProfileUpdaterSinkMock;
    private Config configMock;
    private Integer deviceStoreId = 2;
    private Gson gson = new Gson();
    private Type mapType = new TypeToken<Map<String, Object>>() { }.getType();

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        cassandraConnectMock=mock(CassandraConnect.class);

        deviceProfileUpdaterSinkMock = mock(DeviceProfileUpdaterSink.class);
        configMock = mock(Config.class);
        stub(redisConnectMock.getConnection(deviceStoreId)).toReturn(deviceJedisMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.deviceStore.id", deviceStoreId)).toReturn(deviceStoreId);
        stub(configMock.get("input.device.topic.name","device.profile")).toReturn("events_deviceprofile");

        stub(configMock.get("cassandra.keyspace")).toReturn("device_db");
        stub(configMock.get("cassandra.device_profile_table")).toReturn("device_profile");
        stub(configMock.get("redis.database.deviceLocationStore.id")).toReturn("1");
        stub(configMock.get("location.db.redis.key.expiry.seconds")).toReturn("86400");
        stub(configMock.get("cache.unresolved.location.key.expiry.seconds")).toReturn("3600");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "events_deviceprofile", new Partition(0)));
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);
        deviceProfileUpdaterService = new DeviceProfileUpdaterService(configMock, redisConnectMock, cassandraConnectMock);

        jedisMock.flushAll();
    }

    @Test
    public void shouldupdateCache() throws Exception {
        jedisMock.flushAll();

        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> event = gson.fromJson(EventFixture.DEVICE_PROFILE_DETAILS, mapType);
        String device_id = event.get("device_id");

        jedisMock.hmset(device_id, event);

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        Map<String,String> cachedData = jedisMock.hgetAll(device_id);
        System.out.println(cachedData);

        assertEquals("232455", cachedData.get("device_id"));
        assertEquals("Bengaluru", cachedData.get("city"));
        assertEquals("Karnataka", cachedData.get("state"));
        assertEquals("dev.sunbird.portal", cachedData.get("producer_id"));
        assertEquals("IN", cachedData.get("country_code"));
        assertEquals("Bengaluru",cachedData.get("user_declared_district"));
        assertEquals("Karnataka",cachedData.get("user_declared_state"));

        verify(deviceProfileUpdaterSinkMock, times(1)).deviceCacheUpdateSuccess();
    }

    @Test
    public void shouldupdateDB() throws Exception {
        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        verify(deviceProfileUpdaterSinkMock, times(1)).deviceDBUpdateSuccess();
    }

    @Test
    public void shouldNotUpdateFordidNull() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_WITH_NO_DEVICE_ID);

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        verify(deviceProfileUpdaterSinkMock, times(1)).failed();

    }

    @Test
    public void shouldNotUpdateFirstAccessifPresent() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);
        Map<String, String> newDeviceData = gson.fromJson(EventFixture.DEVICE_PROFILE_DETAILS, mapType);
        Map<String, String> deviceDetails = new HashMap<>();
        deviceDetails.put("firstaccess","156990957889");
        deviceJedisMock.hmset(newDeviceData.get("device_id"), deviceDetails);

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        Map<String, String> data = deviceJedisMock.hgetAll(newDeviceData.get("device_id"));
        assertEquals("156990957889", data.get("firstaccess"));
    }

    @Test
    public void shouldUpdateFirstAccessifNotPresent() {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);
        Map<String, String> newDeviceData = gson.fromJson(EventFixture.DEVICE_PROFILE_DETAILS, mapType);
        String deviceId = newDeviceData.get("device_id");

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        Map<String, String> data = deviceJedisMock.hgetAll(deviceId);
        assertEquals("1568377184000", data.get("firstaccess"));
    }
}
