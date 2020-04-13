package org.sunbird.spec

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.sunbird.dp.core.{BaseProcessFunction, Metrics}


class TestEventStreamFunc(config: BaseProcessTestConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event](config) {
  val totalProcessedCount = "processed-messages-count"

  override def getMetricsList(): List[String] = {
    List(totalProcessedCount)
  }
  override def processElement(event: Event,
                              context: KeyedProcessFunction[Integer, Event, Event]#Context,
                              metrics: Metrics): Unit = {
    metrics.incCounter(totalProcessedCount)
    println("========invoked the eventStream function=========")
    context.output(config.eventOutPutTag, event)
  }
}
