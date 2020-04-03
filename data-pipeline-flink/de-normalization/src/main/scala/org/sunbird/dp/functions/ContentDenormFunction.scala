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

class ContentDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[ContentDenormFunction])

  private var dataCache: DataCache = _;

  override def open(parameters: Configuration): Unit = {

    dataCache = new DataCache(config, new RedisConnect(config), config.contentStore, config.contentFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close();
  }

  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, out: Collector[Event]): Unit = {
    
    Console.println("ContentDenormFunction:processElement", event.getTelemetry.read("dialcodedata"), event.getTelemetry.read("flags"));
    val objectType = event.objectType();
    val objectId = event.objectID();
    if(!List("user","qr","dialcode").contains(objectType) && null != objectId) {
      val contentData = dataCache.getWithRetry(objectId);
      if(contentData.size > 0) {
        event.addContentData(contentData)
      } else {
        event.setFlag("content_denorm", false);
      }
      if(event.checkObjectIdNotEqualsRollUpl1Id()) {
        event.addCollectionData(dataCache.getWithRetry(event.objectRollUpl1ID()))
      }
    }
    
    context.output(config.withContentEventsTag, event);
  }
}
