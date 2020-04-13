package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.task.TelemetryExtractorConfig
import org.sunbird.dp.core.{BaseDeduplication, BaseProcessFunction, Metrics}

class DeduplicationFunction(config: TelemetryExtractorConfig, @transient var dedupEngine: DedupEngine = null)
                           (implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]])
  extends BaseProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  override def metricsList(): List[String] = {
    List(config.totalBatchEventCount) ::: deduplicationMetrics
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
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
                              metrics: Metrics): Unit = {

    metrics.incCounter(config.totalBatchEventCount)
    deDup[util.Map[String, AnyRef]](getMsgIdentifier(batchEvents), batchEvents, context,
      config.uniqueEventOutputTag, config.duplicateEventOutputTag, flagName = "extractor_duplicate")(dedupEngine, metrics)

    def getMsgIdentifier(batchEvents: util.Map[String, AnyRef]): String = {
      val paramsObj = Option(batchEvents.get("params"))
      val messageId = paramsObj.map {
        params => params.asInstanceOf[util.Map[String, AnyRef]].get("msgid").asInstanceOf[String]
      }
      messageId.orNull
    }
  }
}
