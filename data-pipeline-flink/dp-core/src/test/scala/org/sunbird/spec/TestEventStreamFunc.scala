package org.sunbird.spec

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.{BaseDeduplication, BaseProcessFunction, Metrics}


class TestEventStreamFunc(config: BaseProcessTestConfig, @transient var dedupEngine: DedupEngine = null)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) with BaseDeduplication {

  override def metricsList(): List[String] = {
    List(config.telemetryEventCount) ::: deduplicationMetrics
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
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {
    try {

      deDup[Event](event.mid(), event, context, config.eventOutPutTag, config.eventOutPutTag, flagName = "test-dedup")(dedupEngine, metrics)
      println("========invoked the eventStream function=========")
      context.output(config.eventOutPutTag, event)
      metrics.incCounter(config.telemetryEventCount)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()

    }
  }
}
