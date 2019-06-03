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
  private Map<String, String> device_spec;
  private Long first_access;
  private Gson gson = new Gson();

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
    this.device_spec = new HashMap<>();
    this.first_access = 0L;
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
    values.put("device_spec", gson.toJson(DeviceProfile.getValueOrDefault(this.device_spec, new HashMap<>())));
    values.put("first_access", DeviceProfile.getValueOrDefault(String.valueOf(this.first_access), ""));
    return values;
  }

  public DeviceProfile fromMap(Map<String, String> map) {
    Type type = new TypeToken<Map<String, Object>>() {
    }.getType();
    this.countryCode = map.getOrDefault("country_code", "");
    this.country = map.getOrDefault("country", "");
    this.stateCode = map.getOrDefault("state_code", "");
    this.state = map.getOrDefault("state", "");
    this.city = map.getOrDefault("city", "");
    this.districtCustom = map.getOrDefault("district_custom", "");
    this.stateCustomName = map.getOrDefault("state_custom", "");
    this.stateCodeCustom = map.getOrDefault("state_code_custom", "");
    this.uaspec = gson.fromJson(map.getOrDefault("uaspec", ""), type);
    this.device_spec = gson.fromJson(map.getOrDefault("device_spec", ""), type);
    this.first_access = Long.valueOf(map.getOrDefault("first_access", "0"));
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
    this.device_spec = device_spec;
    this.first_access = first_access;
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

  public Map getDevice_spec() {
    return device_spec;
  }

  public Long getFirst_access() {
    return first_access;
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

  public void setDevice_spec(Map<String, String> device_spec) {
    this.device_spec = device_spec;
  }

  public void setFirst_access(Long first_access) {
    this.first_access = first_access;
  }

  public Boolean isLocationResolved() {
    return this.state != null && !this.state.isEmpty();
  }

  public Boolean isStateDistrictResolved() {
    return this.state != null && !this.state.isEmpty() && this.district != null && !this.district.isEmpty();
  }

  public Boolean isDeviceProfileResolved() {

    return this.isLocationResolved() || (!this.uaspec.isEmpty() || !this.device_spec.isEmpty() || this.first_access > 0);
  }

  public static <T> T getValueOrDefault(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
