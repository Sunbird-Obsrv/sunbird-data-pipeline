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
import org.sunbird.dp.usercache.task.{UserCacheUpdaterConfig, UserCacheUpdaterStreamTask}
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.clients.jedis.Jedis
import redis.embedded.RedisServer

class UserCacheUpdatetStreamTaskSpec extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val userCacheConfig: UserCacheUpdaterConfig = new UserCacheUpdaterConfig(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()
  var jedis: Jedis = _

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    println("******Starting the Embedded Cassandra*******")
    EmbeddedCassandraServerHelper.startEmbeddedCassandra(80000L)
    val cassandraUtil = new CassandraUtil(userCacheConfig.cassandraHost, userCacheConfig.cassandraPort)
    val session = cassandraUtil.session
    val dataLoader = new CQLDataLoader(session)
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
    jedis.set("user-3", EventFixture.userCacheData3)
    jedis.set("user-4", EventFixture.userCacheData4)
    jedis.close()
  }

  "UserCache Updater pipeline" should "Should able to add user data into cache" in {

    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new InputSource)

    val task = new UserCacheUpdaterStreamTask(userCacheConfig, mockKafkaUtil)
    task.process()

    /**
     * Metrics Assertions
     */
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.userCacheHit}").getValue() should be(5)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.totalEventsCount}").getValue() should be(8)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.dbReadSuccessCount}").getValue() should be(2)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.dbReadMissCount}").getValue() should be(2)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.skipCount}").getValue() should be(4)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.successCount}").getValue() should be(5)

    /**
     * UserId = 89490534-126f-4f0b-82ac-3ff3e49f3468
     * EData state is "Created"
     * User SignupType is "sso"
     * It should able to insert The Map(usersignintype, Validated)
     */
    jedis.select(userCacheConfig.userStore)

    val ssoUser = jedis.get("user-1")
    ssoUser should not be null
    val ssoUserMap: util.Map[String, AnyRef] = gson.fromJson(ssoUser, new util.LinkedHashMap[String, AnyRef]().getClass)
    ssoUserMap.get("usersignintype") should be("Validated")

    /**
     * UserId = user-2
     * EData state is "Created"
     * User SignupType is "google"
     * It should able to insert The Map(usersignintype, Self-Signed-In)
     */
    val googleUser = jedis.get("user-2")
    googleUser should not be null
    val googleUserMap: util.Map[String, AnyRef] = gson.fromJson(googleUser, new util.LinkedHashMap[String, AnyRef]().getClass)
    googleUserMap.get("usersignintype") should be("Self-Signed-In")


    // When action is Update and location id's are empty

    val userInfo = jedis.get("user-3")
    val userInfoMap: util.Map[String, AnyRef] = gson.fromJson(userInfo, new util.LinkedHashMap[String, AnyRef]().getClass)
    userInfoMap.get("createdby") should be("MANJU")
    userInfoMap.get("location") should be("Banglore")
    userInfoMap.get("maskedphone") should be("******181")

    // When action is Updated and location ids are present
    val locationInfo = jedis.get("user-4")
    val locationInfoMap: util.Map[String, AnyRef] = gson.fromJson(locationInfo, new util.LinkedHashMap[String, AnyRef]().getClass)

    // Initially, in the redis for the user-4 is loaded with location details
    // State - Telangana & District - Hyderabad
    // Now it should be bellow assertions
    locationInfoMap.get("state") should be("KARNATAKA")
    locationInfoMap.get("district") should be("TUMKUR")


    val emptyProps = jedis.get("user-5")
    val emptyPropsMap: util.Map[String, AnyRef] = gson.fromJson(emptyProps, new util.LinkedHashMap[String, AnyRef]().getClass)
    emptyPropsMap.get(userCacheConfig.userLoginTypeKey) should be("25cb0530-7c52-ecb1-cff2-6a14faab7910")

  }

  def testCassandraUtil(cassandraUtil: CassandraUtil): Unit = {
    cassandraUtil.reconnect()
    val response = cassandraUtil.findOne("SELECT * FROM sunbird.location;")
    response should not be null
    val upsert = cassandraUtil.upsert("SELECT * FROM sunbird.location;")
    upsert should be(true)
    cassandraUtil.getUDTType("sunbird", "test") should not be (not)
    cassandraUtil.close()
    val onSessionClose = cassandraUtil.find("SELECT * FROM sunbird.location;");
    onSessionClose should not be null

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

