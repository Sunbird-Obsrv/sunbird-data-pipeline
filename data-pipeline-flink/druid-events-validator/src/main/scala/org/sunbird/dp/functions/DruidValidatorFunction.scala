package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.{BaseProcessFunction, Metrics}
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DruidValidatorConfig
import org.sunbird.dp.util.SchemaValidator

class DruidValidatorFunction(config: DruidValidatorConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DruidValidatorFunction])

  lazy val schemaValidator: SchemaValidator = new SchemaValidator(config)

  override def getMetricsList(): List[String] = {
    List(config.processedMetricsCount, config.validationSkipMetricsCount, config.validationSuccessMetricsCount, config.validationFailureMetricsCount)
  }

  override def processElement(event: Event, ctx: KeyedProcessFunction[Integer, Event, Event]#Context, metrics: Metrics): Unit = {

    metrics.incCounter(config.processedMetricsCount)
    if(event.isLogEvent) {
      event.markSkippedValidation()
      metrics.incCounter(config.validationSkipMetricsCount)
      ctx.output(config.validEventOutputTag, event)
    }
    else {
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
}
