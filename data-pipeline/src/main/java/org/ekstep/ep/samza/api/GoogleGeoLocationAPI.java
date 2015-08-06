package org.ekstep.ep.samza.api;


import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;

public class GoogleGeoLocationAPI {
    private final GeoApiContext apiContext;

    public GoogleGeoLocationAPI(String apiKey) {
        apiContext = new GeoApiContext().setApiKey("AIzaSyDd1SVvNpqDYQKAghY1-aY2EtdBoPI94l4");
    }

    public GeocodingResult[] requestFor(LatLng latLng) {
        try {
            return GeocodingApi.newRequest(apiContext).latlng(latLng).await();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
