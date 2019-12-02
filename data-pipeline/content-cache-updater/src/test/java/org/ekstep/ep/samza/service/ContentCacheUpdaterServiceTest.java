package org.ekstep.ep.samza.service;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.core.BaseCacheUpdaterService;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.service.Fixtures.EventFixture;
import org.ekstep.ep.samza.task.ContentCacheConfig;
import org.ekstep.ep.samza.task.ContentCacheUpdaterSink;
import org.ekstep.ep.samza.task.ContentCacheUpdaterSource;
import org.ekstep.ep.samza.task.ContentCacheUpdaterTask;
import org.ekstep.ep.samza.util.ContentData;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.apache.samza.metrics.Counter;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class ContentCacheUpdaterServiceTest {

    private RedisConnect redisConnectMock;
    private IncomingMessageEnvelope envelopeMock;
    private JobMetrics jobMetricsMock;
    private Jedis jedisMock = new MockJedis("test");
    private ContentCacheUpdaterService contentCacheUpdaterService;
    private ContentCacheUpdaterSink contentCacheUpdaterSinkMock;
    private ContentCacheUpdaterTask contentCacheUpdaterTask;
    private ContentCacheConfig contentCacheConfig;
    private TaskContext contextMock;
    private TaskCoordinator taskCoordinator;
    private MessageCollector messageCollector;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private Config configMock;
    private Integer contentStoreId = 5;
    private Integer dialCodeStoreId = 6;

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        contentCacheUpdaterSinkMock = mock(ContentCacheUpdaterSink.class);
        configMock = mock(Config.class);
        contextMock = mock(TaskContext.class);
        taskCoordinator = mock(TaskCoordinator.class);
        messageCollector = mock(MessageCollector.class);
        jobMetricsMock = mock(JobMetrics.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = mock(Counter.class);

        stub(redisConnectMock.getConnection(contentStoreId)).toReturn(jedisMock);
        stub(redisConnectMock.getConnection(dialCodeStoreId)).toReturn(jedisMock);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.contentStore.id", contentStoreId)).toReturn(contentStoreId);
        stub(configMock.getInt("redis.database.dialCodeStore.id", dialCodeStoreId)).toReturn(dialCodeStoreId);
        stub(configMock.getInt("location.db.redis.key.expiry.seconds", 86400)).toReturn(86400);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "learning.graph.events", new Partition(0)));

        List<String> defaultListValues = new ArrayList<>();
        defaultListValues.add("gradeLevel");
        defaultListValues.add("subject");
        defaultListValues.add("medium");
        defaultListValues.add("language");

        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(configMock.getList("contentModel.fields.listType", new ArrayList<>()))
                .toReturn(defaultListValues);
        contentCacheConfig = new ContentCacheConfig(configMock);
        new BaseCacheUpdaterService(redisConnectMock);
        contentCacheUpdaterService = new ContentCacheUpdaterService(contentCacheConfig, redisConnectMock, jobMetricsMock);
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

        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        Map<String,Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap(), "Content");
        jedisMock.set(contentId, contentData.toString());

        String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(5, parsedData.size());
        assertEquals("in.ekstep", parsedData.get("channel"));
        assertEquals("TestCollection", parsedData.get("description"));
        assertEquals("testbook1", parsedData.get("code"));
        assertEquals("Draft", parsedData.get("status"));
        assertEquals(parsedData.get("ownershipType") instanceof List, true);
        assertEquals(Arrays.asList("createdBy"), parsedData.get("ownershipType"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void handleStringToList() throws Exception {
        jedisMock.flushAll();

        stub(envelopeMock.getMessage()).toReturn(EventFixture.INCORRECT_LIST_TYPE_FIELDS);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.INCORRECT_LIST_TYPE_FIELDS, Map.class);
        jedisMock.select(contentStoreId);
        String contentId = (String) event.get("nodeUniqueId");
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        Map<String,Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap(), "Content");
        jedisMock.set(contentId, contentData.toString());

                String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(4, parsedData.size());
        assertEquals(parsedData.get("language") instanceof List, true);
        assertEquals(Arrays.asList("Spanish"), parsedData.get("language"));
        assertEquals(parsedData.get("subject") instanceof List, true);
        assertEquals(Arrays.asList("CS"), parsedData.get("subject"));
        assertEquals(22.0, parsedData.get("ageGroup"));
        assertEquals(parsedData.get("ownershipType") instanceof List, true);
        assertEquals(Arrays.asList("createdBy"), parsedData.get("ownershipType"));
        verify(contentCacheUpdaterSinkMock, times(1)).success();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUpdateCacheIfDataExist() throws Exception {
        jedisMock.flushAll();

        // Update Redis Cache with First Event
        when(envelopeMock.getMessage()).thenReturn(EventFixture.OBJECT_TYPE_CONTENT_EVENT_1, EventFixture.OBJECT_TYPE_CONTENT_EVENT_1_UPDATED);
        contentCacheUpdaterService.process(new ContentCacheUpdaterSource(envelopeMock), contentCacheUpdaterSinkMock);

        // Update the redis cache with Second event with updated fields: channel, status
        // removed fields: ownershipType
        // new fields: visibility
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_CONTENT_EVENT_1_UPDATED, Map.class);
        String contentId = (String) event.get("nodeUniqueId");

        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        Map<String,Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap(), "Content");
        jedisMock.set(contentId, contentData.toString());
        String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(6, parsedData.size());
        assertEquals("testbook1", parsedData.get("code"));
        assertEquals("sunbird.portal", parsedData.get("channel"));
        assertEquals("Live", parsedData.get("status"));
        assertEquals("Default", parsedData.get("visibility"));
        assertEquals("TestCollection", parsedData.get("description"));
        assertEquals(Arrays.asList("createdBy"), parsedData.get("ownershipType"));
    }

    @Test
    public void shouldNotAddToCacheIfKeyValueIsNullOrEmpty() throws Exception {
        jedisMock.flushAll();

        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        when(envelopeMock.getMessage()).thenReturn(EventFixture.CONTENT_EVENT_EMPTY_NODE_UNIQUEID, EventFixture.CONTENT_EVENT_EMPTY_PROPERTIES);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        assertEquals(0, jedisMock.keys("*").size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotUpdateEmptyMessageProperties() throws Exception {
        jedisMock.flushAll();
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);

        // Update Redis Cache with First Event
        when(envelopeMock.getMessage()).thenReturn(EventFixture.CONTENT_EVENT_EMPTY_PROPERTIES);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);

        // Second event with empty properties
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.CONTENT_EVENT_EMPTY_PROPERTIES, Map.class);
        String contentId = (String) event.get("nodeUniqueId");

        String cachedData = jedisMock.get(contentId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(0, parsedData.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void updateDialCodeCache() throws Exception {
        jedisMock.flushAll();

        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_DIAL_CODE_1);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_DIAL_CODE_1, Map.class);
        contentCacheUpdaterTask = new ContentCacheUpdaterTask(configMock, contextMock, redisConnectMock);
        contentCacheUpdaterTask.process(envelopeMock, messageCollector, taskCoordinator);
        String dialCode = (String) event.get("nodeUniqueId");

        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        Map<String,Object> dialCodeData = contentCacheUpdaterService.getCacheData(source.getMap(), "DialCode");
        jedisMock.set(dialCode, gson.toJson(dialCodeData));

        String cachedData = jedisMock.get(dialCode);
        Map<String, String> cachedObject = gson.fromJson(cachedData, Map.class);
        assertEquals(261351.0, cachedObject.get("dialcode_index"));
        assertEquals("YC9EP8", cachedObject.get("identifier"));
        assertEquals("b00bc992ef25f1a9a8d63291e20efc8d", cachedObject.get("channel"));
        assertEquals("do_112692236142379008136", cachedObject.get("batchcode"));
        assertEquals(null, cachedObject.get("publisher"));
        assertEquals("2019-02-05T05:40:56.762", cachedObject.get("generated_on"));
        assertEquals("Draft", cachedObject.get("status"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldAddOtherObjectTypeToCache() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_CONCEPT_EVENT);

        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_CONCEPT_EVENT, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        Map<String,Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap(), "Content");
        jedisMock.set(conceptId, contentData.toString());

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
    }

    @Test
    public void shouldMarkEventSkippedForNonodeUniqueId() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.CONTENT_EVENT_EMPTY_NODE_UNIQUEID);
        contentCacheUpdaterTask = new ContentCacheUpdaterTask(configMock, contextMock, redisConnectMock);
        contentCacheUpdaterTask.process(envelopeMock, messageCollector, taskCoordinator);
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);

        verify(contentCacheUpdaterSinkMock, times(1)).markSkipped();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldNotAddEventIfItIsEmpty() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EMPTY_LANGUAGE_FIELD_EVENT);

        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.EMPTY_LANGUAGE_FIELD_EVENT, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");

        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        Map<String,Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap(), "Content");
        jedisMock.set(conceptId, contentData.toString());

        String cachedData = jedisMock.get(conceptId);
        Map<String, Object> parsedData = null;
        if (cachedData != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();
            parsedData = gson.fromJson(cachedData, type);
        }
        assertEquals(0, parsedData.size());
        assertEquals(null, parsedData.get("language"));
    }
}
