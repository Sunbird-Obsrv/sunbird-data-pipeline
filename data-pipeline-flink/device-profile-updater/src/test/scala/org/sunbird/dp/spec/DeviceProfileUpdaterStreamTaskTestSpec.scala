package org.sunbird.dp.spec

import java.sql.{ResultSet, Timestamp}
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
import org.junit.Assert.assertEquals
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
    postgresConnect.execute("CREATE TABLE IF NOT EXISTS device_profile(\n" + "   device_id text PRIMARY KEY,\n" + "   api_last_updated_on TIMESTAMP,\n" + "    avg_ts float,\n" + "    city TEXT,\n" + "    country TEXT,\n" + "    country_code TEXT,\n" + "    device_spec json,\n" + "    district_custom TEXT,\n" + "    fcm_token TEXT,\n" + "    first_access TIMESTAMP,\n" + "    last_access TIMESTAMP,\n" + "    user_declared_on TIMESTAMP,\n" + "    producer_id TEXT,\n" + "    state TEXT,\n" + "    state_code TEXT,\n" + "    state_code_custom TEXT,\n" + "    state_custom TEXT,\n" + "    total_launches bigint,\n" + "    total_ts float,\n" + "    uaspec json,\n" + "    updated_date TIMESTAMP,\n" + "    user_declared_district TEXT,\n" + "    user_declared_state TEXT)")
    val updateQuery = String.format("UPDATE %s SET user_declared_on = '%s' WHERE device_id = '%s'", deviceProfileUpdaterConfig.postgresTable, new Timestamp(1587542087).toString, "232455")
    val truncateQuery = String.format("TRUNCATE TABLE  %s RESTART IDENTITY", deviceProfileUpdaterConfig.postgresTable)
    postgresConnect.execute(truncateQuery)
    postgresConnect.execute(updateQuery)
    val result1 = postgresConnect.executeQuery("SELECT * from device_profile where device_id='232455'")
    while ( {
      result1.next
    }) {
      assertEquals("Bengaluru", result1.getString("city"))
      assertEquals("Karnataka", result1.getString("state"))
      assertEquals("India", result1.getString("country"))
      assertEquals("IN", result1.getString("country_code"))
      assertEquals("dev.sunbird.portal", result1.getString("producer_id"))
      assertEquals("Bengaluru", result1.getString("user_declared_district"))
      assertEquals("Karnataka", result1.getString("user_declared_state"))
      assertEquals("1970-01-19 14:29:02.087", result1.getString("user_declared_on"))
    }

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
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.successCount}").getValue() should be(3)
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.failedEventCount}").getValue() should be(1)
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.cacheHitCount}").getValue() should be(3)
    BaseMetricsReporter.gaugeMetrics(s"${deviceProfileUpdaterConfig.jobName}.${deviceProfileUpdaterConfig.deviceDbHitCount}").getValue() should be(3)

    // Should get the all device details from the postgres
    val result1 = postgresConnect.executeQuery("SELECT * from device_profile where device_id='232455'")
    while ( {
      result1.next
    }) {
      assertEquals("Bengaluru", result1.getString("city"))
      assertEquals("Karnataka", result1.getString("state"))
      assertEquals("India", result1.getString("country"))
      assertEquals("IN", result1.getString("country_code"))
      assertEquals("dev.sunbird.portal", result1.getString("producer_id"))
      assertEquals("Bengaluru", result1.getString("user_declared_district"))
      assertEquals("Karnataka", result1.getString("user_declared_state"))
    }

    // When the value is stored with special characters
    val result2 = postgresConnect.executeQuery(String.format("SELECT user_declared_state FROM %s WHERE device_id='568089542';", deviceProfileUpdaterConfig.postgresTable))
    while ( {
      result2.next
    }) {
      assertEquals("Karnataka's", result2.getString(1))
    }

    // Should not addUserDeclared on if it is already present in the postgres

    //val query: String = String.format("INSERT INTO %s (device_id, user_declared_on) VALUES ('232455','2019-09-24 01:03:04.999');", deviceProfileUpdaterConfig.postgresTable)
    //println(query)
    //postgresConnect.execute(query)

    val rs = postgresConnect.executeQuery(String.format("SELECT user_declared_on FROM %s WHERE device_id='232455';", deviceProfileUpdaterConfig.postgresTable))
    while ( {
      rs.next
    }) assertEquals("2019-09-24 01:03:04.999", rs.getString(1))


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