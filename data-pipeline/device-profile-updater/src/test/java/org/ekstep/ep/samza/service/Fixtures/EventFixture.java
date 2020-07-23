package org.ekstep.ep.samza.service.Fixtures;

public class EventFixture {
    public static final String DEVICE_PROFILE_DETAILS = "{\n" +
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

    public static final String DEVICE_PROFILE_WITH_STATE_NULL = "{\n" +
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
            "\"api_last_updated_on\": 1568377184000\n" +
            "}";

    public static final String DEVICE_PROFILE_WITH_NO_DEVICE_ID = "{\n" +
            "\"fcm_token\" : \"\",\n" +
            "\"city\" : \"Bengaluru\",\n" +
            "\"device_id\" : \"\",\n" +
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

    public static final String DEVICE_PROFILE_DETAILS_WITH_SPECIAL_CHAR = "{\n" +
            "\"fcm_token\" : \"\",\n" +
            "\"device_id\" : \"568089542\",\n" +
            "\"state\" : \"Karnataka\",\n" +
            "\"first_access\": 1568377184000,\n" +
            "\"api_last_updated_on\": 1568377184000,\n" +
            "\"user_declared_district\" : \"Bengaluru\",\n" +
            "\"user_declared_state\" : \"Karnataka's\"\n" +
            "}";

    public static final String DEVICE_PROFILE_DETAILS_WITH_SPACE_CHAR = "{\n" +
            "\"fcm_token\" : \"\",\n" +
            "\"device_id\" : \"test-did\",\n" +
            "\"state\" : \"Karnataka\",\n" +
            "\"first_access\": 1568377184000,\n" +
            "\"api_last_updated_on\": 1568377184000,\n" +
            "\"user_declared_district\" : \"BENGALURU URBAN SOUTH\",\n" +
            "\"user_declared_state\" : \"Karnataka\"\n" +
            "}";

    public static final String MALFORMED_DEVICE_PROFILE_OBJECT= "{\"fcm_token\":\"\",\"city\":\"Bengaluru\",\"device_id\":\"232455\"\"device_spec”:”{‘make’}”,”state\":\"Karnataka\",\"uaspec\":\"{'agent':'Chrome','ver':'76.0.3809.132','system':'Mac OSX','raw':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36'}\",\"country\":\"India\",\"updated_date\":1567765886277,\"country_code\":\"IN\",\"producer_id\":\"dev.sunbird.portal\",\"state_code_custom\":\"29\",\"state_code\":\"KA\",\"state_custom\":\"Karnataka\",\"district_custom\":\"null\"}";
}

