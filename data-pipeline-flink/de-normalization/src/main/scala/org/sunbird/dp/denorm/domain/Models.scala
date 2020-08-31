package org.sunbird.dp.denorm.domain

import java.util

import com.google.gson.Gson

object Models {
  val gson = new Gson()
}

class DeviceProfile(val countryCode: String, val country: String, val stateCode: String, val state: String, val city: String, 
    val districtCustom: String, val stateCodeCustom: String, val stateCustomName: String, val userDeclaredState: String, 
    val userDeclaredDistrict: String, val devicespec: util.Map[String, String], val firstaccess: java.lang.Long) {
  
}
    
 object DeviceProfile {
   
  // def apply(map: scala.collection.mutable.Map[String, String]) = new DeviceProfile(
  def apply(map: scala.collection.mutable.Map[String, AnyRef]) = new DeviceProfile(
      map.getOrElse("country_code", "").asInstanceOf[String],
      map.getOrElse("country", "").asInstanceOf[String],
      map.getOrElse("state_code", "").asInstanceOf[String],
      map.getOrElse("state", "").asInstanceOf[String],
      map.getOrElse("city", "").asInstanceOf[String],
      map.getOrElse("district_custom", "").asInstanceOf[String],
      map.getOrElse("state_code_custom", "").asInstanceOf[String],
      map.getOrElse("state_custom", "").asInstanceOf[String], map.getOrElse("user_declared_state", "").asInstanceOf[String],
      map.getOrElse("user_declared_district", "").asInstanceOf[String],
      // Models.gson.fromJson(map.getOrElse("devicespec", "{}").asInstanceOf[String], new util.HashMap[String, AnyRef]().getClass),
      map.getOrElse("devicespec", new util.HashMap[String, AnyRef]()).asInstanceOf[util.Map[String, String]],
      java.lang.Long.valueOf(map.getOrElse("firstaccess", "0").asInstanceOf[String]))
}