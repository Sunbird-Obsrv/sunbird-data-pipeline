package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core.BaseDeduplication
import org.sunbird.dp.domain.Event
import org.sunbird.dp.core.DataCache
import org.sunbird.dp.core.BaseProcessFunction
import org.sunbird.dp.core.Metrics
import org.apache.flink.streaming.api.functions.KeyedProcessFunction

class ContentDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event]) extends BaseProcessFunction[Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[ContentDenormFunction])

  private var dataCache: DataCache = _;
  
  val total = "content-total";
  val cacheHit = "content-cache-hit";
  val cacheMiss = "content-cache-miss";
  
  override def getMetricsList(): List[String] = {
    List(total, cacheHit, cacheMiss)
  }

  override def open(parameters: Configuration): Unit = {

    dataCache = new DataCache(config, new RedisConnect(config), config.contentStore, config.contentFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close();
  }

  override def processElement(event: Event, context: KeyedProcessFunction[String, Event, Event]#Context, metrics: Metrics): Unit = {
    
    Console.println("ContentDenormFunction:processElement", event.getTelemetry.read("dialcodedata"), event.getTelemetry.read("flags"));
    val objectType = event.objectType();
    val objectId = event.objectID();
    if(!List("user","qr","dialcode").contains(objectType) && null != objectId) {
      metrics.incCounter(total)
      val contentData = dataCache.getWithRetry(objectId);
      if(contentData.size > 0) {
        metrics.incCounter(cacheHit)
        event.addContentData(contentData)
      } else {
        metrics.incCounter(cacheMiss)
        event.setFlag("content_denorm", false);
      }
      if(event.checkObjectIdNotEqualsRollUpl1Id()) {
        event.addCollectionData(dataCache.getWithRetry(event.objectRollUpl1ID()))
      }
    }
    
    context.output(config.withContentEventsTag, event);
  }
}
