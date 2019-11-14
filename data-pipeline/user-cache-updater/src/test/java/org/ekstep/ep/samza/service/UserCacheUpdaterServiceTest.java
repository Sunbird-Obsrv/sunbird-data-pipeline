package org.ekstep.ep.samza.service;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.querybuilder.QueryBuilder;
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
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
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
    private UserCacheUpdaterSink userCacheUpdaterSinkMock;
    private UserCacheUpdaterConfig userCacheUpdaterConfig;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private Config configMock;
    private Integer userStoreId = 4;
    private JedisException jedisException;
    private UserCacheUpdaterTask userCacheUpdaterTask;

    private static final String MALFORMED_TOPIC = "telemetry.malformed";

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        userCacheUpdaterSinkMock = mock(UserCacheUpdaterSink.class);
        configMock = mock(Config.class);
        cassandraConnectMock = mock(CassandraConnect.class);
//        cassandraConnectMock=new CassandraConnect("localhost",9042);
        contextMock = mock(TaskContext.class);
        jobMetricsMock = mock(JobMetrics.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        jedisException = mock(JedisException.class);
        taskCoordinator=mock(TaskCoordinator.class);
        messageCollector=mock(MessageCollector.class);
        counter=mock(Counter.class);


        stub(redisConnectMock.getConnection(userStoreId)).toReturn(jedisMock);
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
//System.out.println(userCacheUpdaterConfig.userStoreDb());
        userCacheUpdaterService = new UserCacheUpdaterService(userCacheUpdaterConfig, redisConnectMock, cassandraConnectMock, jobMetricsMock);
        jedisMock.flushAll();
    }

    @Test
    public void shouldNotUpdateCacheForInvalidEvent() throws Exception {
        userCacheUpdaterTask = new UserCacheUpdaterTask(configMock, contextMock, cassandraConnectMock,redisConnectMock);
        userCacheUpdaterTask.process(envelopeMock,messageCollector,taskCoordinator);

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT);
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
//        verify(userCacheUpdaterSinkMock, times(1)).error();
    }

    @Test
    public void shouldNotUpdateCacheForAnonymousUsers() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT2);
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
        verify(userCacheUpdaterSinkMock, times(1)).error();
    }

    @Test
    public void shouldUpdateUserSignInDetailsinCacheFORAUDIT() {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_SIGINTYPE);
        Gson gson = new Gson();
        String userId = "89490534-126f-4f0b-82ac-3ff3e49f3468";
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false}");
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);

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
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false}");
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);

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
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false}");
        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);

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
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(parsedData.get("channel"), "dikshacustodian");
        verify(cassandraConnectMock, times(0)).find(anyString());
    }

    @Test
    public void shouldUpdateCacheWithMetadataChangesAndLocationFORAUDIT() {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_METADATA_UPDATED);
        Gson gson = new Gson();
        String userId = "52226956-61d8-4c1b-b115-c660111866d3";
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
        assertEquals(parsedData.get("channel"), "dikshacustodian");
        verify(cassandraConnectMock, times(1)).find(anyString());
    }

    @Test
    public void shouldUpdateCache() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT);


//        row.ge

//stub(row.get("loca"))
//        stub(cassandraConnectMock.find("SELECT * FROM sunbird.user WHERE id='627a431d-4f5c-4adc-812d-1f01c5588555'"))
//                .toReturn(Row);


//        ArrayList<String> list = new ArrayList<>();
//        list.add("6ffc5101-735d-43a3-a400-10f162361cd9");
//        list.add("e70b2313-a73d-4f53-b0a1-7a5f6308a14d");
//
//
//        ResultSet MockResultSet = mock(ResultSet.class);
//        Row row = mock(Row.class);

//        Map<String, Object> result = new HashMap<>();
//        result.put("locationids",list);
//        System.out.println(result);
//
//        List <Row> roeset = mock(List.class);
//        roeset.add(0,row);

//        Row mockRow = mock(Row.class, "locationStateid");
//        Row mockDRow = mock(Row.class, "locationDistrictid");

        Row mockRow = mock(Row.class, "6ffc5101-735d-43a3-a400-10f162361cd9");

//        System.out.println(mockRow.getColumnDefinitions());
        Row mockDRow = mock(Row.class, "e70b2313-a73d-4f53-b0a1-7a5f6308a14d");

        List <Row> rowSet = new ArrayList<>();
        rowSet.add(0, mockRow);
        rowSet.add(1, mockDRow);

        RowSetFactory aFactory = RowSetProvider.newFactory();
        aFactory.createCachedRowSet();
        System.out.println("cached row set factory: "+aFactory);


//        System.out.println("row: "+rowSet);
//        System.out.println(rowSet.get(0));

//        String MetadataQuery = QueryBuilder.select().all()
//                .from("sunbird", "user")
//                .where(QueryBuilder.eq("id", "627a431d-4f5c-4adc-812d-1f01c5588555"))
//                .toString();
////        System.out.println(MetadataQuery);
//
//        System.out.println("cassandra mock connection: "+cassandraConnectMock);
//        stub(cassandraConnectMock.find(MetadataQuery)).toReturn(rowSet);

//        userCacheUpdaterTask = new UserCacheUpdaterTask(configMock, contextMock, cassandraConnectMock,redisConnectMock);
//        userCacheUpdaterTask.process(envelopeMock,messageCollector,taskCoordinator);

//        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
//        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
//
//
//        verify(userCacheUpdaterSinkMock, times(1)).success();
    }

    @Test
    public void shouldImproveCoverage() throws JedisException {
        //    jedisMock.flushAll();
        //      jedisMock.close();
//        redisConnectMock.resetConnection();
//        userCacheUpdaterService = new UserCacheUpdaterService(configMock, redisConnectMock, cassandraConnectMock, jobMetricsMock);

//        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT);
//        String userId = "627a431d-4f5c-4adc-812d-1f01c5588555";
//
//        when(redisConnectMock.getConnection(userStoreId)).thenThrow(jedisException);

//       when(jedisMock.get(userId)).thenThrow(JedisException.class);
//        stub(jedisMock.get(userId)).toThrow(jedisException);
//jedisMock.set(userId, "627a431d-4f5c-4adc-812d-1f01c5588555");

        //  doThrow(new RuntimeException());

//        UserCacheUpdaterSource source = new UserCacheUpdaterSource(envelopeMock);
//        userCacheUpdaterService.process(source, userCacheUpdaterSinkMock);
    }
}
