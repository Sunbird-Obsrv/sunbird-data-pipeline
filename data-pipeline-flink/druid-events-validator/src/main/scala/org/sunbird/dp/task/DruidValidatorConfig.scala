package org.sunbird.dp.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.sunbird.dp.domain.Event
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.BaseJobConfig

class DruidValidatorConfig(override val config: Config) extends BaseJobConfig(config, "druid-validator") {

  private val serialVersionUID = 2905979434303791379L
  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  val validatorParallelism: Int = config.getInt("task.validator.parallelism")
  val routerParallelism: Int = config.getInt("task.router.parallelism")

  val telemetrySchemaPath: String = config.getString("denorm.telemetry.schema.path")
  val summarySchemaPath: String = config.getString("denorm.summary.schema.path")
  val defaultSchemaFile: String = config.getString("default.schema.file")
  val searchSchemaFile: String = config.getString("search.schema.file")
  val summarySchemaFile: String = config.getString("summary.schema.file")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaTelemetryRouteTopic: String = config.getString("kafka.output.telemetry.route.topic")
  val kafkaSummaryRouteTopic: String = config.getString("kafka.output.summary.route.topic")
  val kafkaLogRouteTopic: String = config.getString("kafka.output.log.route.topic")
  val kafkaErrorRouteTopic: String = config.getString("kafka.output.error.route.topic")
  val kafkaFailedTopic: String = config.getString("kafka.output.failed.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")

  val VALID_EVENTS_OUTPUT_TAG = "valid-events"
  val TELEMETRY_ROUTER_OUTPUT_TAG = "valid-telemetry-events"
  val SUMMARY_ROUTER_OUTPUT_TAG = "valid-summary-events"
  val LOG_ROUTER_OUTPUT_TAG = "valid-log-events"
  val ERROR_ROUTER_OUTPUT_TAG = "valid-error-events"
  val INVALID_EVENTS_OUTPUT_TAG = "invalid-events"
  val DUPLICATE_EVENTS_OUTPUT_TAG = "duplicate-events"

  lazy val validEventOutputTag: OutputTag[Event] = OutputTag[Event](VALID_EVENTS_OUTPUT_TAG)
  lazy val telemetryRouterOutputTag: OutputTag[Event] = OutputTag[Event](TELEMETRY_ROUTER_OUTPUT_TAG)
  lazy val summaryRouterOutputTag: OutputTag[Event] = OutputTag[Event](SUMMARY_ROUTER_OUTPUT_TAG)
  lazy val logRouterOutputTag: OutputTag[Event] = OutputTag[Event](id = LOG_ROUTER_OUTPUT_TAG)
  lazy val errorRouterOutputTag: OutputTag[Event] = OutputTag[Event](id = ERROR_ROUTER_OUTPUT_TAG)
  lazy val invalidEventOutputTag: OutputTag[Event] = OutputTag[Event](id = INVALID_EVENTS_OUTPUT_TAG)
  lazy val duplicateEventOutputTag: OutputTag[Event] = OutputTag[Event](id = DUPLICATE_EVENTS_OUTPUT_TAG)

}
