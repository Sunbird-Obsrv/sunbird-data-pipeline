package org.ekstep.ep.samza.service;

import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import org.ekstep.ep.samza.api.GoogleGeoLocationAPI;
import org.ekstep.ep.samza.system.Location;
import org.ekstep.ep.samza.util.LatLongUtils;

public class GoogleReverseSearchService {

    GoogleGeoLocationAPI locationAPI;

    public GoogleReverseSearchService(GoogleGeoLocationAPI api) {
       locationAPI=api;
    }

    public Location getLocation(String loc) {
        LatLng latLng = LatLongUtils.parseLocation(loc);
        if (latLng == null){
            return null;
        }

        return locationFrom(latLng);
    }

    private Location locationFrom(LatLng latLng){
        try {
            Location location = new Location();
            GeocodingResult[] results = locationAPI.requestFor(latLng);
            for (GeocodingResult r: results) {
                if(location.isReverseSearched()){
                    break;
                }
                location=getAddressInformation(location, r);
            }
            return location;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Location getAddressInformation(Location location, GeocodingResult r) {
        for(AddressComponent a:r.addressComponents){
            if(location.isReverseSearched()){
               break;
            }
            for (AddressComponentType t: a.types) {
                if(location.isReverseSearched()){
                    break;
                }
                switch(t.ordinal()){
                    case 11:  location.setCity(a.longName);
                        break;
                    case 6:  location.setDistrict(a.longName);
                        break;
                    case 5:  location.setState(a.longName);
                        break;
                    case 4:  location.setCountry(a.longName);
                        break;
                    default: break;
                }
            }
        }
        return location;
    }
}
