package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DeduplicationConfig

class DeduplicationFunction(config: DeduplicationConfig, @transient var dedupEngine: DedupEngine = null)
                           (implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  lazy val duplicateEventOutput: OutputTag[Event] = new OutputTag[Event](id = "duplicate-events")
  lazy val uniqueEventOuput: OutputTag[Event] = new OutputTag[Event](id = "unique-events")

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
                              out: Collector[Event]): Unit = {
    
    val duplicationCheckRequired = isDuplicateCheckRequired(event)
    if (duplicationCheckRequired) {
      if (!dedupEngine.isUniqueEvent(event.mid)) {
        logger.info(s"Duplicate Event mid: ${event.mid}")
        event.markDuplicate()
        ctx.output(duplicateEventOutput, event)
      } else {
        logger.info(s"Adding mid: ${event.mid} to Redis")
        dedupEngine.storeChecksum(event.mid)
        event.markSuccess()
        ctx.output(uniqueEventOuput, event)
      }
    } else {
      event.markSuccess()
      ctx.output(uniqueEventOuput, event)
    }
  }

  def isDuplicateCheckRequired(event: Event): Boolean = {
    config.includedProducersForDedup.contains(event.producerId())
  }
}
