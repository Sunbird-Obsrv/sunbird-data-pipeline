package org.ekstep.ep.samza.service;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.system.Event;
import org.ekstep.ep.samza.system.Location;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class LocationServiceTest {

    @Mock
    private KeyValueStore<String, Object> reverseSearchStore;
    @Mock
    private KeyValueStore<String, Object> deviceStore;
    @Mock
    private GoogleReverseSearchService googleReverseSearch;

    private LocationService locationService;

    @Before
    public void setMock() {
        initMocks(this);
        locationService = new LocationService(reverseSearchStore, googleReverseSearch);
    }

    @Test
    public void shouldGetLocationFromExternalApiIfNotPresentInCache() {
        String expectedLocationJson = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Bengaluru\",\"district\":\"Bengaluru\",\"state\":\"Karnataka\",\"country\":\"India\"}";

        Location expectedLocation = (Location) JsonReader.jsonToJava(expectedLocationJson);
        when(googleReverseSearch.getLocation("15.9310593,78.6238299")).thenReturn(expectedLocation);
        when(reverseSearchStore.get("15.9310593,78.6238299")).thenReturn(null);

        Location actualLocation = locationService.getLocation("15.9310593,78.6238299");

        assertEquals(expectedLocation, actualLocation);
        verify(googleReverseSearch, times(1)).getLocation("15.9310593,78.6238299");
        verify(reverseSearchStore, times(1)).put("15.9310593,78.6238299", expectedLocationJson);
    }

    @Test
    public void shouldTakeLocationFromCacheWhenPresent() {
        Event event = createEventMock("15.9310593,78.6238299");

        String expectedLocation = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Chennai\",\"district\":\"Chennai\",\"state\":\"Tamil Nadu\",\"country\":\"India\"}";
        when(reverseSearchStore.get("15.9310593,78.6238299")).thenReturn(expectedLocation);
        when(event.getDid()).thenReturn("bc811958-b4b7-4873-a43a-03718edba45b");

        Location actualLocation = locationService.getLocation("15.9310593,78.6238299");

        verify(reverseSearchStore, times(1)).get("15.9310593,78.6238299");
        verify(googleReverseSearch, times(0)).getLocation(anyString());

        assertEquals(expectedLocation, JsonWriter.objectToJson(actualLocation));
    }

    private Event createEventMock(String loc) {
        Event event = mock(Event.class);
        when(event.getGPSCoordinates()).thenReturn(loc);
        return event;
    }
}
