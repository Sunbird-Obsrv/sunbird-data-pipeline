package org.sunbird.dp.spec

import java.util

import com.google.gson.Gson
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.fixture.EventFixture
import redis.embedded.RedisServer
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.extractor.task.{TelemetryExtractorConfig, TelemetryExtractorStreamTask}
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}

import collection.JavaConverters._

class ExtractionStreamTaskTestSpec extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val extractorConfig: TelemetryExtractorConfig = new TelemetryExtractorConfig(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6341)
    redisServer.start()
    BaseMetricsReporter.gaugeMetrics.clear()

    when(mockKafkaUtil.kafkaStringSource(extractorConfig.kafkaInputTopic)).thenReturn(new ExtractorEventSource)
    when(mockKafkaUtil.kafkaMapSink(extractorConfig.kafkaDuplicateTopic)).thenReturn(new DupEventsSink)
    when(mockKafkaUtil.kafkaMapSink(extractorConfig.kafkaSuccessTopic)).thenReturn(new RawEventsSink)
    when(mockKafkaUtil.kafkaMapSink(extractorConfig.kafkaFailedTopic)).thenReturn(new FailedEventsSink)
    when(mockKafkaUtil.kafkaMapSink(extractorConfig.kafkaAssessRawTopic)).thenReturn(new AssessRawEventsSink)

    setupRedisTestData
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }
  def setupRedisTestData() {

    val redisConnect = new RedisConnect(extractorConfig)

    // Insert content test data
    val jedis = redisConnect.getConnection(extractorConfig.contentStore)
    jedis.set("do_21299582901864857613016", EventFixture.contentCacheData1)
    jedis.set("do_312526125187809280139355", EventFixture.contentCacheData2)
    jedis.close()

  }

  "Extraction job pipeline" should "extract events" in {

    val task = new TelemetryExtractorStreamTask(extractorConfig, mockKafkaUtil)
    task.process()

    RawEventsSink.values.size() should be (45) // 43 events + 2 log events generated for auditing
    FailedEventsSink.values.size() should be (2)
    DupEventsSink.values.size() should be (1)
    AssessRawEventsSink.values.size() should be (2)

    val rawEvent = gson.fromJson(gson.toJson(RawEventsSink.values.get(0)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    val dupEvent = gson.fromJson(gson.toJson(DupEventsSink.values.get(0)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    val failedEvent = gson.fromJson(gson.toJson(FailedEventsSink.values.get(0)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    rawEvent("flags").asInstanceOf[util.Map[String, Boolean]].get("ex_processed") should be(true)
    dupEvent("flags").asInstanceOf[util.Map[String, Boolean]].get("extractor_duplicate") should be(true)
    failedEvent("flags").asInstanceOf[util.Map[String, Boolean]].get("ex_processed") should be(false)

    // Assertions for redactor logic
    val responseEventWithoutValues = gson.fromJson(gson.toJson(RawEventsSink.values.get(39)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    responseEventWithoutValues("edata").asInstanceOf[util.Map[String, AnyRef]].get("values").asInstanceOf[util.ArrayList[AnyRef]].size should be (0)
    val assessEventWithoutResValues = gson.fromJson(gson.toJson(RawEventsSink.values.get(40)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    assessEventWithoutResValues("edata").asInstanceOf[util.Map[String, AnyRef]].get("resvalues").asInstanceOf[util.ArrayList[AnyRef]].size should be (0)
    val assessEventWithResValues = gson.fromJson(gson.toJson(RawEventsSink.values.get(42)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    assessEventWithResValues("edata").asInstanceOf[util.Map[String, AnyRef]].get("resvalues").asInstanceOf[util.ArrayList[AnyRef]].size should be (1)

    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.successBatchCount}").getValue() should be (3)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.failedBatchCount}").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.successEventCount}").getValue() should be (43)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.unique-event-count").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.duplicate-event-count").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.failedEventCount}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.auditEventCount}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.skippedEventCount}").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.cacheHitCount}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.cacheMissCount}").getValue() should be (2)
  }

}

class ExtractorEventSource extends SourceFunction[String] {

  override def run(ctx: SourceContext[String]) {
    val gson = new Gson()
    val event1 = EventFixture.EVENT_WITH_MESSAGE_ID
    val event2 = EventFixture.EVENT_WITHOUT_MESSAGE_ID
    val event3 = EventFixture.INVALID_BATCH_EVENT
    ctx.collect(event1)
    ctx.collect(event1)
    ctx.collect(event2)
    ctx.collect(event3)
  }

  override def cancel() = {}

}

class RawEventsSink extends SinkFunction[util.Map[String, AnyRef]] {

  override def invoke(value: util.Map[String, AnyRef]): Unit = {
    synchronized {
      RawEventsSink.values.add(value)
    }
  }
}

object RawEventsSink {
  val values: util.List[util.Map[String, AnyRef]] = new util.ArrayList()
}

class FailedEventsSink extends SinkFunction[util.Map[String, AnyRef]] {

  override def invoke(value: util.Map[String, AnyRef]): Unit = {
    synchronized {
      FailedEventsSink.values.add(value)
    }
  }
}

object FailedEventsSink {
  val values: util.List[util.Map[String, AnyRef]] = new util.ArrayList()
}

class LogEventsSink extends SinkFunction[util.Map[String, AnyRef]] {

  override def invoke(value: util.Map[String, AnyRef]): Unit = {
    synchronized {
      LogEventsSink.values.add(value)
    }
  }
}

object LogEventsSink {
  val values: util.List[util.Map[String, AnyRef]] = new util.ArrayList()
}

class DupEventsSink extends SinkFunction[util.Map[String, AnyRef]] {

  override def invoke(value: util.Map[String, AnyRef]): Unit = {
    synchronized {
      DupEventsSink.values.add(value)
    }
  }
}

object DupEventsSink {
  val values: util.List[util.Map[String, AnyRef]] = new util.ArrayList()
}

class AssessRawEventsSink extends SinkFunction[util.Map[String, AnyRef]] {

  override def invoke(value: util.Map[String, AnyRef]): Unit = {
    synchronized {
        AssessRawEventsSink.values.add(value)
    }
  }
}

object AssessRawEventsSink {
  val values: util.List[util.Map[String, AnyRef]] = new util.ArrayList()
}