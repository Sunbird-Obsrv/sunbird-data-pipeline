package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class EventTest {

    @Test
    public void shouldUpdateEventWithAlteredETSField() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().plusDays(2).getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        Long output = input.compareAndAlterEts();
        assertTrue(output < ets);
    }

    @Test
    public void shouldNotUpdateEventWithETSField() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        Long output = input.compareAndAlterEts();
        assertTrue(output == ets);
    }

    @Test
    public void shouldReturnTrueForOlderData() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().minusMonths(7).getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        assertTrue(input.isOlder(6));
    }

    @Test
    public void shouldReturnFalseForOlderData() throws Exception {

        Map eventMap = new HashMap();
        Long ets = new DateTime().minusMonths(3).getMillis();
        eventMap.put("ets", ets);
        Event input = new Event(eventMap);
        assertFalse(input.isOlder(6));
    }

    @Test
    public void shouldReturnLatestVersion() throws Exception {

        Map eventMap = new HashMap();
        eventMap.put("ver", "3.0");
        Event input = new Event(eventMap);
        String updatedVer = input.getUpgradedVersion();
        assertTrue(updatedVer.equals("3.1"));
    }

    @Test
    public void shouldReturnSummaryLatestVersion() throws Exception {

        Map eventMap = new HashMap();
        eventMap.put("ver", "2.1");
        Event input = new Event(eventMap);
        String updatedVer = input.getUpgradedVersion();
        assertTrue(updatedVer.equals("2.2"));
    }

    @Test
    public void ShouldaddISOStateCodeToDeviceData() {

        String DEVICE_PROFILE_DETAILS = "{\n" +
                "\"fcm_token\" : \"\",\n" +
                "\"city\" : \"Bengaluru\",\n" +
                "\"device_id\" : \"232455\",\n" +
                "\"device_spec\" : \"{'os':'Android 6.0','cpu':'abi: armeabi-v7a ARMv7 Processor rev 4 (v7l)','make':'Motorola XT1706'}\",\n" +
                "\"state\" : \"Karnataka\",\n" +
                "\"uaspec\" : \"{'agent':'Chrome','ver':'76.0.3809.132','system':'Mac OSX','raw':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36'}\",\n" +
                "\"country\" : \"India\",\n" +
                "\"country_code\" : \"IN\",\n" +
                "\"producer_id\" : \"dev.sunbird.portal\",\n" +
                "\"state_code_custom\" : 29,\n" +
                "\"state_code\" : \"KA\",\n" +
                "\"state_custom\" : \"Karnataka\",\n" +
                "\"district_custom\" : \"Karnataka\",\n" +
                "\"first_access\": 1568377184000,\n" +
                "\"api_last_updated_on\": 1568377184000,\n" +
                "\"user_declared_district\" : \"Bengaluru\",\n" +
                "\"user_declared_state\" : \"Karnataka\"\n" +
                "}";

        Event event = new Event(new Gson().fromJson(DEVICE_PROFILE_DETAILS, Map.class));
        Map deviceData = new HashMap();
        deviceData.put("first_access", "1568377184000");
        deviceData.put("device_id", "232455");
        deviceData.put("statecode", "KA");
        event.addDeviceData(deviceData);
        Map result = event.addISOStateCodeToDeviceData(deviceData);
        Assert.assertEquals(result.get("iso3166statecode"), "IN-KA");

    }

}
