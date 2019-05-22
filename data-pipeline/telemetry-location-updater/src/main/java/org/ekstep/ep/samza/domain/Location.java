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

import java.util.HashMap;
import java.util.Map;

public class Location {
  private String countryCode;
  private String country;
  private String stateCode;
  private String state;
  private String city;
  private String district;
  private String districtCustom;
  private String stateCodeCustom;
  private String stateCustomName;

  public Location() {
    this.countryCode = "";
    this.country = "";
    this.stateCode = "";
    this.state = "";
    this.city = "";
    this.district = "";
    this.districtCustom = "";
    this.stateCodeCustom = "";
    this.stateCustomName = "";
  }

  public Map<String, String> toMap() {
    Map<String, String> values = new HashMap<>();
    values.put("country_code", Location.getValueOrDefault(this.countryCode, ""));
    values.put("country", Location.getValueOrDefault(this.country, ""));
    values.put("state_code", Location.getValueOrDefault(this.stateCode, ""));
    values.put("state", Location.getValueOrDefault(this.state, ""));
    values.put("city", Location.getValueOrDefault(this.city, ""));
    values.put("district_custom", Location.getValueOrDefault(this.districtCustom, ""));
    values.put("state_custom", Location.getValueOrDefault(this.stateCustomName, ""));
    values.put("state_code_custom", Location.getValueOrDefault(this.stateCodeCustom, ""));
    return values;
  }

  public Location fromMap(Map<String, String> map) {
    Location location = new Location();
    this.countryCode = map.getOrDefault("country_code", "");
    this.country = map.getOrDefault("country", "");
    this.stateCode = map.getOrDefault("state_code", "");
    this.state = map.getOrDefault("state", "");
    this.city = map.getOrDefault("city", "");
    this.districtCustom = map.getOrDefault("district_custom", "");
    this.stateCustomName = map.getOrDefault("state_custom", "");
    this.stateCodeCustom = map.getOrDefault("state_code_custom", "");
    return location;
  }

  public Location(String countryCode, String country, String stateCode, String state, String city) {
    this.countryCode = countryCode;
    this.country = country;
    this.stateCode = stateCode;
    this.state = state;
    this.city = city;
  }

  public Location(String countryCode, String country, String stateCode, String state, String city, String district) {
    this.countryCode = countryCode;
    this.country = country;
    this.stateCode = stateCode;
    this.state = state;
    this.city = city;
    this.district = district;
  }

  public Location (String countryCode, String country, String stateCode, String state, String city, String districtCustom, String stateCustomName, String stateCodeCustom){
    this.countryCode = countryCode;
    this.country = country;
    this.stateCode = stateCode;
    this.state = state;
    this.city = city;
    this.districtCustom = districtCustom;
    this.stateCustomName = stateCustomName;
    this.stateCodeCustom = stateCodeCustom;
  }

  public String getCity(){
    return this.city;
  }
  public String getState(){
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
  public String getDistrict() { return district; }
  public String getDistrictCustom() {return districtCustom;}
  public String getstateCustomName() {return stateCustomName;}
  public String getstateCodeCustom() {return stateCodeCustom;}

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

  public Boolean isLocationResolved(){
    return this.state != null && !this.state.isEmpty();
  }

  public Boolean isStateDistrictResolved(){
    return this.state != null && !this.state.isEmpty() && this.district != null && !this.district.isEmpty();
  }

  public static <T> T getValueOrDefault(T value, T defaultValue) {
    return value == null ? defaultValue : value;
  }
}
