package org.sunbird.dp.deviceprofile.domain

import java.util


case class DeviceProfile(country_code: Option[String],
                         country: Option[String],
                         state_code: Option[String],
                         city: Option[String],
                         state: Option[String],
                         district_custom: Option[String],
                         state_custom: Option[String],
                         state_code_custom: Option[String],
                         user_declared_state: Option[String],
                         user_declared_district: Option[String],
                         uaspec: Option[util.Map[String, String]],
                         devicespec: Option[util.Map[String, String]],
                         firstaccess: Option[Long] = Some(0L),
                         user_declared_on: Option[Long] = Some(0L),
                         api_last_updated_on: Option[Long] = Some(0L)
                        )




