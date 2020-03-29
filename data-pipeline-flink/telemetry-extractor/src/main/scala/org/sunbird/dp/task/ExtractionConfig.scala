package org.sunbird.dp.task

import org.ekstep.dp.core.BaseJobConfig

class ExtractionConfig extends BaseJobConfig {

  private val serialVersionUID = 2905979434303791379L

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")
  val kafkaFailedTopic: String = config.getString("kafka.output.failed.topic")
  val rawEventSize:Long = config.getLong("kafka.event.max.size")
  val isDuplicationCheckRequired: Boolean = config.getBoolean("dedup.validation.required")

}
