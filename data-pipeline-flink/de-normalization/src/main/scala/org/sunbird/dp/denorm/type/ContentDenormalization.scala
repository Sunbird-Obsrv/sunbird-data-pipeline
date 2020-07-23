package org.sunbird.dp.denorm.`type`

import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig

class ContentDenormalization(config: DenormalizationConfig) {

  private val contentDataCache =
    new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config),
      config.contentStore, config.contentFields)
  contentDataCache.init()

  def denormalize(event: Event, metrics: Metrics): Event = {
    val objectType = event.objectType()
    val objectId = event.objectID()
    if (!List("user", "qr", "dialcode").contains(objectType) && null != objectId) {
      metrics.incCounter(config.contentTotal)
      val contentData = contentDataCache.getWithRetry(objectId).map(f => {(f._1.toLowerCase().replace("_", ""), f._2)})

      if (contentData.nonEmpty) {
        metrics.incCounter(config.contentCacheHit)
        event.addContentData(contentData)
      } else {
        metrics.incCounter(config.contentCacheMiss)
        event.setFlag("content_denorm", value = false)
      }

      if (event.checkObjectIdNotEqualsRollUpl1Id()) {
        event.addCollectionData(contentDataCache.getWithRetry(event.objectRollUpl1ID()))
      }
    }
    event
  }

  def closeDataCache() = {
    contentDataCache.close()
  }
}
