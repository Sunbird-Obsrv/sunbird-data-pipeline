package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{ DedupEngine, RedisConnect }
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core.BaseDeduplication
import org.sunbird.dp.domain.Event
import org.sunbird.dp.core.DataCache
import org.sunbird.dp.core.BaseProcessFunction
import org.sunbird.dp.core.Metrics
import org.apache.flink.streaming.api.functions.KeyedProcessFunction

class DialCodeDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event]) extends BaseProcessFunction[Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DialCodeDenormFunction])

  private var dataCache: DataCache = _;
  
  val total = "dialcode-total";
  val cacheHit = "dialcode-cache-hit";
  val cacheMiss = "dialcode-cache-miss";
  
  override def getMetricsList(): List[String] = {
    List(total, cacheHit, cacheMiss)
  }

  override def open(parameters: Configuration): Unit = {

    dataCache = new DataCache(config, new RedisConnect(config), config.dialcodeStore, config.dialcodeFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close();
  }

  override def processElement(event: Event, context: KeyedProcessFunction[String, Event, Event]#Context, metrics: Metrics): Unit = {

    if (null != event.objectType() && List("dialcode", "qr").contains(event.objectType().toLowerCase())) {
      metrics.incCounter(total);
      val dialcodeData = dataCache.getWithRetry(event.objectID().toUpperCase());
      if (dialcodeData.size > 0) {
        metrics.incCounter(cacheHit);
        event.addDialCodeData(dialcodeData)
      } else {
        metrics.incCounter(cacheMiss);
        event.setFlag("dialcode_denorm", false)
      }
    }
    context.output(config.withDialCodeEventsTag, event);
  }
}
