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
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.core.FlinkKafkaConnector
import org.sunbird.dp.domain.Event
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.task.{DeduplicationConfig, DeduplicationStreamTask}
import redis.embedded.RedisServer

import scala.collection.mutable.ListBuffer

class DeduplicationStreamTaskTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val flinkTestCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val dedupConfig = new DeduplicationConfig(config)
  val mockFlinkKafkaConnector: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    println("Initializing test suite...")
    redisServer = new RedisServer(6340)
    redisServer.start()

    when(mockFlinkKafkaConnector.kafkaEventSource[Event](dedupConfig.kafkaInputTopic)).thenReturn(new DeduplicationEventSource)
    when(mockFlinkKafkaConnector.kafkaEventSink[Event](dedupConfig.kafkaSuccessTopic)).thenReturn(new UniqueEventSink)
    when(mockFlinkKafkaConnector.kafkaEventSink[Event](dedupConfig.kafkaDuplicateTopic)).thenReturn(new DuplicateEventSink)

    flinkTestCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkTestCluster.after()
  }

  "Deduplication job" should "push unique events to unique sink and duplicate events to duplicate sink" in {
    val deduplicationTask = new DeduplicationStreamTask(dedupConfig, mockFlinkKafkaConnector)
    deduplicationTask.process()
    UniqueEventSink.outputEvents.size should be (2)
    DuplicateEventSink.outputEvents.size should be (0)

  }

}

class DeduplicationEventSource extends SourceFunction[Event] {
  override def run(context: SourceContext[Event]): Unit = {
    val gson = new Gson()
    val event1 = gson.fromJson(EventFixture.EVENT_WITH_MID, new util.HashMap[String, AnyRef]().getClass)
    val event2 = gson.fromJson(EventFixture.EVENT2_WITH_MID, new util.HashMap[String, AnyRef]().getClass)
    context.collect(new Event(event1))
    context.collect(new Event(event2))
    context.collect(new Event(event1))
  }

  override def cancel(): Unit = {}
}

class UniqueEventSink extends SinkFunction[Event] {
  val outputEvents = new util.ArrayList[Event]()
  override def invoke(value: Event): Unit = {
    synchronized {
      println("unique stream = " + value.getJson)
      UniqueEventSink.outputEvents.add(value)
    }
  }
}


object UniqueEventSink {
  val outputEvents: util.List[Event] = new util.ArrayList[Event]()
}



class DuplicateEventSink extends SinkFunction[Event] {
  val outputEvents = new util.ArrayList[Event]()
  override def invoke(value: Event): Unit = {
    synchronized {
      println("duplicate stream = " + value.getJson)
      DuplicateEventSink.outputEvents.add(value)
    }
  }
}


object DuplicateEventSink {
  val outputEvents: util.List[Event] = new util.ArrayList[Event]()
}
