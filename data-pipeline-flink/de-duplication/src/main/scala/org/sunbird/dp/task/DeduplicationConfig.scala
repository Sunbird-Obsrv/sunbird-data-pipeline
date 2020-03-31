package org.sunbird.dp.task

import org.ekstep.dp.core.BaseJobConfig

import scala.collection.JavaConverters._
import com.typesafe.config.Config

class DeduplicationConfig(override val config: Config) extends BaseJobConfig(config) {

  private val serialVersionUID = 2905979434303791379L

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")
  val kafkaMalformedTopic: String = config.getString("kafka.output.malformed.topic")
  val includedProducersForDedup: List[String] = config.getStringList("dedup.producer.included.ids").asScala.toList

}
