package org.sunbird.dp.denorm.`type`

import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfigV2

class DialcodeDenormalizationV2(config: DenormalizationConfigV2) {

  private val dialcodeDataCache =
    new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config),
      config.dialcodeStore, config.dialcodeFields)
  dialcodeDataCache.init()

  def denormalize(event: Event, metrics: Metrics): Event = {
    if (null != event.objectType() && List("dialcode", "qr").contains(event.objectType().toLowerCase())) {
      metrics.incCounter(config.dialcodeTotal)
      val dialcodeData = dialcodeDataCache.getWithRetry(event.objectID().toUpperCase()).map(f => {(f._1.toLowerCase().replace("_", ""), f._2)})

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
