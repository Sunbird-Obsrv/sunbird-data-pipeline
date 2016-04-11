package org.ekstep.ep.samza.service;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
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
        String stored_location = (String) reverseSearchStore.get(area.midpointLocationString());
        if (stored_location == null) {
            return getLocationFromGoogle(area.midpointLocationString());
        } else {
            System.out.println("Picking store data for reverse search");
            return (Location) JsonReader.jsonToJava(stored_location);
        }
    }

    private Location getLocationFromGoogle(String loc) {
        System.out.println("Performing reverse search");
        Location location = googleReverseSearch.getLocation(loc);
        String json = JsonWriter.objectToJson(location);

        System.out.println("Setting device loc in stores");
        reverseSearchStore.put(loc, json);
        return location;
    }


}
