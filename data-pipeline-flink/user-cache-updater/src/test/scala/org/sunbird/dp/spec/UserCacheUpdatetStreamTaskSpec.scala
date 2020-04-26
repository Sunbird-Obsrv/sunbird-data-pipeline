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
import org.sunbird.dp.core.util.CassandraConnect
import org.sunbird.dp.denorm.task.UserCacheUpdaterStreamTask
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfig
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
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

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    println("******Starting the Embedded Cassandra*******")
    EmbeddedCassandraServerHelper.startEmbeddedCassandra(80000L)
    println("Host" + EmbeddedCassandraServerHelper.getHost)
    println("Port" + EmbeddedCassandraServerHelper.getRpcPort)
    println("Port1" + EmbeddedCassandraServerHelper.getNativeTransportPort)

    val cassandraUtil = new CassandraConnect(userCacheConfig.cassandraHost, userCacheConfig.cassandraPort)
    val session = cassandraUtil.session
    val dataLoader = new CQLDataLoader(session);
//   // val path = getClass.getResource("/test.cql").getPath
//   // println("path" + path)
//    val query = "CREATE TABLE IF NOT EXISTS learner_db.learnercontentsummary(\n\tlearner_id text,\n\tcontent_id text,\n\ttime_spent double,\n\tinteractions_per_min double,\n\tnum_of_sessions_played int,\n\tupdated_date timestamp,\n\tPRIMARY KEY (learner_id, content_id)\n);"
    val keySpaceQuery = s"CREATE KEYSPACE ${userCacheConfig.keySpace}\nWITH replication = {'class':'SimpleStrategy', 'replication_factor' : 3};"

    session.execute(keySpaceQuery)
    dataLoader.load(new FileCQLDataSet("/Users/manju/Documents/Ekstep/Github/sunbird-data-pipeline/data-pipeline-flink/user-cache-updater/src/test/scala/org/sunbird/dp/spec/test.cql", true, true));
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

    val redisConnect = new RedisConnect(userCacheConfig)

    var jedis = redisConnect.getConnection(userCacheConfig.userStore)

    // Insert user test data
    jedis.set("b7470841-7451-43db-b5c7-2dcf4f8d3b23", EventFixture.userCacheData1)
    jedis.set("610bab7d-1450-4e54-bf78-c7c9b14dbc81", EventFixture.userCacheData2)
    jedis.close()
  }

  "UserCache Updater pipeline" should "Should able to add user data into cache" in {

    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new InputSource)

    val task = new UserCacheUpdaterStreamTask(userCacheConfig, mockKafkaUtil)
    task.process()

    /**
     * User Cache Assertion
     */
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.userCacheHit}").getValue() should be(0)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.userCacheMiss}").getValue() should be(0)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.dbHitCount}").getValue() should be(0)

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

