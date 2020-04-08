package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.{BaseProcessFunction, Metrics}
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.PipelinePreprocessorConfig

class TelemetryRouterFunction(config: PipelinePreprocessorConfig)
                             (implicit val eventTypeInfo: TypeInformation[Event]) extends BaseProcessFunction[Event](config) {
  private[this] val logger = LoggerFactory.getLogger(classOf[TelemetryRouterFunction])

  override def getMetricsList(): List[String] = {
    List(config.primaryRouterMetricCount,
      config.secondaryRouterMetricCount,
      config.auditEventRouterMetricCount,
      config.shareEventsRouterMetricCount)
  }

  override def processElement(event: Event,
                              ctx: KeyedProcessFunction[Integer, Event, Event]#Context,
                              metrics: Metrics): Unit = {


    event.eid().toUpperCase() match {
      case "AUDIT" =>
        ctx.output(config.auditRouteEventsOutputTag, event)
        metrics.incCounter(metric = config.auditEventRouterMetricCount)
      case "SHARE" =>
        ctx.output(config.shareRouteEventsOutputTag, event)
        metrics.incCounter(metric = config.shareEventsRouterMetricCount)
      case _ => if (config.secondaryRouteEids.contains(event.eid())) {
        ctx.output(config.secondaryRouteEventsOutputTag, event)
        metrics.incCounter(metric = config.secondaryRouterMetricCount)
      } else {
        ctx.output(config.primaryRouteEventsOutputTag, event)
        metrics.incCounter(metric = config.primaryRouterMetricCount)
      }
    }
  }
}
