package org.sunbird.dp.spec

import java.util

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.cache.RedisConnect
import org.sunbird.dp.core.FlinkKafkaConnector
import org.sunbird.dp.domain.Event
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.task.{ContentCacheUpdaterConfig, ContentCacheUpdaterStreamTask}
import org.sunbird.dp.util.RestUtil
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
    val config = ConfigFactory.load("test.conf");
    val contentConfig: ContentCacheUpdaterConfig = new ContentCacheUpdaterConfig(config);
    val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
    //val mockRestUtil: RestUtil = mock[RestUtil](Mockito.withSettings().serializable())
    val mockRestUtil = new RestUtil
    val gson = new Gson()

    override protected def beforeAll(): Unit = {
        super.beforeAll()
        BaseMetricsReporter.gaugeMetrics.clear()
         redisServer = new RedisServer(6340)
        redisServer.start()

        setupRedisTestData();
        flinkCluster.before()
    }

    override protected def afterAll(): Unit = {
        super.afterAll()
        redisServer.stop()
        flinkCluster.after()
    }


    def setupRedisTestData() {

        val redisConnect = new RedisConnect(contentConfig)
        var jedis = redisConnect.getConnection(contentConfig.dialcodeStore)
        // Insert dialcode test data
        jedis.set("X3J6W3", "{\"identifier\" :\"X3J6W3\",\"channel\": \"0124784842112040965\"}")
        jedis.close()

    }


    "ContentUpdaterTask" should "test the content function" in {

        when(mockKafkaUtil.kafkaEventSource[Event](contentConfig.inputTopic)).thenReturn(new ContentDialCodeSource)
        val json = "{\n    \"id\": \"sunbird.dialcode.read\",\n    \"ver\": \"3.0\",\n    \"ts\": \"2020-04-20T12:43:32ZZ\",\n    \"params\": {\n        \"resmsgid\": \"9e7302d9-9756-4b41-adf3-5fb65cba27bf\",\n        \"msgid\": null,\n        \"err\": null,\n        \"status\": \"successful\",\n        \"errmsg\": null\n    },\n    \"responseCode\": \"OK\",\n    \"result\": {\n        \"dialcode\": {\n            \"identifier\": \"X3J6W1\",\n            \"channel\": \"0124784842112040965\",\n            \"publisher\": null,\n            \"batchCode\": \"do_2129902851973693441453\",\n            \"status\": \"Draft\",\n            \"generatedOn\": \"2020-04-01T08:10:09.830+0000\",\n            \"publishedOn\": null,\n            \"metadata\": null\n        }\n    }\n}"
        val dialCodeMap = gson.fromJson[LinkedTreeMap[String, Object]](json, new TypeToken[LinkedTreeMap[String, Object]]() {}.getType)

       // when(mockRestUtil.get[LinkedTreeMap[String, Object]](ArgumentMatchers.anyString(), Option(ArgumentMatchers.any()))).thenReturn(dialCodeMap)
        val task = new ContentCacheUpdaterStreamTask(contentConfig, mockKafkaUtil, mockRestUtil)

        task.process()
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.dialCodeApiHit}").getValue() should be(1)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.contentCacheHit}").getValue() should be(5)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.dialCodeApiMissHit}").getValue() should be(1)
        BaseMetricsReporter.gaugeMetrics(s"${contentConfig.jobName}.${contentConfig.dialCodeCacheHit}").getValue() should be(2)
        val redisConnect = new RedisConnect(contentConfig)
        var jedis = redisConnect.getConnection(contentConfig.dialcodeStore)
        assert(jedis.get("X3J6W1").contains("channel"))

    }
}


class ContentDialCodeSource extends SourceFunction[Event] {

    override def run(ctx: SourceContext[Event]) {
        val gson = new Gson()
        val event1 = gson.fromJson(EventFixture.contentData1, new util.LinkedHashMap[String, Any]().getClass)
        val event2 = gson.fromJson(EventFixture.contentData2, new util.LinkedHashMap[String, Any]().getClass)
        val event3 = gson.fromJson(EventFixture.contentData3, new util.LinkedHashMap[String, Any]().getClass)
        val event4 = gson.fromJson(EventFixture.dialcodedata1, new util.LinkedHashMap[String, Any]().getClass)
        val event5 = gson.fromJson(EventFixture.invalid_dialcocedata, new util.LinkedHashMap[String, Any]().getClass)
        val event6 = gson.fromJson(EventFixture.reserved_dialcocedata, new util.LinkedHashMap[String, Any]().getClass)
        val event7 = gson.fromJson(EventFixture.dialcodedata2, new util.LinkedHashMap[String, Any]().getClass)
        ctx.collect(new Event(event1, 0))
        ctx.collect(new Event(event2, 0))
        ctx.collect(new Event(event3, 0))
        ctx.collect(new Event(event4, 0))
        ctx.collect(new Event(event5, 0))
        ctx.collect(new Event(event6, 0))
        ctx.collect(new Event(event7, 0))

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