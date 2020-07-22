package org.sunbird.dp.validator.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.validator.domain.Event
import org.sunbird.dp.validator.task.DruidValidatorConfig

class DruidRouterFunction(config: DruidValidatorConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DruidRouterFunction])

  override def metricsList(): List[String] = {
    List(config.telemetryRouterMetricCount, config.summaryRouterMetricCount)
  }

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

    if (event.isSummaryEvent) {
      metrics.incCounter(config.summaryRouterMetricCount)
      ctx.output(config.summaryRouterOutputTag, event)
    } else {
      metrics.incCounter(config.telemetryRouterMetricCount)
      ctx.output(config.telemetryRouterOutputTag, event)
    }

  }
}
