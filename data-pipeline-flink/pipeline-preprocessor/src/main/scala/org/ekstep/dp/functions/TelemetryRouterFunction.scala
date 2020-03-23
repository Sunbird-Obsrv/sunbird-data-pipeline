package org.ekstep.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.ekstep.dp.domain.Event
import org.ekstep.dp.task.PipelinePreprocessorConfig

class TelemetryRouterFunction(config: PipelinePreprocessorConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private val secondaryRouteEids: List[String] = config.secondaryRouteEids

  lazy val secondaryRouteEvents: OutputTag[Event] = new OutputTag[Event]("secondary-route-events")
  lazy val primaryRouteEvents: OutputTag[Event] = new OutputTag[Event]("primary-route-events")
  lazy val auditEvents: OutputTag[Event] = new OutputTag[Event]("audit-route-events")

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]): Unit = {

    if (secondaryRouteEids.contains(event.eid())) {
      ctx.output(secondaryRouteEvents, event)
    } else {
      ctx.output(primaryRouteEvents, event)
    }

    if ("AUDIT".equalsIgnoreCase(event.eid)) {
      ctx.output(auditEvents, event)
    }

  }
}
