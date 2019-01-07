package org.ekstep.ep.samza.task;

import static org.junit.Assert.assertNotNull;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.engine.LocationEngine;
import org.ekstep.ep.samza.util.LocationCache;
import org.ekstep.ep.samza.util.LocationSearchServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LocationEngineTest {

    private CacheService<String, Location> locationStoreMock;
    private LocationSearchServiceClient searchServiceClientMock;
    private LocationCache locationCacheMock;

    private LocationEngine locationEngine;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        locationStoreMock = mock(CacheService.class);
        searchServiceClientMock = mock(LocationSearchServiceClient.class);
        locationCacheMock = mock(LocationCache.class);
        locationEngine = Mockito.spy(new LocationEngine(locationStoreMock, searchServiceClientMock, locationCacheMock));
    }

    @Test
    public void shouldReturnLocationFromCache() throws IOException {
        String channel = "0123221617357783046602";
        Location location = new Location("", "", "", "Karnataka", "");
        stub(locationStoreMock.get(channel)).toReturn(location);
        Location cacheLocation = locationEngine.getLocation(channel);

        assertNotNull(cacheLocation);
        assertEquals(cacheLocation.getState(), "Karnataka");
        assertEquals(cacheLocation.getCity(), "");
    }


    @Test
    public void shouldReturnLocationFromLocationApi() throws IOException {
        String channel = "0123221617357783046602";
        Location location = new Location("", "", "", "Karnataka", "");
        List<String> locationIds = new ArrayList<String>();
        locationIds.add("testLocationId");
        doAnswer((loc) -> {
            assertEquals(channel, loc.getArguments()[0]);
            assertEquals(location, loc.getArguments()[1]);
            return null;
        }).when(locationStoreMock).put(anyString(), any(Location.class));

        doAnswer((loc) -> locationIds).when(searchServiceClientMock).searchChannelLocationId(anyString());
        doAnswer((loc) -> location).when(searchServiceClientMock).searchLocation(locationIds);
        doAnswer((loc) -> location).when(locationEngine).loadChannelAndPopulateCache(anyString());

        stub(locationStoreMock.get(channel)).toReturn(null);

        Location resolvedLocation = locationEngine.getLocation(channel);
        when(locationStoreMock.get(channel)).thenReturn(resolvedLocation);

        assertNotNull(resolvedLocation);
        assertEquals(resolvedLocation.getState(), "Karnataka");
        assertEquals(resolvedLocation.getCity(), "");
        assertEquals(locationStoreMock.get(channel), resolvedLocation);
    }

    @Test
    public void shouldPopulateEmptyValuesInCacheIfLocationNotFound() throws IOException {
        String channel = "0123221617357783046602";
        Location location = new Location("", "", "", "", "");
        doAnswer((loc) -> {
            assertEquals(channel, loc.getArguments()[0]);
            assertEquals(location, loc.getArguments()[1]);
            return null;
        }).when(locationStoreMock).put(anyString(), any(Location.class));

        doAnswer((loc) -> null).when(searchServiceClientMock).searchChannelLocationId(anyString());
        doAnswer((loc) -> location).when(locationEngine).loadChannelAndPopulateCache(anyString());

        stub(locationStoreMock.get(channel)).toReturn(location);

        Location resolvedLocation = locationEngine.getLocation(channel);

        assertEquals(resolvedLocation.getState(), "");
        assertEquals(resolvedLocation.getCity(), "");
        assertEquals(locationStoreMock.get(channel), resolvedLocation);
    }
}
