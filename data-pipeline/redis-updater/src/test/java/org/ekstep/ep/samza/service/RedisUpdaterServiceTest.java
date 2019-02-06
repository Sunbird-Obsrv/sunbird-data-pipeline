package org.ekstep.ep.samza.service;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
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
    private Integer contentStoreId = 2;
    private Integer dialCodeStoreId = 3;

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
        Map<String, String> cachedData = jedisMock.hgetAll(contentId);
        assertEquals(5, cachedData.size());
        verify(redisUpdaterSinkMock, times(1)).success();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateTheCacheIfDataExist() throws Exception {
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
        Map<String, String> cachedData = jedisMock.hgetAll(contentId);
        assertEquals(6, cachedData.size());
        assertEquals("\"testbook1\"", cachedData.get("code"));
        assertEquals("\"sunbird.portal\"", cachedData.get("channel"));
        assertEquals("\"Live\"", cachedData.get("status"));
        assertEquals("\"Default\"", cachedData.get("visibility"));
        assertEquals("\"TestCollection\"", cachedData.get("description"));
        assertEquals("", cachedData.get("ownershipType"));
        verify(redisUpdaterSinkMock, times(2)).success();
    }

    @Test
    public void shouldNotAddToCacheIfKeyAndValueIsNullOrEmpty() throws Exception {
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
    public void shouldNotReplaceTheCacheWhenMessagePropertiesAreEmpty() throws Exception {
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
        Map<String, String> cachedData = jedisMock.hgetAll(contentId);
        assertEquals(5, cachedData.size());
        assertEquals("\"testbook1\"", cachedData.get("code"));
        assertEquals("\"in.ekstep\"", cachedData.get("channel"));
        assertEquals("\"Draft\"", cachedData.get("status"));
        assertEquals("\"TestCollection\"", cachedData.get("description"));
        assertEquals("[\"createdBy\"]", cachedData.get("ownershipType"));
        verify(redisUpdaterSinkMock, times(2)).success();
    }


    @Test
    @SuppressWarnings("unchecked")
    public void updateDialCodeToDialCodeCache() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_DIAL_CODE_1);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_DIAL_CODE_1, Map.class);
        String dialCode = (String) event.get("nodeUniqueId");

        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        jedisMock.select(dialCodeStoreId);
        Map<String, String> cachedData = jedisMock.hgetAll(dialCode);
        assertEquals(7, cachedData.size());
        verify(redisUpdaterSinkMock, times(1)).success();
    }


    @Test
    public void shouldHandleExceptionGracefullyWhenJedisConnectionFail() throws Exception {
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
    public void shouldAddOtherObjectTypeToCacheAlongWithContent() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_CONCEPT_EVENT);

        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_CONCEPT_EVENT, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");

        RedisUpdaterSource source = new RedisUpdaterSource(envelopeMock);
        redisUpdaterService.process(source, redisUpdaterSinkMock);

        jedisMock.select(contentStoreId);
        Map<String, String> cachedData = jedisMock.hgetAll(conceptId);
        assertEquals(1, cachedData.size());
        assertEquals("\"English\"", cachedData.get("language"));
        verify(redisUpdaterSinkMock, times(1)).success();
    }
}
