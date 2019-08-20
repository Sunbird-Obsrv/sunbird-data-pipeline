/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ekstep.ep.samza.domain;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class DeviceProfile {
  private String countryCode;
  private String country;
  private String stateCode;
  private String state;
  private String city;
  private String district;
  private String districtCustom;
  private String stateCodeCustom;
  private String stateCustomName;
  private Map<String, String> uaspec;
  private Map<String, String> devicespec;
  private Long firstaccess;
  private Gson gson = new Gson();
  private Type type = new TypeToken<Map<String, Object>>() {}.getType();

  public DeviceProfile() {
    this.countryCode = "";
    this.country = "";
    this.stateCode = "";
    this.state = "";
    this.city = "";
    this.district = "";
    this.districtCustom = "";
    this.stateCodeCustom = "";
    this.stateCustomName = "";
    this.uaspec = new HashMap<>();
    this.devicespec = new HashMap<>();
    this.firstaccess = 0L;
  }

  public Map<String, String> toMap() {
    Map<String, String> values = new HashMap<>();
    values.put("country_code", DeviceProfile.getValueOrDefault(this.countryCode, ""));
    values.put("country", DeviceProfile.getValueOrDefault(this.country, ""));
    values.put("state_code", DeviceProfile.getValueOrDefault(this.stateCode, ""));
    values.put("state", DeviceProfile.getValueOrDefault(this.state, ""));
    values.put("city", DeviceProfile.getValueOrDefault(this.city, ""));
    values.put("district_custom", DeviceProfile.getValueOrDefault(this.districtCustom, ""));
    values.put("state_custom", DeviceProfile.getValueOrDefault(this.stateCustomName, ""));
    values.put("state_code_custom", DeviceProfile.getValueOrDefault(this.stateCodeCustom, ""));
    values.put("uaspec", gson.toJson(DeviceProfile.getValueOrDefault(this.uaspec, new HashMap<>())));
    values.put("devicespec", gson.toJson(DeviceProfile.getValueOrDefault(this.devicespec, new HashMap<>())));
    values.put("firstaccess", DeviceProfile.getValueOrDefault(String.valueOf(this.firstaccess), ""));
    return values;
  }

  public DeviceProfile fromMap(Map<String, String> map) {
    this.countryCode = map.getOrDefault("country_code", "");
    this.country = map.getOrDefault("country", "");
    this.stateCode = map.getOrDefault("state_code", "");
    this.state = map.getOrDefault("state", "");
    this.city = map.getOrDefault("city", "");
    this.districtCustom = map.getOrDefault("district_custom", "");
    this.stateCustomName = map.getOrDefault("state_custom", "");
    this.stateCodeCustom = map.getOrDefault("state_code_custom", "");
    this.uaspec = gson.fromJson(map.getOrDefault("uaspec", ""), type);
    this.devicespec = gson.fromJson(map.getOrDefault("devicespec", ""), type);
    this.firstaccess = Long.valueOf(map.getOrDefault("firstaccess", "0"));
    return this;
  }

  public DeviceProfile(String countryCode, String country, String stateCode, String state, String city) {
    this.countryCode = countryCode;
    this.country = country;
    this.stateCode = stateCode;
    this.state = state;
    this.city = city;
  }

  public DeviceProfile(String countryCode, String country, String stateCode, String state, String city, String district) {
    this.countryCode = countryCode;
    this.country = country;
    this.stateCode = stateCode;
    this.state = state;
    this.city = city;
    this.district = district;
  }

  public DeviceProfile(String countryCode, String country, String stateCode, String state,
                       String city, String districtCustom, String stateCustomName,
                       String stateCodeCustom, Map<String, String> uaspec, Map<String, String> device_spec, Long first_access) {
    this.countryCode = countryCode;
    this.country = country;
    this.stateCode = stateCode;
    this.state = state;
    this.city = city;
    this.districtCustom = districtCustom;
    this.stateCustomName = stateCustomName;
    this.stateCodeCustom = stateCodeCustom;
    this.uaspec = uaspec;
    this.devicespec = device_spec;
    this.firstaccess = first_access;
  }

  public String getCity() {
    return this.city;
  }

  public String getState() {
    return this.state;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getCountry() {
    return country;
  }

  public String getStateCode() {
    return stateCode;
  }

  public String getDistrict() {
    return district;
  }

  public String getDistrictCustom() {
    return districtCustom;
  }

  public String getstateCustomName() {
    return stateCustomName;
  }

  public String getstateCodeCustom() {
    return stateCodeCustom;
  }

  public Map<String, String> getUaspec() {
    return uaspec;
  }

  public Map getDevicespec() {
    return devicespec;
  }

  public Long getFirstaccess() {
    return firstaccess;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setDistrict(String district) {
    this.district = district;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setStateCode(String stateCode) {
    this.stateCode = stateCode;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setDistrictCustom(String districtCustom) {
    this.districtCustom = districtCustom;
  }

  public void setStateCodeCustom(String stateCodeCustom) {
    this.stateCodeCustom = stateCodeCustom;
  }

  public void setStateCustomName(String stateCustomName) {
    this.stateCustomName = stateCustomName;
  }

  public void setUaspec(Map<String, String> uaspec) {
    this.uaspec = uaspec;
  }

  public void setDevicespec(Map<String, String> devicespec) {
    this.devicespec = devicespec;
  }

  public void setFirstaccess(Long firstaccess) {
    this.firstaccess = firstaccess;
  }

  public Boolean isLocationResolved() {
    return this.state != null && !this.state.isEmpty();
  }

  public Boolean isStateDistrictResolved() {
    return this.state != null && !this.state.isEmpty() && this.district != null && !this.district.isEmpty();
  }

  public Boolean isDeviceProfileResolved() {

    return this.isLocationResolved() || (!this.uaspec.isEmpty() || !this.devicespec.isEmpty() || this.firstaccess > 0);
  }

  public static <T> T getValueOrDefault(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
