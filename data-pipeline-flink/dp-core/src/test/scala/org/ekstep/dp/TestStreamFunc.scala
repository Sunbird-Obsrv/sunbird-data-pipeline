package org.sunbird.dp.functions

import java.util
import java.util.UUID

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.{BaseJobConfig, BaseProcessFunction, Metrics}

class TestStreamFunc(config: BaseJobConfig)
                    (implicit val eventTypeInfo: TypeInformation[String]) extends BaseProcessFunction[String](config) {
  val testMetrics = "test-success-metric"

  override def processElement(event: String,
                              context: KeyedProcessFunction[Integer, String, String]#Context,
                              metrics: Metrics): Unit = {
    val testStreamTag: OutputTag[String] = OutputTag[String]("test-stream-tag")
    context.output(testStreamTag, "")
    metrics.incCounter(testMetrics)
  }

  override def getMetricsList(): List[String] = {
    List(testMetrics)
  }
}
