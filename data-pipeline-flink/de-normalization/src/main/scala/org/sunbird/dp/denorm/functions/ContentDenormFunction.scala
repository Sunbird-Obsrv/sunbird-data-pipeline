package org.sunbird.dp.denorm.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig

class ContentDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[ContentDenormFunction])
  private var dataCache: DataCache = _

  override def metricsList(): List[String] = {
    List(config.contentTotal, config.contentCacheHit, config.contentCacheMiss)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config), config.contentStore, config.contentFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {
    
    val objectType = event.objectType()
    val objectId = event.objectID()
    if(!List("user","qr","dialcode").contains(objectType) && null != objectId) {
      metrics.incCounter(config.contentTotal)
      val contentData = dataCache.getWithRetry(objectId)

      if(contentData.nonEmpty) {
        metrics.incCounter(config.contentCacheHit)
        event.addContentData(contentData)
      } else {
        metrics.incCounter(config.contentCacheMiss)
        event.setFlag("content_denorm", value = false)
      }

      if(event.checkObjectIdNotEqualsRollUpl1Id()) {
        event.addCollectionData(dataCache.getWithRetry(event.objectRollUpl1ID()))
      }
    }
    
    context.output(config.withContentEventsTag, event)
  }
}
