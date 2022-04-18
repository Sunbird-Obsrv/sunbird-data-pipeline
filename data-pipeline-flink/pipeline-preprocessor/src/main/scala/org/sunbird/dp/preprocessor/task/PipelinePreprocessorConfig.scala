package org.sunbird.dp.preprocessor.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig
import org.sunbird.dp.preprocessor.domain.Event

import scala.collection.JavaConverters._

class PipelinePreprocessorConfig(override val config: Config) extends BaseJobConfig(config, "PipelinePreprocessorJob") {

  private val serialVersionUID = 2905979434303791379L

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  val schemaPath: String = config.getString("telemetry.schema.path")

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  // Kafka Topic Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")

  val kafkaPrimaryRouteTopic: String = config.getString("kafka.output.primary.route.topic")
  val kafkaLogRouteTopic: String = config.getString("kafka.output.log.route.topic")
  val kafkaErrorRouteTopic: String = config.getString("kafka.output.error.route.topic")
  val kafkaAuditRouteTopic: String = config.getString("kafka.output.audit.route.topic")
  val kafkaCbAuditRouteTopic: String = config.getString("kafka.output.cb.audit.route.topic")

  val kafkaFailedTopic: String = config.getString("kafka.output.failed.topic")
  val kafkaDuplicateTopic: String = config.getString("kafka.output.duplicate.topic")

  val kafkaDenormSecondaryRouteTopic: String = config.getString("kafka.output.denorm.secondary.route.topic")
  val kafkaDenormPrimaryRouteTopic: String = config.getString("kafka.output.denorm.primary.route.topic")

  val defaultChannel: String = config.getString("default.channel")

  val includedProducersForDedup: List[String] = config.getStringList("dedup.producer.included.ids").asScala.toList

  // Validation & dedup Stream out put tag
  val validationFailedEventsOutputTag: OutputTag[Event] = OutputTag[Event]("validation-failed-events")
  val uniqueEventsOutputTag: OutputTag[Event] = OutputTag[Event]("unique-events")
  val duplicateEventsOutputTag: OutputTag[Event] = OutputTag[Event]("duplicate-events")

  // Router stream out put tags
  val primaryRouteEventsOutputTag: OutputTag[Event] = OutputTag[Event]("primary-route-events")

  // Spliting events on priority for faster denorm processing
  val denormSecondaryEventsRouteOutputTag: OutputTag[Event] = OutputTag[Event]("denorm-secondary-events")
  val denormPrimaryEventsRouteOutputTag: OutputTag[Event] = OutputTag[Event]("denorm-primary-events")

  // Audit, Log & Error Events output tag
  val auditRouteEventsOutputTag: OutputTag[Event] = OutputTag[Event]("audit-route-events")
  val cbAuditRouteEventsOutputTag: OutputTag[Event] = OutputTag[Event]("cb-audit-route-events")
  val logEventsOutputTag: OutputTag[Event] = OutputTag[Event]("log-route-events")
  val errorEventOutputTag: OutputTag[Event] = OutputTag[Event]("error-route-events")

  // Share events out put tags
  val shareRouteEventsOutputTag: OutputTag[Event] = OutputTag[Event]("share-route-events")
  val shareItemEventOutputTag: OutputTag[Event] = OutputTag[Event]("share-item-events")

  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
  val downstreamOperatorsParallelism: Int = config.getInt("task.downstream.operators.parallelism")

  val VALIDATION_FLAG_NAME = "pp_validation_processed"
  val DEDUP_FLAG_NAME = "pp_duplicate"
  val DEDUP_SKIP_FLAG_NAME = "pp_duplicate_skipped"
  val SHARE_EVENTS_FLATTEN_FLAG_NAME = "pp_share_event_processed"

  // Router job metrics
  val primaryRouterMetricCount = "primary-route-success-count"
  val auditEventRouterMetricCount = "audit-route-success-count"
  val shareEventsRouterMetricCount = "share-route-success-count"
  val logEventsRouterMetricsCount = "log-route-success-count"
  val errorEventsRouterMetricsCount = "error-route-success-count"
  val denormSecondaryEventsRouterMetricsCount = "denorm-secondary-route-success-count"
  val denormPrimaryEventsRouterMetricsCount = "denorm-primary-route-success-count"
  val cbAuditEventRouterMetricCount = "cb-audit-route-success-count"

  // Validation job metrics
  val validationSuccessMetricsCount = "validation-success-event-count"
  val validationFailureMetricsCount = "validation-failed-event-count"
  val duplicationEventMetricsCount = "duplicate-event-count"
  val duplicationSkippedEventMetricsCount = "duplicate-skipped-event-count"
  val uniqueEventsMetricsCount = "unique-event-count"
  val validationSkipMetricsCount = "validation-skipped-event-count"

  // ShareEventsFlatten count
  val shareItemEventsMetircsCount = "share-item-event-success-count"

  // Consumers
  val pipelinePreprocessorConsumer = "pipeline-preprocessor-consumer"

  // Functions
  val telemetryValidationFunction = "TelemetryValidationFunction"
  val telemetryRouterFunction = "TelemetryRouterFunction"
  val shareEventsFlattenerFunction = "ShareEventsFlattenerFunction"

  // Producers
  val primaryRouterProducer = "primary-route-sink"
  val auditEventsPrimaryRouteProducer = "audit-events-primary-route-sink"
  val shareEventsPrimaryRouteProducer = "share-events-primary-route-sink"
  val shareItemsPrimaryRouterProducer = "share-items-primary-route-sink"
  val logRouterProducer = "log-route-sink"
  val errorRouterProducer = "error-route-sink"
  val auditRouterProducer = "audit-route-sink"
  val cbAuditRouterProducer = "cb-audit-route-sink"
  val invalidEventProducer = "invalid-events-sink"
  val duplicateEventProducer = "duplicate-events-sink"
  val denormSecondaryEventProducer = "denorm-secondary-events-sink"
  val denormPrimaryEventProducer = "denorm-primary-events-sink"

  val defaultSchemaFile = "envelope.json"

  val secondaryEvents: List[String] = config.getStringList("secondary.events").asScala.toList


}
