package org.sunbird.dp.task

import java.util
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.ekstep.dp.core.BaseJobConfig
import org.apache.flink.streaming.api.scala.OutputTag

class ExtractionConfig extends BaseJobConfig {

  private val serialVersionUID = 2905979434303791379L

  implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")
  val kafkaFailedTopic: String = config.getString("kafka.output.failed.topic")
  val eventMaxSize:Long = config.getLong("kafka.event.max.size")
  val isDuplicationCheckRequired: Boolean = config.getBoolean("task.dedup.validation.required")
  val deDupParallelism: Int = config.getInt("task.dedup.parallelism")
  val extractionParallelism: Int = config.getInt("task.extraction.parallelism")
  
  val UNIQUE_EVENTS_OUTPUT_TAG = "unique-events"
  val RAW_EVENTS_OUTPUT_TAG = "raw-events"
  val FAILED_EVENTS_OUTPUT_TAG = "failed-events"
  val LOG_EVENTS_OUTPUT_TAG = "log-events"
  val DUPLICATE_EVENTS_OUTPUT_TAG = "duplicate-events"
  
  lazy val rawEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](RAW_EVENTS_OUTPUT_TAG)
  lazy val logEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](LOG_EVENTS_OUTPUT_TAG)  
  lazy val failedEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](FAILED_EVENTS_OUTPUT_TAG)
  lazy val duplicateEventOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](id = DUPLICATE_EVENTS_OUTPUT_TAG)
  lazy val uniqueEventOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](id = UNIQUE_EVENTS_OUTPUT_TAG)

}
