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
  private var countryCode: String = _
  private var country: String = _
  private var stateCode: String = _
  private var state: String = _
  private var city: String = _
  private var districtCustom: String = _
  private var stateCodeCustom: String = _
  private var stateCustomName: String = _
  private var userDeclaredState: String = _
  private var userDeclaredDistrict: String = _
  private var uaspec: util.HashMap[String, String] = _
  private var devicespec: util.HashMap[String, String] = _
  private var firstAccess: Long = 0L
  private var userDeclaredOn: Long = 0L
  private var apiLastUpdatedOn: Long = 0L
  private val gson = new Gson
  private val `type` = new TypeToken[util.HashMap[String, String]]() {}.getType

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
  this.userDeclaredOn = 0L
  this.apiLastUpdatedOn = 0L

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
    values.put(config.userDeclaredOn, DeviceProfile.getValueOrDefault(String.valueOf(this.userDeclaredOn), ""))
    values.put(config.apiLastUpdatedOn, DeviceProfile.getValueOrDefault(String.valueOf(this.apiLastUpdatedOn), ""))
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
    this.userDeclaredOn = map.getOrDefault(config.apiLastUpdatedOn, "0").asInstanceOf[Number].longValue()
    this.apiLastUpdatedOn = map.getOrDefault(config.apiLastUpdatedOn, "0").asInstanceOf[Number].longValue()
    this
  }
}

