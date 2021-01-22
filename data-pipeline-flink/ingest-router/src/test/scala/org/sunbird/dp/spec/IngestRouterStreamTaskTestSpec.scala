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

    when(mockKafkaUtil.kafkaBytesSource(ingestRouterConfig.kafkaIngestInputTopic)).thenReturn(new IngestRouterEventSource)
    when(mockKafkaUtil.kafkaBytesSink(ingestRouterConfig.kafkaIngestSuccessTopic)).thenReturn(new IngestSuccessEventsSink)

    when(mockKafkaUtil.kafkaBytesSource(ingestRouterConfig.kafkaRawInputTopic)).thenReturn(new RawRouterEventSource)
    when(mockKafkaUtil.kafkaBytesSink(ingestRouterConfig.kafkaRawSuccessTopic)).thenReturn(new RawSuccessEventsSink)

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    flinkCluster.after()
  }

  "Ingest router job pipeline" should "just pass through ingest events" in {

      val task = new IngestRouterStreamTask(ingestRouterConfig, mockKafkaUtil)
      task.process()

      IngestSuccessEventsSink.values.size() should be(3)

      RawSuccessEventsSink.values.size() should be(3)

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

class RawRouterEventSource extends SourceFunction[Array[Byte]] {

  override def run(ctx: SourceContext[Array[Byte]]) {
    val gson = new Gson()
    val event1 = EventFixture.RAW_LOG_EVENT
    val event2 = EventFixture.RAW_SEARCH_EVENT
    val event3 = EventFixture.RAW_ERROR_EVENT
    ctx.collect(event1.getBytes(StandardCharsets.UTF_8))
    ctx.collect(event2.getBytes(StandardCharsets.UTF_8))
    ctx.collect(event3.getBytes(StandardCharsets.UTF_8))

  }

  override def cancel() = {}

}


class IngestSuccessEventsSink extends SinkFunction[Array[Byte]] {

  override def invoke(value: Array[Byte]): Unit = {
    synchronized {
      IngestSuccessEventsSink.values.add(value)
    }
  }
}

object IngestSuccessEventsSink {
  val values: util.List[Array[Byte]] = new util.ArrayList[Array[Byte]]()
}

class RawSuccessEventsSink extends SinkFunction[Array[Byte]] {

  override def invoke(value: Array[Byte]): Unit = {
    synchronized {
      RawSuccessEventsSink.values.add(value)
    }
  }
}

object RawSuccessEventsSink {
  val values: util.List[Array[Byte]] = new util.ArrayList[Array[Byte]]()
}
