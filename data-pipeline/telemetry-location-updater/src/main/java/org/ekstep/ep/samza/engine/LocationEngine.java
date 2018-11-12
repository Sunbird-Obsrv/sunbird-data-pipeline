package org.ekstep.ep.samza.engine;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.util.LocationSearchServiceClient;

import java.io.IOException;

public class LocationEngine {
    private KeyValueStore<String, Location> locationStore;
    private final LocationSearchServiceClient searchService;

    public LocationEngine(KeyValueStore<String, Location> locationStore, LocationSearchServiceClient searchService) {
        this.locationStore = locationStore;
        this.searchService = searchService;
    }

    public Location getLocation(String channel) throws IOException {
        Location loc = locationStore.get(channel);
        if (loc != null) {
            return loc;
        }
        else {
            loc = loadChannelAndPopulateCache(channel);
            return loc;
        }
    }

    private Location loadChannelAndPopulateCache(String channel) throws IOException {
        Location loc = null;
        String locationId = searchService.searchChannelLocationId(channel);
        if (locationId != null) {
            loc = searchService.searchLocation(locationId);
        }
        if (loc != null) {
            locationStore.put(channel, loc);
        }
        return loc;
    }

}
