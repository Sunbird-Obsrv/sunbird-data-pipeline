package org.sunbird.dp.extractor.task

import java.util

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig
import scala.collection.JavaConverters._

class TelemetryExtractorConfig(override val config: Config) extends BaseJobConfig(config, "TelemetryExtractorJob") {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")
  val kafkaLogRouteTopic: String = config.getString("kafka.output.log.route.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")
  val kafkaFailedTopic: String = config.getString("kafka.output.failed.topic")
  val kafkaBatchFailedTopic: String = config.getString("kafka.output.batch.failed.topic")
  val kafkaAssessRawTopic: String = config.getString("kafka.output.assess.raw.topic")
  val eventMaxSize: Long = config.getLong("kafka.event.max.size")

  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
  val downstreamOperatorsParallelism: Int = config.getInt("task.downstream.operators.parallelism")

  val redactEventsList: List[String] = config.getStringList("redact.events.list").asScala.toList
  val contentStore: Int = config.getInt("redis-meta.database.contentstore.id")

  val UNIQUE_EVENTS_OUTPUT_TAG = "unique-batch-events"
  val RAW_EVENTS_OUTPUT_TAG = "raw-events"
  val LOG_EVENTS_OUTPUT_TAG = "log-events"
  val ERROR_EVENTS_OUTPUT_TAG = "error-events"
  val ASSESS_REDACT_EVENTS_OUTPUT_TAG = "assess-redact-events"
  val ASSESS_RAW_EVENTS_OUTPUT_TAG = "assess-raw-events"
  val FAILED_EVENTS_OUTPUT_TAG = "failed-events"
  val FAILED_BATCH_EVENTS_OUTPUT_TAG = "failed-batch-events"
  val AUDIT_EVENTS_OUTPUT_TAG = "audit-events"
  val DUPLICATE_EVENTS_OUTPUT_TAG = "duplicate-batch-events"

  // Metric List
  val successEventCount = "success-event-count"
  val failedEventCount = "failed-event-count"
  val failedBatchCount = "failed-batch-count"
  val successBatchCount = "success-batch-count"
  val auditEventCount = "audit-event-count"
  val cacheMissCount = "cache-miss-count"
  val cacheHitCount = "cache-hit-count"
  val skippedEventCount = "skipped-event-count"

  val rawEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](RAW_EVENTS_OUTPUT_TAG)
  val logEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](LOG_EVENTS_OUTPUT_TAG)
  val errorEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](ERROR_EVENTS_OUTPUT_TAG)
  val assessRawEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](ASSESS_RAW_EVENTS_OUTPUT_TAG)
  val assessRedactEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](ASSESS_REDACT_EVENTS_OUTPUT_TAG)
  val failedEventsOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](FAILED_EVENTS_OUTPUT_TAG)
  val failedBatchEventOutputTag: OutputTag[String] = OutputTag[String](FAILED_BATCH_EVENTS_OUTPUT_TAG)
  val auditEventsOutputTag: OutputTag[String] = OutputTag[String](AUDIT_EVENTS_OUTPUT_TAG)
  val duplicateEventOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](id = DUPLICATE_EVENTS_OUTPUT_TAG)
  val uniqueEventOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](id = UNIQUE_EVENTS_OUTPUT_TAG)

  // Consumers
  val telemetryExtractorConsumer = "telemetry-extractor-consumer"

  // Functions
  val extractionFunction = "ExtractionFucntion"
  val redactorFunction = "RedactorFunction"

  // Producers
  val extractorDuplicateProducer = "extractor-duplicate-events-sink"
  val extractorBatchFailedEventsProducer = "extractor-batch-failed-events-sink"
  val extractorRawEventsProducer = "extractor-raw-events-sink"
  val extractorAuditEventsProducer = "extractor-audit-events-sink"
  val extractorLogEventsProducer = "extractor-log-events-sink"
  val extractorErrorEventsProducer = "extractor-error-events-sink"
  val extractorFailedEventsProducer = "extractor-failed-events-sink"
  val assessEventsProducer = "assess-events-sink"
  val assessRawEventsProducer = "assess-raw-events-sink"

}
