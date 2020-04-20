package org.sunbird.dp.denorm.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.denorm.domain.{DeviceProfile, Event}
import org.sunbird.dp.denorm.task.DenormalizationConfig

class DeviceDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeviceDenormFunction])
  private var dataCache: DataCache = _

  override def metricsList(): List[String] = {
    List(config.deviceTotal, config.deviceCacheHit, config.deviceCacheMiss, config.eventsExpired)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config), config.deviceStore, config.deviceFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
  }
  

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

    if (event.isOlder(config.ignorePeriodInMonths)) { // Skip events older than configured value (default: 3 months)
      metrics.incCounter(config.eventsExpired)
    } else {
      event.compareAndAlterEts() // Reset ets to today's date if we get future value
      val did = event.did()
      if (null != did && did.nonEmpty) {
        metrics.incCounter(config.deviceTotal)
        val deviceDetails = dataCache.hgetAllWithRetry(did)

        if(deviceDetails.nonEmpty) {
          metrics.incCounter(config.deviceCacheHit)
          event.addDeviceProfile(DeviceProfile.apply(deviceDetails))
        } else {
          metrics.incCounter(config.deviceCacheMiss)
          event.setFlag("device_denorm", value = false)
        }
      }
      context.output(config.withDeviceEventsTag, event)
    }
  }
}
