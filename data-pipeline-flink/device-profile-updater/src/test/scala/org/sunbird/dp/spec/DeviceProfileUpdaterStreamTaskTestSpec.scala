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
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterConfig
import org.sunbird.dp.extractor.task.DeviceProfileUpdaterStreamTask
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.embedded.RedisServer
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.sunbird.dp.core.util.{PostgresConnect, PostgresConnectionConfig}

import scala.collection.JavaConverters._

class DeviceProfileUpdaterStreamTaskTestSpec extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  var postgresConnect: PostgresConnect = _
  val config: Config = ConfigFactory.load("test.conf")
  val deviceProfileUpdaterConfig: DeviceProfileUpdaterConfig = new DeviceProfileUpdaterConfig(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6341)
    redisServer.start()
    EmbeddedPostgres.builder.setPort(deviceProfileUpdaterConfig.postgresPort).start() // Use the same port 5430 which is defined in the base-test.conf
    BaseMetricsReporter.gaugeMetrics.clear()

    val postgresConfig = PostgresConnectionConfig(
      user = deviceProfileUpdaterConfig.postgresUser,
      password = deviceProfileUpdaterConfig.postgresPassword,
      database = deviceProfileUpdaterConfig.postgresDb,
      host = deviceProfileUpdaterConfig.postgresHost,
      port = deviceProfileUpdaterConfig.postgresPort,
      maxConnections = deviceProfileUpdaterConfig.postgresMaxConnections
    )
    postgresConnect = new PostgresConnect(postgresConfig)
    //val connection = postgresConnect.getConnection

    //postgresConnect.execute("CREATE TABLE device_table(id text PRIMARY KEY, channel text);")

    when(mockKafkaUtil.kafkaMapSource(deviceProfileUpdaterConfig.kafkaInputTopic)).thenReturn(new DeviceProfileUpdaterEventSource)
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
    postgresConnect.closeConnection()

  }


  "DeviceProfileUpdater " should "Update the both redis and postgres" in {

    val task = new DeviceProfileUpdaterStreamTask(deviceProfileUpdaterConfig, mockKafkaUtil)
    task.process()
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.successCount}").getValue() should be(0)
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.failedEventCount}").getValue() should be(0)
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.cacheHitCount}").getValue() should be(0)
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.deviceDbHitCount}").getValue() should be(0)

  }

}

class DeviceProfileUpdaterEventSource extends SourceFunction[util.Map[String, AnyRef]] {

  override def run(ctx: SourceContext[util.Map[String, AnyRef]]) {
    val gson = new Gson()
    EventFixture.deviceProfileEvents.foreach(event => {
      val eventMap = gson.fromJson(event, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
      ctx.collect(eventMap.asJava)
    })
  }

  override def cancel() = {}

}