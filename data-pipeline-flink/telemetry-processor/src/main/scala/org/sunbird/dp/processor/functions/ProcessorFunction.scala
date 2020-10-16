package org.sunbird.dp.processor.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.processor.task.TelemetryProcessorConfig

class ProcessorFunction(config: TelemetryProcessorConfig)(implicit val stringTypeInfo: TypeInformation[String])
  extends BaseProcessFunction[String, String](config) {

  override def metricsList(): List[String] = {
    List(config.successEventCount)
  }


  /**
   * Method to process the batch events
   *
   * @param batchEvent - Batch of telemetry events
   * @param context
   */
  override def processElement(batchEvent: String,
                              context: ProcessFunction[String, String]#Context,
                              metrics: Metrics): Unit = {
    context.output(config.eventsOutputTag, batchEvent)
    metrics.incCounter(config.successEventCount)

  }
}

