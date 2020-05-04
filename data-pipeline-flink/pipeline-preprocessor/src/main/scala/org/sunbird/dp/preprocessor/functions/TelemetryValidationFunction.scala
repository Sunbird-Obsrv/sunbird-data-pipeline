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
    List(config.validationFailureMetricsCount, config.validationSkipMetricsCount,
      config.validationSuccessMetricsCount, config.duplicationSkippedEventMetricsCount) ::: deduplicationMetrics
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

    dataCorrection(event)
    if (!schemaValidator.schemaFileExists(event)) {
      logger.info(s"Schema not found, Skipping the: ${event.eid} from validation")
      event.markSkipped(config.VALIDATION_FLAG_NAME) // Telemetry validation skipped
      metrics.incCounter(config.validationSkipMetricsCount)
      if (isDuplicateCheckRequired(event.producerId())) {
        deDup[Event](event.mid(), event, context, config.uniqueEventsOutputTag, config.duplicateEventsOutputTag,
          flagName = config.DE_DUP_FLAG_NAME)(dedupEngine, metrics)
      } else {
        val validationReport = schemaValidator.validate(event, false)
        if (validationReport.isSuccess) {
          onValidationSuccess(event, metrics)
          context.output(config.uniqueEventsOutputTag, event)
        } else {
          onValidationFailure(event, metrics, validationReport)
          context.output(config.validationFailedEventsOutputTag, event)
        }
      }
    } else {
      val validationReport = schemaValidator.validate(event, true)
      if (validationReport.isSuccess) {
        onValidationSuccess(event, metrics)
        if (isDuplicateCheckRequired(event.producerId())) {
          deDup[Event](event.mid(), event, context, config.uniqueEventsOutputTag, config.duplicateEventsOutputTag, flagName = config.DE_DUP_FLAG_NAME)(dedupEngine, metrics)
        } else {
          event.markSkipped(config.DE_DUP_SKIP_FLAG_NAME)
          context.output(config.uniqueEventsOutputTag, event)
          metrics.incCounter(config.duplicationSkippedEventMetricsCount)
        }
      } else {
        onValidationFailure(event, metrics, validationReport)
        context.output(config.validationFailedEventsOutputTag, event)

      }
    }
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

  def onValidationSuccess(event: Event, metrics: Metrics): Unit = {
    logger.info(s"Telemetry schema validation is success: ${event.mid()}")
    event.markSuccess(config.VALIDATION_FLAG_NAME)
    metrics.incCounter(config.validationSuccessMetricsCount)
    event.updateDefaults(config)
  }

  def onValidationFailure(event: Event, metrics: Metrics, validationReport: ProcessingReport): Unit = {
    val failedErrorMsg = schemaValidator.getInvalidFieldName(validationReport.toString)
    logger.info(s"Telemetry schema validation is failed for: ${event.mid()} and error message is: ${validationReport.toString}")
    event.markValidationFailure(failedErrorMsg, config.VALIDATION_FLAG_NAME)
    metrics.incCounter(config.validationFailureMetricsCount)
  }
}
