package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.{BaseDeduplication, BaseProcessFunction, Metrics}
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DeduplicationConfig

class DeduplicationFunction(config: DeduplicationConfig, @transient var dedupEngine: DedupEngine = null)
                           (implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  val duplicateEventsCount = "duplicate-count"
  val uniqueEventsCount = "unique-count"

  override def metricsList(): List[String] = {
    List(duplicateEventsCount, uniqueEventsCount)
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

  override def processElement(event: Event, ctx: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {

    val duplicationCheckRequired = isDuplicateCheckRequired(event)
    println("Duplicate check required = " + duplicationCheckRequired)

    if (duplicationCheckRequired) {
      if (!dedupEngine.isUniqueEvent(event.mid)) {
        logger.info(s"Duplicate Event mid: ${event.mid}")
        println(s"Duplicate Event mid: ${event.mid}")
        event.markDuplicate()
        ctx.output(config.duplicateEventsOutputTag, event)
        metrics.incCounter(duplicateEventsCount)
      } else {
        logger.info(s"Adding mid: ${event.mid} to Redis")
        println(s"Adding mid: ${event.mid} to Redis")
        dedupEngine.storeChecksum(event.mid)
        event.markSuccess()
        ctx.output(config.uniqueEventsOutputTag, event)
        metrics.incCounter(uniqueEventsCount)
      }
    } else {
      event.markSuccess()
      println(s"Skipping mid: ${event.mid}. Sending to unique")
      ctx.output(config.uniqueEventsOutputTag, event)
      metrics.incCounter(uniqueEventsCount)
    }

  }

  def isDuplicateCheckRequired(event: Event): Boolean = {
    config.includedProducersForDedup.contains(event.producerId())
  }
}