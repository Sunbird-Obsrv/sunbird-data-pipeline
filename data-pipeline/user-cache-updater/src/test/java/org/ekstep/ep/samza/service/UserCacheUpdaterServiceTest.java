package org.ekstep.ep.samza.service;

import com.datastax.driver.core.Row;
import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.Fixtures.EventFixture;
import org.ekstep.ep.samza.task.UserCacheUpdaterConfig;
import org.ekstep.ep.samza.task.UserCacheUpdaterSink;
import org.ekstep.ep.samza.task.UserCacheUpdaterSource;
import org.ekstep.ep.samza.task.UserCacheUpdaterTask;
import org.ekstep.ep.samza.core.BaseCacheUpdaterService;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class UserCacheUpdaterServiceTest {

    private RedisConnect redisConnectMock;
    private CassandraConnect cassandraConnectMock;
    private IncomingMessageEnvelope envelopeMock;
    private TaskContext contextMock;
    private JobMetrics jobMetricsMock;
    private TaskCoordinator taskCoordinator;
    private MessageCollector messageCollector;
    private Jedis jedisMock = new MockJedis("test");
    private UserCacheUpdaterService userCacheUpdaterService;
    private BaseCacheUpdaterService basecacheUpdater;
    private UserCacheUpdaterSink userCacheUpdaterSinkMock;
    private UserCacheUpdaterConfig userCacheUpdaterConfig;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private Config configMock;
    private Integer userStoreId = 4;
    private UserCacheUpdaterTask userCacheUpdaterTask;

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        userCacheUpdaterSinkMock = mock(UserCacheUpdaterSink.class);
        configMock = mock(Config.class);
        cassandraConnectMock = mock(CassandraConnect.class);
        contextMock = mock(TaskContext.class);
        jobMetricsMock = mock(JobMetrics.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        taskCoordinator = mock(TaskCoordinator.class);
        messageCollector = mock(MessageCollector.class);
        counter = mock(Counter.class);

        stub(redisConnectMock.getConnection(userStoreId)).toReturn(jedisMock);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.userStore.id", userStoreId)).toReturn(userStoreId);
        stub(configMock.getInt("location.db.redis.key.expiry.seconds", 86400)).toReturn(86400);
        stub(configMock.get("user.signin.type.default", "Anonymous")).toReturn("Anonymous");
        stub(configMock.getList("user.selfsignedin.typeList", Arrays.asList("google", "self"))).toReturn(Arrays.asList("google", "self"));
        stub(configMock.getList("user.validated.typeList", Arrays.asList("sso"))).toReturn(Arrays.asList("sso"));
        stub(configMock.get("user.login.type.default", "NA")).toReturn("NA");
        stub(configMock.get("user.self-siginin.key", "Self-Signed-In")).toReturn("Self-Signed-In");
        stub(configMock.get("user.valid.key", "Validated")).toReturn("Validated");
        stub(configMock.get("middleware.cassandra.host", "127.0.0.1")).toReturn("");
        stub(configMock.get("middleware.cassandra.port", "9042")).toReturn("9042");
        stub(configMock.get("middleware.cassandra.keyspace", "sunbird")).toReturn("sunbird");
        stub(configMock.get("middleware.cassandra.user_table","user")).toReturn("user");
        stub(configMock.get("middleware.cassandra.location_table","location")).toReturn("location");

        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        userCacheUpdaterConfig=new UserCacheUpdaterConfig(configMock);
        userCacheUpdaterService = new UserCacheUpdaterService(userCacheUpdaterConfig, redisConnectMock, cassandraConnectMock, jobMetricsMock);
        jedisMock.flushAll();
    }

    @Test
    public void shouldNotUpdateCacheForInvalidEvent() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.ANY_STRING);
        userCacheUpdaterTask = new UserCacheUpdaterTask(configMock, contextMock, cassandraConnectMock, redisConnectMock);
        userCacheUpdaterTask.process(envelopeMock,messageCollector,taskCoordinator);
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);

        verify(userCacheUpdaterSinkMock, times(1)).error();
    }

    @Test
    public void shouldAddLocationDetailsToCache() {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_METADATA_UPDATED);
        Gson gson = new Gson();
        String userId = "52226956-61d8-4c1b-b115-c660111866d3";
        jedisMock.select(userStoreId);
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false,\"usersignintype\":\"Self-Signed-In\",\"userlogintype\":\"NA\", \"locationids\":[\'8952478975387\']}");
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(5, parsedData.size());
        assertEquals("dikshacustodian",parsedData.get("channel"));
        assertEquals(false, parsedData.get("phoneverified"));
        assertEquals("Self-Signed-In", parsedData.get("usersignintype"));
        assertEquals( "NA", parsedData.get("userlogintype"));
        assertEquals(Arrays.asList("8952478975387"), parsedData.get("locationids"));
    }

    @Test
    public void shouldNotUpdateCacheForAnonymousUsers() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT3);
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
    }

    @Test
    public void shouldMarkEventSkippedForNullUserId() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_NULL_USERID);
        userCacheUpdaterTask = new UserCacheUpdaterTask(configMock, contextMock, cassandraConnectMock,redisConnectMock);
        userCacheUpdaterTask.process(envelopeMock,messageCollector,taskCoordinator);
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
        verify(userCacheUpdaterSinkMock, times(1)).markSkipped();
    }

    @Test public void shouldupdateLocationDetails() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT);
        userCacheUpdaterTask = new UserCacheUpdaterTask(configMock, contextMock, cassandraConnectMock,redisConnectMock);
        userCacheUpdaterTask.process(envelopeMock,messageCollector,taskCoordinator);

        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
    }

    @Test
    public void shouldUpdateUserSignInDetailsinCacheFORAUDIT() {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_SIGINTYPE);
        Gson gson = new Gson();
        String userId = "89490534-126f-4f0b-82ac-3ff3e49f3468";
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
        Map<String, Object> userData = userCacheUpdaterService.updateUserCache(source.getEvent(), userId, userCacheUpdaterSinkMock);

        jedisMock.set(userId, gson.toJson(userData));

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals("Self-Signed-In", parsedData.get("usersignintype"));
    }

    @Test
    public void shouldUpdateUserDetailsinCacheFORAUDIT() {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_SIGN_IN);
        Gson gson = new Gson();
        String userId = "89490534-126f-4f0b-82ac-3ff3e49f3468";
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
        Map<String, Object> userData = userCacheUpdaterService.updateUserCache(source.getEvent(), userId, userCacheUpdaterSinkMock);
        jedisMock.set(userId, gson.toJson(userData));

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals("Validated", parsedData.get("usersignintype"));
    }

    @Test
    public void shouldUpdateUserLoginInTYpeDetailsinCacheFORAUDIT() {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_LOGININTYPE);
        Gson gson = new Gson();
        String userId = "3b46b4c9-3a10-439a-a2cb-feb5435b3a0d";
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
        Map<String, Object> userData = userCacheUpdaterService.updateUserCache(source.getEvent(), userId, userCacheUpdaterSinkMock);
        jedisMock.set(userId, gson.toJson(userData));

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals("student", parsedData.get("userlogintype"));
    }

    @Test
    public void shouldNotUpdateCacheWithMetadataChangesAndLocationFORAUDIT() {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_METADATA_UPDATED);
        Gson gson = new Gson();
        String userId = "52226956-61d8-4c1b-b115-c660111866d3";
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false}");
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = new HashMap<>();
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(0, parsedData.size());
        verify(cassandraConnectMock, times(0)).find(anyString());
    }

    @Test
    public void shouldUpdateCache() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_SIGINTYPE);
        userCacheUpdaterTask = new UserCacheUpdaterTask(configMock, contextMock, cassandraConnectMock,redisConnectMock);
        userCacheUpdaterTask.process(envelopeMock,messageCollector,taskCoordinator);

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_SIGIN_TYPE);
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
    }

    @Test
    public void shouldUpdateCacheWithMetadataChangesAndLocationFORAUDIT() {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_METADATA_UPDATED);
        Gson gson = new Gson();
        String userId = "52226956-61d8-4c1b-b115-c660111866d3";
        jedisMock.select(userStoreId);
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false,\"usersignintype\":\"Self-Signed-In\",\"userlogintype\":\"NA\"}");
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(4, parsedData.size());
        assertEquals(parsedData.get("channel"), "dikshacustodian");
        assertEquals(parsedData.get("phoneverified"), false);
        assertEquals(parsedData.get("usersignintype"), "Self-Signed-In");
        assertEquals(parsedData.get("userlogintype"), "NA");
    }
}
