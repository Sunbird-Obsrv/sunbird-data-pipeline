package org.sunbird.dp.fixture

object EventFixture {

  val VALID_DEVICE_PROFILE_EVENT: String =
    """
      |{
      |  "fcm_token": "",
      |  "city": "Bengaluru",
      |  "device_id": "232455",
      |  "device_spec": "{'os':'Android 6.0','cpu':'abi: armeabi-v7a ARMv7 Processor rev 4 (v7l)','make':'Motorola XT1706'}",
      |  "state": "Karnataka",
      |  "uaspec": "{'agent':'Chrome','ver':'76.0.3809.132','system':'Mac OSX','raw':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36'}",
      |  "country": "India",
      |  "country_code": "IN",
      |  "producer_id": "dev.sunbird.portal",
      |  "state_code_custom": 29,
      |  "state_code": "KA",
      |  "state_custom": "Karnataka",
      |  "district_custom": "Karnataka",
      |  "first_access": 1568377184000,
      |  "api_last_updated_on": 1568377184000,
      |  "user_declared_district": "Bengaluru",
      |  "user_declared_state": "Karnataka"
      |}""".stripMargin

  val DEVICE_PROFILE_EVENT_WITHOUT_DEVICE_ID :String =
    """
      |{
      |"fcm_token" : "",
      |"city" : "Bengaluru",
      |"device_id" : "",
      |"device_spec" : "{'os':'Android 6.0','cpu':'abi: armeabi-v7a ARMv7 Processor rev 4 (v7l)','make':'Motorola XT1706'}",
      |"state" : "Karnataka",
      |"uaspec" : "{'agent':'Chrome','ver':'76.0.3809.132','system':'Mac OSX','raw':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36'}",
      |"country" : "India",
      |"country_code" : "IN",
      |"producer_id" : "dev.sunbird.portal",
      |"state_code_custom" : 29,
      |"state_code" : "KA",
      |"state_custom" : "Karnataka",
      |"district_custom" : "Karnataka",
      |"first_access": 1568377184000,
      |"api_last_updated_on": 1568377184000,
      |"user_declared_district" : "Bengaluru",
      |"user_declared_state" : "Karnataka"
      |}""".stripMargin


    val DEVICE_PROFILE_EVENT_WITHOUT_STATE:String =
      """
        |{
        |"fcm_token" : "",
        |"city" : "Bengaluru",
        |"device_id" : "232455",
        |"device_spec" : "{'os':'Android 6.0','cpu':'abi: armeabi-v7a ARMv7 Processor rev 4 (v7l)','make':'Motorola XT1706'}",
        |"state" : "Karnataka",
        |"uaspec" : "{'agent':'Chrome','ver':'76.0.3809.132','system':'Mac OSX','raw':'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36'}",
        |"country" : "India",
        |"country_code" : "IN",
        |"producer_id" : "dev.sunbird.portal",
        |"state_code_custom" : 29,
        |"state_code" : "KA",
        |"state_custom" : "Karnataka",
        |"district_custom" : "Karnataka",
        |"first_access": 1568377184000,
        |"api_last_updated_on": 1568377184000
        |}""".stripMargin


    val DEVICE_PROFILE_EVENT_WITH_SPECIAL_CHARS:String =
      """
        |{
        |"fcm_token" : "",
        |"device_id" : "568089542",
        |"state" : "Karnataka",
        |"first_access": 1568377184000,
        |"api_last_updated_on": 1568377184000,
        |"user_declared_district" : "Bengaluru",
        |"user_declared_state" : "Karnataka's"
        |}""".stripMargin


}
