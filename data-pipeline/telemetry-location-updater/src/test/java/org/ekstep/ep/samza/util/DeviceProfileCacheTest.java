package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.domain.DeviceProfile;
import org.junit.Assert;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class DeviceProfileCacheTest {


    private DeviceProfileCache cache;

    @Test
    public void shouldReturnNullWhenDeviceIdIsNotPresent() {
        cache = mock(DeviceProfileCache.class);
        stub(cache.getDeviceProfileFromCache("test")).toReturn(null);
        Assert.assertEquals(cache.getDeviceProfileFromCache("564783-5439"), null);
    }

    @Test
    public void shouldReturnProfileInfo() {
        DeviceProfile profile = new DeviceProfile("IN", "India", "KA", "Karnataka",
                "Bengaluru");
        cache = mock(DeviceProfileCache.class);
        stub(cache.getDeviceProfileFromCache("37654387-543534")).toReturn(profile);
        DeviceProfile res = cache.getDeviceProfileFromCache("37654387-543534");
        Assert.assertEquals(res.getCountryCode(), "IN");
        Assert.assertEquals(res.getCountry(), "India");
        Assert.assertEquals(res.getStateCode(), "KA");
        Assert.assertEquals(res.getState(), "Karnataka");
        Assert.assertEquals(res.getCity(), "Bengaluru");

    }


}
