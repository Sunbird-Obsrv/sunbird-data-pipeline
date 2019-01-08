package org.ekstep.ep.samza.engine;

import org.ekstep.ep.samza.cache.CacheService;
import org.ekstep.ep.samza.domain.Location;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.util.LocationCache;
import org.ekstep.ep.samza.util.LocationSearchServiceClient;

import java.io.IOException;
import java.util.List;

public class LocationEngine {
    private CacheService<String, Location> locationStore;
    private final LocationSearchServiceClient searchService;
    private final LocationCache locationCache;
    private static Logger LOGGER = new Logger(LocationEngine.class);

    public LocationEngine(CacheService<String, Location> locationStore,
                          LocationSearchServiceClient searchService, LocationCache locationCache) {
        this.locationStore = locationStore;
        this.searchService = searchService;
        this.locationCache = locationCache;
    }

    public Location getLocation(String channel) throws IOException {
        if (channel != null && !channel.isEmpty()) {
            Location loc = locationStore.get(channel);
            if (loc != null) {
                LOGGER.info("","Retrieved location from LocationStore for Channel " + channel);
                return loc;
            } else {
                loc = loadChannelAndPopulateCache(channel);
                return loc;
            }
        }
        return null;
    }

    public Location loadChannelAndPopulateCache(String channel) throws IOException {
        Location loc = null;
        List<String> locationIds = searchService.searchChannelLocationId(channel);
        if (locationIds != null) {
            loc = searchService.searchLocation(locationIds);
            LOGGER.info("", "State information retrieved from Learner API: " + loc.getState());
        }
        if (loc != null && channel != null && !channel.isEmpty()) {
            LOGGER.info( "",
                    String.format("Adding State: %s data to Location Store for Channel: %s", loc.getState(), channel));
            locationStore.put(channel, loc);
        } else {
            loc = new Location("", "", "", "", "");
            locationStore.put(channel, loc);
        }
        return loc;
    }

    public LocationCache locationCache() {
        return locationCache;
    }
}
