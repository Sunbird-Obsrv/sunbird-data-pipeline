package org.sunbird.spec

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.{BaseDeduplication, BaseProcessFunction, Metrics}


class TestEventStreamFunc(config: BaseProcessTestConfig, @transient var dedupEngine: DedupEngine = null)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event](config) with BaseDeduplication {
  val totalProcessedCount = "processed-messages-count"

  override def getMetricsList(): List[String] = {
    List(totalProcessedCount)::: getDeDupMetrics()
  }

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

  override def processElement(event: Event,
                              context: KeyedProcessFunction[Integer, Event, Event]#Context,
                              metrics: Metrics): Unit = {
    try {

      deDup[Event](event.mid(), event, context, config.eventOutPutTag, config.eventOutPutTag, flagName = "test-dedup")(dedupEngine, metrics)
      println("========invoked the eventStream function=========")
      context.output(config.eventOutPutTag, event)
      metrics.incCounter(totalProcessedCount)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()

    }
  }
}
