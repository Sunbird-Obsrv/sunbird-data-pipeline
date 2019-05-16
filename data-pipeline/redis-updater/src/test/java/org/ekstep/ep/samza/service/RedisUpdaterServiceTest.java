package org.ekstep.ep.samza.service;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.ekstep.ep.samza.service.Fixtures.EventFixture;
import org.ekstep.ep.samza.task.RedisUpdaterSink;
import org.ekstep.ep.samza.task.RedisUpdaterSource;
import org.ekstep.ep.samza.util.*;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class RedisUpdaterServiceTest {

    private RedisConnect redisConnectMock;
    private IncomingMessageEnvelope envelopeMock;
    private Jedis jedisMock = new MockJedis("test");
    private RedisUpdaterService redisUpdaterService;
    private RedisUpdaterSink redisUpdaterSinkMock;
    private Config configMock;
    private Integer contentStoreId = 5;
    private Integer dialCodeStoreId = 6;

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        redisUpdaterSinkMock = mock(RedisUpdaterSink.class);
        configMock = mock(Config.class);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.contentStore.id", contentStoreId)).toReturn(contentStoreId);
        stub(configMock.getInt("redis.database.dialCodeStore.id", dialCodeStoreId)).toReturn(dialCodeStoreId);
        stub(configMock.getInt("location.db.redis.key.expiry.seconds", 86400)).toReturn(86400);
        redisUpdaterService = new RedisUpdaterService(configMock, redisConnectMock);
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

        jedisMock.select(contentStoreId);
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

        jedisMock.select(contentStoreId);
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
        jedisMock.select(contentStoreId);
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

        jedisMock.select(contentStoreId);
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
        ArrayList<String> list=new ArrayList<String>();
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
        jedisMock.select(dialCodeStoreId);
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


    @Test
    public void shouldHandleJedisConnectionFail() throws Exception {
        jedisMock.flushAll();
        when(redisConnectMock.getConnection()).thenThrow(new JedisException("connection pool exhausted!"));
        when(envelopeMock.getMessage()).thenReturn(EventFixture.OBJECT_TYPE_DIAL_CODE_1, EventFixture.OBJECT_TYPE_CONTENT_EVENT_1);
        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);

        // Dialcode Event
        redisUpdaterService.process(source, redisUpdaterSinkMock);
        // Content Event
        redisUpdaterService.process(source, redisUpdaterSinkMock);
        verify(redisUpdaterSinkMock, times(2)).error();
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
        jedisMock.select(contentStoreId);
        String cachedData = jedisMock.get(conceptId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(1, parsedData.size());
        assertEquals("English", parsedData.get("language"));
        verify(redisUpdaterSinkMock, times(1)).success();
    }
}
