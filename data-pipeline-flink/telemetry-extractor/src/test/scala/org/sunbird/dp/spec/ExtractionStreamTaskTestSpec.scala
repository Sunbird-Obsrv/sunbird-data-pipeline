package org.sunbird.dp.spec

import java.util
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }
import org.sunbird.dp.cache.{ DedupEngine, RedisConnect }
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.functions.{ DeduplicationFunction, ExtractionFunction }
import org.sunbird.dp.task.ExtractionConfig
import redis.embedded.RedisServer
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.task.ExtractorStreamTask
import org.sunbird.dp.core.FlinkKafkaConnector
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.sunbird.dp.task.ExtractionConfig
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.FlinkKafkaConnector

class ExtractionStreamTaskTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)
  var redisServer: RedisServer = _
  val config = ConfigFactory.load("test.conf");
  val extConfig: ExtractionConfig = new ExtractionConfig(config);
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6340)
    redisServer.start()
    
    when(mockKafkaUtil.getObjectSource(extConfig.kafkaInputTopic)).thenReturn(new ExtractorEventSource)
    when(mockKafkaUtil.getObjectSink(extConfig.kafkaDuplicateTopic)).thenReturn(new DupEventsSink)
    when(mockKafkaUtil.getObjectSink(extConfig.kafkaSuccessTopic)).thenReturn(new LogEventsSink)
    when(mockKafkaUtil.getRawSink(extConfig.kafkaSuccessTopic)).thenReturn(new RawEventsSink)
    when(mockKafkaUtil.getRawSink(extConfig.kafkaFailedTopic)).thenReturn(new FailedEventsSink)

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }

  "Extraction job pipeline" should "extract events" in {

    val task = new ExtractorStreamTask(extConfig, mockKafkaUtil);
    task.process()
    
    RawEventsSink.values.size() should be (40)
    FailedEventsSink.values.size() should be (0)
    DupEventsSink.values.size() should be (1)
    LogEventsSink.values.size() should be (2)
  }

}

class ExtractorEventSource extends SourceFunction[util.Map[String, AnyRef]] {

  override def run(ctx: SourceContext[util.Map[String, AnyRef]]) {
    val gson = new Gson()
    val event1 = gson.fromJson(EventFixture.EVENT_WITH_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    val event2 = gson.fromJson(EventFixture.EVENT_WITHOUT_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    ctx.collect(event1)
    ctx.collect(event1)
    ctx.collect(event2)
  }

  override def cancel() = {

  }

}

class RawEventsSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      RawEventsSink.values.add(value)
    }
  }
}

object RawEventsSink {
  val values: util.List[String] = new util.ArrayList()
}

class FailedEventsSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      FailedEventsSink.values.add(value)
    }
  }
}

object FailedEventsSink {
  val values: util.List[String] = new util.ArrayList()
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