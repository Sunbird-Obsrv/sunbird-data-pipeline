package org.sunbird.dp.deviceprofile.task

import java.util

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig

class DeviceProfileUpdaterConfig(override val config: Config) extends BaseJobConfig(config, jobName = "DeviceProfileUpdaterJob") {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

  val deviceDbStore: Int = config.getInt("redis-meta.database.devicestore.id")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")

  val deviceProfileParallelism: Int = config.getInt("task.deviceprofile.parallelism")

  // Metric List
  val deviceDbHitCount = "device-db-update-count"
  val cacheHitCount = "device-cache-update-count"
  val successCount = "success-event-count"
  val failedEventCount = "failed-event-count"

  //Postgress
  val postgresUser: String = config.getString("postgres.user")
  val postgresPassword: String = config.getString("postgres.password")
  val postgresTable: String = config.getString("postgres.table")
  val postgresDb: String = config.getString("postgres.database")
  val postgresHost: String = config.getString("postgres.host")
  val postgresPort: Int = config.getInt("postgres.port")
  val postgresMaxConnections: Int = config.getInt("postgres.maxConnections")
  val postgresSslMode: Boolean = config.getBoolean("postgres.sslMode")


  val countryCode = "country_code"
  val country = "country"
  val stateCode = "state_code"
  val state = "state"
  val city = "city"
  val districtCustom = "district_custom"
  val stateCustom = "state_custom"
  val stateCustomCode = "state_code_custom"
  val userDeclaredDistrict = "user_declared_district"
  val userDeclaredState = "user_declared_state"
  val uaSpec = "uaspec"
  val deviceSpec = "devicespec"
  val firstAccess = "firstaccess"
  val userDeclaredOn = "user_declared_on"
  val apiLastUpdatedOn = "api_last_updated_on"
  val deviceId = "device_id"

  val fields = List(countryCode, country, stateCode, state, city, districtCustom, stateCustom, stateCustomCode,
    userDeclaredDistrict, uaSpec, deviceSpec, firstAccess, userDeclaredOn, apiLastUpdatedOn
  )

  // Consumers
  val deviceProfileConsumer = "device-profile-consumer"

  // Functions
  val deviceProfileUpdaterFunction = "DeviceProfileUpdaterFunction"


}
