package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.library.checksum.system.ChecksumGenerator;
import com.library.checksum.system.KeysToAccept;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.service.LocationService;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReverseSearchTest {

    @Mock
    KeyValueStore<String, Object> deviceStore;
    @Mock
    LocationService locationService;
    @Mock
    private MessageCollector collector;

    @Before
    public void setMock() {
        initMocks(this);
    }

    @Test
    public void shouldDoReverseSearchIfLocPresent() {
        when(locationService.getLocation("15.9310593,78.6238299", null)).thenReturn(new Location());
        Event event = createEventMock("15.9310593,78.6238299");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore,
                "false", locationService);

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
        verify(locationService, times(1)).getLocation("15.9310593,78.6238299", null);
    }

    @Test
    public void shouldUpdateProperFlagIfSearchReturnEmpty() {

        when(locationService.getLocation("15.9310593,78.6238299", null)).thenReturn(null);
        Map<String, Object> map = createMap("15.9310593,78.6238299");
        Event event = new Event(map);
        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

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

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        reverseSearchStreamTask.processEvent(event, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceStore, times(1)).get("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));

        Event event1 = createEventMock("");
        when(event1.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        reverseSearchStreamTask.processEvent(event1, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceStore, times(2)).get("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldNotFailIfLocationInDeviceStoreIsNotPresent() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn(null);
        Event event = createEventMock("");

        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        reverseSearchStreamTask.processEvent(event, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceStore, times(1)).get("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldNotFailIfDeviseIdIsNull() {

        when(deviceStore.get(null)).thenThrow(new NullPointerException("Null is not a valid key"));
        Event event = createEventMock("");

        when(event.getDid()).thenReturn(null);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldIgnoreInvalidMessages() {

        IncomingMessageEnvelope envelope = mock(IncomingMessageEnvelope.class);
        when(envelope.getMessage()).thenThrow(new RuntimeException());
        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

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

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Assert.assertEquals(true, event.getMap().containsKey("metadata"));
    }

    @Test
    public void ShouldUseMidAsChecksumIfEventContainsMid() {
        Map<String, Object> eventMap = new Gson().fromJson(EventFixture.JSON_WITH_MID, Map.class);
        Event event = new Event(eventMap);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Map<String, Object> metadata = (Map<String, Object>) event.getMap().get("metadata");
        Assert.assertEquals(event.getMid(), metadata.get("checksum"));
    }

    @Test
    public void ShouldValidateTimestampAndCreateIfNotPresent() throws Exception {
        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("ets", 1453202865000L);
        Event event = new Event(eventMap);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Assert.assertEquals(true, event.getMap().containsKey("ts"));
    }

    @Test
    public void shouldRemoveDeviseStoreEntryIfSessionStartContainsEmptyLocation() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Device\",\"id\":\"bc811958-b4b7-4873-a43a-03718edba45b\",\"location\":{\"city\":null,\"district\":null,\"state\":null,\"country\":null}}");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        Event event = createEventMock("");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        when(event.shouldRemoveDeviceStoreEntry()).thenReturn(true);
        reverseSearchStreamTask.processEvent(event, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceStore, times(1)).delete("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldNotRemoveDeviseStoreEntryForOtherEventsWhichContainsEmptyLocation() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Device\",\"id\":\"bc811958-b4b7-4873-a43a-03718edba45b\",\"location\":{\"city\":null,\"district\":null,\"state\":null,\"country\":null}}");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(deviceStore, "false", locationService);

        Event event = createEventMock("");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        when(event.shouldRemoveDeviceStoreEntry()).thenReturn(false);
        reverseSearchStreamTask.processEvent(event, collector);
        verify(locationService, times(0)).getLocation(anyString(), isNull(String.class));
        verify(deviceStore, times(0)).delete("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
    }

    private Event createEventMock(String loc) {
        Event event = mock(Event.class);

        Map<String, Object> eks = mock(Map.class);
        when(event.getGPSCoordinates()).thenReturn(loc);
        return event;
    }

    private Map<String, Object> createMap(String loc) {
        Map<String, Object> eks = mock(Map.class);
        when(eks.get("loc")).thenReturn(loc);
        Map<String, Object> edata = mock(Map.class);
        when(edata.get("eks")).thenReturn(eks);
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
