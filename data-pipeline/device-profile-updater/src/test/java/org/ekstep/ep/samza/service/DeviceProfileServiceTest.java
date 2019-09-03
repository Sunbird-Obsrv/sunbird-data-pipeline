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

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        cassandraConnectMock=mock(CassandraConnect.class);

        deviceProfileUpdaterSinkMock = mock(DeviceProfileUpdaterSink.class);
        configMock = mock(Config.class);
        stub(redisConnectMock.getConnection(deviceStoreId)).toReturn(deviceJedisMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.deviceStore.id", deviceStoreId)).toReturn(deviceStoreId);
        stub(configMock.get("input.device.topic.name","device.profile")).toReturn("device.profile");

        stub(configMock.get("cassandra.keyspace")).toReturn("device_db");
        stub(configMock.get("cassandra.device_profile_table")).toReturn("device_profile");
        stub(configMock.get("redis.database.deviceLocationStore.id")).toReturn("1");
        stub(configMock.get("location.db.redis.key.expiry.seconds")).toReturn("86400");
        stub(configMock.get("cache.unresolved.location.key.expiry.seconds")).toReturn("3600");
        deviceProfileUpdaterService = new DeviceProfileUpdaterService(configMock, redisConnectMock, cassandraConnectMock);

        jedisMock.flushAll();
    }

    @Test
    public void shouldupdateDeviceCacheToRedis() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "device.profile", new Partition(0)));
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);

        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.DEVICE_PROFILE_DETAILS, Map.class);
        String device_id = (String) event.get("device_id");
        jedisMock.set(device_id,"232455");

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        String cachedData = jedisMock.get(device_id);
        assertEquals("232455", cachedData);
    }

}
