package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.PipelinePreprocessorConfig

class TelemetryRouterFunction(config: PipelinePreprocessorConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private val secondaryRouteEids: List[String] = config.secondaryRouteEids

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]): Unit = {

    if (secondaryRouteEids.contains(event.eid())) {
      ctx.output(config.secondaryRouteEventsOutputTag, event)
    } else {
      ctx.output(config.primaryRouteEventsOutputTag, event)
    }

    if ("AUDIT".equalsIgnoreCase(event.eid)) {
      ctx.output(config.auditRouteEventsOutputTag, event)
    }

  }
}
