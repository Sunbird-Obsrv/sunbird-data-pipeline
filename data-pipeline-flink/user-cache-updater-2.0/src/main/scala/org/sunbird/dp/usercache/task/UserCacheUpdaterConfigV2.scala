package org.sunbird.dp.usercache.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.sunbird.dp.core.job.BaseJobConfig
import org.sunbird.dp.usercache.domain.Event

import java.util.{List => JList}

class UserCacheUpdaterConfigV2(override val config: Config) extends BaseJobConfig(config, "UserCacheUpdaterJobV2") {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  // Kafka Topics Configuration
  val inputTopic: String = config.getString("kafka.input.topic")
  val userFields = List("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype","locationids")

  // User cache updater job metrics
  val userCacheHit = "user-cache-hit"
  val skipCount = "skipped-message-count"
  val successCount = "success-message-count"
  val dbReadSuccessCount = "db-read-success-count"
  val dbReadMissCount = "db-read-miss-count"
  val totalEventsCount ="total-audit-events-count"

  val userSelfSignedInTypeList: JList[String] = config.getStringList("user.self.signin.types")
  val userValidatedTypeList: JList[String] = config.getStringList("user.validated.types")
  val userSelfSignedKey: String = config.getString("user.self.signin.key")
  val userValidatedKey: String = config.getString("user.valid.key")

  // Redis
  val userStore: Int = config.getInt("redis-meta.database.userstore.id")

  // lms-cassandra
  val keySpace: String = config.getString("lms-cassandra.keyspace")
  val locationTable: String = config.getString("lms-cassandra.table.location")
  val userTable: String = config.getString("lms-cassandra.table.user")
  val orgTable: String = config.getString("lms-cassandra.table.organisation")
  val userExternalIdTable: String = config.getString("lms-cassandra.table.usr_external_identity")
  val userOrgTable: String = config.getString("lms-cassandra.table.user_org")
  val systemSettingsTable: String = config.getString("lms-cassandra.table.system_settings")
  val userDeclarationTable: String = config.getString("lms-cassandra.table.user_declarations")
  val cassandraHost: String =  config.getString("lms-cassandra.host")
  val cassandraPort: Int =  config.getInt("lms-cassandra.port")

  val userCacheParallelism: Int = config.getInt("task.usercache.updater.parallelism")

  // constants
  val userSignInTypeKey = "usersignintype"
  val userLoginTypeKey = "userlogintype"
  val stateKey = "state"
  val districtKey = "district"
  val blockKey = "block"
  val orgnameKey = "orgname"
  val externalidKey = "externalid"
  val schoolUdiseCodeKey = "schooludisecode"
  val schoolNameKey = "schoolname"
  val orgcodeKey = "orgcode"
  val userChannelKey="userchannel"
  val originalprovider="originalprovider"

  val persona = "teacher"
  val declareExternalId = "declared-ext-id"
  val declaredSchoolName = "declared-school-name"
  val declaredSchoolCode = "declared-school-udise-code"

  //user store key prefix
  val userStoreKeyPrefix = "user:"
  // Consumers
  val userCacheConsumer = "user-cache-consumer"

  // Functions
  val userCacheUpdaterFunction = "UserCacheUpdaterFunctionV2"
}