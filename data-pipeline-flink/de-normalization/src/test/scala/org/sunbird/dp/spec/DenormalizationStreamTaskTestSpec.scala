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
    jedis.set("b7470841-7451-43db-b5c7-2dcf4f8d3b23", EventFixture.userCacheData1)
    jedis.set("610bab7d-1450-4e54-bf78-c7c9b14dbc81", EventFixture.userCacheData2)
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
    jedis.close()

  }

  "De-normalization pipeline" should "denormalize content, user, device and location metadata" in {

    when(mockKafkaUtil.kafkaEventSource[Event](denormConfig.telemetryInputTopic)).thenReturn(new InputSource)
    when(mockKafkaUtil.kafkaEventSink[Event](denormConfig.denormSuccessTopic)).thenReturn(new DenormEventsSink)

    val task = new DenormalizationStreamTask(denormConfig, mockKafkaUtil)
    task.process()
    DenormEventsSink.values.size should be (10)
    DenormEventsSink.values.get("mid10") should be (None)

    var event = DenormEventsSink.values("mid1")
    event.kafkaKey() should be ("758e054a400f20f7677f2def76427dc13ad1f837")

    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("dialcode_denorm")) should be (None)
    Option(event.flags().get("content_denorm")) should be (None)
    Option(event.flags().get("location_denorm")) should be (None)
    
    event = DenormEventsSink.values("mid2")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("dialcode_denorm")) should be (None)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("coll_denorm")) should be (None)

    event.getMap().get("contentdata").asInstanceOf[util.Map[String, Any]].get("lastsubmittedon") should be(1529068016090L)
    
    event = DenormEventsSink.values("mid3")
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("coll_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)

    event.getMap().get("collectiondata").asInstanceOf[util.Map[String, Any]].get("contenttype") should be("Asset")
    
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

    // TODO: Complete the assertions
    event = DenormEventsSink.values("mid6")
    event = DenormEventsSink.values("mid7")
    event = DenormEventsSink.values("mid8")
    event = DenormEventsSink.values("mid9")

    // Location Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locCacheHit}").getValue() should be (7)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locCacheMiss}").getValue() should be (3)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locTotal}").getValue() should be (10)

    // Content Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentCacheHit}").getValue() should be (4)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentCacheMiss}").getValue() should be (3)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentTotal}").getValue() should be (7)

    // User Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userCacheHit}").getValue() should be (3)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userCacheMiss}").getValue() should be (4)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userTotal}").getValue() should be (7)

    // Dialcode Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeCacheHit}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeCacheMiss}").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeTotal}").getValue() should be (3)

    // Device Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceCacheHit}").getValue() should be (7)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceCacheMiss}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceTotal}").getValue() should be (9)

    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.eventsExpired}").getValue() should be (1)

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