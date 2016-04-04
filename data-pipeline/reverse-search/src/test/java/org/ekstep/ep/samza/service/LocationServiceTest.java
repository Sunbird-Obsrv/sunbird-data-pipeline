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
        verify(reverseSearchStore, times(1)).put(nearestCacheLocation, expectedLocationJson);
    }

    @Test
    public void shouldTakeLocationFromCacheWhenPresent() {
        String preciseLocation = "12.972442, 77.580643";
        String nearestCacheLocation = "12.972512,77.580848";
        String expectedLocationJson = "{\"@type\":\"org.ekstep.ep.samza.system.Location\",\"city\":\"Bengaluru\",\"district\":\"Bengaluru\",\"state\":\"Karnataka\",\"country\":\"India\"}";
        when(reverseSearchStore.get(nearestCacheLocation)).thenReturn(expectedLocationJson);

        Location actualLocation = locationService.getLocation(preciseLocation);

        verify(reverseSearchStore, times(1)).get(nearestCacheLocation);
        verify(googleReverseSearch, times(0)).getLocation(anyString());
        assertEquals(expectedLocationJson, JsonWriter.objectToJson(actualLocation));
    }
}
