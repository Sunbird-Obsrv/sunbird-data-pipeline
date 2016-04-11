package org.ekstep.ep.samza.service;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.samza.storage.kv.KeyValueStore;
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
        locationService = new LocationService(reverseSearchStore, googleReverseSearch, 200);
    }

    @Test
    public void shouldGetLocationFromExternalApiIfNotPresentInCache() {
        String preciseLocation = "12.972455, 77.580655";
        String nearestCacheLocation = "12.972512,77.580848";
        String expectedLocationJson = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Bengaluru\",\"district\":\"Bengaluru\",\"state\":\"Karnataka\",\"country\":\"India\"}";
        Location expectedLocation = (Location) JsonReader.jsonToJava(expectedLocationJson);
        when(googleReverseSearch.getLocation(nearestCacheLocation)).thenReturn(expectedLocation);
        when(reverseSearchStore.get(nearestCacheLocation)).thenReturn(null);

        Location actualLocation = locationService.getLocation(preciseLocation);

        assertEquals(expectedLocation, actualLocation);
        verify(googleReverseSearch, times(1)).getLocation(nearestCacheLocation);
        verify(reverseSearchStore, times(1)).put(nearestCacheLocation, expectedLocation);
    }

    @Test
    public void shouldTakeLocationFromCacheWhenPresent() {
        String preciseLocation = "12.972442, 77.580643";
        String nearestCacheLocation = "12.972512,77.580848";
        String expectedLocationJson = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Bengaluru\",\"district\":\"Bengaluru\",\"state\":\"Karnataka\",\"country\":\"India\"}";
        Location expectedLocation = (Location) JsonReader.jsonToJava(expectedLocationJson);
        when(reverseSearchStore.get(nearestCacheLocation)).thenReturn(expectedLocation);

        Location actualLocation = locationService.getLocation(preciseLocation);

        verify(reverseSearchStore, times(1)).get(nearestCacheLocation);
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        assertEquals(expectedLocationJson, JsonWriter.objectToJson(actualLocation));
    }

    // Lisbon - 38.736946, -9.142685
    // London - 51.5085300, -0.1257400
    // Madrid - 40.4165000, -3.7025600
    @Test
    public void shouldGetAppropriateCoordinatesIfLatitudeIsNegative() {
        String preciseLocation = "-37.813611,144.963056";
        String nearestCacheLocation = "-37.814409,144.963169";
        String expectedLocationJson = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Melbourne\",\"district\":\"Melbourne\",\"state\":\"Melbourne\",\"country\":\"Australia\"}";
        Location expectedLocation = (Location) JsonReader.jsonToJava(expectedLocationJson);
        when(googleReverseSearch.getLocation(nearestCacheLocation)).thenReturn(expectedLocation);
        when(reverseSearchStore.get(nearestCacheLocation)).thenReturn(null);

        Location actualLocation = locationService.getLocation(preciseLocation);

        verify(googleReverseSearch, times(1)).getLocation(nearestCacheLocation);
        verify(reverseSearchStore, times(1)).put(nearestCacheLocation, expectedLocation);
        assertEquals(expectedLocationJson, JsonWriter.objectToJson(actualLocation));
    }

    @Test
    public void shouldGetAppropriateCoordinatesIfLongitudeIsNegative() {
        String preciseLocation = "40.7142700,-74.0059700";
        String nearestCacheLocation = "40.714157,-74.00557";
        String expectedLocationJson = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"New York\",\"district\":\"New York\",\"state\":\"New York\",\"country\":\"USA\"}";
        Location expectedLocation = (Location) JsonReader.jsonToJava(expectedLocationJson);
        when(googleReverseSearch.getLocation(nearestCacheLocation)).thenReturn(expectedLocation);
        when(reverseSearchStore.get(nearestCacheLocation)).thenReturn(null);

        Location actualLocation = locationService.getLocation(preciseLocation);

        verify(googleReverseSearch, times(1)).getLocation(nearestCacheLocation);
        verify(reverseSearchStore, times(1)).put(nearestCacheLocation, expectedLocation);
        assertEquals(expectedLocationJson, JsonWriter.objectToJson(actualLocation));
    }

    @Test
    public void shouldGetAppropriateCoordinatesIfBothLatitudeLongitudeAreNegative() {
        String preciseLocation = "-12.043333, -77.028333";
        String nearestCacheLocation = "-12.043658,-77.027488";
        String expectedLocationJson = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Lima\",\"district\":\"Lima\",\"state\":\"Lima\",\"country\":\"Peru\"}";
        Location expectedLocation = (Location) JsonReader.jsonToJava(expectedLocationJson);
        when(googleReverseSearch.getLocation(nearestCacheLocation)).thenReturn(expectedLocation);
        when(reverseSearchStore.get(nearestCacheLocation)).thenReturn(null);

        Location actualLocation = locationService.getLocation(preciseLocation);

        verify(googleReverseSearch, times(1)).getLocation(nearestCacheLocation);
        verify(reverseSearchStore, times(1)).put(nearestCacheLocation, expectedLocation);
        assertEquals(expectedLocationJson, JsonWriter.objectToJson(actualLocation));
    }
}
