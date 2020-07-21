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
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.validator.domain.Event
import org.sunbird.dp.validator.task.{DruidValidatorConfig, DruidValidatorStreamTask}
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.embedded.RedisServer


class DruidValidatorStreamTaskTestSpec extends BaseTestSpec {

    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

    val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
      .setConfiguration(testConfiguration())
      .setNumberSlotsPerTaskManager(1)
      .setNumberTaskManagers(1)
      .build)

    var redisServer: RedisServer = _
    val config: Config = ConfigFactory.load("test.conf")
    val druidValidatorConfig: DruidValidatorConfig = new DruidValidatorConfig(config)
    val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
    val gson = new Gson()

    override protected def beforeAll(): Unit = {
        super.beforeAll()
        redisServer = new RedisServer(6341)
        redisServer.start()

        BaseMetricsReporter.gaugeMetrics.clear()

        when(mockKafkaUtil.kafkaEventSource[Event](druidValidatorConfig.kafkaInputTopic)).thenReturn(new DruidValidatorEventSource)

        when(mockKafkaUtil.kafkaEventSink[Event](druidValidatorConfig.kafkaDuplicateTopic)).thenReturn(new DupEventsSink)
        when(mockKafkaUtil.kafkaEventSink[Event](druidValidatorConfig.kafkaTelemetryRouteTopic)).thenReturn(new TelemetryEventsSink)
        when(mockKafkaUtil.kafkaEventSink[Event](druidValidatorConfig.kafkaSummaryRouteTopic)).thenReturn(new SummaryEventsSink)
        when(mockKafkaUtil.kafkaEventSink[Event](druidValidatorConfig.kafkaFailedTopic)).thenReturn(new FailedEventsSink)

        flinkCluster.before()
    }

    override protected def afterAll(): Unit = {
        super.afterAll()
        redisServer.stop()
        flinkCluster.after()
    }

    "Druid Validator job pipeline" should "validate events and route events to respective kafka topics" in {

        val task = new DruidValidatorStreamTask(druidValidatorConfig, mockKafkaUtil)
        task.process()
        TelemetryEventsSink.values.size() should be (2)
        SummaryEventsSink.values.size() should be (1)
        FailedEventsSink.values.size() should be (1)
        DupEventsSink.values.size() should be (1)

        DupEventsSink.values.get(0).getFlags.get("dv_processed").booleanValue() should be(true)
        DupEventsSink.values.get(0).getFlags.get("dv_duplicate").booleanValue() should be(true)
        
        FailedEventsSink.values.get(0).getFlags.get("dv_processed").booleanValue() should be(false)
        FailedEventsSink.values.get(0).getFlags.get("dv_validation_failed").booleanValue() should be(true)
        
        BaseMetricsReporter.gaugeMetrics(s"${druidValidatorConfig.jobName}.${druidValidatorConfig.validationSuccessMetricsCount}").getValue() should be (4)
        BaseMetricsReporter.gaugeMetrics(s"${druidValidatorConfig.jobName}.${druidValidatorConfig.validationFailureMetricsCount}").getValue() should be (1)

        BaseMetricsReporter.gaugeMetrics(s"${druidValidatorConfig.jobName}.duplicate-event-count").getValue() should be (1)
        BaseMetricsReporter.gaugeMetrics(s"${druidValidatorConfig.jobName}.unique-event-count").getValue() should be (3)

        BaseMetricsReporter.gaugeMetrics(s"${druidValidatorConfig.jobName}.${druidValidatorConfig.summaryRouterMetricCount}").getValue() should be (1)
        BaseMetricsReporter.gaugeMetrics(s"${druidValidatorConfig.jobName}.${druidValidatorConfig.telemetryRouterMetricCount}").getValue() should be (2)

    }

}

class DruidValidatorEventSource  extends SourceFunction[Event] {

    override def run(ctx: SourceContext[Event]) {
        val gson = new Gson()
        val event1 = gson.fromJson(EventFixture.VALID_DENORM_TELEMETRY_EVENT, new util.LinkedHashMap[String, Any]().getClass)
        val event2 = gson.fromJson(EventFixture.INVALID_DENORM_TELEMETRY_EVENT, new util.LinkedHashMap[String, Any]().getClass)
        val event3 = gson.fromJson(EventFixture.VALID_DENORM_SUMMARY_EVENT, new util.LinkedHashMap[String, Any]().getClass)
        val event4 = gson.fromJson(EventFixture.VALID_SERACH_EVENT, new util.LinkedHashMap[String, Any]().getClass)
        ctx.collect(new Event(event1))
        ctx.collect(new Event(event2))
        ctx.collect(new Event(event3))
        ctx.collect(new Event(event4))
        ctx.collect(new Event(event1))
    }

    override def cancel() = {

    }

}

class TelemetryEventsSink extends SinkFunction[Event] {

    override def invoke(value: Event): Unit = {
        synchronized {
            TelemetryEventsSink.values.add(value)
        }
    }
}

object TelemetryEventsSink {
    val values: util.List[Event] = new util.ArrayList()
}

class SummaryEventsSink extends SinkFunction[Event] {

    override def invoke(value: Event): Unit = {
        synchronized {
            SummaryEventsSink.values.add(value)
        }
    }
}

object SummaryEventsSink {
    val values: util.List[Event] = new util.ArrayList()
}

class FailedEventsSink extends SinkFunction[Event] {

    override def invoke(value: Event): Unit = {
        synchronized {
            FailedEventsSink.values.add(value)
        }
    }
}

object FailedEventsSink {
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