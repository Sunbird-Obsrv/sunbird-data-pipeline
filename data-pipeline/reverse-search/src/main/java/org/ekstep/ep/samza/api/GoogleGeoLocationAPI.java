package org.ekstep.ep.samza.api;


import org.ekstep.ep.samza.core.Logger;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class GoogleGeoLocationAPI {
    static Logger LOGGER = new Logger(GoogleGeoLocationAPI.class);

    private final GeoApiContext apiContext;

    public GoogleGeoLocationAPI(String apiKey) {
        apiContext = new GeoApiContext().setApiKey(apiKey);
    }

    public GeocodingResult[] requestFor(LatLng latLng, String eventId) {
        try {
            return GeocodingApi.newRequest(apiContext).latlng(latLng).await();
        } catch (Exception e) {
            LOGGER.error(eventId, "GEO_LOCATION_API ERROR", e);
        }
        return null;
    }
}
