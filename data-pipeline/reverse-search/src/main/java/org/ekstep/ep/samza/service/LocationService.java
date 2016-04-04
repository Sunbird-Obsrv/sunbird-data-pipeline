package org.ekstep.ep.samza.service;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.system.Location;

public class LocationService {
    private KeyValueStore<String, Object> reverseSearchStore;
    private GoogleReverseSearchService googleReverseSearch;

    public LocationService(KeyValueStore<String, Object> reverseSearchStore, GoogleReverseSearchService googleReverseSearch) {
        this.reverseSearchStore = reverseSearchStore;
        this.googleReverseSearch = googleReverseSearch;
    }

    public Location getLocation(String loc) {
        String stored_location = (String) reverseSearchStore.get(loc);
        if (stored_location == null) {
            return getLocationFromGoogle(loc);
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
