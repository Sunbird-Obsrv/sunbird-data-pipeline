package org.sunbird.dp.deviceprofile.domain


import java.util

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterConfig


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

  def toMap(config: DeviceProfileUpdaterConfig): util.Map[String, String] = {
    val values = new util.HashMap[String, String]
    values.put(config.countryCode, DeviceProfile.getValueOrDefault(this.countryCode, ""))
    values.put(config.country, DeviceProfile.getValueOrDefault(this.country, ""))
    values.put(config.stateCode, DeviceProfile.getValueOrDefault(this.stateCode, ""))
    values.put(config.state, DeviceProfile.getValueOrDefault(this.state, ""))
    values.put(config.city, DeviceProfile.getValueOrDefault(this.city, ""))
    values.put(config.districtCustom, DeviceProfile.getValueOrDefault(this.districtCustom, ""))
    values.put(config.stateCustom, DeviceProfile.getValueOrDefault(this.stateCustomName, ""))
    values.put(config.stateCustomCode, DeviceProfile.getValueOrDefault(this.stateCodeCustom, ""))
    values.put(config.userDeclaredState, DeviceProfile.getValueOrDefault(this.userDeclaredState, ""))
    values.put(config.userDeclaredDistrict, DeviceProfile.getValueOrDefault(this.userDeclaredDistrict, ""))
    values.put(config.uaSpec, gson.toJson(DeviceProfile.getValueOrDefault(this.uaspec, new util.HashMap[String, String])))
    values.put(config.deviceSpec, gson.toJson(DeviceProfile.getValueOrDefault(this.devicespec, new util.HashMap[String, String])))
    values.put(config.firstAccess, DeviceProfile.getValueOrDefault(String.valueOf(this.firstAccess), ""))
    values.put(config.userDeclaredOn, DeviceProfile.getValueOrDefault(String.valueOf(this.user_declared_on), ""))
    values.put(config.apiLastUpdatedOn, DeviceProfile.getValueOrDefault(String.valueOf(this.api_last_updated_on), ""))
    values
  }

  def fromMap(map: util.Map[String, String], config: DeviceProfileUpdaterConfig): DeviceProfile = {
    this.countryCode = map.getOrDefault(config.countryCode, null)
    this.country = map.getOrDefault(config.country, "")
    this.stateCode = map.getOrDefault(config.stateCode, "")
    this.state = map.getOrDefault(config.state, "")
    this.city = map.getOrDefault(config.city, "")
    this.districtCustom = map.getOrDefault(config.districtCustom, "")
    this.stateCustomName = map.getOrDefault(config.stateCustom, "")
    this.stateCodeCustom = String.valueOf(map.getOrDefault(config.stateCustomCode, ""))
    this.userDeclaredState = map.getOrDefault(config.userDeclaredState, "")
    this.userDeclaredDistrict = map.getOrDefault(config.userDeclaredDistrict, "")
    this.uaspec = gson.fromJson(map.getOrDefault(config.uaSpec, ""), `type`)
    this.devicespec = gson.fromJson(map.getOrDefault("device_spec", ""), `type`)
    this.firstAccess = map.getOrDefault("first_access", "0").asInstanceOf[Number].longValue()
    this.user_declared_on = map.getOrDefault(config.apiLastUpdatedOn, "0").asInstanceOf[Number].longValue()
    this.api_last_updated_on = map.getOrDefault(config.apiLastUpdatedOn, "0").asInstanceOf[Number].longValue()
    this
  }
}

