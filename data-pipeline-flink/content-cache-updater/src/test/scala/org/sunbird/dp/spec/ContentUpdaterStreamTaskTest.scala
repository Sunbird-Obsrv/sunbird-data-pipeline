package org.sunbird.dp.contentupdater.spec

import java.io.IOException
import java.util

import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import okhttp3.mockwebserver.{MockResponse, MockWebServer}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.contentupdater.domain.Event
import org.sunbird.dp.contentupdater.task.{ContentCacheUpdaterConfig, ContentCacheUpdaterStreamTask}
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.JSONUtil
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.embedded.RedisServer

class ContentUpdaterStreamTaskTest extends BaseTestSpec {

    implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
      .setConfiguration(testConfiguration())
      .setNumberSlotsPerTaskManager(1)
      .setNumberTaskManagers(1)
      .build)
    var redisServer: RedisServer = _
    val config = ConfigFactory.load("test.conf")
    val contentConfig: ContentCacheUpdaterConfig = new ContentCacheUpdaterConfig(config)
    val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())

    val gson = new Gson()
    val server = new MockWebServer()
    override protected def beforeAll(): Unit = {
        super.beforeAll()
        BaseMetricsReporter.gaugeMetrics.clear()
        redisServer = new RedisServer(6340)
        redisServer.start()

        setupRedisTestData()
        setupRestUtilData()
        flinkCluster.before()
    }

    override protected def afterAll(): Unit = {
        super.afterAll()
        redisServer.stop()
        server.close()
        flinkCluster.after()
    }

    def setupRestUtilData(): Unit = {

        val json = """{"id":"sunbird.dialcode.read","ver":"3.0","ts":"2020-04-21T02:51:39ZZ","params":{"resmsgid":"4544fce4-efee-4ee2-8816-fdb3f60ac492","msgid":null,"err":null,"status":"successful","errmsg":null},"responseCode":"OK","result":{"dialcode":{"identifier":"X3J6W1","channel":"0124784842112040965","publisher":null,"batchCode":"do_2129902851973693441453","status":"Draft","generatedOn":"2020-04-01T08:10:09.830+0000","publishedOn":null,"metadata":null}}}"""
        val invalid_json = "{\"id\":\"sunbird.dialcode.read\",\"ver\":\"3.0\",\"ts\":\"2020-04-21T02:51:39ZZ\",\"params\":{\"resmsgid\":\"4544fce4-efee-4ee2-8816-fdb3f60ac492\",\"msgid\":null,\"err\":null,\"status\":\"successful\",\"errmsg\":null},\"responseCode\":\"OK\",\"result\":{\"status\":\"No dialcodeFound\"}}"
        try
            server.start(3000)
        catch {
            case e: IOException =>
                System.out.println("Exception" + e)
        }
        server.enqueue(new MockResponse().setBody(json))
        server.url("http://127.0.0.1:3000/api/dialcode/v3/read/X3J6W1")
        server.enqueue(new MockResponse().setBody(invalid_json))
        server.url("http://127.0.0.1:3000/api/dialcode/v3/read/X3J6W2")
        server.enqueue(new MockResponse().setBody(invalid_json))
        server.url("http://127.0.0.1:3000/api/dialcode/v3/read/X3J6W3")
        server.enqueue(new MockResponse().setHeader("Authorization", "auth_token"))

    }

    def setupRedisTestData() {

        val redisConnect = new RedisConnect(contentConfig.metaRedisHost, contentConfig.metaRedisPort, contentConfig)
        val jedis = redisConnect.getConnection(contentConfig.dialcodeStore)
        // Insert dialcode test data
        jedis.set("X3J6W3", "{\"identifier\" :\"X3J6W3\",\"channel\": \"0124784842112040965\"}")
        jedis.close()

    }


    "ContentUpdaterTask" should "test the content function" in {

        when(mockKafkaUtil.kafkaEventSource[Event](contentConfig.inputTopic)).thenReturn(new ContentDialCodeSource)
        val task = new ContentCacheUpdaterStreamTask(contentConfig, mockKafkaUtil)
        task.process()
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.skippedEventCount}").getValue() should be(1)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.dialCodeApiHit}").getValue() should be(1)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.contentCacheHit}").getValue() should be(11)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.dialCodeApiMissHit}").getValue() should be(1)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.dialCodeCacheHit}").getValue() should be(2)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.totaldialCodeCount}").getValue() should be(3)
        val redisConnect = new RedisConnect(contentConfig.metaRedisHost, contentConfig.metaRedisPort, contentConfig)
        val jedis = redisConnect.getConnection(contentConfig.dialcodeStore)
        assert(jedis.get("X3J6W1").contains("channel"))
        val contentJedis = redisConnect.getConnection(contentConfig.contentStore)
        assert(contentJedis.get("do_312999792564027392148").contains("\"board\":\"CBSE\""))
        assert(contentJedis.get("do_312999792564027392148").contains("Class 8"))
        assert(!contentJedis.get("do_312999792564027392148").contains("Class 12"))
        assert(contentJedis.get("do_312999792564027392148").contains("\"copyright\":\"Ekstep\""))
        assert(!contentJedis.get("do_312999792564027392148").contains("\"copyright\":\"EKSTEP\""))
        assert(contentJedis.get("do_312999792564027392148").contains(""""keywords":["Story"]"""))
    }
}


class ContentDialCodeSource extends SourceFunction[Event] {

    override def run(ctx: SourceContext[Event]) {
        val gson = new Gson()
        val event1 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.contentData1)
        val event2 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.contentData2)
        val event3 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.contentData3)
        val event4 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.dialcodedata1)
        val event5 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.invalid_dialcocedata)
        val event6 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.reserved_dialcocedata)
        val event7 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.dialcodedata2)
        val event8 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.invalid_data)
        val event9 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.empty_dialcode)
        val event10 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.contentUpdateData3)
        val event11 = JSONUtil.deserialize[util.HashMap[String, Any]](EventFixture.invalidNewValueEvent)

        ctx.collect(new Event(event1))
        ctx.collect(new Event(event2))
        ctx.collect(new Event(event3))
        ctx.collect(new Event(event4))
        ctx.collect(new Event(event5))
        ctx.collect(new Event(event6))
        ctx.collect(new Event(event7))
        ctx.collect(new Event(event8))
        ctx.collect(new Event(event9))
        ctx.collect(new Event(event10))
        ctx.collect(new Event(event11))
        // for invalid event check - EventSerializationSchema returns empty map for invalid JSON.
        // EX: """Type":"DialCode"}""" and """reatedOn":"2021-02-11T07:41:59.691+0000","objectType":"DialCode"}"""
        ctx.collect(new Event(new util.HashMap[String, Any]()))
    }

    override def cancel() = {

    }

}


class MetricsEventsSink extends SinkFunction[String] {

    override def invoke(value: String): Unit = {
        synchronized {
            MetricsEventsSink.values.append(value)
        }
    }
}

object MetricsEventsSink {
    val values: scala.collection.mutable.Buffer[String] = scala.collection.mutable.Buffer[String]()
}