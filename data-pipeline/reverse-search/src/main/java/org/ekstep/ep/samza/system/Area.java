package org.ekstep.ep.samza.system;

import com.google.maps.model.LatLng;
import org.ekstep.ep.samza.util.LatLongUtils;

import java.text.NumberFormat;

public class Area {
    // This value is optimised points which are near equator
    private static final double ONE_DECIMAL_DEGREE_IN_METER_AT_EQUATOR_IN_KMS = 111320.0;
    private final double latitude;
    private final double longitude;
    private double x;
    private double y;
    private double midLongitude;
    private double midLatitude;

    public Area(double latitude, double longitude, double midLongitude, double midLatitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.midLongitude = midLongitude;
        this.midLatitude = midLatitude;
    }

    public static Area findAreaLocationBelongsTo(String loc, double areaSizeInMeters) {
        LatLng latLng = LatLongUtils.parseLocation(loc);
        double areaSize = convertAreaSizeFromMetersToDecimalDegrees(areaSizeInMeters);

        double midX = getMidPointCoordinate(latLng.lng, areaSize);
        double midY = getMidPointCoordinate(latLng.lat, areaSize);

        return new Area(latLng.lat, latLng.lng, midX, midY);
    }

    private static double getMidPointCoordinate(double value, double areaSize) {
        double areaSideLength = value < 0 ? -areaSize : areaSize;
        int squareNumber = (int) (value / areaSideLength);
        return sixDigitPrecision((squareNumber * areaSideLength) + areaSideLength / 2);
    }

    private static double sixDigitPrecision(double x) {
        final NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setMaximumFractionDigits(6);
        final String resultS = numFormat.format(x);
        resultS.replace(".", "").replace(",", ".");
        String parsable = resultS.replace(",", ".");
        return Double.parseDouble(parsable);
    }

    private static double convertAreaSizeFromMetersToDecimalDegrees(double reverseSearchCacheAreaSizeInMeters) {
        // This calculation is optimised for points near Equator
        return reverseSearchCacheAreaSizeInMeters /
                (ONE_DECIMAL_DEGREE_IN_METER_AT_EQUATOR_IN_KMS * Math.cos(0 * (Math.PI / 180)));
    }

    @Override
    public String toString() {
        return "{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", y=" + y +
                ", x=" + x +
                ", midLatitude=" + midLatitude +
                ", midLongitude=" + midLongitude +
                '}';
    }

    public String midpointLocationString() {
        return midLatitude + "," + midLongitude;
    }
}
