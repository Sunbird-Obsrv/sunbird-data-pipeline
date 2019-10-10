package org.ekstep.ep.samza.task;

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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class DruidProcessorTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.with_denorm";
    private static final String FAILED_TOPIC = "telemetry.failed";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";
    private static final Integer ignorePeriodInMonths = 6;

    private static final String TELEMETRY_EVENTS_TOPIC = "events.telemetry";
    private static final String SUMMARY_EVENTS_TOPIC = "events.summary";
    private static final String LOG_EVENTS_TOPIC = "events.log";

    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private RedisConnect redisConnectMock;
    private CassandraConnect cassandraConnectMock;
    private DruidProcessorTask druidProcessorTask;
    private DeviceDataCache deviceCacheMock;
    private UserDataCache userCacheMock;
    private ContentDataCache contentCacheMock;
    private DialCodeDataCache dailcodeCacheMock;
    private JobMetrics jobMetrics;
    private SchemaValidator schemaValidator;
    private DruidProcessorConfig schemaConfigMock;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() throws Exception {
        collectorMock = mock(MessageCollector.class);
        contextMock = mock(TaskContext.class);
        metricsRegistry = mock(MetricsRegistry.class);
        counter = mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = mock(Config.class);
        redisConnectMock = mock(RedisConnect.class);
        cassandraConnectMock = mock(CassandraConnect.class);
        deviceCacheMock = mock(DeviceDataCache.class);
        userCacheMock = mock(UserDataCache.class);
        contentCacheMock = mock(ContentDataCache.class);
        dailcodeCacheMock = mock(DialCodeDataCache.class);
        jobMetrics = mock(JobMetrics.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);
        stub(configMock.getInt("telemetry.ignore.period.months", ignorePeriodInMonths)).toReturn(ignorePeriodInMonths);
        stub(configMock.get("router.events.telemetry.route.topic", TELEMETRY_EVENTS_TOPIC)).toReturn(TELEMETRY_EVENTS_TOPIC);
        stub(configMock.get("router.events.summary.route.topic", SUMMARY_EVENTS_TOPIC)).toReturn(SUMMARY_EVENTS_TOPIC);
        stub(configMock.get("router.events.log.route.topic", LOG_EVENTS_TOPIC)).toReturn(LOG_EVENTS_TOPIC);
        List<String> defaultSummaryEvents = new ArrayList<>();
        defaultSummaryEvents.add("ME_WORKFLOW_SUMMARY");
        stub(configMock.getList("summary.filter.events", defaultSummaryEvents)).toReturn(defaultSummaryEvents);
        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);
        stub(envelopeMock.getOffset()).toReturn("2");
        stub(envelopeMock.getSystemStreamPartition())
                .toReturn(new SystemStreamPartition("kafka", "telemetry.with_location", new Partition(1)));

        schemaConfigMock = mock(DruidProcessorConfig.class);
        stub(schemaConfigMock.telemetrySchemaPath()).toReturn("schemas/telemetry");
        stub(schemaConfigMock.summarySchemaPath()).toReturn("schemas/summary");
        stub(schemaConfigMock.defaultSchemafile()).toReturn("envelope.json");
        schemaValidator = new SchemaValidator(schemaConfigMock);

        druidProcessorTask = new DruidProcessorTask(configMock, contextMock, deviceCacheMock, userCacheMock,
                contentCacheMock, cassandraConnectMock, dailcodeCacheMock, schemaValidator, jobMetrics);
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfDidIsNullWithUserContentEmptyData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("devicedata"));
                assertNull(outputEvent.get("userdata"));
                assertNull(outputEvent.get("contentdata"));
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), false);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfDidIsNullWithUserContentData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradelist", "[4, 5]");
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(user);
        Map content = new HashMap(); content.put("name", "content-1"); content.put("objecttype", "Content"); content.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(content);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertNull(outputEvent.get("devicedata"));
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 2);
                Map<String, Object> contentData = new Gson().fromJson(outputEvent.get("contentdata").toString(), mapType);
                assertEquals(contentData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithDeviceUserContentData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
        Map device = new HashMap(); device.put("os", "Android 6.0"); device.put("make", "Motorola XT1706"); device.put("agent", "Mozilla");
        device.put("ver", "5.0"); device.put("system", "iPad"); device.put("platform", "AppleWebKit/531.21.10"); device.put("raw", "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)");
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(device);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradelist", "[4, 5]");
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(user);
        Map content = new HashMap(); content.put("name", "content-1"); content.put("objecttype", "Content"); content.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(content);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertTrue(outputMessage.contains("\"agent\":\"Mozilla\""));
                assertTrue(outputMessage.contains("\"ver\":\"5.0\""));
                assertTrue(outputMessage.contains("\"system\":\"iPad\""));
                assertTrue(outputMessage.contains("\"os\":\"Android 6.0\""));
                assertTrue(outputMessage.contains("\"make\":\"Motorola XT1706\""));
                assertTrue(outputMessage.contains("\"platform\":\"AppleWebKit/531.21.10\""));
                assertTrue(outputMessage.contains("\"raw\":\"Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)\""));
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 2);
                Map<String, Object> contentData = new Gson().fromJson(outputEvent.get("contentdata").toString(), mapType);
                assertEquals(contentData.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), true);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfDidAndContentIdIsNullWithUserData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_OBJECT);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradeList", "[4, 5]");
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(user);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertNull(outputEvent.get("devicedata"));
                assertNull(outputEvent.get("contentdata"));
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 2);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), null);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfDidIsNullAndContentDataIsEmptyWithUserAsSystem() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_ACTOR_AS_SYSTEM);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradelist", "[4, 5]");
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(user);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("devicedata"));
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), null);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithStringDialCodeData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_DIALCODE_AS_STRING);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP");
        Map dataMap = new HashMap(); dataMap.put("identifier", "8ZEDTP"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertNull(outputEvent.get("devicedata"));
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                List<Map<String, Object>> dialcodesList = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), List.class);
                assertEquals(dialcodesList.size(), 1);
                Map data = dialcodesList.get(0);
                assertEquals(data.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), null);
                assertEquals(flags.get("content_data_retrieved"), null);
                assertEquals(flags.get("dialcode_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithListDialCodeData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_DIALCODE_AS_LIST);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP"); ids.add("4ZEDTP");
        Map dataMap1 = new HashMap(); dataMap1.put("identifier", "8ZEDTP"); dataMap1.put("channel", "test-channel");
        dataMap1.put("status", "Draft");
        Map dataMap2 = new HashMap(); dataMap2.put("identifier", "4ZEDTP"); dataMap2.put("channel", "test-channel");
        dataMap2.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap1); data.add(dataMap2);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertNull(outputEvent.get("devicedata"));
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                List<Map<String, Object>> dialcodesList = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), List.class);
                assertEquals(dialcodesList.size(), 2);
                Map data1 = dialcodesList.get(0);
                assertEquals(data1.size(), 3);
                Map data2 = dialcodesList.get(1);
                assertEquals(data2.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), null);
                assertEquals(flags.get("content_data_retrieved"), null);
                assertEquals(flags.get("dialcode_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithOutDialCodeData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITHOUT_DIALCODE);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
//        List ids = new ArrayList(); ids.add("8ZEDTP"); ids.add("4ZEDTP");
//        stub(dailcodeCacheMock.getDataForDialCodes(ids)).toReturn(null);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.0");
                assertNull(outputEvent.get("devicedata"));
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                assertNull(outputEvent.get("dialcodedata"));
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), null);
                assertEquals(flags.get("content_data_retrieved"), null);
                assertEquals(flags.get("dialcode_data_retrieved"), null);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithExistingDeviceData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_DEVICEDATA);
        Map device = new HashMap(); device.put("os", "Android 6.0"); device.put("make", "Motorola XT1706"); device.put("agent", "Mozilla");
        device.put("ver", "5.0"); device.put("system", "iPad"); device.put("platform", "AppleWebKit/531.21.10"); device.put("raw", "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)");
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(device);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradelist", "[4, 5]");
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(user);
        Map content = new HashMap(); content.put("name", "content-1"); content.put("objecttype", "Content"); content.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(content);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertTrue(outputMessage.contains("\"iso3166statecode\":\"IN-KA\""));
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), true);
                assertEquals(flags.get("user_data_retrieved"), false);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithExistingEmptyStateCode() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITH_EMPTY_LOC);
        Map device = new HashMap(); device.put("os", "Android 6.0"); device.put("make", "Motorola XT1706"); device.put("agent", "Mozilla");
        device.put("ver", "5.0"); device.put("system", "iPad"); device.put("platform", "AppleWebKit/531.21.10"); device.put("raw", "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)");
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(device);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradelist", "[4, 5]");
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(user);
        Map content = new HashMap(); content.put("name", "content-1"); content.put("objecttype", "Content"); content.put("contenttype", "TextBook");
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(content);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                assertFalse(outputMessage.contains("\"iso3166statecode\":\"IN-KA\""));
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), true);
                assertEquals(flags.get("user_data_retrieved"), false);
                assertEquals(flags.get("content_data_retrieved"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithDialCodeDataByObjectLookup() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.IMPRESSION_EVENT_WITH_DIALCODE_AS_OBJECT);
        stub(deviceCacheMock.getDataForDeviceId("a49cfadff97c698c1766c71a42779d4e",
                "505c7c48ac6dc1edc9b08f21db5a571d")).toReturn(null);
        stub(userCacheMock.getData("anonymous")).toReturn(null);
        stub(contentCacheMock.getData("977D3I")).toReturn(null);
        Map dataMap = new HashMap(); dataMap.put("identifier", "977D3I"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        stub(dailcodeCacheMock.getData("977D3I")).toReturn(dataMap);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertNull(outputEvent.get("devicedata"));
                assertNull(outputEvent.get("contentdata"));
                assertNull(outputEvent.get("userdata"));
                List<Map<String, Object>> dialcodesList = new Gson().fromJson(outputEvent.get("dialcodedata").toString(), List.class);
                assertEquals(dialcodesList.size(), 1);
                Map data = dialcodesList.get(0);
                assertEquals(data.size(), 3);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), false);
                assertEquals(flags.get("user_data_retrieved"), false);
                assertEquals(flags.get("content_data_retrieved"), null);
                assertEquals(flags.get("dialcode_data_retrieved"), true);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithStringDialCodeDataCamelCase() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_CAMELCASE_DIALCODE_AS_STRING);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP");
        Map dataMap = new HashMap(); dataMap.put("identifier", "8ZEDTP"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertEquals(outputMessage.contains("dialcodes"), true);
                assertEquals(outputMessage.contains("dialCodes"), false);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventsToSuccessTopicWithAlteredEts() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_FUTURE_ETS);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP");
        Map dataMap = new HashMap(); dataMap.put("identifier", "8ZEDTP"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "2.2");
                Long ets = new Gson().fromJson(outputEvent.get("ets").toString(), Long.class);
                assertFalse(2530937155000L == ets);
                return true;
            }
        }));
    }

    @Test
    public void shouldSendEventToFaildTopicIfEventIsOlder() throws Exception{

        stub(envelopeMock.getMessage()).toReturn(EventFixture.SEARCH_EVENT_WITH_OLDER_ETS);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        List ids = new ArrayList(); ids.add("8ZEDTP");
        Map dataMap = new HashMap(); dataMap.put("identifier", "8ZEDTP"); dataMap.put("channel", "test-channel");
        dataMap.put("status", "Draft");
        List<Map> data = new ArrayList<>(); data.add(dataMap);
        stub(dailcodeCacheMock.getData(ids)).toReturn(data);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), FAILED_TOPIC)));
    }

    @Test
    public void shouldSendSummaryEventsToSuccessTopicWithDeviceUserData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.WFS_EVENT);
        Map device = new HashMap(); device.put("os", "Android 6.0"); device.put("make", "Motorola XT1706"); device.put("agent", "Mozilla");
        device.put("ver", "5.0"); device.put("system", "iPad"); device.put("platform", "AppleWebKit/531.21.10"); device.put("raw", "Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)");
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(device);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradelist", "[4, 5]");
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(user);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "1.2");
                assertTrue(outputMessage.contains("\"agent\":\"Mozilla\""));
                assertTrue(outputMessage.contains("\"ver\":\"5.0\""));
                assertTrue(outputMessage.contains("\"system\":\"iPad\""));
                assertTrue(outputMessage.contains("\"os\":\"Android 6.0\""));
                assertTrue(outputMessage.contains("\"make\":\"Motorola XT1706\""));
                assertTrue(outputMessage.contains("\"platform\":\"AppleWebKit/531.21.10\""));
                assertTrue(outputMessage.contains("\"raw\":\"Mozilla/5.0 (X11 Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko)\""));
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 2);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), true);
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
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
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
        stub(deviceCacheMock.getDataForDeviceId("923c675274cfbf19fd0402fe4d2c37afd597f0ab",
                "505c7c48ac6dc1edc9b08f21db5a571d")).toReturn(null);
        Map user = new HashMap(); user.put("type", "Registered"); user.put("gradelist", "[4, 5]");
        stub(userCacheMock.getData("0b251080-3230-415e-a593-ab7c1fac7ae3")).toReturn(user);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                System.out.println(outputMessage);
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
                assertEquals(outputEvent.get("ver").toString(), "3.1");
                assertNull(outputEvent.get("devicedata"));
                Map<String, Object> userData = new Gson().fromJson(outputEvent.get("userdata").toString(), mapType);
                assertEquals(userData.size(), 2);
                Map<String, Object> flags = new Gson().fromJson(outputEvent.get("flags").toString(), mapType);
                assertEquals(flags.get("device_data_retrieved"), null);
                assertEquals(flags.get("user_data_retrieved"), true);
                assertEquals(flags.get("content_data_retrieved"), null);
                return true;
            }
        }));
    }

    @Test
    public void shouldSkipProcessingForErrorEvents() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.ERROR_EVENT);
        Answer answer = new Answer();
        doAnswer(answer).when(jobMetrics).incSkippedCounter();
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        assertEquals(true, answer.isSkipped);
    }

    class Answer implements org.mockito.stubbing.Answer {

        private Boolean isSkipped = false;
        @Override
        public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
            isSkipped = true;
            System.out.println("SkippedCounter is executed");
            return null;
        }
    }

    @Test
    public void shouldSkipProcessingForEdataTypeOtherThanApiAccess(){
        stub(envelopeMock.getMessage()).toReturn(EventFixture.TEST_LOG_EVENT);
        Answer answer = new Answer();
        doAnswer(answer).when(jobMetrics).incSkippedCounter();
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        assertEquals(true, answer.isSkipped);
    }

    @Test
    public void shouldRouteSummaryEventsToSummaryTopic() throws Exception {

        stub(configMock.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY")).toReturn("ME_WORKFLOW_SUMMARY");
        stub(envelopeMock.getMessage()).toReturn(EventFixture.WFS_EVENT);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUMMARY_EVENTS_TOPIC)));
    }

    @Test
    public void shouldRouteTelemetryEventsToTelemetryTopic() throws Exception {

        stub(configMock.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY")).toReturn("ME_WORKFLOW_SUMMARY");
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), TELEMETRY_EVENTS_TOPIC)));

    }

    @Test
    public void shouldSendEventToFailedTopicIfEventIsNotParseable() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.UNPARSABLE_START_EVENT);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
    }

    @Test
    public void shouldSendEventToMalformedTopicIfEventIsAnyRandomString() throws Exception {

        stub(envelopeMock.getMessage()).toReturn(EventFixture.ANY_STRING);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), MALFORMED_TOPIC)));
    }

    @Test
    public void shouldRouteLogEventsToLogEventsTopic() throws Exception {

        stub(configMock.get("router.events.summary.route.events", "ME_WORKFLOW_SUMMARY")).toReturn("ME_WORKFLOW_SUMMARY");
        stub(envelopeMock.getMessage()).toReturn(EventFixture.LOG_EVENT);
        druidProcessorTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), LOG_EVENTS_TOPIC)));
    }

}
