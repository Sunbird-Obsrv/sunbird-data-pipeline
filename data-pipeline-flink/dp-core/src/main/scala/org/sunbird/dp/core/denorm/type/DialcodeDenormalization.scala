package org.sunbird.dp.core.denorm.`type`

import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.denorm.config.DenormalizationConfig
import org.sunbird.dp.core.denorm.domain.Event
import org.sunbird.dp.core.job.Metrics

class DialcodeDenormalization(config: DenormalizationConfig) {

  private val dialcodeDataCache =
    new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config),
      config.dialcodeStore, config.dialcodeFields)
  dialcodeDataCache.init()

  def denormalize(event: Event, metrics: Metrics): Event = {
    if (null != event.objectType() && List("dialcode", "qr").contains(event.objectType().toLowerCase())) {
      metrics.incCounter(config.dialcodeTotal)
      val dialcodeData = dialcodeDataCache.getWithRetry(event.objectID().toUpperCase())

      if (dialcodeData.nonEmpty) {
        metrics.incCounter(config.dialcodeCacheHit)
        event.addDialCodeData(dialcodeData)
      } else {
        metrics.incCounter(config.dialcodeCacheMiss)
        event.setFlag("dialcode_denorm", value = false)
      }
    }
    event
  }

  def closeDataCache() = {
    dialcodeDataCache.close()
  }

}
