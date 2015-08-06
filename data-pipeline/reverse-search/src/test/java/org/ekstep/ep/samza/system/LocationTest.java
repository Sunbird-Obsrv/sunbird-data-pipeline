package org.ekstep.ep.samza.system;

import org.junit.Assert;
import org.junit.Test;

public class LocationTest {

    @Test
    public void reverseSearchedFalseIfAllDataNotAvailable(){
        Location loc = new Location();
        Assert.assertFalse(loc.isReverseSearched());
    }
    @Test
    public void reverseSearchedTrueIfAllDataAvailable(){
        Location location = new Location();
        location.setCity("City1");
        Assert.assertFalse(location.isReverseSearched());
        location.setCountry("India");
        Assert.assertFalse(location.isReverseSearched());
        location.setDistrict("Dist1");
        Assert.assertFalse(location.isReverseSearched());
        location.setState("State1");
        Assert.assertTrue(location.isReverseSearched());
    }
}
