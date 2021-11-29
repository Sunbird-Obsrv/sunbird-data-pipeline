package org.sunbird.dp.spec

import java.util

import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.{DenormalizationConfig, DenormalizationStreamTask}
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.embedded.RedisServer

import scala.collection.JavaConverters._

class DenormalizationStreamTaskTestSpec extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val denormConfig: DenormalizationConfig = new DenormalizationConfig(config, "DenormTest")
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6341)
    redisServer.start()

    BaseMetricsReporter.gaugeMetrics.clear()

    setupRedisTestData()
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }

  def setupRedisTestData() {

    val redisConnect = new RedisConnect(denormConfig.metaRedisHost, denormConfig.metaRedisPort, denormConfig)

    // Insert device test data
    var jedis = redisConnect.getConnection(denormConfig.deviceStore)
    jedis.hmset("264d679186d4b0734d858d4e18d4d31e", gson.fromJson(EventFixture.deviceCacheData1, new util.HashMap[String, String]().getClass))
    jedis.hmset("45f32f48592cb9bcf26bef9178b7bd20abe24932", gson.fromJson(EventFixture.deviceCacheData2, new util.HashMap[String, String]().getClass))
    jedis.close()

    // Insert user test data
    jedis = redisConnect.getConnection(denormConfig.userStore)
    jedis.hmset(denormConfig.userStoreKeyPrefix + "b7470841-7451-43db-b5c7-2dcf4f8d3b23", EventFixture.userCacheDataMap1)
    jedis.hmset(denormConfig.userStoreKeyPrefix + "610bab7d-1450-4e54-bf78-c7c9b14dbc81", EventFixture.userCacheDataMap2)
    jedis.close()

    // Insert dialcode test data
    jedis = redisConnect.getConnection(denormConfig.dialcodeStore)
    jedis.set("GWNI38", EventFixture.dialcodeCacheData1)
    jedis.set("PCZKA3", EventFixture.dialcodeCacheData2)
    jedis.close()

    // Insert content test data
    jedis = redisConnect.getConnection(denormConfig.contentStore)
    jedis.set("do_31249064359802470412856", EventFixture.contentCacheData1)
    jedis.set("do_312526125187809280139353", EventFixture.contentCacheData2)
    jedis.set("do_312526125187809280139355", EventFixture.contentCacheData3)
    jedis.set("do_312523863923441664117896", EventFixture.contentCacheData4)
    jedis.set("7426472e-8b1a-4387-8b7a-962cb6cda006", EventFixture.contentCacheData5)
    jedis.set("do_31331086175718604812701", EventFixture.collectionCache1)
    jedis.close()

  }

  "De-normalization pipeline" should "denormalize content, user, device and location metadata" in {

    when(mockKafkaUtil.kafkaEventSource[Event](denormConfig.telemetryInputTopic)).thenReturn(new InputSource)
    when(mockKafkaUtil.kafkaEventSink[Event](denormConfig.telemetryDenormOutputTopic)).thenReturn(new DenormEventsSink)

    val task = new DenormalizationStreamTask(denormConfig, mockKafkaUtil)
    task.process()

    DenormEventsSink.values.size should be (13)
    DenormEventsSink.values.get("mid10") should be (None)

    var event = DenormEventsSink.values("mid1")
    event.kafkaKey() should be ("758e054a400f20f7677f2def76427dc13ad1f837")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("dialcode_denorm")) should be (None)
    Option(event.flags().get("content_denorm")) should be (None)
    Option(event.flags().get("location_denorm")) should be (None)

    val user1Data = event.getMap().get("userdata").asInstanceOf[util.Map[String, Any]]
    user1Data.get("usersignintype") should be("Anonymous")
    user1Data.get("usertype") should be("TEACHER")
    user1Data.get("userlogintype") should be("NA")
    
    event = DenormEventsSink.values("mid2")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("dialcode_denorm")) should be (None)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("coll_denorm")) should be (None)

    event.getMap().get("contentdata").asInstanceOf[util.Map[String, Any]].get("lastsubmittedon") should be(1529068016090L)
    event.getMap().get("contentdata").asInstanceOf[util.Map[String, Any]].get("channel") should be("in.ekstep")
    event.getMap().get("contentdata").asInstanceOf[util.Map[String, Any]].get("lastpublishedon") should be(1.571999041881E12)
    event.getMap().get("contentdata").asInstanceOf[util.Map[String, Any]].get("contenttype") should be("Resource")
    event.getMap().get("contentdata").asInstanceOf[util.Map[String, Any]].get("keywords").asInstanceOf[util.ArrayList[String]].get(0) should be ("Story")

    event.getMap().get("devicedata").asInstanceOf[util.Map[String, Any]].get("statecustomcode") should be("29")
    event.getMap().get("devicedata").asInstanceOf[util.Map[String, Any]].get("countrycode") should be("IN")
    event.getMap().get("devicedata").asInstanceOf[util.Map[String, Any]].get("firstaccess") should be(1571999041881L)
    event.getMap().get("devicedata").asInstanceOf[util.Map[String, Any]].get("districtcustom") should be("BENGALURU URBAN SOUTH")

    val user2Data = event.getMap().get("userdata").asInstanceOf[util.Map[String, Any]]
    user2Data.get("userlogintype") should be("Student")
    user2Data.get("usersignintype") should be("Self-Signed-In")
    user2Data.get("usertype") should be("administrator")
    user2Data.get("usersubtype") should be("deo,hm")
    user2Data.get("subject").asInstanceOf[util.List[String]].asScala should be(List("English"))
    user2Data.get("state") should be("Telangana")
    user2Data.get("cluster") should be("Cluster001")
    user2Data.get("schoolname") should be("[RPMMAT M.S UDHADIH")
    user2Data.get("block") should be ("Sri Sai ACC Block")

    event = DenormEventsSink.values("mid3")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("coll_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)

    event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]].get("contenttype") should be("Asset")
    Option(event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]].get("contentType")) should be (None)
    event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]].get("contenttype") should be("Asset")
    event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]].get("framework") should be("NCF")
    event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]].get("name") should be("do_312526125187809280139355")
    event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]].get("lastupdatedon") should be(1489169400448L)

    event.getMap().get("derivedlocationdata").asInstanceOf[util.Map[String, Any]].get("district") should be("Raigad")
    event.getMap().get("derivedlocationdata").asInstanceOf[util.Map[String, Any]].get("state") should be("Maharashtra")
    event.getMap().get("derivedlocationdata").asInstanceOf[util.Map[String, Any]].get("from") should be("user-declared")

    val device3Data = event.getMap().get("devicedata").asInstanceOf[util.Map[String, Any]]
    device3Data.get("firstaccess") should be (1578972432419L)
    val deviceSpecData = device3Data.get("devicespec").asInstanceOf[util.Map[String, Any]]
    deviceSpecData.get("edisk") should be ("25.42")
    deviceSpecData.get("make") should be ("Samsung SM-J400F")
    val userDeclaredLocationData = device3Data.get("userdeclared").asInstanceOf[util.Map[String, Any]]
    userDeclaredLocationData.get("district") should be ("Raigad")
    userDeclaredLocationData.get("state") should be ("Maharashtra")

    event = DenormEventsSink.values("mid4")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("dialcode_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("coll_denorm")) should be (None)
    
    event = DenormEventsSink.values("mid5")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("dialcode_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("content_denorm")) should be (None)
    Option(event.flags().get("location_denorm")) should be (None)

    event.getMap().get("dialcodedata").asInstanceOf[util.Map[String, Any]].get("batchcode") should be("jkpublisher.20180801T122031")
    event.getMap().get("dialcodedata").asInstanceOf[util.Map[String, Any]].get("channel") should be("01254592085869363222")
    event.getMap().get("dialcodedata").asInstanceOf[util.Map[String, Any]].get("generatedon") should be(1.571999041881E12)
    event.getMap().get("dialcodedata").asInstanceOf[util.Map[String, Any]].get("publishedon") should be(1533130913695L)

    // TODO: Complete the assertions
    event = DenormEventsSink.values("mid6")
    event = DenormEventsSink.values("mid7")
    event = DenormEventsSink.values("mid8")
    event.flags().get("coll_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("l2_denorm").asInstanceOf[Boolean] should be (true)
    val l2Data = event.getMap().get("l2data").asInstanceOf[util.Map[String, Any]]
    l2Data should not be null

    l2Data.get("contenttype") should be("TextBook")
    l2Data.get("mimetype") should be("application/vnd.ekstep.content-collection")
    l2Data.get("contenttype") should be("TextBook")
    l2Data.get("channel") should be("0123221617357783046602")
    l2Data.get("board") should be("State (Maharashtra)")
    l2Data.get("name") should be("test")
    l2Data.get("framework") should be("mh_k-12_1")
    event = DenormEventsSink.values("mid9")

    event = DenormEventsSink.values("LP.AUDIT.d14d8be6-da4e-4ee9-b833-fd86d57b8808")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("coll_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)

    val contentData = event.getMap().get("contentdata").asInstanceOf[util.Map[String, Any]]
    contentData.get("name") should be ("Anction Words")
    contentData.get("mimetype") should be ("application/vnd.ekstep.content-collection")
    contentData.get("objecttype") should be ("Content")
    contentData.get("contenttype") should be ("TextBookUnit")
    contentData.get("status") should be ("Draft")

    val collData = event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]]
    collData.get("name") should be ("Anction Words")
    collData.get("mimetype") should be ("application/vnd.ekstep.content-collection")
    collData.get("objecttype") should be ("Content")
    collData.get("contenttype") should be ("TextBookUnit")
    collData.get("status") should be ("Draft")

    // Location Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locCacheHit}").getValue() should be (8)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locCacheMiss}").getValue() should be (5)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locTotal}").getValue() should be (13)

    // Content Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentCacheHit}").getValue() should be (6)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentCacheMiss}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentTotal}").getValue() should be (8)

    // User Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userCacheHit}").getValue() should be (5)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userCacheMiss}").getValue() should be (5)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userTotal}").getValue() should be (10)

    // Dialcode Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeCacheHit}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeCacheMiss}").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeTotal}").getValue() should be (3)

    // Device Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceCacheHit}").getValue() should be (8)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceCacheMiss}").getValue() should be (3)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceTotal}").getValue() should be (11)

    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.eventsExpired}").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.eventsSkipped}").getValue() should be (3) // Skipped INTERRUPT event

  }
  
  it should " test the optional fields in denorm config " in {
    val config = ConfigFactory.load("test2.conf")
    val denormConfig: DenormalizationConfig = new DenormalizationConfig(config, "DenormTest")
    denormConfig.ignorePeriodInMonths should be (6)
    denormConfig.userLoginInTypeDefault should be ("Google")
    denormConfig.userSignInTypeDefault should be ("Default")
    denormConfig.summaryFilterEvents.size should be (2)
    denormConfig.summaryFilterEvents.contains("ME_WORKFLOW_SUMMARY") should be (true)
    denormConfig.summaryFilterEvents.contains("ME_RANDOM_SUMMARY") should be (true)
  }

}

class InputSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    EventFixture.telemetrEvents.foreach(f => {
      val eventMap = gson.fromJson(f, new util.HashMap[String, Any]().getClass)
      ctx.collect(new Event(eventMap))
    })
  }

  override def cancel() = {}
}

class DerivedEventSource extends SourceFunction[Event] {
  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
  }

  override def cancel() = {}
}

class DenormEventsSink extends SinkFunction[Event] {

  override def invoke(event: Event): Unit = {
    synchronized {
      DenormEventsSink.values.put(event.mid(), event)
    }
  }
}

object DenormEventsSink {
  val values: scala.collection.mutable.Map[String, Event] = scala.collection.mutable.Map[String, Event]()
}