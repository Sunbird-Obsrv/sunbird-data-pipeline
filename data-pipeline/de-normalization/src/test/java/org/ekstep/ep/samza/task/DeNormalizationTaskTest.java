package org.ekstep.ep.samza.task;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.samza.Partition;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.system.SystemStreamPartition;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.core.JobMetrics;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.invocation.InvocationOnMock;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class DeNormalizationTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.with_denorm";
    private static final String FAILED_TOPIC = "telemetry.failed";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";
    private static final Integer ignorePeriodInMonths = 6;

    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private DeNormalizationTask deNormalizationTask;

    private ContentDataCache contentCacheMock;
    private DialCodeDataCache dailcodeCacheMock;
    private JobMetrics jobMetrics;
    private Jedis jedisMock = new MockJedis("test");
    private Type mapType = new TypeToken<Map<String, Object>>(){}.getType();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        contextMock = mock(TaskContext.class);
        metricsRegistry = mock(MetricsRegistry.class);
        counter = mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = mock(Config.class);
        RedisConnect redisConnectMock = mock(RedisConnect.class);
        contentCacheMock = mock(ContentDataCache.class);
        dailcodeCacheMock = mock(DialCodeDataCache.class);
        jobMetrics = mock(JobMetrics.class);


        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);
        stub(configMock.getInt("telemetry.ignore.period.months", ignorePeriodInMonths)).toReturn(ignorePeriodInMonths);
        stub(configMock.getList("user.metadata.fields",
                Arrays.asList("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype")))
                .toReturn(Arrays.asList("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype"));
        stub(configMock.getInt("redis.userDB.index", 4)).toReturn(4);
        stub(redisConnectMock.getConnection(4)).toReturn(jedisMock);
        List<String> defaultSummaryEvents = new ArrayList<>();
        defaultSummaryEvents.add("ME_WORKFLOW_SUMMARY");
        stub(configMock.getList("summary.filter.events", defaultSummaryEvents)).toReturn(defaultSummaryEvents);
        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(envelopeMock.getOffset()).toReturn("2");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.with_location", new Partition(1)));
        stub(configMock.get("user.signin.type.default", "Anonymous")).toReturn("Anonymous");
        stub(configMock.get("user.login.type.default", "NA")).toReturn("NA");

        UserDataCache userCacheMock = new UserDataCache(configMock,jobMetrics, redisConnectMock);
        deNormalizationTask = new DeNormalizationTask(configMock, contextMock, userCacheMock , contentCacheMock, dailcodeCacheMock, jobMetrics, redisConnectMock);
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfDidIsNullWithUserContentEmptyData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("contentdata"));
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), false);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfDidIsNullWithUserContentData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
        Map<String, Object> content = new HashMap<>();
        content.put("name", "content-1"); content.put("objecttype", "Content"); content.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(content);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"type\":\"Registered\",\"state\":\"Karnataka\"}");
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 5);
                Map<String, Object> contentData = new Gson().fromJson(outputEvent.get("contentdata").toString(), mapType);
                assertEquals(contentData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithUserContentData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"state\":\"Karnataka\"}");
        Map<String, Object> content = new HashMap<>();
        content.put("name", "content-1"); content.put("objecttype", "Content"); content.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(content);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 5);
                Map<String, Object> contentData = new Gson().fromJson(outputEvent.get("contentdata").toString(), mapType);
                assertEquals(contentData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithoutRollUpID() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"state\":\"Karnataka\"}");
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                System.out.println(outputMessage);
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 5);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithRollUpIdInCollectionData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_OBJECT_ROLLUP);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"state\":\"Karnataka\"}");
        Map<String, Object> collection = new HashMap<>();
        collection.put("name", "collection-1");
        collection.put("objecttype", "TextBook");
        collection.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(collection);
        deNormalizationTask.process(envelopeMock,collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                System.out.println();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> collectionData = new Gson().fromJson(outputEvent.get("collectiondata").toString(), mapType);
                assertEquals(collectionData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("collection_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithObjectIdInCollectionData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_OBJECT_ROLLUP);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"state\":\"Karnataka\"}");
        Map<String, Object> collection = new HashMap<>();
        collection.put("name", "collection-1");
        collection.put("objecttype", "TextBook");
        collection.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31277438304183091217888")).toReturn(collection);
        deNormalizationTask.process(envelopeMock,collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                Map<String, Object> collectionData = new Gson().fromJson(outputEvent.get("contentdata").toString(), mapType);
                assertEquals(collectionData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("collection_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithObjectIdEqualsRollUpIDInCollectionData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_OBJECTID_EQUALS_ROLLUPID);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"state\":\"Karnataka\"}");
        Map<String, Object> collection = new HashMap<>();
        collection.put("name", "collection-1");
        collection.put("objecttype", "TextBook");
        collection.put("contenttype", "TextBook");
        Map<String, Object> content = new HashMap<>();
        content.put("name", "content-1"); content.put("objecttype", "Content"); content.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31277438304183091217888")).toReturn(collection);
        stub(contentCacheMock.getData("do_31277438304183091217888")).toReturn(content);
        deNormalizationTask.process(envelopeMock,collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                System.out.println(outputEvent);
                Map<String, Object> collectionData = new Gson().fromJson(outputEvent.get("contentdata").toString(), mapType);
                assertEquals(collectionData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("content_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfContentIdIsNullWithUserData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_OBJECT);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"type\":\"Registered\"}");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals("3.0", outputEvent.get("ver").toString());
                assertNull(outputEvent.get("contentdata"));
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(true, flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfContentDataIsEmptyWithUserAsSystem() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_ACTOR_AS_SYSTEM);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"type\":\"Registered\"}");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals("3.0", outputEvent.get("ver").toString());
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertNull(flags.get("user_data_retrieved"));
                assertEquals(false, flags.get("content_data_retrieved"));
                return true;
            }
        }));
    }

    public void shouldSendEventsToSuccessTopicWithStringDialCodeData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_DIALCODE_AS_STRING);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP");
        Map dataMap = new HashMap(); dataMap.put("identifier", "8ZEDTP"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("contentdata"));
                List<Map<String, Object>> dialcodesList = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), List.class);
                assertEquals(dialcodesList.size(), 1);
                Map data = dialcodesList.get(0);
                assertEquals(data.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertNull(flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                assertEquals(true, flags.get("dialcode_data_retrieved"));
                return true;
            }
        }));
    }

    public void shouldSendEventsToSuccessTopicWithListDialCodeData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_DIALCODE_AS_LIST);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP"); ids.add("4ZEDTP");
        Map dataMap1 = new HashMap(); dataMap1.put("identifier", "8ZEDTP"); dataMap1.put("channel", "test-channel");
        dataMap1.put("status", "Draft");
        Map dataMap2 = new HashMap(); dataMap2.put("identifier", "4ZEDTP"); dataMap2.put("channel", "test-channel");
        dataMap2.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap1); data.add(dataMap2);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                List<Map<String, Object>> dialcodesList = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), List.class);
                assertEquals(dialcodesList.size(), 2);
                Map data1 = dialcodesList.get(0);
                assertEquals(data1.size(), 3);
                Map data2 = dialcodesList.get(1);
                assertEquals(data2.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertNull(flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                assertEquals(true, flags.get("dialcode_data_retrieved"));
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithOutDialCodeData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITHOUT_DIALCODE);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                assertNull(outputEvent.get("dialcodedata"));
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertNull(flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                assertNull(flags.get("dialcode_data_retrieved"));
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithOutAnyDenormData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_DEVICEDATA);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), false);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    public void shouldSendEventsToSuccessTopicWithExistingEmptyStateCode() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_EMPTY_LOC);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), false);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithDialCodeLowerCaseDataByObjectLookup() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.IMPRESSION_EVENT_WITH_DIALCODE_AS_OBJECT);
        stub(contentCacheMock.getData("977D3I")).toReturn(null);
        Map dataMap = new HashMap(); dataMap.put("identifier", "977D3I"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        stub(dailcodeCacheMock.getData("977D3I")).toReturn(dataMap);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("contentdata"));
                Map data = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), Map.class);
                assertEquals(data.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(false, flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                assertEquals(true, flags.get("dialcode_data_retrieved"));
                return true;
            }
        }));
    }

    //@Test
    public void shouldSendEventsToSuccessTopicWithStringDialCodeDataCamelCase() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_CAMELCASE_DIALCODE_AS_STRING);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP");
        Map dataMap = new HashMap(); dataMap.put("identifier", "8ZEDTP"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertEquals(outputMessage.contains("dialcodes"), true);
                assertEquals(outputMessage.contains("dialCodes"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithAlteredEts() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_FUTURE_ETS);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "2.1");
                Long ets = new Gson().fromJson(outputEvent.get("ets").toString(), Long.class);
                assertFalse(2530937155000L == ets);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventToFaildTopicIfEventIsOlder() throws Exception{

        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_OLDER_ETS);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    public void shouldSendSummaryEventsToSuccessTopicWithUserData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.WFS_EVENT);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"type\":\"Registered\",\"state\":\"Karnataka\"}");
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "1.1");
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 5);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), null);
                return true;
            }
        }));
    }

    @Test
    public void shouldSkipOtherSummaryEvent() throws Exception{

        stub(envelopeMock.getMessage()).toReturn(EventFixture.DEVICE_SUMMARY_EVENT);
        Answer answer = new Answer();
        doAnswer(answer).when(jobMetrics).incSkippedCounter();
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        assertEquals(true, answer.isSkipped);
    }

    public ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                return true;
            }
        };
    }

    @Test
    public void shouldSendEventsToSuccessTopicForLogEvents() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.LOG_EVENT);
        jedisMock.set("0b251080-3230-415e-a593-ab7c1fac7ae3","{\"grade\":[4,5],\"type\":\"Registered\"}");
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(true, flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                return true;
            }
        }));
    }

    @Test
    public void shouldPushToSuccessTopicForErrorEvents() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.ERROR_EVENT);
        Answer answer = new Answer();
        doAnswer(answer).when(jobMetrics).incSuccessCounter();
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        assertEquals(true, answer.isSuccess);
    }

    class Answer implements org.mockito.stubbing.Answer {

        private Boolean isSkipped = false;
        private Boolean isSuccess = false;
        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            isSkipped = true;
            isSuccess = true;
            return null;
        }
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithQRCodeDataByObjectLookup() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.IMPRESSION_EVENT_WITH_QR_AS_OBJECT);
        stub(contentCacheMock.getData("977D3I")).toReturn(null);
        Map dataMap = new HashMap(); dataMap.put("identifier", "977D3I"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        stub(dailcodeCacheMock.getData("977D3I")).toReturn(dataMap);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                Map data = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), Map.class);
                assertEquals(data.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(false, flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                assertEquals(true, flags.get("dialcode_data_retrieved"));
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithDialCodeCamelCaseDataByObjectLookup() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.IMPRESSION_EVENT_WITH_DIALCODE_CAMELCASE_AS_OBJECT);
        stub(contentCacheMock.getData("977D3I")).toReturn(null);
        Map dataMap = new HashMap(); dataMap.put("identifier", "977D3I"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        stub(dailcodeCacheMock.getData("977D3I")).toReturn(dataMap);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                Map data = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), Map.class);
                assertEquals(data.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(false, flags.get("user_data_retrieved"));
                assertNull(flags.get("content_data_retrieved"));
                assertEquals(true, flags.get("dialcode_data_retrieved"));
                return true;
            }
        }));
    }

    @Test
    public void shouldSendAUDITEventsToSuccessTopicWithUserData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.AUDIT_EVENT);
        jedisMock.set("393407b1-66b1-4c86-9080-b2bce9842886","{\"grade\":[4,5],\"district\":\"Bengaluru\",\"type\":\"Registered\",\"state\":\"Karnataka\"}");
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                String jedisMockData = jedisMock.get("393407b1-66b1-4c86-9080-b2bce9842886");
                assertEquals(userData.size(), 5);
                assert jedisMockData.contains("grade");
                assert jedisMockData.contains("district");
                assert jedisMockData.contains("type");
                assert jedisMockData.contains("state");
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), null);
                return true;
            }
        }));
    }

    @Test
    public void shouldStampUserSigninAndLoginTypeForAllEvents() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.IMPRESSION_EVENT);
        String user_id="e9d73b7b-211a-43f4-8c01-cc9333757a9d";
        jedisMock.set(user_id,"{\"subject\":[],\"grade\":[],\"usersignintype\":\"Self-Signed-In\",\"userlogintype\":\"student\"}");
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 4);
                assertEquals(userData.get("usersignintype"),"Self-Signed-In");
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), null);
                return true;
            }
        }));
    }
}
