package org.ekstep.ep.samza.service;


import com.google.gson.Gson;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.ekstep.ep.samza.api.GoogleGeoLocationAPI;
import org.ekstep.ep.samza.system.Location;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleReverseSearchServiceTest {
    @Test
    public void shouldHandleEmptyLatlong(){
        GoogleGeoLocationAPI api = mock(GoogleGeoLocationAPI.class);

        GoogleReverseSearchService reverseSearch = new GoogleReverseSearchService(api);
        Assert.assertNull(reverseSearch.getLocation(""));
    }

    @Test
    public void shouldReturnNullForInvalidLatLong(){
        GoogleGeoLocationAPI api = mock(GoogleGeoLocationAPI.class);

        GoogleReverseSearchService reverseSearch = new GoogleReverseSearchService(api);
        Assert.assertNull(reverseSearch.getLocation("12"));
        Assert.assertNull(reverseSearch.getLocation("12:123"));
        Assert.assertNull(reverseSearch.getLocation("12:123,345"));
        Assert.assertNull(reverseSearch.getLocation("invalid,format"));
    }

    @Test
    public void shouldReturnValidLocation(){
        GoogleGeoLocationAPI api = mock(GoogleGeoLocationAPI.class);

        when(api.requestFor(any(LatLng.class))).thenReturn(generateResult());

        GoogleReverseSearchService reverseSearch = new GoogleReverseSearchService(api);
        Location location = reverseSearch.getLocation("12.90,77.62");
        Assert.assertNotNull(location);
        Assert.assertEquals("Bengaluru", location.getCity());
    }

    @Test
    public void shouldReturnNullForException(){
        GoogleGeoLocationAPI api = mock(GoogleGeoLocationAPI.class);

        when(api.requestFor(any(LatLng.class))).thenThrow(new RuntimeException());

        GoogleReverseSearchService reverseSearch = new GoogleReverseSearchService(api);
        Location location = reverseSearch.getLocation("12.90,77.62");
        Assert.assertNull(location);
    }
    private GeocodingResult[] generateResult(){
        GeocodingResult geocodingResult1 = new Gson().fromJson("{\"addressComponents\":[{\"longName\":\"86-87\",\"shortName\":\"86-87\",\"types\":[\"STREET_NUMBER\"]},{\"longName\":\"7th Cross Road\",\"shortName\":\"7th Cross Rd\",\"types\":[\"ROUTE\"]},{\"longName\":\"Koramangala 3 Block\",\"shortName\":\"Koramangala 3 Block\",\"types\":[\"SUBLOCALITY_LEVEL_2\",\"SUBLOCALITY\",\"POLITICAL\"]},{\"longName\":\"Koramangala\",\"shortName\":\"Koramangala\",\"types\":[\"SUBLOCALITY_LEVEL_1\",\"SUBLOCALITY\",\"POLITICAL\"]},{\"longName\":\"Bangalore Urban\",\"shortName\":\"Bangalore Urban\",\"types\":[\"ADMINISTRATIVE_AREA_LEVEL_2\",\"POLITICAL\"]},{\"longName\":\"Karnataka\",\"shortName\":\"KA\",\"types\":[\"ADMINISTRATIVE_AREA_LEVEL_1\",\"POLITICAL\"]},{\"longName\":\"India\",\"shortName\":\"IN\",\"types\":[\"COUNTRY\",\"POLITICAL\"]},{\"longName\":\"560034\",\"shortName\":\"560034\",\"types\":[\"POSTAL_CODE\"]}],\"formattedAddress\":\"86-87, 7th Cross Road, Koramangala 3 Block, Koramangala, Bengaluru, Karnataka 560034, India\",\"geometry\":{\"bounds\":{\"northeast\":{\"lat\":12.931308,\"lng\":77.6242031},\"southwest\":{\"lat\":12.9309016,\"lng\":77.6231573}},\"location\":{\"lat\":12.931065,\"lng\":77.6238324},\"locationType\":\"RANGE_INTERPOLATED\",\"viewport\":{\"northeast\":{\"lat\":12.9324537802915,\"lng\":77.62502918029151},\"southwest\":{\"lat\":12.9297558197085,\"lng\":77.6223312197085}}},\"placeId\":\"Els4Ni04NywgN3RoIENyb3NzIFJvYWQsIEtvcmFtYW5nYWxhIDMgQmxvY2ssIEtvcmFtYW5nYWxhLCBCZW5nYWx1cnUsIEthcm5hdGFrYSA1NjAwMzQsIEluZGlh\",\"types\":[\"street_address\"]}",GeocodingResult.class);
        GeocodingResult geocodingResult2 = new Gson().fromJson("{\"addressComponents\":[{\"longName\":\"86-87\",\"shortName\":\"86-87\",\"types\":[\"STREET_NUMBER\"]},{\"longName\":\"7th Cross Road\",\"shortName\":\"7th Cross Rd\",\"types\":[\"ROUTE\"]},{\"longName\":\"Koramangala 3 Block\",\"shortName\":\"Koramangala 3 Block\",\"types\":[\"SUBLOCALITY_LEVEL_2\",\"SUBLOCALITY\",\"POLITICAL\"]},{\"longName\":\"Koramangala\",\"shortName\":\"Koramangala\",\"types\":[\"SUBLOCALITY_LEVEL_1\",\"SUBLOCALITY\",\"POLITICAL\"]},{\"longName\":\"Bengaluru\",\"shortName\":\"Bengaluru\",\"types\":[\"LOCALITY\",\"POLITICAL\"]},{\"longName\":\"Bangalore Urban\",\"shortName\":\"Bangalore Urban\",\"types\":[\"ADMINISTRATIVE_AREA_LEVEL_2\",\"POLITICAL\"]},{\"longName\":\"Karnataka\",\"shortName\":\"KA\",\"types\":[\"ADMINISTRATIVE_AREA_LEVEL_1\",\"POLITICAL\"]},{\"longName\":\"India\",\"shortName\":\"IN\",\"types\":[\"COUNTRY\",\"POLITICAL\"]},{\"longName\":\"560034\",\"shortName\":\"560034\",\"types\":[\"POSTAL_CODE\"]}],\"formattedAddress\":\"86-87, 7th Cross Road, Koramangala 3 Block, Koramangala, Bengaluru, Karnataka 560034, India\",\"geometry\":{\"bounds\":{\"northeast\":{\"lat\":12.931308,\"lng\":77.6242031},\"southwest\":{\"lat\":12.9309016,\"lng\":77.6231573}},\"location\":{\"lat\":12.931065,\"lng\":77.6238324},\"locationType\":\"RANGE_INTERPOLATED\",\"viewport\":{\"northeast\":{\"lat\":12.9324537802915,\"lng\":77.62502918029151},\"southwest\":{\"lat\":12.9297558197085,\"lng\":77.6223312197085}}},\"placeId\":\"Els4Ni04NywgN3RoIENyb3NzIFJvYWQsIEtvcmFtYW5nYWxhIDMgQmxvY2ssIEtvcmFtYW5nYWxhLCBCZW5nYWx1cnUsIEthcm5hdGFrYSA1NjAwMzQsIEluZGlh\",\"types\":[\"street_address\"]}",GeocodingResult.class);
        return new GeocodingResult[]{geocodingResult1,geocodingResult2};
    }
}
