package org.sunbird.dp.preprocessor.functions

import com.github.fge.jsonschema.core.report.ProcessingReport
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.preprocessor.domain.Event
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig
import org.sunbird.dp.preprocessor.util.SchemaValidator

class TelemetryValidationFunction(config: PipelinePreprocessorConfig,
                                  @transient var schemaValidator: SchemaValidator = null,
                                  @transient var dedupEngine: DedupEngine = null)
                                 (implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {
  private[this] val logger = LoggerFactory.getLogger(classOf[TelemetryValidationFunction])

  override def metricsList(): List[String] = {
    List(config.validationFailureMetricsCount,
      config.validationSuccessMetricsCount,
      config.duplicationSkippedEventMetricsCount) ::: deduplicationMetrics
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    if (dedupEngine == null) {
      val redisConnect = new RedisConnect(config)
      dedupEngine = new DedupEngine(redisConnect, config.dedupStore, config.cacheExpirySeconds)
    }
    if (schemaValidator == null) {
      schemaValidator = new SchemaValidator(config)
    }
  }

  override def close(): Unit = {
    super.close()
    dedupEngine.closeConnectionPool()
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {
    val isSchemaPresent: Boolean = schemaValidator.schemaFileExists(event)
    if (!isSchemaPresent) onValidationSkip(event)
    val validationReport = schemaValidator.validate(event, isSchemaPresent = isSchemaPresent)
    if (validationReport.isSuccess) onValidationSuccess(event, metrics, context) else onValidationFailure(event, metrics, context, validationReport)
  }

  private def dataCorrection(event: Event): Event = {
    // Remove prefix from federated userIds
    val eventActorId = event.actorId()
    if (eventActorId != null && !eventActorId.isEmpty && eventActorId.startsWith("f:"))
      event.updateActorId(eventActorId.substring(eventActorId.lastIndexOf(":") + 1))
    if (event.eid != null && event.eid().equalsIgnoreCase("SEARCH"))
      event.correctDialCodeKey()
    if (event.objectFieldsPresent && (event.objectType().equalsIgnoreCase("DialCode") || event.objectType().equalsIgnoreCase("qr"))) event.correctDialCodeValue()
    event
  }

  def isDuplicateCheckRequired(producerId: String): Boolean = {
    config.includedProducersForDedup.contains(producerId)
  }

  def onValidationSuccess(event: Event, metrics: Metrics, context: ProcessFunction[Event, Event]#Context): Unit = {
    logger.info(s"Telemetry schema validation is success: ${event.mid()}")
    dataCorrection(event)
    event.markSuccess(config.VALIDATION_FLAG_NAME)
    metrics.incCounter(config.validationSuccessMetricsCount)
    event.updateDefaults(config)
    if (isDuplicateCheckRequired(event.producerId())) {
      deDup[Event, Event](event.mid(), event, context, config.uniqueEventsOutputTag, config.duplicateEventsOutputTag, flagName = config.DE_DUP_FLAG_NAME)(dedupEngine, metrics)
    } else {
      event.markSkipped(config.DE_DUP_SKIP_FLAG_NAME)
      context.output(config.uniqueEventsOutputTag, event)
      metrics.incCounter(config.duplicationSkippedEventMetricsCount)
    }
  }

  def onValidationFailure(event: Event, metrics: Metrics, context: ProcessFunction[Event, Event]#Context, validationReport: ProcessingReport): Unit = {
    val failedErrorMsg = schemaValidator.getInvalidFieldName(validationReport.toString)
    logger.info(s"Telemetry schema validation is failed for: ${event.mid()} and error message is: ${validationReport.toString}")
    event.markValidationFailure(failedErrorMsg, config.VALIDATION_FLAG_NAME)
    metrics.incCounter(config.validationFailureMetricsCount)
    context.output(config.validationFailedEventsOutputTag, event)
  }

  def onValidationSkip(event: Event): Unit = {
    logger.info(s"Schema not found, Skipping the: ${event.eid} from validation")
    event.markSkipped(config.VALIDATION_FLAG_NAME) // Telemetry validation skipped
  }
}