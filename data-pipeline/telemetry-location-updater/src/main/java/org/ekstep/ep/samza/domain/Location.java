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

public class Location {
  private String countryCode;
  private String country;
  private String stateCode;
  private String state;
  private String city;
  private String district;

  public Location(){}
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

  public void setStateName(String state) {
    this.state = state;
  }
  public void setDistrict(String district) { this.district = district; }
  public void setCityName(String city) {
    this.city = city;
  }

  public Boolean isLocationResolved(){
    return this.state != null && !this.state.isEmpty() && this.city != null && !this.city.isEmpty();
  }

  public Boolean isStateDistrictResolved(){
    return this.state != null && !this.state.isEmpty() && this.district != null && !this.district.isEmpty();
  }
}
