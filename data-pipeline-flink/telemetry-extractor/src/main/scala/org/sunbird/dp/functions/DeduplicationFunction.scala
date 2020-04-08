package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.task.ExtractionConfig
import org.sunbird.dp.core.{BaseDeduplication, BaseProcessFunction, Metrics}

class DeduplicationFunction(config: ExtractionConfig, @transient var dedupEngine: DedupEngine = null)
                           (implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]])
  extends BaseProcessFunction[util.Map[String, AnyRef]](config) with BaseDeduplication {

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

  val totalProcessedCount = "processed-batch-messages-count"

  override def getMetricsList(): List[String] = {
    List(totalProcessedCount) ::: getDeDupMetrics
  }

  override def processElement(batchEvents: util.Map[String, AnyRef],
                              context: KeyedProcessFunction[Integer, util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                              metrics: Metrics): Unit = {

    metrics.incCounter(totalProcessedCount)
    deDup[util.Map[String, AnyRef]](getMsgIdentifier(batchEvents), batchEvents, context,
      config.uniqueEventOutputTag, config.duplicateEventOutputTag, flagName = "ex_duplicate")(dedupEngine, metrics)

    def getMsgIdentifier(batchEvents: util.Map[String, AnyRef]): String = {
      val paramsObj = Option(batchEvents.get("params"))
      val messageId = paramsObj.map {
        params => params.asInstanceOf[util.Map[String, AnyRef]].get("msgid").asInstanceOf[String]
      }
      messageId.orNull
    }
  }
}
