package org.sunbird.dp.spec

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
import org.mockito.Mockito.when
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.{DenormalizationConfig, DenormalizationStreamTask}
import org.sunbird.dp.fixture.EventFixture
import redis.embedded.RedisServer

import java.util

class CheckDerivedLocationStreamTaskTestSpec extends BaseTestSpec {
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
    jedis.hmset("45f32f48592cb9bcf26bef9178b7bd20abe24932", gson.fromJson(EventFixture.deviceCacheData3, new util.HashMap[String, String]().getClass))
    jedis.close()

  }

  "De-normalization pipeline" should "denormalize location metadata properly for blank user declared scenario" in {

    when(mockKafkaUtil.kafkaEventSource[Event](denormConfig.telemetryInputTopic)).thenReturn(new InputSource1)
    when(mockKafkaUtil.kafkaEventSink[Event](denormConfig.telemetryDenormOutputTopic)).thenReturn(new DenormEventsSink1)

    val task = new DenormalizationStreamTask(denormConfig, mockKafkaUtil)
    task.process()

    val event = DenormEventsSink1.values("mid1")
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    // derived location should be from ip-resolved
    event.getMap().get("derivedlocationdata").asInstanceOf[util.Map[String, Any]].get("district") should be("Mumbai")
    event.getMap().get("derivedlocationdata").asInstanceOf[util.Map[String, Any]].get("state") should be("Maharashtra")
    event.getMap().get("derivedlocationdata").asInstanceOf[util.Map[String, Any]].get("from") should be("ip-resolved")

    val device3Data = event.getMap().get("devicedata").asInstanceOf[util.Map[String, Any]]
    device3Data.get("firstaccess") should be (1578972432419L)
    val deviceSpecData = device3Data.get("devicespec").asInstanceOf[util.Map[String, Any]]
    deviceSpecData.get("edisk") should be ("25.42")
    deviceSpecData.get("make") should be ("Samsung SM-J400F")
    // user declared is empty
    val userDeclaredLocationData = device3Data.get("userdeclared").asInstanceOf[util.Map[String, Any]]
    userDeclaredLocationData.get("district") should be ("")
    userDeclaredLocationData.get("state") should be ("")

  }

}

class InputSource1 extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    val eventMap = gson.fromJson(EventFixture.telemetryEvent, new util.HashMap[String, Any]().getClass)
    ctx.collect(new Event(eventMap))
  }

  override def cancel() = {}
}

class DenormEventsSink1 extends SinkFunction[Event] {

  override def invoke(event: Event): Unit = {
    synchronized {
      DenormEventsSink1.values.put(event.mid(), event)
    }
  }
}

object DenormEventsSink1 {
  val values: scala.collection.mutable.Map[String, Event] = scala.collection.mutable.Map[String, Event]()
}