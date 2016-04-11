package org.ekstep.ep.samza.service;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.system.Area;
import org.ekstep.ep.samza.system.Location;

public class LocationService {
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

    public Location getLocation(String loc) {
        Area area = Area.findAreaLocationBelongsTo(loc, reverseSearchCacheAreaSizeInMeters);
        Location stored_location = (Location) reverseSearchStore.get(area.midpointLocationString());
        if (stored_location == null) {
            return getLocationFromGoogle(area.midpointLocationString());
        } else {
            System.out.println("Picking store data for reverse search");
            return stored_location;
        }
    }

    private Location getLocationFromGoogle(String loc) {
        System.out.println("Performing reverse search");
        Location location = googleReverseSearch.getLocation(loc);

        System.out.println("Setting device loc in stores");
        reverseSearchStore.put(loc, location);
        return location;
    }


}
