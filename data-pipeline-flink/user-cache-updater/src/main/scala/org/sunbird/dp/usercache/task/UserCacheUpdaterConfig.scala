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
  val userFields = List("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype","locationids")


  val userCacheConsumer = "user-cache-updater-consumer"


  // User cache updater job metrics
  val userCacheHit = "user-cache-hit"
  val skipCount = "skipped-message-count"
  val successCount = "success-message-count"
  val dbHitCount = "db-hit-count"
  val dbMissCount = "db-miss-count"

  val userSelfSignedInTypeList = config.getStringList("user.self.signin.types")
  val userValidatedTypeList = config.getStringList("user.validated.types")
  val userSelfSignedKey = config.getString("user.self.signin.key")
  val userValidatedKey = config.getString("user.valid.key")

  // Redis
  val userStore: Int = config.getInt("redis.database.userstore.id")

  // cassandra
  val keySpace = config.getString("cassandra.keyspace")
  val locationTable = config.getString("cassandra.table.location")
  val userTable = config.getString("cassandra.table.user")
  val cassandraHost :String =  config.getString("cassandra.host")
  val cassandraPort :Int =  config.getInt("cassandra.port")

  val userCacheParallelism = config.getInt("task.usercache.updater.parallelism")



  // constants

  val userSignInTypeKey = "usersignintype"
  val userLoginTypeKey = "userlogintype"
  val stateKey = "state"
  val districtKey = "district"


}
