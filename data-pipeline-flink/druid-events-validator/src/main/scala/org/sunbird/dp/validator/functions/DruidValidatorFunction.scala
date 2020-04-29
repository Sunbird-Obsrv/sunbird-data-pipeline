package org.sunbird.dp.validator.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.validator.domain.Event
import org.sunbird.dp.validator.task.DruidValidatorConfig
import org.sunbird.dp.validator.util.SchemaValidator

class DruidValidatorFunction(config: DruidValidatorConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DruidValidatorFunction])

  lazy val schemaValidator: SchemaValidator = new SchemaValidator(config)

  override def metricsList(): List[String] = {
    List(config.processedMetricsCount, config.validationSuccessMetricsCount,
      config.validationFailureMetricsCount)
  }

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

    metrics.incCounter(config.processedMetricsCount)
    val validationReport = schemaValidator.validate(event)
    if (validationReport.isSuccess) {
      event.markValidationSuccess()
      metrics.incCounter(config.validationSuccessMetricsCount)
      ctx.output(config.validEventOutputTag, event)
    } else {
      val failedErrorMsg = schemaValidator.getInvalidFieldName(validationReport.toString)
      event.markValidationFailure(failedErrorMsg)
      metrics.incCounter(config.validationFailureMetricsCount)
      ctx.output(config.invalidEventOutputTag, event)
    }
  }
}
