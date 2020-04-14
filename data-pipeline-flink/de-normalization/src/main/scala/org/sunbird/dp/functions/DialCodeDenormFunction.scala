package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.RedisConnect
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core._
import org.sunbird.dp.domain.Event

class DialCodeDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DialCodeDenormFunction])

  private var dataCache: DataCache = _

  override def metricsList(): List[String] = {
    List(config.dialcodeTotal, config.dialcodeCacheHit, config.dialcodeCacheMiss)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config), config.dialcodeStore, config.dialcodeFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

    if (null != event.objectType() && List("dialcode", "qr").contains(event.objectType().toLowerCase())) {
      metrics.incCounter(config.dialcodeTotal)
      val dialcodeData = dataCache.getWithRetry(event.objectID().toUpperCase())

      if (dialcodeData.nonEmpty) {
        metrics.incCounter(config.dialcodeCacheHit)
        event.addDialCodeData(dialcodeData)
      } else {
        metrics.incCounter(config.dialcodeCacheMiss)
        event.setFlag("dialcode_denorm", value = false)
      }
    }
    context.output(config.withDialCodeEventsTag, event)
  }
}
