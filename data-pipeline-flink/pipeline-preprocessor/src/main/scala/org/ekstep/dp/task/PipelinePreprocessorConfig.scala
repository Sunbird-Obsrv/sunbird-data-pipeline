package org.ekstep.dp.task

import org.ekstep.dp.core.BaseJobConfig

import scala.collection.JavaConverters._

class PipelinePreprocessorConfig extends BaseJobConfig {

  private val serialVersionUID = 2905979434303791379L

  val schemaPath: String = config.getString("telemetry.schema.path")

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topic Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")
  val kafkaFailedTopic: String = config.getString("kafka.output.failed.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")
  val kafkaPrimaryRouteTopic: String = config.getString("kafka.output.primary.route.topic")
  val kafkaSecondaryRouteTopic: String = config.getString("kafka.output.secondary.route.topic")
  val kafkaAuditRouteTopic: String = config.getString("kafka.output.audit.route.topic")
  val kafkaMalformedTopic: String = config.getString("kafka.output.malformed.topic")


  val secondaryRouteEids: List[String] = config.getStringList("router.secondary.routes.eid").asScala.toList
  val defaultChannel: String = config.getString("default.channel")

  val includedProducersForDedup: List[String] = config.getStringList("dedup.producer.included.ids").asScala.toList

}
