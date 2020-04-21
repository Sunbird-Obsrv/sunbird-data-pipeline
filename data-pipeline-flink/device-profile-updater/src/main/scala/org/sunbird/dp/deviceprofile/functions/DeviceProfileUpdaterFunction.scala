package org.sunbird.dp.deviceprofile.functions

import java.lang.reflect.Type
import java.util
import java.util.UUID

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.joda.time.format.DateTimeFormat
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterConfig
import org.sunbird.dp.extractor.domain._
import org.sunbird.dp.extractor.domain.{Context => EventContext}
import org.sunbird.dp.extractor.task.TelemetryExtractorConfig

class DeviceProfileUpdaterFunction(config: DeviceProfileUpdaterConfig)(implicit val stringTypeInfo: TypeInformation[String])
  extends BaseProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]](config) {

  val mapType: Type = new TypeToken[util.Map[String, AnyRef]]() {}.getType

  override def metricsList(): List[String] = {
    List(config.successEventCount, config.auditEventCount, config.failedEventCount)
  }


  /**
   * Method to process the events extraction from the batch
   *
   * @param batchEvent - Batch of telemetry events
   * @param context
   */
  override def processElement(batchEvent: util.Map[String, AnyRef],
                              context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                              metrics: Metrics): Unit = {


  }

}

