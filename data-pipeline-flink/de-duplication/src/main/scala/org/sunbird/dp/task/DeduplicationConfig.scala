package org.sunbird.dp.task

import org.sunbird.dp.core.BaseJobConfig

import scala.collection.JavaConverters._
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.domain.Event

class DeduplicationConfig(override val config: Config) extends BaseJobConfig(config) {

  private val serialVersionUID = 2905979434303791379L

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")
  val kafkaMalformedTopic: String = config.getString("kafka.output.malformed.topic")
  val includedProducersForDedup: List[String] = config.getStringList("dedup.producer.included.ids").asScala.toList

  val uniqueEventsOutputTag: OutputTag[Event] = OutputTag[Event]("unique-events")
  val duplicateEventsOutputTag: OutputTag[Event] = OutputTag[Event]("duplicate-evemts")

}
