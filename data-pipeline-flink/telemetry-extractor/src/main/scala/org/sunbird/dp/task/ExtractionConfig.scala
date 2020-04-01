package org.sunbird.dp.task

import java.util
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.ekstep.dp.core.BaseJobConfig
import org.apache.flink.streaming.api.scala.OutputTag
import com.typesafe.config.Config

class ExtractionConfig(override val config: Config) extends BaseJobConfig(config) {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

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
  
  val rawEventsOutputTag: OutputTag[String] = OutputTag[String](RAW_EVENTS_OUTPUT_TAG)
  val failedEventsOutputTag: OutputTag[String] = OutputTag[String](FAILED_EVENTS_OUTPUT_TAG)
  val logEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](LOG_EVENTS_OUTPUT_TAG)

  val duplicateEventOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](id = DUPLICATE_EVENTS_OUTPUT_TAG)
  val uniqueEventOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](id = UNIQUE_EVENTS_OUTPUT_TAG)

  val jobName = "telemetry-extractor"

}
