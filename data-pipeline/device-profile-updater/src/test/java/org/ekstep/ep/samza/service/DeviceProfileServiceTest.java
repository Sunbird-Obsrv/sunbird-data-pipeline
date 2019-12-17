package org.ekstep.ep.samza.service;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.service.Fixtures.EventFixture;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterTask;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSource;
import org.ekstep.ep.samza.task.DeviceProfileUpdaterSink;
import org.ekstep.ep.samza.util.PostgresConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class DeviceProfileServiceTest {

    private RedisConnect redisConnectMock;
    private PostgresConnect postgresConnectMock;
    private IncomingMessageEnvelope envelopeMock;
    private Jedis jedisMock = new MockJedis("test");
    private Jedis deviceJedisMock = new MockJedis("device");
    private Connection connectionMock;
    private Statement statementMock;
    private Statement statement;
    private DeviceProfileUpdaterService deviceProfileUpdaterService;
    private DeviceProfileUpdaterSink deviceProfileUpdaterSinkMock;
    private Config configMock;
    private Integer deviceStoreId = 2;
    private String postgres_table;
    private Gson gson = new Gson();
    private Type mapType = new TypeToken<Map<String, Object>>() { }.getType();

    @Before
    public void setUp() throws Exception {
        redisConnectMock = mock(RedisConnect.class);
        postgresConnectMock=mock(PostgresConnect.class);

        deviceProfileUpdaterSinkMock = mock(DeviceProfileUpdaterSink.class);
        configMock = mock(Config.class);
        connectionMock = mock(Connection.class);
        statementMock = mock(Statement.class);
        stub(redisConnectMock.getConnection(deviceStoreId)).toReturn(deviceJedisMock);
        stub(postgresConnectMock.getConnection()).toReturn(connectionMock);
        stub(postgresConnectMock.resetConnection()).toReturn(connectionMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.deviceStore.id", deviceStoreId)).toReturn(deviceStoreId);
        stub(configMock.get("input.device.topic.name","device.profile")).toReturn("events_deviceprofile");
        postgres_table = "device_profile";

        EmbeddedPostgres pg = EmbeddedPostgres.start();
        Connection con = pg.getPostgresDatabase().getConnection();
        statement = con.createStatement();

        stub(connectionMock.createStatement()).toReturn(statement);
        statement.execute("CREATE TABLE device_profile(\n" +
                "   device_id text PRIMARY KEY,\n" +
                "   api_last_updated_on TIMESTAMP,\n" +
                "    avg_ts float,\n" +
                "    city TEXT,\n" +
                "    country TEXT,\n" +
                "    country_code TEXT,\n" +
                "    device_spec json,\n" +
                "    district_custom TEXT,\n" +
                "    fcm_token TEXT,\n" +
                "    first_access TIMESTAMP,\n" +
                "    last_access TIMESTAMP,\n" +
                "    producer_id TEXT,\n" +
                "    state TEXT,\n" +
                "    state_code TEXT,\n" +
                "    state_code_custom TEXT,\n" +
                "    state_custom TEXT,\n" +
                "    total_launches bigint,\n" +
                "    total_ts float,\n" +
                "    uaspec json,\n" +
                "    updated_date TIMESTAMP,\n" +
                "    user_declared_district TEXT,\n" +
                "    user_declared_state TEXT)");

        stub(configMock.get("cassandra.keyspace")).toReturn("device_db");
        stub(configMock.get("cassandra.device_profile_table")).toReturn("device_profile");
        stub(configMock.get("redis.database.deviceLocationStore.id")).toReturn("1");
        stub(configMock.get("location.db.redis.key.expiry.seconds")).toReturn("86400");
        stub(configMock.get("cache.unresolved.location.key.expiry.seconds")).toReturn("3600");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "events_deviceprofile", new Partition(0)));
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);
        stub(configMock.get("postgres.device_profile_table","device_profile")).toReturn("device_profile");
        deviceProfileUpdaterService = new DeviceProfileUpdaterService(configMock, redisConnectMock, postgresConnectMock);

        jedisMock.flushAll();
    }

    @Test
    public void shouldupdateCache() throws Exception {
        stub(connectionMock.createStatement()).toReturn(statementMock);
        jedisMock.flushAll();

        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> event = gson.fromJson(EventFixture.DEVICE_PROFILE_DETAILS, mapType);
        String device_id = event.get("device_id");

        jedisMock.hmset(device_id, event);

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        Map<String,String> cachedData = jedisMock.hgetAll(device_id);

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
        stub(connectionMock.createStatement()).toReturn(statementMock);
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
        stub(connectionMock.createStatement()).toReturn(statementMock);
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
    public void shouldUpdateFirstAccessifNotPresent() throws Exception{
        stub(connectionMock.createStatement()).toReturn(statementMock);
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);
        Map<String, String> newDeviceData = gson.fromJson(EventFixture.DEVICE_PROFILE_DETAILS, mapType);
        String deviceId = newDeviceData.get("device_id");

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        Map<String, String> data = deviceJedisMock.hgetAll(deviceId);
        assertEquals("1568377184000", data.get("firstaccess"));
    }

    @Test(expected = SQLException.class)
    public void shouldHandlePostgresException() throws Exception{
        when(connectionMock.createStatement()).thenThrow(SQLException.class);
        when(postgresConnectMock.resetConnection()).thenReturn(connectionMock);
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);
    }

    @Test
    public void shouldAddDeviceDataToPostgres() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        ResultSet rs = statement.executeQuery("SELECT * from device_profile where device_id='232455'");
        while(rs.next()) {
            assertEquals("Bengaluru", rs.getString("city"));
            assertEquals("Karnataka", rs.getString("state"));
            assertEquals("India", rs.getString("country"));
            assertEquals("IN", rs.getString("country_code"));
            assertEquals("dev.sunbird.portal", rs.getString("producer_id"));
            assertEquals("Bengaluru", rs.getString("user_declared_district"));
            assertEquals("Karnataka", rs.getString("user_declared_state"));
        }

        verify(deviceProfileUpdaterSinkMock, times(1)).deviceDBUpdateSuccess();
    }

    @Test
    public void shouldNotAddFirstAccessIfPresentInPostgres()throws Exception{
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_PROFILE_DETAILS);

        String query = String.format("INSERT INTO %s (device_id, first_access) VALUES ('232455','2019-09-24 01:03:04.999');", postgres_table);
        statement.execute(query);

        DeviceProfileUpdaterSource source = new DeviceProfileUpdaterSource(envelopeMock);
        deviceProfileUpdaterService.process(source, deviceProfileUpdaterSinkMock);

        ResultSet rs=statement.executeQuery(String.format("SELECT first_access FROM %s WHERE device_id='232455';", postgres_table));
        while(rs.next()) {
            assertEquals("2019-09-24 01:03:04.999", rs.getString(1));
        }
    }
}
