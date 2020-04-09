package org.sunbird.spec

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.sunbird.dp.core.{BaseProcessFunction, Metrics}


class TestStreamFunc(config: BaseProcessTestConfig)(implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]])
  extends BaseProcessFunction[util.Map[String, AnyRef]](config) {
  val totalProcessedCount = "processed-messages-count"

  override def getMetricsList(): List[String] = {
    List(totalProcessedCount)
  }
  override def processElement(event: util.Map[String, AnyRef],
                              context: KeyedProcessFunction[Integer, util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                              metrics: Metrics): Unit = {
    metrics.incCounter(totalProcessedCount)
    println("========invoked the teststream function=========")
    context.output(config.eventOutPutTag, event)
  }
}
