package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.library.checksum.system.ChecksumGenerator;
import com.library.checksum.system.KeysToAccept;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.rule.*;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.service.DeviceService;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReverseSearchTest {

    @Mock
    KeyValueStore<String, Object> deviceStore;
    @Mock
    LocationService locationService;
    @Mock
    DeviceService deviceService;
    @Mock
    private MessageCollector collector;

    private List<Rule> locationRules;

    @Before
    public void setMock() {
        initMocks(this);
        locationRules = Arrays.asList(new LocationPresent(), new LocationEmpty(), new LocationAbsent());
    }

    @Test
    public void shouldDoReverseSearchIfLocPresent() {
        when(locationService.getLocation("15.9310593,78.6238299", null)).thenReturn(new Location());
        Event event = createEventMock("15.9310593,78.6238299");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        when(deviceService.getLocation(eq("bc811958-b4b7-4873-a43a-03718edba45b"), isNull(String.class))).thenReturn(new Location());
        when(event.isLocationPresent()).thenReturn(true);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore,
                "false", locationService, deviceService, locationRules, Mockito.mock(Config.class));

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
        verify(locationService, times(1)).getLocation("15.9310593,78.6238299", null);
        verify(deviceService, times(1)).getLocation(eq("bc811958-b4b7-4873-a43a-03718edba45b"), isNull(String.class));
    }

    @Test
    public void shouldUpdateProperFlagIfSearchReturnEmpty() {

        when(locationService.getLocation("15.9310593,78.6238299", null)).thenReturn(null);
        Map<String, Object> map = createMap("15.9310593,78.6238299");
        Event event = new Event(map);
        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService,locationRules, Mockito.mock(Config.class));

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
        verify(locationService, times(1)).getLocation("15.9310593,78.6238299", null);

        Assert.assertNotNull(event.getMap());

        Map<String, Object> flagsResult = (Map<String, Object>) event.getMap().get("flags");
        Assert.assertNotNull(flagsResult);

        Assert.assertEquals(false, flagsResult.get("ldata_obtained"));
        Assert.assertEquals(true, flagsResult.get("ldata_processed"));
    }


    @Test
    public void shouldTakeLocationFromDeviceStoreIfNotPresent() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Device\",\"id\":\"bc811958-b4b7-4873-a43a-03718edba45b\",\"location\":{\"city\":null,\"district\":null,\"state\":null,\"country\":null}}");
        Event event = createEventMock("");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        when(event.isLocationPresent()).thenReturn(false);
        when(event.isLocationEmpty()).thenReturn(false);
        when(event.isLocationAbsent()).thenReturn(true);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService,locationRules, Mockito.mock(Config.class));

        reverseSearchStreamTask.processEvent(event, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceService, times(1)).getLocation(eq("bc811958-b4b7-4873-a43a-03718edba45b"),isNull(String.class));
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));

        Event event1 = createEventMock("");
        when(event1.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        when(event.isLocationPresent()).thenReturn(false);
        when(event.isLocationEmpty()).thenReturn(false);
        when(event.isLocationAbsent()).thenReturn(true);

        reverseSearchStreamTask.processEvent(event1, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceService, times(2)).getLocation(eq("bc811958-b4b7-4873-a43a-03718edba45b"), isNull(String.class));
        verify(collector, times(4)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldNotFailIfLocationInDeviceStoreIsNotPresent() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn(null);
        Event event = createEventMock("");

        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService, locationRules, Mockito.mock(Config.class));

        reverseSearchStreamTask.processEvent(event, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceService, times(1)).getLocation(eq("bc811958-b4b7-4873-a43a-03718edba45b"), isNull(String.class));
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldNotFailIfDeviseIdIsNull() {

        when(deviceStore.get(null)).thenThrow(new NullPointerException("Null is not a valid key"));
        Event event = createEventMock("");

        when(event.getDid()).thenReturn(null);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService, locationRules, Mockito.mock(Config.class));

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldIgnoreInvalidMessages() {

        IncomingMessageEnvelope envelope = mock(IncomingMessageEnvelope.class);
        when(envelope.getMessage()).thenThrow(new RuntimeException());
        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService, locationRules, Mockito.mock(Config.class));

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.process(envelope, collector, task);

        verify(deviceStore, times(0)).get(anyString());
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(collector, times(0)).send(any(OutgoingMessageEnvelope.class));

    }

    @Test
    public void ShouldStampChecksumToEventIfChecksumNotPresent() throws Exception {
        Map<String, Object> eventMap = new Gson().fromJson(EventFixture.JSON, Map.class);
        Event event = new Event(eventMap);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService, locationRules, Mockito.mock(Config.class));

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Assert.assertEquals(true, event.getMap().containsKey("metadata"));
    }

    @Test
    public void ShouldUseMidAsChecksumIfEventContainsMid() {
        Map<String, Object> eventMap = new Gson().fromJson(EventFixture.JSON_WITH_MID, Map.class);
        Event event = new Event(eventMap);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService, locationRules, Mockito.mock(Config.class));

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Map<String, Object> metadata = (Map<String, Object>) event.getMap().get("metadata");
        Assert.assertEquals(event.getMid(), metadata.get("checksum"));
    }

    @Test
    public void ShouldValidateTimestampAndCreateIfNotPresent() throws Exception {
        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("ets", 1453202865000d);
        Event event = new Event(eventMap);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService, locationRules, Mockito.mock(Config.class));

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Assert.assertEquals(true, event.getMap().containsKey("ts"));
    }

    @Test
    public void shouldRemoveDeviseStoreEntryForAllEventsWhichContainsEmptyLocation() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Device\",\"id\":\"bc811958-b4b7-4873-a43a-03718edba45b\",\"location\":{\"city\":null,\"district\":null,\"state\":null,\"country\":null}}");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService, deviceService,locationRules, Mockito.mock(Config.class));

        Event event = createEventMock("");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        when(event.isLocationPresent()).thenReturn(false);
        when(event.isLocationAbsent()).thenReturn(false);
        when(event.isLocationEmpty()).thenReturn(true);
        reverseSearchStreamTask.processEvent(event, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceService, times(1)).deleteLocation(eq("bc811958-b4b7-4873-a43a-03718edba45b"), isNull(String.class));
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    private Event createEventMock(String loc) {
        Event event = mock(Event.class);

        Map<String, Object> eks = mock(Map.class);
        when(event.getGPSCoordinates()).thenReturn(loc);
        return event;
    }

    private Map<String, Object> createMap(String loc) {
        Map<String, Object> eks = new HashMap<String, Object>();
        eks.put("loc", loc);
        Map<String, Object> edata = new HashMap<String, Object>();
        edata.put("eks", eks);
        Map<String, Object> event = new HashMap<String, Object>();
        event.put("edata", edata);
        return event;
    }

    private ChecksumGenerator getChecksumGenerator() {
        String[] keys_to_accept = {"uid", "ts", "cid", "gdata", "edata"};
        ChecksumGenerator checksumGenerator = new ChecksumGenerator(new KeysToAccept(keys_to_accept));
        return checksumGenerator;
    }
}
