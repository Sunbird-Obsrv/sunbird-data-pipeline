package org.sunbird.dp.spec

import java.util

import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.cassandraunit.CQLDataLoader
import org.cassandraunit.dataset.cql.FileCQLDataSet
import org.cassandraunit.utils.EmbeddedCassandraServerHelper
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.{UserCacheUpdaterConfigV2, UserCacheUpdaterStreamTaskV2}
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.clients.jedis.Jedis
import redis.embedded.RedisServer

class UserCacheUpdatetStreamTaskSpecV2 extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val userCacheConfig: UserCacheUpdaterConfigV2 = new UserCacheUpdaterConfigV2(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()
  var jedis: Jedis = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    println("******Starting the Embedded Cassandra*******")
    EmbeddedCassandraServerHelper.startEmbeddedCassandra(80000L)
    val cassandraUtil = new CassandraUtil(userCacheConfig.cassandraHost, userCacheConfig.cassandraPort)
    val session = cassandraUtil.session
    val dataLoader = new CQLDataLoader(session);
    dataLoader.load(new FileCQLDataSet(getClass.getResource("/data.cql").getPath, true, true));
    testCassandraUtil(cassandraUtil)
    redisServer = new RedisServer(6340)
    redisServer.start()
    BaseMetricsReporter.gaugeMetrics.clear()
    val redisConnect = new RedisConnect(userCacheConfig.metaRedisHost, userCacheConfig.metaRedisPort, userCacheConfig)

    jedis = redisConnect.getConnection(userCacheConfig.userStore)
    setupRedisTestData(jedis)
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }

  def setupRedisTestData(jedis: Jedis) {

    // Insert user test data
    jedis.hmset(userCacheConfig.userStoreKeyPrefix + "user-3", EventFixture.userCacheDataMap3)
    jedis.hmset(userCacheConfig.userStoreKeyPrefix + "user-4", EventFixture.userCacheDataMap4)
    jedis.hmset(userCacheConfig.userStoreKeyPrefix + "user-6", EventFixture.userCacheDataMap3)
    jedis.hmset(userCacheConfig.userStoreKeyPrefix + "user-9", EventFixture.userCacheData9)
    jedis.hmset(userCacheConfig.userStoreKeyPrefix + "user-12", EventFixture.userCacheData11)
    jedis.hmset(userCacheConfig.userStoreKeyPrefix + "user-11", EventFixture.userCacheData11)

    jedis.close()
  }

  "UserCache Updater pipeline" should "Should able to add user data into cache" in {

    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new InputSource)

    val task = new UserCacheUpdaterStreamTaskV2(userCacheConfig, mockKafkaUtil)
    task.process()

    /**
      * Metrics Assertions
      */
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.userCacheHit}").getValue() should be(9)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.totalEventsCount}").getValue() should be(12)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.dbReadSuccessCount}").getValue() should be(29)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.dbReadMissCount}").getValue() should be(12)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.skipCount}").getValue() should be(4)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.successCount}").getValue() should be(9)

    /**
      * UserId = 89490534-126f-4f0b-82ac-3ff3e49f3468
      * EData state is "Created"
      * User SignupType is "sso"
      * It should able to insert The Map(usersignintype, Validated)
      */
    jedis.select(userCacheConfig.userStore)

    var userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-1")
    userInfo should not be (null)
    userInfo.get("usersignintype") should be("Validated")

    /**
      * UserId = user-2
      * EData state is "Created"
      * User SignupType is "google"
      * It should able to insert The Map(usersignintype, Self-Signed-In)
      */
    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-2")
    userInfo should not be (null)
    userInfo.get("usersignintype") should be("Self-Signed-In")


    // When action is Update and location id's are empty

    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-3")
    userInfo.get("firstname") should be("Manjunath")
    userInfo.get("lastname") should be("Davanam")
    userInfo.get("location") should be("Banglore")
    userInfo.get("maskedphone") should be("******181")
    userInfo.get("iscustodianuser") should  be ("true")
    userInfo.get("externalid") should be("93nsoa01")
    userInfo.get("schoolname") should be("SBA School")
    userInfo.get("schooludisecode") should be ("038sba937")
    userInfo.get("state") should be ("KARNATAKA")
    userInfo.get("district") should be ("TUMKUR")
    userInfo.get("block") should be ("MANVI")
    userInfo.get("userchannel") should be ("012850193028235264771")
    assert(userInfo.get("orgname").contains("Custodian ORG"))

    // When action is Updated and location ids are present
    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-4")

    // Initially, in the redis for the user-4 is loaded with location details
    // State - Telangana & District - Hyderabad
    // Now it should be bellow assertions
    userInfo.get("state") should be("KARNATAKA")
    userInfo.get("district") should be("TUMKUR")
    userInfo.get("externalid") should be ("948ext1")
    userInfo.get("rootorgid") should be ("01285019302823526479")

    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix + "user-9")
    userInfo.get("firstname") should be("UT")
    userInfo.get("rootorgid") should be("01285019302823526477")

    /** user: user-11
      * State user having channel null in user table
      * */

    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix + "user-11")
    userInfo.get("firstname") should be ("Sowmya")
    userInfo.get("channel") should be ("")
    userInfo.get("externalid") should be ("")

    //user-12
    //Framework not having subject
    var userInfoMap = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix + "user-12")
    userInfoMap.get("firstname") should be("Isha")
    userInfoMap.get("lastname") should be("Wakankar")
    userInfoMap.get("location") should be("Jhansi")
    userInfoMap.get("maskedphone") should be("******181")
    userInfoMap.get("iscustodianuser") should  be ("true")
    userInfoMap.get("externalid") should be("93nsoa01")
    userInfoMap.get("schoolname") should be("")
    userInfoMap.get("schooludisecode") should be ("")
    userInfoMap.get("state") should be ("KARNATAKA")
    userInfoMap.get("district") should be ("TUMKUR")
    userInfoMap.get("block") should be ("MANVI")
    userInfoMap.get("userchannel") should be ("01285019302823526477")
    assert(userInfoMap.get("orgname").contains("Custodian ORG"))

    userInfoMap = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix + "user-6")
    userInfoMap.get("firstname") should be("Revathi")
    userInfoMap.get("lastname") should be("Kotla")
    assert(userInfoMap.containsKey("userinfo").equals(false))
    assert(userInfoMap.containsKey("externalid").equals(false))
    assert(userInfoMap.containsKey("schooludisecode").equals(false))
    assert(userInfoMap.containsKey("schoolname").equals(false))
    assert(userInfoMap.containsKey("userchannel").equals(false))

    val emptyProps = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-5")
    emptyProps.get(userCacheConfig.userLoginTypeKey) should be("25cb0530-7c52-ecb1-cff2-6a14faab7910")


  }

  def testCassandraUtil(cassandraUtil: CassandraUtil): Unit = {
    cassandraUtil.reconnect()
    val response = cassandraUtil.findOne("SELECT * FROM sunbird.location;")
    response should not be (null)
    val upsert = cassandraUtil.upsert("SELECT * FROM sunbird.location;")
    upsert should be(true)
    cassandraUtil.getUDTType("sunbird", "test") should not be (not)
//    cassandraUtil.close()
    val onSessionClose = cassandraUtil.find("SELECT * FROM sunbird.location;");
    onSessionClose should not be (null)

  }

}

class InputSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    EventFixture.telemetrEvents.foreach(f => {
      val eventMap = gson.fromJson(f, new util.HashMap[String, Any]().getClass)
      val event = new Event(eventMap)
      event.kafkaKey()
      ctx.collect(event)
    })
  }

  override def cancel() = {}
}

