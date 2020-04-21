package org.sunbird.dp.deviceprofile.functions

import java.lang.reflect.Type
import java.util

import com.google.gson.reflect.TypeToken
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterConfig

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

