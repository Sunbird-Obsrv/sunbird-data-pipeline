package org.ekstep.ep.samza.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ekstep.ep.samza.domain.DeviceProfile;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DeviceProfileTest {

    /**
     * Method should return the city, country, countryCode
     */
    @Test
    public void shouldReturnValues() {

        DeviceProfile df = new DeviceProfile("IN", "India", "KA", "Karnatka", "Bengaluru");
        DeviceProfile df2 = new DeviceProfile("IN", "India", "KA", "Karnatka", "Bengaluru");
        Assert.assertEquals("Bengaluru", df.getCity());
        Assert.assertEquals("India", df.getCountry());
        Assert.assertEquals("IN", df.getCountryCode());
        Assert.assertEquals("KA", df.getStateCode());
    }


    @Test
    public void deviceProfileValuesShouldBeEmpty() {
        DeviceProfile df = new DeviceProfile();
        Assert.assertEquals("", df.getCity());
        Assert.assertEquals("", df.getCountry());
        Assert.assertEquals("", df.getCountryCode());
        Assert.assertEquals("", df.getStateCode());
        Assert.assertEquals("", df.getDistrictCustom());
        Assert.assertEquals("", df.getstateCodeCustom());
        Assert.assertEquals("", df.getUserDeclaredState());
        Assert.assertEquals("", df.getstateCustomName());

    }

    @Test
    public void deviceProfileValuesShouldAddTheValuesToCache() {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{\"country_code\":\"IN\",\"country\":\"India\",\"state_code\":\"KA\",\"state\":\"Karnataka\",\"city\":\"Bengaluru\",\"user_declared_state\":\"Karnataka\",\"user_declared_district\":\"Tumkur\",\"district_custom\":\"BA\"}";
        try {

            // convert JSON string to Map
            Map<String, String> values = mapper.readValue(json, Map.class);
            DeviceProfile df = new DeviceProfile();
            df.fromMap(values);

            Assert.assertEquals("Bengaluru", df.getCity());
            Assert.assertEquals("India", df.getCountry());
            Assert.assertEquals("IN", df.getCountryCode());
            Assert.assertEquals("KA", df.getStateCode());
            Assert.assertEquals("BA", df.getDistrictCustom());
            Assert.assertEquals("Karnataka", df.getUserDeclaredState());
            Assert.assertEquals("Tumkur", df.getUserDeclaredDistrict());

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
