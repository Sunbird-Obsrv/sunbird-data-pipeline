package org.ekstep.ep.samza.task;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.engine.LocationEngine;
import org.ekstep.ep.samza.fixtures.EventFixture;
import org.ekstep.ep.samza.util.*;
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
    private UserLocationCache userLocationCacheMock;
    private LocationEngine locationEngine;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        locationStoreMock = mock(CacheService.class);
        searchServiceClientMock = mock(LocationSearchServiceClient.class);
        locationCacheMock = mock(LocationCache.class);
        userLocationCacheMock = mock(UserLocationCache.class);
        locationEngine = spy(new LocationEngine(locationStoreMock, searchServiceClientMock,
                locationCacheMock, userLocationCacheMock));
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
        Location location = new Location();
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

    @Test
    public void testChannelResponseBodyParsing() {
        LocationSearchServiceClient client =
                new LocationSearchServiceClient("test-channel-endpoint",
                        "test-location-endpoint", "test-api-token");
        ChannelSearchResponse response = client.parseChannelResponse(EventFixture.CHANNEL_RESPONSE_BODY);
        assertTrue(response.successful());
        assertEquals("969dd3c1-4e98-4c17-a994-559f2dc70e18", String.join("," , response.value()));

    }

    @Test
    public void testSuccessfulLocationResponseBodyParsing() {
        LocationSearchServiceClient client =
                new LocationSearchServiceClient("test-channel-endpoint",
                        "test-location-endpoint", "test-api-token");
        LocationSearchResponse response = client.parseLocationResponse(EventFixture.LOCATION_SEARCH_RESPONSE_BODY);
        assertTrue(response.successful());
        assertEquals("Karnataka", response.value().getState());
        System.out.println(response.toString());

    }

    @Test
    public void testUnsuccessfulLocationResponseBodyParsing() {
        LocationSearchServiceClient client =
                new LocationSearchServiceClient("test-channel-endpoint",
                        "test-location-endpoint", "test-api-token");
        LocationSearchResponse response = client.parseLocationResponse(EventFixture.LOCATION_SEARCH_UNSUCCESSFUL_RESPONSE);
        assertTrue(response.successful());
        assertEquals("Response {}", response.toString());

    }

    @Test
    public void testEmptyChannelResponseBodyParsing() {
        LocationSearchServiceClient client =
                new LocationSearchServiceClient("test-channel-endpoint",
                        "test-location-endpoint", "test-api-token");
        ChannelSearchResponse response = client.parseChannelResponse(EventFixture.CHANNEL_SEARCH_EMPTY_LOCATIONIDS_RESPONSE);
        assertTrue(response.successful());
        assertNull(response.value());

    }

    @Test
    public void getUserLocationShouldReturnLocation() {
        String userId = "58dcd631-fe97-4f48-b799-99c5c3e2a165";
        Location location = new Location(null, null, null, "Karnataka", "Bengaluru");
        when(userLocationCacheMock.getLocationByUser(userId)).thenReturn(location);
        locationEngine.getLocationByUser(userId);
        verify(userLocationCacheMock, times(1)).getLocationByUser(userId);
    }

    /**
     * It should return the custom location details from the cache
     */
    @Test
    public void shouldgetCustomLocationDetails() throws IOException {
        String channel = "0123221617357783046602";
        Location location = new Location("", "India", "KA", "Karnataka", "Banglore","Custom_Banglore", "Custom_karnataka","Custom_KA");
        stub(locationStoreMock.get(channel)).toReturn(location);
        Location cacheLocation = locationEngine.getLocation(channel);
        assertNotNull(cacheLocation);
        assertEquals(cacheLocation.getState(), "Karnataka");
        assertEquals(cacheLocation.getCity(), "Banglore");
        assertEquals(cacheLocation.getDistrictCustom(), "Custom_Banglore");
        assertEquals(cacheLocation.getstateCodeCustom(), "Custom_KA");
        assertEquals(cacheLocation.getstateCustomName(), "Custom_karnataka");
    }

    /**
     * It should populate the empty values in the cache if the custom location details
     * are not found in the database
     */
    @Test
    public void shouldPopulateEmptyValue() throws IOException {
        String channel = "0123221617357783046602";
        Location location = new Location();
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
        assertEquals(resolvedLocation.getDistrictCustom(), "");
        assertEquals(resolvedLocation.getstateCodeCustom(), "");
        assertEquals(resolvedLocation.getstateCustomName(), "");
    }
}
