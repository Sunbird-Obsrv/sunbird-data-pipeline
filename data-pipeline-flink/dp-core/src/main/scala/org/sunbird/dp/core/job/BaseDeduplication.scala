package org.sunbird.dp.core.job

import java.util

import com.google.gson.Gson
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
      val updatedEvent = updateFlag(event, flagName, value = true)
      context.output(duplicateOutputTag, updatedEvent)
    } else {
      if (key != null) {
        logger.info(s"Adding key: $key to Redis")
        deDupEngine.storeChecksum(key)
        metrics.incCounter(uniqueEventMetricCount)
      }
      logger.info(s"Pushing event to further process, key is: $key")
      val updatedEvent = updateFlag(event, flagName, value = false)
      context.output(successOutputTag, updatedEvent)
    }
  }

  def updateFlag[T](event: T, flagName: String, value: Boolean): T = {
    val flags: util.HashMap[String, Boolean] = new util.HashMap[String, Boolean]()
    flags.put(flagName, value)
    if (event.isInstanceOf[Events]) {
      event.asInstanceOf[Events].updateFlags(flagName, value)
    }
    if (event.isInstanceOf[String]) {
      val upda = event.toString
      var event2 = new Gson().fromJson(upda, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]]
      event2.put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
      new Gson().toJson(event2).asInstanceOf[T]
//      val k = new Gson().fromJson(event.toString, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]]
//        .put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
//      k.asInstanceOf[T]

    } else {
      event.asInstanceOf[util.Map[String, AnyRef]].put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
      event.asInstanceOf[T]
    }
  }

  def deduplicationMetrics: List[String] = {
    List(uniqueEventMetricCount, duplicateEventMetricCount)
  }
}
