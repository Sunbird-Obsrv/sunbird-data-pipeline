package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.{BaseDeduplication, BaseProcessFunction, Metrics}
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DruidValidatorConfig

class RouterFunction(config: DruidValidatorConfig, @transient var dedupEngine: DedupEngine = null)
                    (implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event](config)  with BaseDeduplication {

    private[this] val logger = LoggerFactory.getLogger(classOf[RouterFunction])

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

    override def getMetricsList(): List[String] = {
        List(config.skipDedupMetricCount, config.logRouterMetricCount, config.errorRouterMetricCount,
            config.telemetryRouterMetricCount, config.summaryRouterMetricCount) ::: getDeDupMetrics
    }

    override def processElement(event: Event, ctx: KeyedProcessFunction[Integer, Event, Event]#Context, metrics: Metrics): Unit = {

        if (event.isLogEvent) {
            event.markSkippedDedup()
            metrics.incCounter(config.skipDedupMetricCount)
            metrics.incCounter(config.logRouterMetricCount)
            ctx.output(config.logRouterOutputTag, event)
        }
        else if (event.isErrorEvent) {
            event.markSkippedDedup()
            metrics.incCounter(config.skipDedupMetricCount)
            metrics.incCounter(config.errorRouterMetricCount)
            ctx.output(config.errorRouterOutputTag, event)
        }
        else {
            val outputTag = if (event.isSummaryEvent) {
                metrics.incCounter(config.summaryRouterMetricCount)
                config.summaryRouterOutputTag
            } else {
                metrics.incCounter(config.telemetryRouterMetricCount)
                config.telemetryRouterOutputTag
            }
            deDup[Event](event.mid(), event, ctx,
                outputTag, config.duplicateEventOutputTag, flagName = "dv_duplicate")(dedupEngine, metrics)
        }

    }
}
