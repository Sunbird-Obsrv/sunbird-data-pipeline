package org.ekstep.ep.samza.util;

import com.google.maps.model.LatLng;

public class LatLongUtils {
    public static LatLng parseLocation(String loc){
        if (loc.isEmpty()) return null;
        String[] latlong = loc.split(",");
        if (latlong.length!=2){
            return null;
        }
        try {
            Double _lat = Double.parseDouble(latlong[0]);
            Double _long = Double.parseDouble(latlong[1]);
            return new LatLng(_lat,_long);
        }catch(NumberFormatException e){
            return null;
        }
    }
}
