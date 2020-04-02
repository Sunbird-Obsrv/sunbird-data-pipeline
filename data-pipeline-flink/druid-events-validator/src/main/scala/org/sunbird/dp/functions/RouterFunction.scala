package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.BaseDeduplication
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DruidValidatorConfig

class RouterFunction(config: DruidValidatorConfig, @transient var dedupEngine: DedupEngine = null)
                    (implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] with BaseDeduplication {

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

    override def processElement(event: Event,
                                ctx: ProcessFunction[Event, Event]#Context,
                                collector: Collector[Event]): Unit = {

        if (event.isLogEvent) {
            event.markSkippedDedup()
            ctx.output(config.logRouterOutputTag, event)
        }
        else if (event.isErrorEvent) {
            event.markSkippedDedup()
            ctx.output(config.errorRouterOutputTag, event)
        }
        else {
            val outputTag = if (event.isSummaryEvent) config.summaryRouterOutputTag else config.telemetryRouterOutputTag
//            ctx.output(outputTag, event)
            deDup[Event](event.mid(), event, ctx,
                outputTag, config.duplicateEventOutputTag, flagName = "dv_duplicate")(dedupEngine)
        }

    }
}
