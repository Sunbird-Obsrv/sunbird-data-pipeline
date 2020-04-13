package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.RedisConnect
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core._
import org.sunbird.dp.domain.Event

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

      /*
      println("content-cache-hit = " + metrics.get(config.contentCacheHit))
      println("content-cache-miss = " + metrics.get(config.contentCacheMiss))
      println("content-total = " + metrics.get(config.contentTotal))
      */
    }
    
    context.output(config.withContentEventsTag, event)
  }
}
