package org.sunbird.dp.spec

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.core.FlinkKafkaConnector
import org.sunbird.dp.domain.Event
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.task.DenormalizationStreamTask

import com.google.gson.Gson
import com.typesafe.config.ConfigFactory

import redis.embedded.RedisServer
import org.joda.time.DateTime

class DenormalizationStreamTaskTest extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)
  var redisServer: RedisServer = _
  val config = ConfigFactory.load("test.conf");
  val extConfig: DenormalizationConfig = new DenormalizationConfig(config);
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6340)
    redisServer.start()

    when(mockKafkaUtil.kafkaEventSource[Event](extConfig.inputTopic)).thenReturn(new InputSource)
    when(mockKafkaUtil.kafkaEventSink[Event](extConfig.denormSuccessTopic)).thenReturn(new DenormEventsSink)
    when(mockKafkaUtil.kafkaStringSink(extConfig.metricsTopic)).thenReturn(new MetricsEventsSink)

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }

  "Extraction job pipeline" should "extract events" in {

    val task = new DenormalizationStreamTask(extConfig, mockKafkaUtil);
    task.process()
    Thread.sleep(600)
    Console.println("DenormEventsSink.size", DenormEventsSink.values.size())
    Console.println("Denorm Event:", (new Gson()).toJson(DenormEventsSink.values.get(0).getMap))
    Console.println("Metrics.size", MetricsEventsSink.values.size())
    Console.println("Metrics Event:", MetricsEventsSink.values.get(0))

  }

}

class InputSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {

    Console.println("InputSource: run");
    val gson = new Gson()
    val eventMap1 = gson.fromJson(EventFixture.EVENT_WITH_MID, new util.HashMap[String, AnyRef]().getClass)
    val event1 = new Event(eventMap1);
    ctx.collect(event1);
  }

  override def cancel() = {

  }

}

class DerivedEventSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    //val event1 = gson.fromJson(EventFixture.EVENT_WITH_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    //val event2 = gson.fromJson(EventFixture.EVENT_WITHOUT_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    //    ctx.collect(event1)
    //    ctx.collect(event1)
    //    ctx.collect(event2)
  }

  override def cancel() = {

  }

}

class DenormEventsSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      DenormEventsSink.values.add(value)
    }
  }
}

object DenormEventsSink {
  val values: util.List[Event] = new util.ArrayList[Event]()
}

class MetricsEventsSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      MetricsEventsSink.values.add(value)
    }
  }
}

object MetricsEventsSink {
  val values: util.List[String] = new util.ArrayList[String]()
}