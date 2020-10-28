package org.sunbird.dp.spec

import java.nio.charset.StandardCharsets
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
import org.sunbird.dp.ingestrouter.task.{IngestRouterConfig, IngestRouterStreamTask}
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}

import collection.JavaConverters._

class IngestRouterStreamTaskTestSpec extends BaseTestSpec {

  implicit val bytesTypeInfo: TypeInformation[Array[Byte]] = TypeExtractor.getForClass(classOf[Array[Byte]])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  val config: Config = ConfigFactory.load("test.conf")
  val ingestRouterConfig: IngestRouterConfig = new IngestRouterConfig(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    BaseMetricsReporter.gaugeMetrics.clear()

    when(mockKafkaUtil.kafkaBytesSource(ingestRouterConfig.kafkaInputTopic)).thenReturn(new IngestRouterEventSource)
    when(mockKafkaUtil.kafkaBytesSink(ingestRouterConfig.kafkaSuccessTopic)).thenReturn(new SuccessEventsSink)

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    flinkCluster.after()
  }

  "Ingest router job pipeline" should "just pass through events" in {

    val task = new IngestRouterStreamTask(ingestRouterConfig, mockKafkaUtil)
    task.process()

      SuccessEventsSink.values.size() should be (3)

    val firstEvent = SuccessEventsSink.values.get(0)
//    println(new String(firstEvent, StandardCharsets.UTF_8))

  }

}

class IngestRouterEventSource extends SourceFunction[Array[Byte]] {

  override def run(ctx: SourceContext[Array[Byte]]) {
    val gson = new Gson()
    val event1 = EventFixture.EVENT_WITH_MESSAGE_ID
    val event2 = EventFixture.EVENT_WITHOUT_MESSAGE_ID
    val event3 = EventFixture.INVALID_BATCH_EVENT
    ctx.collect(event1.getBytes(StandardCharsets.UTF_8))
    ctx.collect(event2.getBytes(StandardCharsets.UTF_8))
    ctx.collect(event3.getBytes(StandardCharsets.UTF_8))
  }

  override def cancel() = {}

}

class SuccessEventsSink extends SinkFunction[Array[Byte]] {

  override def invoke(value: Array[Byte]): Unit = {
    synchronized {
      SuccessEventsSink.values.add(value)
    }
  }
}

object SuccessEventsSink {
  val values: util.List[Array[Byte]] = new util.ArrayList[Array[Byte]]()
}
