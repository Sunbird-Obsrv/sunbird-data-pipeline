package org.sunbird.dp.denorm.`type`

import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.domain.EventsPath
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig
import org.sunbird.dp.denorm.util.CacheData

class ContentDenormalization(config: DenormalizationConfig) {

  private val contentDataCache =
    new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config),
      config.contentStore, config.contentFields)
  contentDataCache.init()

  def denormalize(event: Event, cacheData: CacheData, metrics: Metrics) = {
    val objectType = event.objectType()
    val objectId = event.objectID()
    if (!List("user", "qr", "dialcode").contains(objectType) && null != objectId) {
      metrics.incCounter(config.contentTotal)
      val contentData = cacheData.content.map(f => {
        (f._1.toLowerCase().replace("_", ""), f._2)
      })

      if (contentData.nonEmpty) {
        metrics.incCounter(config.contentCacheHit)
        event.addContentData(contentData)
      } else {
        metrics.incCounter(config.contentCacheMiss)
        event.setFlag("content_denorm", value = false)
      }

      if (event.checkObjectIdNotEqualsRollUpId(EventsPath.OBJECT_ROLLUP_L1)) {
        event.addCollectionData(cacheData.collection.map(f => {
          (f._1.toLowerCase().replace("_", ""), f._2)
        }))
      }
      if (event.checkObjectIdNotEqualsRollUpId(EventsPath.OBJECT_ROLLUP_L2)) {
        event.addL2Data(cacheData.l2data.filter(x => config.l2DataFields.contains(x._1)).map(f => {
          (f._1.toLowerCase().replace("_", ""), f._2)
        }))
      }
    }
  }

  def closeDataCache() = {
    contentDataCache.close()
  }
}
