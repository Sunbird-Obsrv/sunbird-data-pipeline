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
import org.sunbird.dp.denorm.task.{DenormalizationConfig, SummaryDenormalizationStreamTask}
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.embedded.RedisServer

import scala.collection.JavaConverters._

class SummaryDenormalizationStreamTaskTestSpec extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val denormConfig: DenormalizationConfig = new DenormalizationConfig(config, "SummaryDenormTest")
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
    jedis.close()

  }

  "Summary Denormalization pipeline" should "denormalize content, user, device and location metadata for summary events" in {

    when(mockKafkaUtil.kafkaEventSource[Event](denormConfig.summaryInputTopic)).thenReturn(new SummaryInputSource)
    when(mockKafkaUtil.kafkaEventSink[Event](denormConfig.summaryDenormOutputTopic)).thenReturn(new SummaryDenormEventsSink)
    when(mockKafkaUtil.kafkaEventSink[Event](denormConfig.summaryUniqueEventsTopic)).thenReturn(new SummaryEventsSink)
    when(mockKafkaUtil.kafkaEventSink[Event](denormConfig.duplicateTopic)).thenReturn(new DuplicateEventsSink)

    val task = new SummaryDenormalizationStreamTask(denormConfig, mockKafkaUtil)
    task.process()
    SummaryDenormEventsSink.values.size should be (2)
    DuplicateEventsSink.values.size should be (1)
    SummaryEventsSink.values.size should be (3)

    val event1 = SummaryDenormEventsSink.values("mid1")
    event1.kafkaKey() should be ("45f32f48592cb9bcf26bef9178b7bd20abe24932")

    event1.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event1.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event1.flags().get("dialcode_denorm")) should be (None)
    Option(event1.flags().get("content_denorm")) should be (None)
    Option(event1.flags().get("location_denorm")) should be (None)
    val event1UserData = event1.getTelemetry.read[util.HashMap[String, AnyRef]]("userdata").getOrElse(new util.HashMap()).asScala
    event1UserData("usersignintype").asInstanceOf[String] should be ("Anonymous")
    event1UserData("userlogintype").asInstanceOf[String] should be ("NA")
    event1UserData("usertype").asInstanceOf[String] should be ("TEACHER")

    val event2 = SummaryDenormEventsSink.values("mid2")
    event2.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event2.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event2.flags().get("dialcode_denorm")) should be (None)
    event2.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)
    event2.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    Option(event2.flags().get("coll_denorm")) should be (Some(true))

    val event2UserData = event2.getTelemetry.read[util.HashMap[String, AnyRef]]("userdata").getOrElse(new util.HashMap()).asScala
    event2UserData("usersignintype") should be ("Self-Signed-In")
    event2UserData("userlogintype") should be ("Student")
    event2UserData("usertype") should be ("administrator")
    event2UserData("usersubtype") should be ("deo,hm")
    event2UserData("subject").asInstanceOf[util.List[String]].asScala should be(List("English"))
    event2UserData("cluster") should be ("Cluster001")
    event2UserData("schoolname") should be ("[RPMMAT M.S UDHADIH")
    event2UserData("block") should be ("Sri Sai ACC Block")

    val event2ContentData = event2.getTelemetry.read[util.HashMap[String, AnyRef]]("contentdata").getOrElse(new util.HashMap()).asScala
    event2ContentData.get("keywords").get.asInstanceOf[util.ArrayList[String]].get(0) should be ("Story")

    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locCacheHit}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locCacheMiss}").getValue() should be (0)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.locTotal}").getValue() should be (2)

    // Content Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentCacheHit}").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentCacheMiss}").getValue() should be (0)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.contentTotal}").getValue() should be (1)

    // User Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userCacheHit}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userCacheMiss}").getValue() should be (0)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.userTotal}").getValue() should be (2)

    // Dialcode Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeCacheHit}").getValue() should be (0)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeCacheMiss}").getValue() should be (0)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.dialcodeTotal}").getValue() should be (0)

    // Device Denorm Metrics Assertion
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceCacheHit}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceCacheMiss}").getValue() should be (0)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.deviceTotal}").getValue() should be (2)

    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.${denormConfig.eventsExpired}").getValue() should be (0)

    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.unique-event-count").getValue() should be(3)
    BaseMetricsReporter.gaugeMetrics(s"${denormConfig.jobName}.duplicate-event-count").getValue() should be(1)

  }

}

class SummaryInputSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    EventFixture.summaryEvents.foreach(f => {
      val eventMap = gson.fromJson(f, new util.HashMap[String, Any]().getClass)
      ctx.collect(new Event(eventMap))
    })
  }

  override def cancel() = {}
}

class SummaryDenormEventsSink extends SinkFunction[Event] {

  override def invoke(event: Event): Unit = {
    synchronized {
      SummaryDenormEventsSink.values.put(event.mid(), event)
    }
  }
}

object SummaryDenormEventsSink {
  val values: scala.collection.mutable.Map[String, Event] = scala.collection.mutable.Map[String, Event]()
}

class DuplicateEventsSink extends SinkFunction[Event] {

  override def invoke(event: Event): Unit = {
    synchronized {
      DuplicateEventsSink.values.put(event.mid(), event)
    }
  }
}

object DuplicateEventsSink {
  val values: scala.collection.mutable.Map[String, Event] = scala.collection.mutable.Map[String, Event]()
}

class SummaryEventsSink extends SinkFunction[Event] {

  override def invoke(event: Event): Unit = {
    synchronized {
      SummaryEventsSink.values.put(event.mid(), event)
    }
  }
}

object SummaryEventsSink {
  val values: scala.collection.mutable.Map[String, Event] = scala.collection.mutable.Map[String, Event]()
}

