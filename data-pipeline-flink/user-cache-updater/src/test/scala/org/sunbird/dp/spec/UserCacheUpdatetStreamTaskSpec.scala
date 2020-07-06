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
    val dataLoader = new CQLDataLoader(session);
    dataLoader.load(new FileCQLDataSet(getClass.getResource("/data.cql").getPath, true, true));
    testCassandraUtil(cassandraUtil)
    redisServer = new RedisServer(6340)
    redisServer.start()
    BaseMetricsReporter.gaugeMetrics.clear()
    val redisConnect = new RedisConnect(userCacheConfig)

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
    jedis.hmset("user-3", EventFixture.userCacheDataMap3)
    jedis.hmset("user-4", EventFixture.userCacheDataMap4)
    jedis.close()
  }

  "UserCache Updater pipeline" should "Should able to add user data into cache" in {

    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new InputSource)

    val task = new UserCacheUpdaterStreamTask(userCacheConfig, mockKafkaUtil)
    task.process()

    /**
     * Metrics Assertions
     */
//    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.userCacheHit}").getValue() should be(5)
//    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.totalEventsCount}").getValue() should be(8)
//    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.dbReadSuccessCount}").getValue() should be(1)
//    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.dbReadMissCount}").getValue() should be(1)
//    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.skipCount}").getValue() should be(4)
//    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.successCount}").getValue() should be(5)

    /**
     * UserId = 89490534-126f-4f0b-82ac-3ff3e49f3468
     * EData state is "Created"
     * User SignupType is "sso"
     * It should able to insert The Map(usersignintype, Validated)
     */
    jedis.select(userCacheConfig.userStore)

    val ssoUser = jedis.hgetAll("user-1")
    ssoUser should not be (null)
    ssoUser.get("usersignintype") should be("Validated")

    /**
     * UserId = user-2
     * EData state is "Created"
     * User SignupType is "google"
     * It should able to insert The Map(usersignintype, Self-Signed-In)
     */
    val googleUser = jedis.hgetAll("user-2")
    googleUser should not be (null)
    googleUser.get("usersignintype") should be("Self-Signed-In")


    // When action is Update and location id's are empty

    val userInfo = jedis.hgetAll("user-3")
    userInfo.get("createdby") should be("MANJU")
    userInfo.get("location") should be("Banglore")
    userInfo.get("maskedphone") should be("******181")
    userInfo.get("iscustodianuser") should  be ("true")
    userInfo.get("externalid") should be("93nsoa01")
    userInfo.get("schoolname") should be("SBA School")

    // When action is Updated and location ids are present
    val locationInfo = jedis.hgetAll("user-4")

    // Initially, in the redis for the user-4 is loaded with location details
    // State - Telangana & District - Hyderabad
    // Now it should be bellow assertions
    locationInfo.get("state") should be("KARNATAKA")
    locationInfo.get("district") should be("TUMKUR")


    val emptyProps = jedis.hgetAll("user-5")
    emptyProps.get(userCacheConfig.userLoginTypeKey) should be("25cb0530-7c52-ecb1-cff2-6a14faab7910")

  }

  def testCassandraUtil(cassandraUtil: CassandraUtil): Unit = {
    cassandraUtil.reconnect()
    val response = cassandraUtil.findOne("SELECT * FROM sunbird.location;")
    response should not be (null)
    val upsert = cassandraUtil.upsert("SELECT * FROM sunbird.location;")
    upsert should be(true)
    cassandraUtil.getUDTType("sunbird", "test") should not be (not)
    cassandraUtil.close()
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

