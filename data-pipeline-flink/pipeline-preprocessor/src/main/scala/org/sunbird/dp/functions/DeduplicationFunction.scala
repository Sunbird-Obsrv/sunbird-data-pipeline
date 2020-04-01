package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.PipelinePreprocessorConfig

class DeduplicationFunction(config: PipelinePreprocessorConfig, @transient var dedupEngine: DedupEngine = null)(implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  override def open(parameters: Configuration): Unit = {
    if (dedupEngine == null) {
      val redisConnect = new RedisConnect(config)
      dedupEngine = new DedupEngine(redisConnect, config.dedupStore, config.cacheExpirySeconds)
    }
  }

  override def close(): Unit = {
    dedupEngine.closeConnectionPool()
  }

  override def processElement(event: Event,
                              ctx: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]): Unit = {

    val duplicationCheckRequired = isDuplicateCheckRequired(event)
    if(duplicationCheckRequired) {
      if (!dedupEngine.isUniqueEvent(event.mid)) {
        logger.info(s"Duplicate Event mid: ${event.mid}")
        event.markDuplicate()
        ctx.output(config.duplicateEventsOutputTag, event)
      } else {
        logger.info(s"Adding mid: ${event.mid} to Redis")
        dedupEngine.storeChecksum(event.mid)
        ctx.output(config.uniqueEventsOutputTag, event)
      }
    } else {
      ctx.output(config.uniqueEventsOutputTag, event)
    }
  }

  def isDuplicateCheckRequired(event: Event): Boolean = {
    config.includedProducersForDedup.contains(event.producerId())
  }
}
