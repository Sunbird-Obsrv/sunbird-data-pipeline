package org.sunbird.dp.spec

import java.util
import com.google.gson.Gson
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{ BeforeAndAfterAll, FlatSpec, Matchers }
import org.sunbird.dp.fixture.EventFixture
import redis.embedded.RedisServer
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.task.ExtractorStreamTask
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.sunbird.dp.task.ExtractionConfig
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.FlinkKafkaConnector
import collection.JavaConverters._

class ExtractionStreamTaskTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)
  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val extConfig: ExtractionConfig = new ExtractionConfig(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson();

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6340)
    redisServer.start()

    when(mockKafkaUtil.kafkaMapSource(extConfig.kafkaInputTopic)).thenReturn(new ExtractorEventSource)
    when(mockKafkaUtil.kafkaMapSink(extConfig.kafkaDuplicateTopic)).thenReturn(new DupEventsSink)
//    when(mockKafkaUtil.kafkaMapSink(extConfig.kafkaSuccessTopic)).thenReturn(new LogEventsSink)
    when(mockKafkaUtil.kafkaMapSink(extConfig.kafkaSuccessTopic)).thenReturn(new RawEventsSink)
    when(mockKafkaUtil.kafkaMapSink(extConfig.kafkaFailedTopic)).thenReturn(new FailedEventsSink)
    when(mockKafkaUtil.kafkaStringSink(extConfig.metricsTopic)).thenReturn(new MetricsEventsSink)

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
//    Thread.sleep(extConfig.metricsWindowSize + 2000); // Wait for metrics to be triggered

    RawEventsSink.values.size() should be (42) // 40 events + 2 log events generated for auditing
    FailedEventsSink.values.size() should be (0)
    DupEventsSink.values.size() should be (1)
//    LogEventsSink.values.size() should be (2)

    MetricsEventsSink.values.size should be (2)
    val metricsMap: scala.collection.mutable.Map[String, Double] = scala.collection.mutable.Map[String, Double]();
    MetricsEventsSink.values.foreach(metricJson => {
      val metricEvent = gson.fromJson(metricJson, new util.HashMap[String, AnyRef]().getClass);
      val list = metricEvent.get("metrics").asInstanceOf[util.List[util.Map[String, AnyRef]]].asScala
      for(metric <- list) {
        metricsMap.put(metric.get("id").asInstanceOf[String], metric.get("value").asInstanceOf[Double])
      }
    })

    metricsMap.get("processed-batch-messages-count").get should be (3)
    metricsMap.get("success-messages-count").get should be (40)
    metricsMap.get("failed-messages-count").get should be (0)
    metricsMap.get("log-events-generated-count").get should be (2)
    metricsMap.get("unique-event-count").get should be (1)
    metricsMap.get("duplicate-event-count").get should be (1)
  }

}

class ExtractorEventSource extends SourceFunction[util.Map[String, AnyRef]] {

  override def run(ctx: SourceContext[util.Map[String, AnyRef]]) {
    val gson = new Gson()
    val event1 = gson.fromJson(EventFixture.EVENT_WITH_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala ++ Map("partition" -> 0.asInstanceOf[AnyRef])
    val event2 = gson.fromJson(EventFixture.EVENT_WITHOUT_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]].asScala ++ Map("partition" -> 0.asInstanceOf[AnyRef])
    ctx.collect(event1.asJava)
    ctx.collect(event1.asJava)
    ctx.collect(event2.asJava)
  }

  override def cancel() = {

  }

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

class MetricsEventsSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      MetricsEventsSink.values.append(value);
    }
  }
}

object MetricsEventsSink {
  val values: scala.collection.mutable.Buffer[String] = scala.collection.mutable.Buffer[String]()
}