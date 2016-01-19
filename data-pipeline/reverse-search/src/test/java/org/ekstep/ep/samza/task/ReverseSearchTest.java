package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import com.library.checksum.system.ChecksumGenerator;
import com.library.checksum.system.Mappable;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.actions.GoogleReverseSearch;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Location;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class ReverseSearchTest {

    KeyValueStore<String, Object> reverseSearchStore;
    KeyValueStore<String, Object> deviceStore;
    GoogleReverseSearch googleReverseSearch;
    private MessageCollector collector;
    @Before
    public void setMock() {
        reverseSearchStore = mock(KeyValueStore.class);
        deviceStore = mock(KeyValueStore.class);
        googleReverseSearch = mock(GoogleReverseSearch.class);
        collector = mock(MessageCollector.class);
    }

    @Test
    public void shouldDoReverseSearchIfLocPresent() {

        when(googleReverseSearch.getLocation("15.9310593,78.6238299")).thenReturn(new Location());

        Event event = createEventMock("15.9310593,78.6238299");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch, "false");

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
        verify(googleReverseSearch, times(1)).getLocation("15.9310593,78.6238299");
    }

    @Test
    public void shouldUpdateProperFlagIfSearchReturnEmpty() {

        when(googleReverseSearch.getLocation("15.9310593,78.6238299")).thenReturn(null);
        Map<String, Object> map = createMap("15.9310593,78.6238299");
        Event event = new Event(map);
        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch, "false");

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
        verify(googleReverseSearch, times(1)).getLocation("15.9310593,78.6238299");

        Assert.assertNotNull(event.getMap());

        Map<String,Object> flagsResult = (Map<String, Object>) event.getMap().get("flags");
        Assert.assertNotNull(flagsResult);

        Assert.assertEquals(false, flagsResult.get("ldata_obtained"));
        Assert.assertEquals(true, flagsResult.get("ldata_processed"));
    }


    @Test
    public void shouldTakeLocationFromDeviceStoreIfNotPresent() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Device\",\"id\":\"bc811958-b4b7-4873-a43a-03718edba45b\",\"location\":{\"city\":null,\"district\":null,\"state\":null,\"country\":null}}");
        Event event = createEventMock("");


        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch, "false");

        reverseSearchStreamTask.processEvent(event, collector);
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        verify(deviceStore, times(1)).get("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));

        Event event1 = createEventMock("");
        when(event1.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");
        reverseSearchStreamTask.processEvent(event1, collector);
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        verify(deviceStore, times(2)).get("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(collector, times(2)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldTakeLocationfromStore(){
        Event event = createEventMock("15.9310593,78.6238299");

        when(reverseSearchStore.get("15.9310593,78.6238299")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Chennai\",\"district\":\"Chennai\",\"state\":\"Tamil Nadu\",\"country\": \"India\"}");
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch, "false");

        reverseSearchStreamTask.processEvent(event, collector);
        verify(deviceStore, times(0)).get(anyString());
        verify(reverseSearchStore, times(1)).get("15.9310593,78.6238299");
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldIgnoreInvalidMessages(){

        IncomingMessageEnvelope envelope = mock(IncomingMessageEnvelope.class);
        when(envelope.getMessage()).thenThrow(new RuntimeException());
        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch, "false");

        Config config = mock(Config.class);
        TaskContext context=mock(TaskContext.class);

        reverseSearchStreamTask.init(config, context);
        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.process(envelope, collector, task);

        verify(deviceStore, times(0)).get(anyString());
        verify(reverseSearchStore, times(0)).get(anyString());
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        verify(collector, times(0)).send(any(OutgoingMessageEnvelope.class));

    }

    @Test
    public void ShouldStampChecksumToEventIfChecksumNotPresent() throws Exception{
        Map<String, Object> eventMap = new Gson().fromJson(EventFixture.JSON, Map.class);
        Event event = new Event(eventMap);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch, "false");

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Assert.assertEquals(true, event.getMap().containsKey("metadata"));
    }

    @Test
    public void ShouldValidateTimestampAndCreateIfNotPresent() throws Exception{
        Map<String, Object> eventMap = new HashMap<String, Object>();
        eventMap.put("ets", 1453202865000L);
        Event event = new Event(eventMap);

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch, "false");

        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.processEvent(event, collector);

        Assert.assertEquals(true, event.getMap().containsKey("ts"));
    }

    private Event createEventMock(String loc) {
        Event event = mock(Event.class);

        Map<String, Object> eks = mock(Map.class);
        when(event.getGPSCoordinates()).thenReturn(loc);
        return event;
    }

    private Map<String,Object> createMap(String loc){
        Map<String, Object> eks = mock(Map.class);
        when(eks.get("loc")).thenReturn(loc);
        Map<String, Object> edata = mock(Map.class);
        when(edata.get("eks")).thenReturn(eks);
        Map<String, Object> event = new HashMap<String,Object>();
        event.put("edata",edata);
        return event;
    }
}
