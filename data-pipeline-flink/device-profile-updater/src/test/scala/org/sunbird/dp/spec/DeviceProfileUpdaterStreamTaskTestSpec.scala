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
    redisServer = new RedisServer(6340)
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

    //postgresConnect.execute("CREATE TABLE device_table(device_id text, api_last_updated_on timestamptz, avg_ts float, city text, country text, country_code text, device_spec json, district_custom text, fcm_token text, first_access timestamptz, last_access timestamptz, producer_id text, state text, state_code text, state_code_custom text, state_custom text, total_launches bigint, total_ts float, uaspec json, updated_date timestamptz, user_declared_district text, user_declared_state text, user_declared_on timestamptz, PRIMARY KEY(device_id)")
    //postgresConnect.execute("CREATE TABLE IF NOT EXISTS device_table(device_id text PRIMARY KEY, api_last_updated_on timestamptz, avg_ts float, city text,country text, country_code text, device_spec json, district_custom text, fcm_token text, first_access timestamptz, last_access timestamptz, producer_id text, state text, state_code text, state_code_custom text, state_custom text, total_launches bigint, total_ts float, uaspec json, updated_date timestamptz, user_declared_district text, user_declared_state text, user_declared_on timestamptz);")

    postgresConnect.execute("CREATE TABLE IF NOT EXISTS device_profile(\n" + "   device_id text PRIMARY KEY,\n" + "   api_last_updated_on TIMESTAMP,\n" + "    avg_ts float,\n" + "    city TEXT,\n" + "    country TEXT,\n" + "    country_code TEXT,\n" + "    device_spec json,\n" + "    district_custom TEXT,\n" + "    fcm_token TEXT,\n" + "    first_access TIMESTAMP,\n" + "    last_access TIMESTAMP,\n" + "    user_declared_on TIMESTAMP,\n" + "    producer_id TEXT,\n" + "    state TEXT,\n" + "    state_code TEXT,\n" + "    state_code_custom TEXT,\n" + "    state_custom TEXT,\n" + "    total_launches bigint,\n" + "    total_ts float,\n" + "    uaspec json,\n" + "    updated_date TIMESTAMP,\n" + "    user_declared_district TEXT,\n" + "    user_declared_state TEXT)")

    when(mockKafkaUtil.kafkaMapSource(deviceProfileUpdaterConfig.kafkaInputTopic)).thenReturn(new DeviceProfileUpdaterEventSource)
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
    //postgresConnect.closeConnection()

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