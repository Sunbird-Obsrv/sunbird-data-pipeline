package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{ DedupEngine, RedisConnect }
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core.BaseDeduplication
import org.sunbird.dp.domain.Event
import org.sunbird.dp.core.DataCache
import org.sunbird.dp.domain.DeviceProfile

class DeviceDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event]) extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeviceDenormFunction])
  private var dataCache: DataCache = _;

  override def open(parameters: Configuration): Unit = {

    dataCache = new DataCache(config, new RedisConnect(config), config.deviceStore, config.deviceFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close();
  }

  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, out: Collector[Event]): Unit = {

    Console.println("DeviceDenormFunction:processElement", event.getTelemetry.read("devicedata"), event.getTelemetry.read("flags"));
    if (event.isOlder(config.ignorePeriodInMonths)) { // Skip events older than configured value (default: 3 months)
      // TODO: incExpiredEventCount
    } else {
      event.compareAndAlterEts(); // Reset ets to today's date if we get future value
      val did = event.did();
      if (null != did && did.nonEmpty) {
        val deviceDetails = dataCache.hgetAllWithRetry(did)
        if(deviceDetails.size > 0) {
          event.addDeviceProfile(DeviceProfile.apply(deviceDetails))
        } else {
          event.setFlag("device_denorm", false)
        }
      }
      context.output(config.withDeviceEventsTag, event);
    }
  }
}
