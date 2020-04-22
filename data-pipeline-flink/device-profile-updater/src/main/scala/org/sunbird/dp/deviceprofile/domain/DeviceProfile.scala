package org.sunbird.dp.deviceprofile.domain


import java.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object DeviceProfile {
  def getValueOrDefault[T](value: T, defaultValue: T): T = if (value == null) defaultValue
  else value
}

class DeviceProfile() {
  this.countryCode = ""
  this.country = ""
  this.stateCode = ""
  this.state = ""
  this.city = ""
  this.districtCustom = ""
  this.stateCodeCustom = ""
  this.stateCustomName = ""
  this.userDeclaredState = ""
  this.userDeclaredDistrict = ""
  this.uaspec = new util.HashMap[String, String]
  this.devicespec = new util.HashMap[String, String]
  this.firstAccess = 0L
  this.user_declared_on = 0L
  this.api_last_updated_on = 0L
  private var countryCode: String = null
  private var country: String = null
  private var stateCode: String = null
  private var state: String = null
  private var city: String = null
  private var districtCustom: String = null
  private var stateCodeCustom: String = null
  private var stateCustomName: String = null
  private var userDeclaredState: String = null
  private var userDeclaredDistrict: String = null
  private var uaspec: util.HashMap[String, String] = null
  private var devicespec: util.HashMap[String, String] = null
  private var firstAccess: Long = 0L
  private var user_declared_on: Long = 0L
  private var api_last_updated_on: Long = 0L
  private val gson = new Gson
  private val `type` = new TypeToken[util.HashMap[String, String]]() {}.getType

  def toMap: util.Map[String, String] = {
    val values = new util.HashMap[String, String]
    values.put("country_code", DeviceProfile.getValueOrDefault(this.countryCode, ""))
    values.put("country", DeviceProfile.getValueOrDefault(this.country, ""))
    values.put("state_code", DeviceProfile.getValueOrDefault(this.stateCode, ""))
    values.put("state", DeviceProfile.getValueOrDefault(this.state, ""))
    values.put("city", DeviceProfile.getValueOrDefault(this.city, ""))
    values.put("district_custom", DeviceProfile.getValueOrDefault(this.districtCustom, ""))
    values.put("state_custom", DeviceProfile.getValueOrDefault(this.stateCustomName, ""))
    values.put("state_code_custom", DeviceProfile.getValueOrDefault(this.stateCodeCustom, ""))
    values.put("user_declared_state", DeviceProfile.getValueOrDefault(this.userDeclaredState, ""))
    values.put("user_declared_district", DeviceProfile.getValueOrDefault(this.userDeclaredDistrict, ""))
    values.put("uaspec", gson.toJson(DeviceProfile.getValueOrDefault(this.uaspec, new util.HashMap[String, String])))
    values.put("devicespec", gson.toJson(DeviceProfile.getValueOrDefault(this.devicespec, new util.HashMap[String, String])))
    values.put("firstaccess", DeviceProfile.getValueOrDefault(String.valueOf(this.firstAccess), ""))
    values.put("user_declared_on", DeviceProfile.getValueOrDefault(String.valueOf(this.user_declared_on), ""))
    values.put("api_last_updated_on", DeviceProfile.getValueOrDefault(String.valueOf(this.api_last_updated_on), ""))
    values
  }

  def fromMap(map: util.Map[String, String]): DeviceProfile = {
    this.countryCode = map.getOrDefault("country_code", null)
    this.country = map.getOrDefault("country", "")
    this.stateCode = map.getOrDefault("state_code", "")
    this.state = map.getOrDefault("state", "")
    this.city = map.getOrDefault("city", "")
    this.districtCustom = map.getOrDefault("district_custom", "")
    this.stateCustomName = map.getOrDefault("state_custom", "")
    this.stateCodeCustom = String.valueOf(map.getOrDefault("state_code_custom", ""))
    this.userDeclaredState = map.getOrDefault("user_declared_state", "")
    this.userDeclaredDistrict = map.getOrDefault("user_declared_district", "")
    this.uaspec = gson.fromJson(map.getOrDefault("uaspec", ""), `type`)
    this.devicespec = gson.fromJson(map.getOrDefault("device_spec", ""), `type`)
    this.firstAccess = map.getOrDefault("first_access", "0").asInstanceOf[Number].longValue()
    this.user_declared_on = map.getOrDefault("api_last_updated_on", "0").asInstanceOf[Number].longValue()
    this.api_last_updated_on = map.getOrDefault("api_last_updated_on", "0").asInstanceOf[Number].longValue()
    this
  }
}

