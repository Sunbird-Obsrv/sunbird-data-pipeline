package org.sunbird.dp.core.denorm.domain

import java.util

import com.google.gson.Gson

object Models {
  val gson = new Gson()
}

class DeviceProfile(val countryCode: String, val country: String, val stateCode: String, val state: String, val city: String, 
    val districtCustom: String, val stateCodeCustom: String, val stateCustomName: String, val userDeclaredState: String, 
    val userDeclaredDistrict: String, val devicespec: util.Map[String, String], val firstaccess: Long) {
  
}
    
 object DeviceProfile {
   
  def apply(map: scala.collection.mutable.Map[String, String]) = new DeviceProfile(
      map.getOrElse("country_code", ""), map.getOrElse("country", ""), map.getOrElse("state_code", ""),
      map.getOrElse("state", ""), map.getOrElse("city", ""), map.getOrElse("district_custom", ""),
      map.getOrElse("state_code_custom", ""), map.getOrElse("state_custom", ""), map.getOrElse("user_declared_state", ""),
      map.getOrElse("user_declared_district", ""), Models.gson.fromJson(map.getOrElse("devicespec", "{}"), new util.HashMap[String, AnyRef]().getClass), map.getOrElse("firstaccess", "0").toLong)
  
}