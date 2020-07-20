package org.sunbird.dp.denorm.`type`

import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.{DeviceProfile, Event}
import org.sunbird.dp.denorm.task.DenormalizationConfigV2

class DeviceDenormalizationV2(config: DenormalizationConfigV2) {

  private val deviceDataCache: DataCache =
    new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config),
      config.deviceStore, config.deviceFields)
  deviceDataCache.init()

  def denormalize(event: Event, metrics: Metrics): Event = {
    event.compareAndAlterEts() // Reset ets to today's date if we get future value
    val did = event.did()
    if (null != did && did.nonEmpty) {
      metrics.incCounter(config.deviceTotal)
      val deviceDetails = deviceDataCache.hgetAllWithRetry(did)
      if (deviceDetails.nonEmpty) {
        metrics.incCounter(config.deviceCacheHit)
        event.addDeviceProfile(DeviceProfile.apply(deviceDetails))
      } else {
        metrics.incCounter(config.deviceCacheMiss)
        event.setFlag("device_denorm", value = false)
      }
    }
    event
  }

  def closeDataCache() = {
    deviceDataCache.close()
  }
}
