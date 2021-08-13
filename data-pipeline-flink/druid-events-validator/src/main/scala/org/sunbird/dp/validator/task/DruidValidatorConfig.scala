package org.sunbird.dp.validator.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig
import org.sunbird.dp.validator.domain.Event

class DruidValidatorConfig(override val config: Config) extends BaseJobConfig(config, "DruidValidatorJob") {

  private val serialVersionUID = 2905979434303791379L
  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
  val downstreamOperatorsParallelism: Int = config.getInt("task.downstream.operators.parallelism")

  val druidValidationEnabled: Boolean = config.getBoolean("task.druid.validation.enabled")
  val druidDeduplicationEnabled: Boolean = config.getBoolean("task.druid.deduplication.enabled")

  val telemetrySchemaPath: String = config.getString("schema.path.telemetry")
  val summarySchemaPath: String = config.getString("schema.path.summary")
  val defaultSchemaFile: String = config.getString("schema.file.default")
  val searchSchemaFile: String = config.getString("schema.file.search")
  val summarySchemaFile: String = config.getString("schema.file.summary")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaTelemetryRouteTopic: String = config.getString("kafka.output.telemetry.route.topic")
  val kafkaSummaryRouteTopic: String = config.getString("kafka.output.summary.route.topic")
  val kafkaFailedTopic: String = config.getString("kafka.output.failed.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")

  val VALID_EVENTS_OUTPUT_TAG = "valid-events"
  val TELEMETRY_ROUTER_OUTPUT_TAG = "valid-telemetry-events"
  val SUMMARY_ROUTER_OUTPUT_TAG = "valid-summary-events"
  val INVALID_EVENTS_OUTPUT_TAG = "invalid-events"
  val DUPLICATE_EVENTS_OUTPUT_TAG = "duplicate-events"

  lazy val validEventOutputTag: OutputTag[Event] = OutputTag[Event](VALID_EVENTS_OUTPUT_TAG)
  lazy val telemetryRouterOutputTag: OutputTag[Event] = OutputTag[Event](TELEMETRY_ROUTER_OUTPUT_TAG)
  lazy val summaryRouterOutputTag: OutputTag[Event] = OutputTag[Event](SUMMARY_ROUTER_OUTPUT_TAG)
  lazy val invalidEventOutputTag: OutputTag[Event] = OutputTag[Event](id = INVALID_EVENTS_OUTPUT_TAG)
  lazy val duplicateEventOutputTag: OutputTag[Event] = OutputTag[Event](id = DUPLICATE_EVENTS_OUTPUT_TAG)

  // Router job metrics
  val summaryRouterMetricCount = "summary-route-success-count"
  val telemetryRouterMetricCount = "telemetry-route-success-count"

  // Validation job metrics
  val validationSuccessMetricsCount = "validation-success-message-count"
  val validationFailureMetricsCount = "validation-failed-message-count"

  // Consumers
  val druidValidatorConsumer = "druid-validator-consumer"

  // Functions
  val druidValidatorFunction = "DruidValidatorFunction"

  // Producers
  val telemetryEventsProducer = "telemetry-events-sink"
  val summaryEventsProducer = "summary-events-sink"
  val druidDuplicateEventsProducer = "druid-duplicate-events-sink"
  val druidInvalidEventsProducer = "druid-invalid-events-sink"

}
