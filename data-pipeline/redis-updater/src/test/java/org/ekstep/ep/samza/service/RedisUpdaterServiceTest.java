package org.ekstep.ep.samza.service;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.Fixtures.EventFixture;
import org.ekstep.ep.samza.task.RedisUpdaterSink;
import org.ekstep.ep.samza.task.RedisUpdaterSource;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RedisUpdaterServiceTest {

    private RedisConnect redisConnectMock;
    private CassandraConnect cassandraConnectMock;
    private IncomingMessageEnvelope envelopeMock;
    private JobMetrics jobMetricsMock;
    private Jedis jedisMock = new MockJedis("test");
    private RedisUpdaterService redisUpdaterService;
    private RedisUpdaterSink redisUpdaterSinkMock;
    private Config configMock;
    private Integer contentStoreId = 5;
    private Integer dialCodeStoreId = 6;
    private Integer userStoreId = 4;

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        redisUpdaterSinkMock = mock(RedisUpdaterSink.class);
        configMock = mock(Config.class);
        cassandraConnectMock = mock(CassandraConnect.class);
        jobMetricsMock = mock(JobMetrics.class);

        stub(redisConnectMock.getConnection(contentStoreId)).toReturn(jedisMock);
        stub(redisConnectMock.getConnection(dialCodeStoreId)).toReturn(jedisMock);
        stub(redisConnectMock.getConnection(userStoreId)).toReturn(jedisMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.contentStore.id", contentStoreId)).toReturn(contentStoreId);
        stub(configMock.getInt("redis.database.dialCodeStore.id", dialCodeStoreId)).toReturn(dialCodeStoreId);
        stub(configMock.getInt("redis.database.userStore.id", userStoreId)).toReturn(userStoreId);
        stub(configMock.getInt("location.db.redis.key.expiry.seconds", 86400)).toReturn(86400);
        stub(configMock.get("input.audit.topic.name", "telemetry.audit")).toReturn("telemetry.audit");
        stub(configMock.get("user.signin.type.default", "Anonymous")).toReturn("Anonymous");
        stub(configMock.getList("user.selfsignedin.typeList", Arrays.asList("google", "self"))).toReturn(Arrays.asList("google", "self"));
        stub(configMock.getList("user.validated.typeList", Arrays.asList("sso"))).toReturn(Arrays.asList("sso"));
        stub(configMock.get("user.login.type.default", "NA")).toReturn("NA");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "learning.graph.events", new Partition(0)));
        stub(configMock.get("user.self-siginin.key", "Self-Signed-In")).toReturn("Self-Signed-In");
        stub(configMock.get("user.valid.key", "Validated")).toReturn("Validated");
        stub(configMock.get("middleware.cassandra.host", "127.0.0.1")).toReturn("");
        stub(configMock.get("middleware.cassandra.port", "9042")).toReturn("9042");
        stub(configMock.get("middleware.cassandra.keyspace", "sunbird")).toReturn("sunbird");
        stub(configMock.get("middleware.cassandra.user_table","user")).toReturn("user");
        stub(configMock.get("middleware.cassandra.location_table","location")).toReturn("location");

        List<String> defaultListValues = new ArrayList<>();
        defaultListValues.add("gradeLevel");
        defaultListValues.add("subject");
        defaultListValues.add("medium");
        defaultListValues.add("language");

        stub(configMock.getList("contentModel.fields.listType", new ArrayList<>()))
                .toReturn(defaultListValues);
        redisUpdaterService = new RedisUpdaterService(configMock, redisConnectMock, cassandraConnectMock, jobMetricsMock);
        jedisMock.flushAll();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateContentsToContentStoreCache() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_CONTENT_EVENT_1);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_CONTENT_EVENT_1, Map.class);
        String contentId = (String) event.get("nodeUniqueId");

        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(5, parsedData.size());
        verify(redisUpdaterSinkMock, times(1)).success();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleStringToList() throws Exception {
        jedisMock.flushAll();

        stub(envelopeMock.getMessage()).toReturn(EventFixture.INCORRECT_LIST_TYPE_FIELDS);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.INCORRECT_LIST_TYPE_FIELDS, Map.class);
        String contentId = (String) event.get("nodeUniqueId");
        // initial data in cache
        jedisMock.set(contentId, "{\"language\": \"English\"}");
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(4, parsedData.size());
        assertEquals(parsedData.get("language") instanceof List, true);
        assertEquals(parsedData.get("subject") instanceof List, true);
        assertEquals(parsedData.get("ageGroup") instanceof List, false);
        assertEquals(parsedData.get("ownershipType") instanceof List, true);
        verify(redisUpdaterSinkMock, times(1)).success();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateCacheIfDataExist() throws Exception {
        jedisMock.flushAll();

        // Update Redis Cache with First Event
        when(envelopeMock.getMessage()).thenReturn(EventFixture.OBJECT_TYPE_CONTENT_EVENT_1, EventFixture.OBJECT_TYPE_CONTENT_EVENT_1_UPDATED);
        redisUpdaterService.process(new RedisUpdaterSource(envelopeMock), redisUpdaterSinkMock);

        // Update the redis cache with Second event with updated fields: channel, status
        // removed fields: ownershipType
        // new fields: visibility
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_CONTENT_EVENT_1_UPDATED, Map.class);
        String contentId = (String) event.get("nodeUniqueId");

        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(5, parsedData.size());
        assertEquals("testbook1", parsedData.get("code"));
        assertEquals("sunbird.portal", parsedData.get("channel"));
        assertEquals("Live", parsedData.get("status"));
        assertEquals("Default", parsedData.get("visibility"));
        assertEquals("TestCollection", parsedData.get("description"));
        assertEquals(null, parsedData.get("ownershipType"));
        verify(redisUpdaterSinkMock, times(2)).success();
    }

    @Test
    public void shouldNotAddToCacheIfKeyValueIsNullOrEmpty() throws Exception {
        jedisMock.flushAll();
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);

        // Update Redis Cache with First Event
        when(envelopeMock.getMessage()).thenReturn(EventFixture.CONTENT_EVENT_EMPTY_NODE_UNIQUEID, EventFixture.CONTENT_EVENT_EMPTY_PROPERTIES);
        redisUpdaterService.process(source, redisUpdaterSinkMock);
        verify(redisUpdaterSinkMock, times(0)).success();
        redisUpdaterService.process(source, redisUpdaterSinkMock);
        verify(redisUpdaterSinkMock, times(0)).success();
        assertEquals(0, jedisMock.keys("*").size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotUpdateEmptyMessageProperties() throws Exception {
        jedisMock.flushAll();
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);

        // Update Redis Cache with First Event
        when(envelopeMock.getMessage()).thenReturn(EventFixture.OBJECT_TYPE_CONTENT_EVENT_1, EventFixture.CONTENT_EVENT_EMPTY_PROPERTIES);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        // Second event with empty properties
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.CONTENT_EVENT_EMPTY_PROPERTIES, Map.class);
        String contentId = (String) event.get("nodeUniqueId");

        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(5, parsedData.size());
        assertEquals("testbook1", parsedData.get("code"));
        assertEquals("in.ekstep", parsedData.get("channel"));
        assertEquals("Draft", parsedData.get("status"));

        assertEquals("TestCollection", parsedData.get("description"));
        ArrayList<String> list = new ArrayList<String>();
        list.add("createdBy");
        assertEquals(list, parsedData.get("ownershipType"));
        verify(redisUpdaterSinkMock, times(2)).success();
    }


    @Test
    @SuppressWarnings("unchecked")
    public void updateDialCodeCache() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_DIAL_CODE_1);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_DIAL_CODE_1, Map.class);
        String dialCode = (String) event.get("nodeUniqueId");

        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(dialCode);
        Map<String, String> cachedObject = gson.fromJson(cachedData, Map.class);
        assertEquals(261351.0, cachedObject.get("dialcode_index"));
        assertEquals("YC9EP8", cachedObject.get("identifier"));
        assertEquals("b00bc992ef25f1a9a8d63291e20efc8d", cachedObject.get("channel"));
        assertEquals("do_112692236142379008136", cachedObject.get("batchcode"));
        assertEquals(null, cachedObject.get("publisher"));
        assertEquals("2019-02-05T05:40:56.762", cachedObject.get("generated_on"));
        assertEquals("Draft", cachedObject.get("status"));
        verify(redisUpdaterSinkMock, times(1)).success();
    }

    @Ignore
    public void shouldHandleJedisConnectionFail() throws Exception {
        jedisMock.flushAll();
        when(jedisMock.get("YC9EP8")).thenThrow(new JedisException("RedisException"));
        when(envelopeMock.getMessage()).thenReturn(EventFixture.OBJECT_TYPE_DIAL_CODE_1, EventFixture.OBJECT_TYPE_CONTENT_EVENT_1);
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);

        // Dialcode Event
        redisUpdaterService.process(source, redisUpdaterSinkMock);
        // Content Event
        // redisUpdaterService.process(source, redisUpdaterSinkMock);
        verify(redisUpdaterSinkMock, times(1)).error();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddOtherObjectTypeToCache() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_CONCEPT_EVENT);

        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_CONCEPT_EVENT, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(conceptId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(1, parsedData.size());
        ArrayList<String> str = new ArrayList<>();
        str.add("English");
        assertEquals(str, parsedData.get("language"));
        verify(redisUpdaterSinkMock, times(1)).success();
    }


    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotAddEventIfItIsEmpty() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EMPTY_LANGUAGE_FIELD_EVENT);

        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.EMPTY_LANGUAGE_FIELD_EVENT, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");
        // initial data in cache
        jedisMock.set(conceptId, "{\"language\": [\"English\"] }");

        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(conceptId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(0, parsedData.size());
        assertEquals(null, parsedData.get("language"));
        verify(redisUpdaterSinkMock, times(1)).success();
    }

    @Test
    public void shouldUpdateUserSignInDetailsinCacheFORAUDIT() {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_SIGINTYPE);
        Gson gson = new Gson();
        String userId = "89490534-126f-4f0b-82ac-3ff3e49f3468";
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false}");
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals("Self-Signed-In", parsedData.get("usersignintype"));
        assertEquals("NA", parsedData.get("userlogintype"));
    }

    @Test
    public void shouldUpdateUserLoginInTYpeDetailsinCacheFORAUDIT() {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_LOGININTYPE);
        Gson gson = new Gson();
        String userId = "3b46b4c9-3a10-439a-a2cb-feb5435b3a0d";
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false}");
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals("Anonymous", parsedData.get("usersignintype"));
        assertEquals("student", parsedData.get("userlogintype"));
    }

    @Test
    public void shouldNotUpdateSiginIntypeWhenUpdatingLoginType() {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_LOGININTYPE);
        Gson gson = new Gson();
        String userId = "3b46b4c9-3a10-439a-a2cb-feb5435b3a0d";
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false,\"usersignintype\":google}");
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        String cachedData = jedisMock.get(userId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals("google", parsedData.get("usersignintype"));
        assertEquals("student", parsedData.get("userlogintype"));
    }

    @Test
    public void shouldNotUpdateCacheWithMetadataChangesAndLocationFORAUDIT() {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT_METADATA_UPDATED);
        Gson gson = new Gson();
        String userId = "52226956-61d8-4c1b-b115-c660111866d3";
        jedisMock.set(userId, "{\"channel\":\"dikshacustodian\",\"phoneverified\":false}");
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        redisUpdaterService.process(source, redisUpdaterSinkMock);

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
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        stub(envelopeMock.getSystemStreamPartition()).toReturn(new SystemStreamPartition("kafka", "telemetry.audit", new Partition(1)));
        redisUpdaterService.process(source, redisUpdaterSinkMock);

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


}
