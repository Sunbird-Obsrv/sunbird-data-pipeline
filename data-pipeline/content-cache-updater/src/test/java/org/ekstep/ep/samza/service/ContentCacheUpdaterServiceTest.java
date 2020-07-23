package org.ekstep.ep.samza.service;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.*;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
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
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.util.RestUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

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
    private static final String DIAL_CODE_API_ENDPOINT = "/api/dialcode/v3/read/";
    private static final String DIAL_CODE_HOST = "https://localhost";
    private static final String DIAL_CODE_API_KEY = "";
    private RestUtil restUtilMock;


    @Before
    public void setUp() throws IOException {
        redisConnectMock = mock(RedisConnect.class);
        contentCacheUpdaterSinkMock = mock(ContentCacheUpdaterSink.class);
        configMock = mock(Config.class);
        contextMock = mock(TaskContext.class);
        taskCoordinator = mock(TaskCoordinator.class);
        messageCollector = mock(MessageCollector.class);
        jobMetricsMock = mock(JobMetrics.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = mock(Counter.class);
        restUtilMock = mock(RestUtil.class);

        stub(redisConnectMock.getConnection(contentStoreId)).toReturn(jedisMock);
        stub(redisConnectMock.getConnection(dialCodeStoreId)).toReturn(jedisMock);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        stub(configMock.getInt("redis.database.contentStore.id", contentStoreId)).toReturn(contentStoreId);
        stub(configMock.getInt("redis.database.dialCodeStore.id", dialCodeStoreId)).toReturn(dialCodeStoreId);
        stub(configMock.getInt("location.db.redis.key.expiry.seconds", 86400)).toReturn(86400);
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "learning.graph.events", new Partition(0)));
        stub(configMock.get("dialcode.api.host", DIAL_CODE_HOST)).toReturn(DIAL_CODE_HOST);
        stub(configMock.get("dialcode.api.endpoint", DIAL_CODE_API_ENDPOINT)).toReturn(DIAL_CODE_API_ENDPOINT);
        stub(configMock.get("dialcode.api.authorizationkey", DIAL_CODE_API_KEY)).toReturn(DIAL_CODE_API_KEY);
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
        String validDialCodeUrl = "https://localhost/api/dialcode/v3/read/E1L8W5";
        String inValidDialCodeUrl_Case1 = "https://localhost/api/dialcode/v3/read/test";
        String inValidDialCodeUrl_Case2 = "https://localhost/api/dialcode/v3/read/4328W56";
        String validDialCodeUrl_case1 = "https://localhost/api/dialcode/v3/read/Z5A8W1";
        String validDialCodeUrl_case2 = "https://localhost/api/dialcode/v3/read/H4D5Q9";
        String validDialCodeUrl_case3 = "https://localhost/api/dialcode/v3/read/C1M7J9";
        createStub(validDialCodeUrl, createTestResponse(validDialCodeUrl, EventFixture.VALID_DIAL_CODE_RESPONSE, 200, contentCacheConfig.getAuthorizationKey()), contentCacheConfig.getAuthorizationKey());
        createStub(validDialCodeUrl_case1, createTestResponse(validDialCodeUrl_case1, EventFixture.VALID_DIAL_CODE_RESPONSE, 200, contentCacheConfig.getAuthorizationKey()), contentCacheConfig.getAuthorizationKey());
        createStub(validDialCodeUrl_case2, createTestResponse(validDialCodeUrl_case2, EventFixture.VALID_DIAL_CODE_RESPONSE, 200, contentCacheConfig.getAuthorizationKey()), contentCacheConfig.getAuthorizationKey());
        createStub(validDialCodeUrl_case3, createTestResponse(validDialCodeUrl_case3, EventFixture.VALID_DIAL_CODE_RESPONSE, 200, contentCacheConfig.getAuthorizationKey()), contentCacheConfig.getAuthorizationKey());
        createStub(inValidDialCodeUrl_Case1, createTestResponse(inValidDialCodeUrl_Case1, EventFixture.INVALID_DIAL_CODE_RESPONSE, 404, contentCacheConfig.getAuthorizationKey()), contentCacheConfig.getAuthorizationKey());
        createStub(inValidDialCodeUrl_Case2, createTestResponse(inValidDialCodeUrl_Case2, EventFixture.EMPTY_DIAL_CODE_RESPONSE, 200, contentCacheConfig.getAuthorizationKey()), contentCacheConfig.getAuthorizationKey());
        contentCacheUpdaterService = new ContentCacheUpdaterService(contentCacheConfig, redisConnectMock, jobMetricsMock, restUtilMock);
        jedisMock.flushAll();
    }


    public String createTestResponse(String apiUrl, String response, int status, String authKey) throws IOException {
        Request mockRequest = new Request.Builder()
                .url(apiUrl)
                .header("Authorization", authKey)
                .build();
        return Objects.requireNonNull(new Response.Builder()
                .request(mockRequest)
                .protocol(Protocol.HTTP_2)
                .code(status) // status code
                .message("")
                .body(ResponseBody.create(
                        MediaType.get("application/json; charset=utf-8"),
                        response
                ))
                .build().body()).string();
    }

    public void createStub(String apiUrl, String response, String authKey) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", authKey);
        try {
            when(restUtilMock.get(apiUrl, headers)).thenReturn(response);
        } catch (Exception e) {
            System.out.println("Exception is" + e);
        }
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
        Map<String, Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap());
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
        Map<String, Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap());
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
        Map<String, Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap());
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
    public void shouldAddOtherObjectTypeToCache() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OBJECT_TYPE_CONCEPT_EVENT);

        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.OBJECT_TYPE_CONCEPT_EVENT, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
        Map<String, Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap());
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
        contentCacheUpdaterTask = new ContentCacheUpdaterTask(configMock, contextMock, redisConnectMock, restUtilMock);
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
        Map<String, Object> contentData = contentCacheUpdaterService.getCacheData(source.getMap());
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

    @Test
    @SuppressWarnings("unchecked")
    public void shouldInvokeDialCodeAPICall() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.DIALCODE_LINKED_TEXTBOOK);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.DIALCODE_LINKED_TEXTBOOK, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void WhenDialCodeIsNotPresetInTheExternalSystemAPIShouldReturnResourceNotFound() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INVALID_DIALCODE_LINKED_TEXTBOOK);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.INVALID_DIALCODE_LINKED_TEXTBOOK, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void WhenEmptyDialCodeAPIResponse() throws Exception {
        jedisMock.flushAll();
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EMPTY_DIALCODE_LINKED_TEXTBOOK);
        Gson gson = new Gson();
        Map<String, Object> event = gson.fromJson(EventFixture.EMPTY_DIALCODE_LINKED_TEXTBOOK, Map.class);
        String conceptId = (String) event.get("nodeUniqueId");
        ContentCacheUpdaterSource source = new ContentCacheUpdaterSource(envelopeMock);
        contentCacheUpdaterService.process(source, contentCacheUpdaterSinkMock);
    }
}
