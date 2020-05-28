package org.sunbird.dp.extractor.functions

import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.extractor.task.TelemetryExtractorConfig

class DeduplicationFunction(config: TelemetryExtractorConfig, @transient var dedupEngine: DedupEngine = null)
                           (implicit val stringTypeInfo: TypeInformation[String])
  extends BaseProcessFunction[String, String](config) {

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

  override def processElement(batchEvents: String,
                              context: ProcessFunction[String, String]#Context,
                              metrics: Metrics): Unit = {

    metrics.incCounter(config.totalBatchEventCount)
    deDup[String](getMsgIdentifier(batchEvents), batchEvents, context,
      config.uniqueEventOutputTag, config.duplicateEventOutputTag, flagName = "extractor_duplicate")(dedupEngine, metrics)

    def getMsgIdentifier(batchEvents: String): String = {
      val event = new Gson().toJson(batchEvents, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]]
      val paramsObj = Option(event.get("params"))
      val messageId = paramsObj.map {
        params => params.asInstanceOf[util.Map[String, AnyRef]].get("msgid").asInstanceOf[String]
      }
      messageId.orNull
    }
  }
}
