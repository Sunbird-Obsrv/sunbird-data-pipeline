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

class DenormalizationStreamTaskTestSpecV2 extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test2.conf")
  val denormConfig: DenormalizationConfig = new DenormalizationConfig(config, "DenormTestV2")
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6340)
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

    // Insert user test data
    var jedis = redisConnect.getConnection(denormConfig.userStore)
      jedis.set("b7470841-7451-43db-b5c7-2dcf4f8d3b23", EventFixture.userCacheData1)
      jedis.set("610bab7d-1450-4e54-bf78-c7c9b14dbc81", EventFixture.userCacheData2)
    jedis.close()
  }

  "De-normalization pipeline v2" should "denormalize user data by fetching in string format from cache" in {

    when(mockKafkaUtil.kafkaEventSource[Event](denormConfig.telemetryInputTopic)).thenReturn(new DenormInputSource)
    when(mockKafkaUtil.kafkaEventSink[Event](denormConfig.denormSuccessTopic)).thenReturn(new DenormEventsSinkV2)

    val task = new DenormalizationStreamTask(denormConfig, mockKafkaUtil)
    task.process()

    val event = DenormEventsSinkV2.values("mid1")

    event.kafkaKey() should be ("758e054a400f20f7677f2def76427dc13ad1f837")

    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("dialcode_denorm")) should be (None)
    Option(event.flags().get("content_denorm")) should be (None)
    Option(event.flags().get("location_denorm")) should be (None)

    println("event: " + event)
    event.getMap().get("userdata").asInstanceOf[util.Map[String, Any]].get("usersignintype") should be("Anonymous")
    event.getMap().get("userdata").asInstanceOf[util.Map[String, Any]].get("usertype") should be("TEACHER")
    event.getMap().get("userdata").asInstanceOf[util.Map[String, Any]].get("userlogintype") should be("Google")

  }
}


class DenormInputSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    EventFixture.telemetrEvents.foreach(f => {
      val eventMap = gson.fromJson(f, new util.HashMap[String, Any]().getClass)
      ctx.collect(new Event(eventMap))
    })
  }

  override def cancel() = {}
}

class DerivedEventSourceV2 extends SourceFunction[Event] {
  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
  }

  override def cancel() = {}
}

class DenormEventsSinkV2 extends SinkFunction[Event] {

  override def invoke(event: Event): Unit = {
    synchronized {
      DenormEventsSinkV2.values.put(event.mid(), event)
    }
  }
}

object DenormEventsSinkV2 {
  val values: scala.collection.mutable.Map[String, Event] = scala.collection.mutable.Map[String, Event]()
}
