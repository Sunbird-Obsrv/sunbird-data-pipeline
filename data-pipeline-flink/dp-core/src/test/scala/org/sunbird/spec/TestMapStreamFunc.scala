package org.sunbird.spec

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}


class TestMapStreamFunc(config: BaseProcessTestConfig)(implicit val stringTypeInfo: TypeInformation[String])
  extends BaseProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]](config) {

  override def metricsList(): List[String] = {
    List(config.mapEventCount)
  }

  override def processElement(event: util.Map[String, AnyRef],
                              context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                              metrics: Metrics): Unit = {
    metrics.get(config.mapEventCount)
    metrics.reset(config.mapEventCount)
    metrics.incCounter(config.mapEventCount)
    context.output(config.mapOutputTag, event)
  }
}
