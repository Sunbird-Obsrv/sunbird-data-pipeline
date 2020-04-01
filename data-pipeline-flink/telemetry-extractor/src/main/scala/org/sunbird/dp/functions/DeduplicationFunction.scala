package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.task.ExtractionConfig
import org.sunbird.dp.core.BaseDeduplication

class DeduplicationFunction(config: ExtractionConfig, @transient var dedupEngine: DedupEngine = null)
                           (implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]])
  extends ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]] with BaseDeduplication {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  override def open(parameters: Configuration): Unit = {
    if (dedupEngine == null) {
      val redisConnect = new RedisConnect(config)
      dedupEngine = new DedupEngine(redisConnect, config.dedupStore, config.cacheExpirySeconds)
    }
  }

  override def close(): Unit = {
    super.close()
    dedupEngine.closeConnectionPool()
  }

  override def processElement(batchEvents: util.Map[String, AnyRef],
                              context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                              out: Collector[util.Map[String, AnyRef]]): Unit = {

    deDup[util.Map[String, AnyRef]](getMsgIdentifier(batchEvents), batchEvents, context,
      config.uniqueEventOutputTag, config.duplicateEventOutputTag, flagName = "ex_duplicate")(dedupEngine)

    def getMsgIdentifier(batchEvents: util.Map[String, AnyRef]): String = {
      val paramsObj = Option(batchEvents.get("params"))
      val messageId = paramsObj.map {
        params => params.asInstanceOf[util.Map[String, AnyRef]].get("msgid").asInstanceOf[String]
      }
      messageId.orNull
    }
  }
}
