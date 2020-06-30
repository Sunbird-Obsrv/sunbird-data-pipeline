package org.sunbird.dp.spec

import java.sql.Timestamp
import java.util

import com.google.gson.Gson
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.junit.Assert.assertEquals
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.{PostgresConnect, PostgresConnectionConfig}
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterConfig
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterStreamTask
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.clients.jedis.Jedis
import redis.embedded.RedisServer

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

  var redisConnect: RedisConnect = _
  var jedis: Jedis = _
  val device_id = "232455"


  override protected def beforeAll(): Unit = {
    super.beforeAll()
    // Start Redis Server
    redisServer = new RedisServer(6340)
    redisServer.start()
    redisConnect = new RedisConnect(deviceProfileUpdaterConfig.metaRedisHost, deviceProfileUpdaterConfig.metaRedisPort, deviceProfileUpdaterConfig)
    // get the connect to specific db
    jedis = redisConnect.getConnection(deviceProfileUpdaterConfig.deviceDbStore)
    // Start the embeddedPostgress Server
    EmbeddedPostgres.builder.setPort(deviceProfileUpdaterConfig.postgresPort).start() // Use the same port 5430 which is defined in the base-test.conf
    // Clear the metrics
    BaseMetricsReporter.gaugeMetrics.clear()

    /**
     * This device_id data will be inserted into both redis and postgres before starting the job
     */


    val postgresConfig = PostgresConnectionConfig(
      user = deviceProfileUpdaterConfig.postgresUser,
      password = deviceProfileUpdaterConfig.postgresPassword,
      database = deviceProfileUpdaterConfig.postgresDb,
      host = deviceProfileUpdaterConfig.postgresHost,
      port = deviceProfileUpdaterConfig.postgresPort,
      maxConnections = deviceProfileUpdaterConfig.postgresMaxConnections
    )
    postgresConnect = new PostgresConnect(postgresConfig)

    // Create the postgres Table
    postgresConnect.execute("CREATE TABLE IF NOT EXISTS device_profile(\n" + "   device_id text PRIMARY KEY,\n" + "   api_last_updated_on TIMESTAMP,\n" + "    avg_ts float,\n" + "    city TEXT,\n" + "    country TEXT,\n" + "    country_code TEXT,\n" + "    device_spec json,\n" + "    district_custom TEXT,\n" + "    fcm_token TEXT,\n" + "    first_access TIMESTAMP,\n" + "    last_access TIMESTAMP,\n" + "    user_declared_on TIMESTAMP,\n" + "    producer_id TEXT,\n" + "    state TEXT,\n" + "    state_code TEXT,\n" + "    state_code_custom TEXT,\n" + "    state_custom TEXT,\n" + "    total_launches bigint,\n" + "    total_ts float,\n" + "    uaspec json,\n" + "    updated_date TIMESTAMP,\n" + "    user_declared_district TEXT,\n" + "    user_declared_state TEXT)")

    /*
    * Update the few fields like (last_acccess , user_declared_on)
    * Since these fields should not get update again if the these fields are already present in the db's
    */
    val updateQuery = String.format("UPDATE %s SET user_declared_on = '%s' WHERE device_id = '%s'", deviceProfileUpdaterConfig.postgresTable, new Timestamp(1587542087).toString, device_id)

    /*
    * If need to truncate the table uncomment the below and execute the query
    * val truncateQuery = String.format("TRUNCATE TABLE  %s RESTART IDENTITY", deviceProfileUpdaterConfig.postgresTable)
    * postgresConnect.execute(truncateQuery)
    *
    */

    postgresConnect.execute(updateQuery)

    /**
     * Inserting device data into redis initially, Since while updating redis,
     * Job should update the missing fields
     */
    val deviceData = new util.HashMap[String, String]()
    deviceData.put("api_last_updated_on", "1568377184000")
    deviceData.put("firstaccess", "1568377184000")
    deviceData.put("country_code", "IN")

    jedis.hmset(device_id, deviceData)
    val redisData = jedis.hgetAll(device_id)
    redisData should not be(null)
    redisData.size() should be(3)


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
    redisTableAssertion()
    postgresTableDataAssertion()

  }

  def redisTableAssertion(): Unit = {
    val redisResponseData = jedis.hgetAll(device_id)
    redisResponseData should not be (null)
    redisResponseData.get("api_last_updated_on") should be("1568377184000")
    redisResponseData.get("country") should be("India")
    redisResponseData.get("country_code") should be("IN")
    redisResponseData.get("user_declared_district") should be("Bengaluru's")
    redisResponseData.get("uaspec") should be("{\"agent\":\"Chrome\",\"ver\":\"76.0.3809.132\",\"system\":\"Mac OSX\",\"raw\":\"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Safari/537.36\"}")
    redisResponseData.get("city") should be("Bengaluru")
    redisResponseData.get("district_custom") should be("Karnataka,s")
    redisResponseData.get("user_declared_state") should be("Karnataka,'s")
    redisResponseData.get("state") should be("Karnataka")
    redisResponseData.get("state_code") should be("KA")
    redisResponseData.get("devicespec") should be("{\"os\":\"Android 6.0\",\"cpu\":\"abi: armeabi-v7a ARMv7 Processor rev 4 (v7l)\",\"make\":\"Motorola XT1706\"}")
    redisResponseData.get("state_code_custom") should be("29.0")
    redisResponseData.get("firstaccess") should be("1568377184000")
    redisResponseData.get("state_custom") should be("Karnataka")


  }

  def postgresTableDataAssertion(): Unit = {
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
      assertEquals("Bengaluru's", result1.getString("user_declared_district"))
      assertEquals("Karnataka,'s", result1.getString("user_declared_state"))
    }

    // When the value is stored with special characters
    val result2 = postgresConnect.executeQuery(String.format("SELECT user_declared_state FROM %s WHERE device_id='568089542';", deviceProfileUpdaterConfig.postgresTable))
    while ( {
      result2.next
    }) {
      assertEquals("Karnataka's", result2.getString(1))
    }

    // Should not addUserDeclared on if it is already present in the postgres
    val rs = postgresConnect.executeQuery(String.format("SELECT user_declared_on FROM %s WHERE device_id='232455';", deviceProfileUpdaterConfig.postgresTable))
//    while ( {
//      rs.next
//    }) //assertEquals("2019-09-13 12:19:44", rs.getString(1))
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