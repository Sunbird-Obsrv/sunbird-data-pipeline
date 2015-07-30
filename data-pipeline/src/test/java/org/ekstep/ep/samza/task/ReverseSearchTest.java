package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.actions.GoogleReverseSearch;
import org.ekstep.ep.samza.system.Location;
import org.junit.Before;
import org.junit.Test;

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

        Map<String, Object> event = createEventMock("15.9310593,78.6238299");
        when(event.get("did")).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch);

        reverseSearchStreamTask.processEvent(event, collector);
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
        verify(googleReverseSearch, times(1)).getLocation("15.9310593,78.6238299");
    }

    @Test
    public void shouldTakeLocationFromDeviceStoreIfNotPresent() {

        when(deviceStore.get("bc811958-b4b7-4873-a43a-03718edba45b")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Device\",\"id\":\"bc811958-b4b7-4873-a43a-03718edba45b\",\"location\":{\"city\":null,\"district\":null,\"state\":null,\"country\":null}}");
        Map<String, Object> event = createEventMock(null);

        when(event.get("did")).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch);

        reverseSearchStreamTask.processEvent(event, collector);
        verify(deviceStore, times(1)).get("bc811958-b4b7-4873-a43a-03718edba45b");
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldTakeLocationfromStore(){
        Map<String, Object> event = createEventMock("15.9310593,78.6238299");

        when(reverseSearchStore.get("15.9310593,78.6238299")).thenReturn("{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Chennai\",\"district\":\"Chennai\",\"state\":\"Tamil Nadu\",\"country\": \"India\"}");
        when(event.get("did")).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask(reverseSearchStore, deviceStore, googleReverseSearch);

        reverseSearchStreamTask.processEvent(event, collector);
        verify(deviceStore, times(0)).get(anyString());
        verify(reverseSearchStore, times(1)).get("15.9310593,78.6238299");
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        verify(collector, times(1)).send(any(OutgoingMessageEnvelope.class));
    }

    @Test
    public void shouldIgnoreInValidMessages(){

        IncomingMessageEnvelope envelope = mock(IncomingMessageEnvelope.class);
        when(envelope.getMessage()).thenThrow(new RuntimeException());
        ReverseSearchStreamTask reverseSearchStreamTask = new ReverseSearchStreamTask();

        Config config = mock(Config.class);
        TaskContext context=mock(TaskContext.class);

        reverseSearchStreamTask.init(config,context);
        when(context.getStore("reverse-search")).thenReturn(reverseSearchStore);
        when(context.getStore("device")).thenReturn(deviceStore);
        TaskCoordinator task = mock(TaskCoordinator.class);
        reverseSearchStreamTask.process(envelope,collector,task);

        verify(deviceStore, times(0)).get(anyString());
        verify(reverseSearchStore, times(0)).get(anyString());
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        verify(collector, times(0)).send(any(OutgoingMessageEnvelope.class));

    }
    private Map<String, Object> createEventMock(Object loc) {
        Map<String, Object> eks = mock(Map.class);
        when(eks.get("loc")).thenReturn(loc);
        Map<String, Object> edata = mock(Map.class);
        when(edata.get("eks")).thenReturn(eks);
        Map<String, Object> event = mock(Map.class);
        when(event.get("edata")).thenReturn(edata);
        return event;
    }
}
