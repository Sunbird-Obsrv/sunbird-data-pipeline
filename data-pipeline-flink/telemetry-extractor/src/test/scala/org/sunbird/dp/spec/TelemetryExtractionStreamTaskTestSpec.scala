package org.sunbird.dp.spec

import java.util

import com.google.gson.Gson
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.mockito.Mockito
import org.mockito.Mockito._
import org.sunbird.dp.fixture.{Event, EventFixture}
import redis.embedded.RedisServer
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.domain.Events
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
    when(mockKafkaUtil.kafkaMapSink(extractorConfig.kafkaLogRouteTopic)).thenReturn(new LogEventsSink)
    when(mockKafkaUtil.kafkaStringSink(extractorConfig.kafkaLogRouteTopic)).thenReturn(new AuditEventsSink)
    when(mockKafkaUtil.kafkaMapSink(extractorConfig.kafkaFailedTopic)).thenReturn(new FailedEventsSink)
    when(mockKafkaUtil.kafkaStringSink(extractorConfig.kafkaBatchFailedTopic)).thenReturn(new FailedBatchEventsSink)
    when(mockKafkaUtil.kafkaMapSink(extractorConfig.kafkaAssessRawTopic)).thenReturn(new AssessRawEventsSink)

    setupRedisTestData()
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }
  def setupRedisTestData() {

    val redisConnect = new RedisConnect(extractorConfig.redisHost, extractorConfig.redisPort, extractorConfig)

    // Insert content test data
    val jedis = redisConnect.getConnection(extractorConfig.contentStore)
    jedis.set("do_21299582901864857613016", EventFixture.contentCacheData1)
    jedis.set("do_312526125187809280139355", EventFixture.contentCacheData2)
    jedis.close()

  }

  "Extraction job pipeline" should "extract events" in {

    val task = new TelemetryExtractorStreamTask(extractorConfig, mockKafkaUtil)
    task.process()

    // RawEventsSink.values.size() should be (43) // 43 events + 2 log events generated for auditing
    RawEventsSink.values.size() should be (6) // includes 1 Error Event
    AuditEventsSink.values.size() should be (2)
    LogEventsSink.values.size() should be (38)
    FailedEventsSink.values.size() should be (2)
    FailedBatchEventsSink.values.size() should be (1)
    DupEventsSink.values.size() should be (1)
    AssessRawEventsSink.values.size() should be (2)

    // val rawEvent = gson.fromJson(gson.toJson(RawEventsSink.values.get(0)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    val rawEvent = RawEventsSink.values.get(0)
    val dupEvent = gson.fromJson(gson.toJson(DupEventsSink.values.get(0)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    val failedEvent = gson.fromJson(gson.toJson(FailedEventsSink.values.get(0)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    // rawEvent("flags").asInstanceOf[util.Map[String, Boolean]].get("ex_processed") should be(true)
    rawEvent.flags().asInstanceOf[util.Map[String, Boolean]].get("ex_processed") should be(true)
    dupEvent("flags").asInstanceOf[util.Map[String, Boolean]].get("extractor_duplicate") should be(true)
    failedEvent("flags").asInstanceOf[util.Map[String, Boolean]].get("ex_processed") should be(false)

    // Assertions for redactor logic
    val responseEventWithoutValues = RawEventsSink.values.asScala.toList.filter(ev => ev.mid().equals("RESPONSE:4dd15933b5b8230deda7c4f67d8b61fc")).head
    responseEventWithoutValues.getTelemetry.read[util.Map[String, AnyRef]]("edata").get.get("values").asInstanceOf[util.ArrayList[AnyRef]].size should be (0)

    val assessEventWithoutResValues = RawEventsSink.values.asScala.filter(ev => ev.mid().equals("ASSESS:12159f2827880221eef12a6be9560379:test1")).head
    assessEventWithoutResValues.getTelemetry.read[util.Map[String, AnyRef]]("edata").get.get("resvalues").asInstanceOf[util.ArrayList[AnyRef]].size should be (0)


    val assessEventWithResValues = RawEventsSink.values.asScala.filter(ev => ev.mid().equals("ASSESS:12159f2827880221eef12a6be9560379:test2")).head
    assessEventWithResValues.getTelemetry.read[util.Map[String, AnyRef]]("edata").get.get("resvalues").asInstanceOf[util.ArrayList[AnyRef]].size should be (1)

    /*
    val responseEventWithoutValues = gson.fromJson(gson.toJson(RawEventsSink.values.get(39)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    responseEventWithoutValues("edata").asInstanceOf[util.Map[String, AnyRef]].get("values").asInstanceOf[util.ArrayList[AnyRef]].size should be (0)

    val assessEventWithoutResValues = gson.fromJson(gson.toJson(RawEventsSink.values.get(40)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    assessEventWithoutResValues("edata").asInstanceOf[util.Map[String, AnyRef]].get("resvalues").asInstanceOf[util.ArrayList[AnyRef]].size should be (0)

    val assessEventWithResValues = gson.fromJson(gson.toJson(RawEventsSink.values.get(42)), new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala
    assessEventWithResValues("edata").asInstanceOf[util.Map[String, AnyRef]].get("resvalues").asInstanceOf[util.ArrayList[AnyRef]].size should be (1)
    */

    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.successBatchCount}").getValue() should be (3)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.failedBatchCount}").getValue() should be (1)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.failedEventCount}").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.${extractorConfig.successEventCount}").getValue() should be (44)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.unique-event-count").getValue() should be (2)
    BaseMetricsReporter.gaugeMetrics(s"${extractorConfig.jobName}.duplicate-event-count").getValue() should be (1)
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
      val res = new Event(value.asInstanceOf[util.Map[String, Any]])
      RawEventsSink.values.add(new Event(value.asInstanceOf[util.Map[String, Any]]))
    }
  }
}

object RawEventsSink {
  val values: util.List[Event] = new util.ArrayList[Event]()
}

class FailedEventsSink extends SinkFunction[util.Map[String, AnyRef]] {

  override def invoke(value: util.Map[String, AnyRef]): Unit = {
    synchronized {
      FailedEventsSink.values.add(value)
    }
  }
}

class FailedBatchEventsSink extends SinkFunction[String] {
  override def invoke(value: String): Unit = {
    synchronized {
      FailedBatchEventsSink.values.add(value)
    }
  }
}

object FailedBatchEventsSink {
  val values: util.List[String] = new util.ArrayList()
}

object FailedEventsSink {
  val values: util.List[util.Map[String, AnyRef]] = new util.ArrayList()
}

class LogEventsSink extends SinkFunction[util.Map[String, AnyRef]] {

  override def invoke(value: util.Map[String, AnyRef]): Unit = {
    synchronized {
      LogEventsSink.values.add(new Event(value.asInstanceOf[util.Map[String, Any]]))
    }
  }
}

object LogEventsSink {
  val values: util.List[Event] = new util.ArrayList[Event]()
}


class AuditEventsSink extends SinkFunction[String] {
  override def invoke(value: String): Unit = {
    synchronized {
      AuditEventsSink.values.add(value)
    }
  }
}

object AuditEventsSink {
  val values: util.List[String] = new util.ArrayList()
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