package org.ekstep.ep.samza.task;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;
import static org.mockito.Mockito.verify;

public class DeNormalizationTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.with_denorm";
    private static final String FAILED_TOPIC = "telemetry.failed";
    private static final String MALFORMED_TOPIC = "telemetry.malformed";

    private MessageCollector collectorMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private RedisConnect redisConnectMock;
    private CassandraConnect cassandraConnectMock;
    private DeNormalizationTask deNormalizationTask;
    private DeviceDataCache deviceCacheMock;
    private UserDataCache userCacheMock;
    private ContentDataCache contentCacheMock;
    private DialCodeDataCache dailcodeCacheMock;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        contextMock = mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);
        redisConnectMock = Mockito.mock(RedisConnect.class);
        cassandraConnectMock = Mockito.mock(CassandraConnect.class);
        deviceCacheMock = Mockito.mock(DeviceDataCache.class);
        userCacheMock = Mockito.mock(UserDataCache.class);
        contentCacheMock = Mockito.mock(ContentDataCache.class);
        dailcodeCacheMock = Mockito.mock(DialCodeDataCache.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("output.malformed.topic.name", MALFORMED_TOPIC)).toReturn(MALFORMED_TOPIC);

        stub(metricsRegistry.newCounter(anyString(), anyString())).toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        deNormalizationTask = new DeNormalizationTask(configMock, contextMock, deviceCacheMock, userCacheMock, contentCacheMock, cassandraConnectMock, dailcodeCacheMock);
    }

    @Test
    public void shouldSendEventsToSuccessTopicIfDidIsNullWithUserContentEmptyData() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.INTERACT_EVENT_WITHOUT_DID);
        stub(deviceCacheMock.getDataForDeviceId("68dfc64a7751ad47617ac1a4e0531fb761ebea6f",
                "0123221617357783046602")).toReturn(null);
        stub(userCacheMock.getData("393407b1-66b1-4c86-9080-b2bce9842886")).toReturn(null);
        stub(contentCacheMock.getData("do_31249561779090227216256")).toReturn(null);
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
        deNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        verify(collectorMock).send(argThat(new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                String outputMessage = (String) outgoingMessageEnvelope.getMessage();
                Map<String, Object> outputEvent = new Gson().fromJson(outputMessage, mapType);
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
}
