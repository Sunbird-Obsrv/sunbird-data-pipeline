package org.sunbird.dp.denorm.`type`

import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig
import org.sunbird.dp.denorm.util.CacheData

class DialcodeDenormalization(config: DenormalizationConfig) {

  private val dialcodeDataCache =
    new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config),
      config.dialcodeStore, config.dialcodeFields)
  dialcodeDataCache.init()

  def denormalize(event: Event, cacheData: CacheData, metrics: Metrics) = {
    if (null != event.objectType() && List("dialcode", "qr").contains(event.objectType().toLowerCase())) {
      metrics.incCounter(config.dialcodeTotal)
      val dialcodeData = cacheData.dialCode.map(f => {(f._1.toLowerCase().replace("_", ""), f._2)})

      if (dialcodeData.nonEmpty) {
        metrics.incCounter(config.dialcodeCacheHit)
        event.addDialCodeData(dialcodeData)
      } else {
        metrics.incCounter(config.dialcodeCacheMiss)
        event.setFlag("dialcode_denorm", value = false)
      }
    }
  }

  def closeDataCache() = {
    dialcodeDataCache.close()
  }

}
