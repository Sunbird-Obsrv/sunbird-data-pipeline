package org.sunbird.dp.preprocessor.util

import com.github.fge.jsonschema.core.report.ProcessingReport
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.preprocessor.domain.Event
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig

class TelemetryValidator(config: PipelinePreprocessorConfig) extends java.io.Serializable {

  private val serialVersionUID = - 2903685244618349843L
  private[this] val logger = LoggerFactory.getLogger(classOf[TelemetryValidator])

  private val schemaValidator = new SchemaValidator(config)

  def validate(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Boolean = {
    val isSchemaPresent: Boolean = schemaValidator.schemaFileExists(event)
    if (isSchemaPresent) {
      val validationReport = schemaValidator.validate(event, isSchemaPresent = isSchemaPresent)
      if (validationReport.isSuccess) {
        onValidationSuccess(event, metrics, context)
      } else {
        onValidationFailure(event, metrics, context, validationReport)
      }
      validationReport.isSuccess
    } else {
      onMissingSchema(event, metrics, context, "Schema not found: eid looks incorrect, sending to failed")
      false
    }
  }

  private def dataCorrection(event: Event): Unit = {
    // Remove prefix from federated userIds
    val eventActorId = event.actorId()
    if (eventActorId != null && !eventActorId.isEmpty && eventActorId.startsWith("f:"))
      event.updateActorId(eventActorId.substring(eventActorId.lastIndexOf(":") + 1))
    if (event.objectFieldsPresent && (event.objectType().equalsIgnoreCase("DialCode") || event.objectType().equalsIgnoreCase("qr"))) event.correctDialCodeValue()
  }

  def onValidationSuccess(event: Event, metrics: Metrics, context: ProcessFunction[Event, Event]#Context): Unit = {
    logger.debug(s"Telemetry schema validation is success: ${event.mid()}")
    dataCorrection(event)
    event.markSuccess(config.VALIDATION_FLAG_NAME)
    metrics.incCounter(config.validationSuccessMetricsCount)
    event.updateDefaults(config)
  }

  def onValidationFailure(event: Event, metrics: Metrics, context: ProcessFunction[Event, Event]#Context, validationReport: ProcessingReport): Unit = {
    val failedErrorMsg = schemaValidator.getInvalidFieldName(validationReport.toString)
    logger.debug(s"Telemetry schema validation is failed for: ${event.mid()} and error message is: ${validationReport.toString}")
    event.markValidationFailure(failedErrorMsg, config.VALIDATION_FLAG_NAME)
    metrics.incCounter(config.validationFailureMetricsCount)
    context.output(config.validationFailedEventsOutputTag, event)
  }

  def onMissingSchema(event: Event, metrics: Metrics, context: ProcessFunction[Event, Event]#Context, message: String): Unit = {
    logger.debug(s"Telemetry schema validation is failed for: ${event.mid()} and error message is: $message")
    event.markValidationFailure(message, config.VALIDATION_FLAG_NAME)
    metrics.incCounter(config.validationFailureMetricsCount)
    context.output(config.validationFailedEventsOutputTag, event)
  }

}
