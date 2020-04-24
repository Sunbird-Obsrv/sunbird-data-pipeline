package org.sunbird.dp.usercache.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.sunbird.dp.core.job.BaseJobConfig
import org.sunbird.dp.usercache.domain.Event

class UserCacheUpdaterConfig(override val config: Config) extends BaseJobConfig(config, "user-cache-updater") {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  // Kafka Topics Configuration
  val inputTopic: String = config.getString("kafka.denorm.input.topic")
  val failedTopic: String = config.getString("kafka.output.failed.topic")

  val userStore: Int = config.getInt("redis.database.userstore.id")

  val userFields = List("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype")

  val userSignInTypeDefault: String = if (config.hasPath("user.signin.type.default")) config.getString("user.signin.type.default") else "Anonymous"
  val userLoginInTypeDefault: String = if (config.hasPath("user.login.type.default")) config.getString("user.login.type.default") else "NA"

  val DENORM_EVENTS_PRODUCER = "telemetry-denorm-events-producer"
  val JOB_METRICS_PRODUCER = "telemetry-job-metrics-producer"

  // Device Denorm Metrics
  val deviceTotal = "device-total"
  val deviceCacheHit = "device-cache-hit"
  val deviceCacheMiss = "device-cache-miss"

  // User Denorm Metrics
  val userTotal = "user-total"
  val userCacheHit = "user-cache-hit"
  val userCacheMiss = "user-cache-miss"

  val locTotal = "loc-total"
  val locCacheHit = "loc-cache-hit"
  val locCacheMiss = "loc-cache-miss"

}
