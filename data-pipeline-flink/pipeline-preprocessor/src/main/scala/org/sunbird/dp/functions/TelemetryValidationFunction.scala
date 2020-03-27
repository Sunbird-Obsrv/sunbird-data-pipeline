package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.PipelinePreprocessorConfig
import org.sunbird.dp.util.SchemaValidator

class TelemetryValidationFunction(config: PipelinePreprocessorConfig, @transient var schemaValidator: SchemaValidator = null)
                                 (implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[TelemetryValidationFunction])

  lazy val invalidEvents: OutputTag[Event] = new OutputTag[Event]("validation-falied-events")
  lazy val validEvents: OutputTag[Event] = new OutputTag[Event]("valid-events")


  override def open(parameters: Configuration): Unit = {
    if (schemaValidator == null) {
      schemaValidator = new SchemaValidator(config)
    }
  }

  override def close(): Unit = super.close()

  // lazy val schemaValidator: SchemaValidator = new SchemaValidator(config)

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]): Unit = {

    dataCorrection(event)
    try {
      if (!schemaValidator.schemaFileExists(event)) {
        logger.info(s"SCHEMA NOT FOUND FOR EID: ${event.eid}")
        logger.debug(s"SKIPPING EVENT ${event.mid} FROM VALIDATION")
        event.markSkipped()
        ctx.output(validEvents, event)
      } else {
        val validationReport = schemaValidator.validate(event)
        if (validationReport.isSuccess) {
          event.updateDefaults(config)
          ctx.output(validEvents, event)
        } else {
          val failedErrorMsg = schemaValidator.getInvalidFieldName(validationReport.toString)
          event.markValidationFailure(failedErrorMsg)
          ctx.output(invalidEvents, event)
        }
      }
    } catch {
      case ex: Exception =>
        logger.error("Error validating JSON event", ex)
    }

  }

  private def dataCorrection(event: Event): Event = {
    // Remove prefix from federated userIds
    val eventActorId = event.actorId
    if (eventActorId != null && !eventActorId.isEmpty && eventActorId.startsWith("f:"))
      event.updateActorId(eventActorId.substring(eventActorId.lastIndexOf(":") + 1))
    if (event.eid != null && event.eid.equalsIgnoreCase("SEARCH"))
      event.correctDialCodeKey()
    event
  }

}
