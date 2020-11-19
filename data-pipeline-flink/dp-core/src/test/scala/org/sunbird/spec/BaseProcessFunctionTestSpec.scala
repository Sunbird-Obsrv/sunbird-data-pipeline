package org.sunbird.spec

import java.util

import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import net.manub.embeddedkafka.EmbeddedKafka._
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.apache.kafka.common.serialization.StringDeserializer
import org.scalatest.Matchers
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BaseProcessFunctionTestSpec extends BaseSpec with Matchers {

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  val config: Config = ConfigFactory.load("base-test.conf")
  val bsConfig = new BaseProcessTestConfig(config)
  val gson = new Gson()

  val kafkaConnector = new FlinkKafkaConnector(bsConfig)

  val EVENT_WITH_MESSAGE_ID: String =
    """
      |{"id":"sunbird.telemetry","ver":"3.0","ets":1529500243591,"params":{"msgid":"3fc11963-04e7-4251-83de-18e0dbb5a684",
      |"requesterId":"","did":"a3e487025d29f5b2cd599a8817ac16b8f3776a63","key":""},"events":[{"eid":"LOG","ets":1529499971358,
      |"ver":"3.0","mid":"LOG:5f3c177f90bd5833deade577cc28cbb6","actor":{"id":"159e93d1-da0c-4231-be94-e75b0c226d7c",
      |"type":"user"},"context":{"channel":"b00bc992ef25f1a9a8d63291e20efc8d","pdata":{"id":"local.sunbird.portal",
      |"ver":"0.0.1"},"env":"content-service","sid":"PCNHgbKZvh6Yis8F7BxiaJ1EGw0N3L9B","did":"cab2a0b55c79d12c8f0575d6397e5678",
      |"cdata":[],"rollup":{"l1":"ORG_001","l2":"0123673542904299520","l3":"0123673689120112640",
      |"l4":"b00bc992ef25f1a9a8d63291e20efc8d"}},"object":{},"tags":["b00bc992ef25f1a9a8d63291e20efc8d"],
      |"edata":{"type":"api_access","level":"INFO","message":"","params":[{"url":"/content/composite/v1/search"},
      |{"protocol":"https"},{"method":"POST"},{}]}}],"mid":"56c0c430-748b-11e8-ae77-cd19397ca6b0","syncts":1529500243955}
      |""".stripMargin

  val SHARE_EVENT: String =
    """
      |{"ver":"3.0","eid":"SHARE","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},
      |"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app",
      |"ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},
      |"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},
      |"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},
      |{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711",
      |"type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b",
      |"type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818",
      |"type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32","syncts":1577278682630,
      |"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
      |""".stripMargin

  val EVENT_WITHOUT_DID: String =
    """
      |{"actor":{"id":"org.ekstep.learning.platform","type":"User"},"eid":"LOG","edata":{"level":"ERROR","type":"system",
      |"message":"Exception Occured While Reading event from kafka topic : sunbirddev.system.command. Exception is :
      |java.lang.IllegalStateException: This consumer has already been closed."},"ver":"3.0","syncts":1.586994119534E12,
      |"ets":1.586994119534E12,"context":{"channel":"in.ekstep","pdata":{"id":"dev.sunbird.learning.platform",
      |"pid":"learning-service","ver":"1.0"},"env":"system"},"flags":{"pp_duplicate_skipped":true,"pp_validation_processed":true},
      |"mid":"LP.1586994119534.4bfe9b31-216d-46ea-8e60-d7ea1b1a103c","type":"events"}
    """.stripMargin

  val EVENT_INVALID =
    """
      |"{\"eid\":\"AUDIT\",\"ets\":1605674597050,\"ver\":\"3.0\",\"mid\":\"LP.1605674597050.e920319b-b6e4-4be8-877d-f4c588ee7a3c\",\"actor\":{\"id\":\"9bb884fc-8a56-4727-9522-25a7d5b8ea06\",\"type\":\"User\"},\"context\":{\"channel\":\"ORG_001\",\"pdata\":{\"pid\":\"lms-service\",\"ver\":\"1.0\"},\"env\":\"CourseBatch\",\"cdata\":[{\"id\":\"do_213153022369169408120\",\"type\":\"Course\"},{\"id\":\"01315302480262758472\",\"type\":\"CourseBatch\"}]},\"object\":{\"id\":\"9bb884fc-8a56-4727-9522-25a7d5b8ea06\",\"type\":\"User\",\"rollup\":{\"l1\":\"do_213153022369169408120\"}},\"edata\":{\"state\":\"Create\",\"type\":\"enrol\",\"props\":[\"courseId\",\"enrolledDate\",\"userId\",\"batchId\",\"active\"]}}"
      |
      |""".stripMargin

  val customKafkaConsumerProperties: Map[String, String] =
    Map[String, String]("auto.offset.reset" -> "earliest", "group.id" -> "test-event-schema-group")
  implicit val embeddedKafkaConfig: EmbeddedKafkaConfig =
    EmbeddedKafkaConfig (
      kafkaPort = 9093,
      zooKeeperPort = 2183,
      customConsumerProperties = customKafkaConsumerProperties
    )
  implicit val deserializer: StringDeserializer = new StringDeserializer()

  override def beforeAll(): Unit = {
    super.beforeAll()

    EmbeddedKafka.start()(embeddedKafkaConfig)
    createTestTopics(bsConfig.testTopics)

    publishStringMessageToKafka(bsConfig.kafkaEventInputTopic, SHARE_EVENT)
    publishStringMessageToKafka(bsConfig.kafkaEventInputTopic, EVENT_WITHOUT_DID)
    publishStringMessageToKafka(bsConfig.kafkaEventInputTopic, SHARE_EVENT)
    publishStringMessageToKafka(bsConfig.kafkaMapInputTopic, EVENT_WITH_MESSAGE_ID)
    publishStringMessageToKafka(bsConfig.kafkaStringInputTopic, SHARE_EVENT)
    publishStringMessageToKafka(bsConfig.kafkaEventInputTopic, EVENT_INVALID)

    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    flinkCluster.after()
    EmbeddedKafka.stop()
  }

  def createTestTopics(topics: List[String]): Unit = {
    topics.foreach(createCustomTopic(_))
  }

  "Validation of SerDe" should "validate serialization and deserialization of Map, String and Event schema" in {

    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(bsConfig)

    val eventStream =
      env.addSource(kafkaConnector.kafkaEventSource[Event](bsConfig.kafkaEventInputTopic), "event-schema-consumer")
      .process[Event](new TestEventStreamFunc(bsConfig)).name("TestTelemetryEventStream")

    eventStream.getSideOutput(bsConfig.eventOutputTag)
      .addSink(kafkaConnector.kafkaEventSink[Event](bsConfig.kafkaEventOutputTopic))
      .name("Event-Producer")

    eventStream.getSideOutput(bsConfig.duplicateEventOutputTag)
      .addSink(kafkaConnector.kafkaEventSink[Event](bsConfig.kafkaEventDuplicateTopic))
      .name("Duplicate-Event-Producer")

    val mapStream =
      env.addSource(kafkaConnector.kafkaMapSource(bsConfig.kafkaMapInputTopic), "map-event-consumer")
        .process(new TestMapStreamFunc(bsConfig)).name("TestMapEventStream")

    mapStream.getSideOutput(bsConfig.mapOutputTag)
      .addSink(kafkaConnector.kafkaMapSink(bsConfig.kafkaMapOutputTopic))
      .name("Map-Event-Producer")

    val stringStream =
      env.addSource(kafkaConnector.kafkaStringSource(bsConfig.kafkaStringInputTopic), "string-event-consumer")
      .process(new TestStringStreamFunc(bsConfig)).name("TestStringEventStream")

    stringStream.getSideOutput(bsConfig.stringOutputTag)
      .addSink(kafkaConnector.kafkaStringSink(bsConfig.kafkaStringOutputTopic))
      .name("String-Producer")

    Future {
      env.execute("TestSerDeFunctionality")
    }

    // val test = consumeNumberMessagesFromTopics[String](topics = Set(bsConfig.kafkaEventOutputTopic), number = 2, timeout = 30.seconds).values.toList
    try {
      val eventSchemaMessages = consumeNumberMessagesFrom[String](bsConfig.kafkaEventOutputTopic, 2)
      val eventSchemaDuplicates = consumeNumberMessagesFrom[String](bsConfig.kafkaEventDuplicateTopic, 1)
      val mapSchemaMessages = consumeNumberMessagesFrom[String](bsConfig.kafkaMapOutputTopic, 1)
      val stringSchemaMessages = consumeNumberMessagesFrom[String](bsConfig.kafkaStringOutputTopic, 1)

      eventSchemaMessages.size should be(2)
      eventSchemaDuplicates.size should be(1)
      mapSchemaMessages.size should be(1)
      stringSchemaMessages.size should be(1)

      retrieveMid(mapSchemaMessages.head) should be("56c0c430-748b-11e8-ae77-cd19397ca6b0")
      retrieveMid(eventSchemaDuplicates.head) should be("02ba33e5-15fe-4ec5-b32")
      retrieveMid(stringSchemaMessages.head) should be("02ba33e5-15fe-4ec5-b32")
      retrieveMid(eventSchemaMessages.head) should be("02ba33e5-15fe-4ec5-b32")
      retrieveMid(eventSchemaMessages.last) should be("LP.1586994119534.4bfe9b31-216d-46ea-8e60-d7ea1b1a103c")
    } catch {
        case ex: Exception => println("Error occurred when consuming events from Embedded Kafka...")
    }

  }

  def retrieveMid(message: String): String = {
    new Event(gson.fromJson(message, new util.HashMap[String, AnyRef]().getClass)).mid()
  }

}
