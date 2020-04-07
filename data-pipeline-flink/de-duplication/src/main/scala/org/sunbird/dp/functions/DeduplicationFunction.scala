package org.sunbird.dp.functions

import java.util.concurrent.atomic.AtomicInteger

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.scala.metrics.ScalaGauge
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.DeduplicationConfig

class DeduplicationFunction(config: DeduplicationConfig, @transient var dedupEngine: DedupEngine = null)
                           (implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  private val duplicateCounter: AtomicInteger = new AtomicInteger(0)
  private val uniqueCounter: AtomicInteger = new AtomicInteger(0)

  override def open(parameters: Configuration): Unit = {
    if (dedupEngine == null) {
      val redisConnect = new RedisConnect(config)
      dedupEngine = new DedupEngine(redisConnect, config.dedupStore, config.cacheExpirySeconds)
    }

    getRuntimeContext.getMetricGroup.addGroup("deduplication-job")
      .gauge[Int, ScalaGauge[Int]]("duplicate-count", ScalaGauge[Int]( () => duplicateCounter.getAndSet(0)))

    getRuntimeContext.getMetricGroup.addGroup("deduplication-job")
      .gauge[Int, ScalaGauge[Int]]("unique-count", ScalaGauge[Int]( () => uniqueCounter.getAndSet(0)))

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
        ctx.output(config.duplicateEventsOutputTag, event)
        duplicateCounter.getAndIncrement()
      } else {
        logger.info(s"Adding mid: ${event.mid} to Redis")
        dedupEngine.storeChecksum(event.mid)
        event.markSuccess()
        ctx.output(config.uniqueEventsOutputTag, event)
        uniqueCounter.getAndIncrement()
      }
    } else {
      event.markSuccess()
      ctx.output(config.uniqueEventsOutputTag, event)
      uniqueCounter.getAndIncrement()
    }
  }

  def isDuplicateCheckRequired(event: Event): Boolean = {
    config.includedProducersForDedup.contains(event.producerId())
  }
}
