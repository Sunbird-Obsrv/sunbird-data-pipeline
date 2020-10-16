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
import org.sunbird.dp.core.domain.Events
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.processor.task.{TelemetryProcessorConfig, TelemetryProcessorStreamTask}
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}

import collection.JavaConverters._

class ProcessorStreamTaskTestSpec extends BaseTestSpec {

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  val config: Config = ConfigFactory.load("test.conf")
  val processorConfig: TelemetryProcessorConfig = new TelemetryProcessorConfig(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    BaseMetricsReporter.gaugeMetrics.clear()

    when(mockKafkaUtil.kafkaStringSource(processorConfig.kafkaInputTopic)).thenReturn(new ProcessorEventSource)
    when(mockKafkaUtil.kafkaStringSink(processorConfig.kafkaSuccessTopic)).thenReturn(new SuccessEventsSink)

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    flinkCluster.after()
  }

  "Processor job pipeline" should "just pass through events" in {

    val task = new TelemetryProcessorStreamTask(processorConfig, mockKafkaUtil)
    task.process()

      SuccessEventsSink.values.size() should be (3)

//    val firstEvent = SuccessEventsSink.values.get(0)
    BaseMetricsReporter.gaugeMetrics(s"${processorConfig.jobName}.${processorConfig.successEventCount}").getValue() should be (3)
  }

}

class ProcessorEventSource extends SourceFunction[String] {

  override def run(ctx: SourceContext[String]) {
    val gson = new Gson()
    val event1 = EventFixture.EVENT_WITH_MESSAGE_ID
    val event2 = EventFixture.EVENT_WITHOUT_MESSAGE_ID
    val event3 = EventFixture.INVALID_BATCH_EVENT
    ctx.collect(event1)
    ctx.collect(event2)
    ctx.collect(event3)
  }

  override def cancel() = {}

}

class SuccessEventsSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      SuccessEventsSink.values.add(value)
    }
  }
}

object SuccessEventsSink {
  val values: util.List[String] = new util.ArrayList[String]()
}
