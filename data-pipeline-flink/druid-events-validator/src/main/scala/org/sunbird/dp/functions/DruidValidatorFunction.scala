package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DruidValidatorConfig
import org.sunbird.dp.util.SchemaValidator

class DruidValidatorFunction(config: DruidValidatorConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DruidValidatorFunction])

  lazy val schemaValidator: SchemaValidator = new SchemaValidator(config)

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              collector: Collector[Event]): Unit = {

    if(event.isLogEvent) {
      event.markSkippedValidation()
      ctx.output(config.validEventOutputTag, event)
    }
    else {
      val validationReport = schemaValidator.validate(event)
      if (validationReport.isSuccess) {
        event.markValidationSuccess()
        ctx.output(config.validEventOutputTag, event)
      } else {
        val failedErrorMsg = schemaValidator.getInvalidFieldName(validationReport.toString)
        event.markValidationFailure(failedErrorMsg)
        ctx.output(config.invalidEventOutputTag, event)
      }
    }
  }
}
