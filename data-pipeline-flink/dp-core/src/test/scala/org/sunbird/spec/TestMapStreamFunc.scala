package org.sunbird.spec

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.core.{BaseProcessFunction, Metrics}


class TestMapStreamFunc(config: BaseProcessTestConfig)(implicit val stringTypeInfo: TypeInformation[String])
  extends BaseProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]](config) {

  override def metricsList(): List[String] = {
    val metrics = List(config.processedEventCount)
    metrics
  }
  override def processElement(event: util.Map[String, AnyRef],
                              context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                              metrics: Metrics): Unit = {
    metrics.incCounter(config.processedEventCount)
    println("========invoked the MapStream function=========")
    context.output(config.mapOutPutTag, event)
  }
}
