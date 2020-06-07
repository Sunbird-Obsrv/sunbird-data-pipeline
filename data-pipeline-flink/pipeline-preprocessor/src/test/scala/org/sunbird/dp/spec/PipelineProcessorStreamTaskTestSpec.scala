package org.sunbird.dp.spec

import java.util

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
import org.sunbird.dp.fixture.EventFixtures
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.preprocessor.domain.Event
import org.sunbird.dp.preprocessor.task.{PipelinePreprocessorConfig, PipelinePreprocessorStreamTask}
import redis.embedded.RedisServer

class PipelineProcessorStreamTaskTestSpec extends BaseTestSpec {

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val ppConfig: PipelinePreprocessorConfig = new PipelinePreprocessorConfig(config)

  val gson = new Gson()
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6341)
    redisServer.start()

    BaseMetricsReporter.gaugeMetrics.clear()

    when(mockKafkaUtil.kafkaEventSource[Event](ppConfig.kafkaInputTopic)).thenReturn(new PipeLineProcessorEventSource)

    when(mockKafkaUtil.kafkaEventSink[Event](ppConfig.kafkaDuplicateTopic)).thenReturn(new DupEventsSink)
    when(mockKafkaUtil.kafkaEventSink[Event](ppConfig.kafkaPrimaryRouteTopic)).thenReturn(new TelemetryPrimaryEventSink)
    when(mockKafkaUtil.kafkaEventSink[Event](ppConfig.kafkaLogRouteTopic)).thenReturn(new TelemetryLogEventSink)
    when(mockKafkaUtil.kafkaEventSink[Event](ppConfig.kafkaErrorRouteTopic)).thenReturn(new TelemetryErrorEventSink)
    when(mockKafkaUtil.kafkaEventSink[Event](ppConfig.kafkaFailedTopic)).thenReturn(new TelemetryFailedEventsSink)
    when(mockKafkaUtil.kafkaEventSink[Event](ppConfig.kafkaAuditRouteTopic)).thenReturn(new TelemetryAuditEventSink)
    when(mockKafkaUtil.kafkaStringSink(ppConfig.kafkaPrimaryRouteTopic)).thenReturn(new ShareItemEventSink)

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }

  "Pipline Processor job pipeline" should "process the events" in {

    val task = new PipelinePreprocessorStreamTask(ppConfig, mockKafkaUtil)
    task.process()

    ShareItemEventSink.values.size() should be(3)
    TelemetryPrimaryEventSink.values.size() should be(7)
    TelemetryFailedEventsSink.values.size() should be(2)
    DupEventsSink.values.size() should be(1)
    TelemetryAuditEventSink.values.size() should be(1)
    TelemetryLogEventSink.values.size() should be(1)
    TelemetryErrorEventSink.values.size() should be(1)

    /**
     * * 1. primary-route-success-count -> 07
     * * 2. audit-route-success-count -> 01
     * * 3. share-route-success-count ->
     * * 4. log-route-success-count -> 01
     * * 5. error-route-success-count -> 01
     * * 6. validation-success-event-count -> 09
     * * 7. validation-failed-event-count -> 02
     * * 8. duplicate-event-count -> 01
     * * 9. duplicate-skipped-event-count ->  06
     * * 10. unique-event-count -> 02
     * * 12. share-item-event-success-count -> 03
     */

    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.primaryRouterMetricCount}").getValue() should be(7)
    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.shareItemEventsMetircsCount}").getValue() should be(3)
    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.auditEventRouterMetricCount}").getValue() should be(1)
    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.shareEventsRouterMetricCount}").getValue() should be(1)
    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.logEventsRouterMetricsCount}").getValue() should be(1)
    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.errorEventsRouterMetricsCount}").getValue() should be(1)


    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.validationSuccessMetricsCount}").getValue() should be(10)
    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.${ppConfig.validationFailureMetricsCount}").getValue() should be(2)

    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.unique-event-count").getValue() should be(9)
    BaseMetricsReporter.gaugeMetrics(s"${ppConfig.jobName}.duplicate-event-count").getValue() should be(1)
  }
}

class PipeLineProcessorEventSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    val event1 = gson.fromJson(EventFixtures.EVENT_1, new util.LinkedHashMap[String, Any]().getClass)
    val event2 = gson.fromJson(EventFixtures.EVENT_2, new util.LinkedHashMap[String, Any]().getClass)
    val event3 = gson.fromJson(EventFixtures.EVENT_3, new util.LinkedHashMap[String, Any]().getClass)
    val event4 = gson.fromJson(EventFixtures.EVENT_4, new util.LinkedHashMap[String, Any]().getClass)
    val event5 = gson.fromJson(EventFixtures.EVENT_5, new util.LinkedHashMap[String, Any]().getClass)
    val event6 = gson.fromJson(EventFixtures.EVENT_6, new util.LinkedHashMap[String, Any]().getClass)
    val event7 = gson.fromJson(EventFixtures.EVENT_7, new util.LinkedHashMap[String, Any]().getClass)
    val event8 = gson.fromJson(EventFixtures.EVENT_8, new util.LinkedHashMap[String, Any]().getClass)
    val event9 = gson.fromJson(EventFixtures.EVENT_9, new util.LinkedHashMap[String, Any]().getClass)
    val event10 = gson.fromJson(EventFixtures.EVENT_10, new util.LinkedHashMap[String, Any]().getClass)
    val event11 = gson.fromJson(EventFixtures.EVENT_11, new util.LinkedHashMap[String, Any]().getClass)
    val event12 = gson.fromJson(EventFixtures.EVENT_12, new util.LinkedHashMap[String, Any]().getClass)
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
    ctx.collect(new Event(event12))
  }

  override def cancel() = {}

}

class ShareItemEventSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      ShareItemEventSink.values.add(value)
    }
  }
}

object ShareItemEventSink {
  val values: util.List[String] = new util.ArrayList()
}

class TelemetryFailedEventsSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      TelemetryFailedEventsSink.values.add(value)
    }
  }
}

object TelemetryFailedEventsSink {
  val values: util.List[Event] = new util.ArrayList()
}


class TelemetryPrimaryEventSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      TelemetryPrimaryEventSink.values.add(value)
    }
  }
}

object TelemetryPrimaryEventSink {
  val values: util.List[Event] = new util.ArrayList()
}


class TelemetryLogEventSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      TelemetryLogEventSink.values.add(value)
    }
  }
}

object TelemetryLogEventSink {
  val values: util.List[Event] = new util.ArrayList()
}

class TelemetryErrorEventSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      TelemetryErrorEventSink.values.add(value)
    }
  }
}

object TelemetryErrorEventSink {
  val values: util.List[Event] = new util.ArrayList()
}


class TelemetryAuditEventSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      TelemetryAuditEventSink.values.add(value)
    }
  }
}

object TelemetryAuditEventSink {
  val values: util.List[Event] = new util.ArrayList()
}

class DupEventsSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      DupEventsSink.values.add(value)
    }
  }
}

object DupEventsSink {
  val values: util.List[Event] = new util.ArrayList()
}