package org.sunbird.dp.core.job

import java.util

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.DedupEngine
import org.sunbird.dp.core.domain.Events

trait BaseDeduplication {

  private[this] val logger = LoggerFactory.getLogger(classOf[BaseDeduplication])
  val uniqueEventMetricCount = "unique-event-count"
  val duplicateEventMetricCount = "duplicate-event-count"

  def deDup[T](key: String,
               event: T,
               context: ProcessFunction[T, T]#Context,
               successOutputTag: OutputTag[T],
               duplicateOutputTag: OutputTag[T],
               flagName: String
              )(implicit deDupEngine: DedupEngine, metrics: Metrics): Unit = {

    if (null != key && !deDupEngine.isUniqueEvent(key)) {
      logger.info(s"Duplicate Event message id is found: $key")
      metrics.incCounter(duplicateEventMetricCount)
      context.output(duplicateOutputTag, updateFlag(event, flagName, value = true))
    } else {
      if (key != null) {
        logger.info(s"Adding key: $key to Redis")
        deDupEngine.storeChecksum(key)
        metrics.incCounter(uniqueEventMetricCount)
      }
      logger.info(s"Pushing event to further process, key is: $key")
      context.output(successOutputTag, updateFlag(event, flagName, value = false))
    }
  }

  def updateFlag[T](event: T, flagName: String, value: Boolean): T = {
    if (event.isInstanceOf[Events]) {
      event.asInstanceOf[Events].updateFlags(flagName, value)
    } else {
      val flags: util.HashMap[String, Boolean] = new util.HashMap[String, Boolean]()
      flags.put(flagName, value)
      event.asInstanceOf[util.Map[String, AnyRef]].put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
    }
    event.asInstanceOf[T]
  }

  def deduplicationMetrics: List[String] = {
    List(uniqueEventMetricCount, duplicateEventMetricCount)
  }
}
