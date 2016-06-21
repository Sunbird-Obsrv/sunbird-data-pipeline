package org.ekstep.ep.samza.service;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.system.Area;
import org.ekstep.ep.samza.system.Location;

public class LocationService {

    static Logger LOGGER = new Logger(LocationService.class);
    private KeyValueStore<String, Object> reverseSearchStore;
    private GoogleReverseSearchService googleReverseSearch;
    private double reverseSearchCacheAreaSizeInMeters;

    public LocationService(KeyValueStore<String, Object> reverseSearchStore,
                           GoogleReverseSearchService googleReverseSearch,
                           double reverseSearchCacheAreaSizeInMeters) {
        this.reverseSearchStore = reverseSearchStore;
        this.googleReverseSearch = googleReverseSearch;
        this.reverseSearchCacheAreaSizeInMeters = reverseSearchCacheAreaSizeInMeters;
    }

    public Location getLocation(String loc, String eventId) {
        Area area = Area.findAreaLocationBelongsTo(loc, reverseSearchCacheAreaSizeInMeters);
        Location stored_location = (Location) reverseSearchStore.get(area.midpointLocationString());
        if (stored_location == null) {
            return getLocationFromGoogle(area.midpointLocationString(), eventId);
        } else {
            LOGGER.info(eventId, "PICKING CACHED LOCATION {}", stored_location);
            return stored_location;
        }
    }

    private Location getLocationFromGoogle(String loc, String eventId) {
        LOGGER.info(eventId, "PERFORMING REVERSE SEARCH {}", loc);
        Location location = googleReverseSearch.getLocation(loc, eventId);

        reverseSearchStore.put(loc, location);
        return location;
    }


}
