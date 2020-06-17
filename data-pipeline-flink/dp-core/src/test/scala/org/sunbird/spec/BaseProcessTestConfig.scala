package org.sunbird.spec

import java.util

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig

class BaseProcessTestConfig(override val config: Config) extends BaseJobConfig(config, "Test-job") {
  private val serialVersionUID = -2349318979085017498L
  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val mapOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]]("test-map-stream-tag")
  val eventOutputTag: OutputTag[Event] = OutputTag[Event]("test-event-stream-tag")
  val duplicateEventOutputTag: OutputTag[Event] = OutputTag[Event]("duplicate-event-stream")
  val stringOutputTag: OutputTag[String] = OutputTag[String]("test-string-stream-tag")

  val kafkaMapInputTopic: String = config.getString("kafka.map.input.topic")
  val kafkaMapOutputTopic: String = config.getString("kafka.map.output.topic")
  val kafkaEventInputTopic: String = config.getString("kafka.event.input.topic")
  val kafkaEventOutputTopic: String = config.getString("kafka.event.output.topic")
  val kafkaEventDuplicateTopic: String = config.getString("kafka.event.duplicate.topic")
  val kafkaStringInputTopic: String = config.getString("kafka.string.input.topic")
  val kafkaStringOutputTopic: String = config.getString("kafka.string.output.topic")

  val testTopics = List(kafkaMapInputTopic, kafkaMapOutputTopic, kafkaEventInputTopic, kafkaEventOutputTopic,
    kafkaStringInputTopic, kafkaStringOutputTopic)

  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")
  val mapEventCount = "map-event-count"
  val telemetryEventCount = "telemetry-event-count"
  val stringEventCount = "string-event-count"

}