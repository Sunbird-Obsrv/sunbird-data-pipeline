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
  private lazy val gson = new Gson()

  def deDup[T, R](key: String,
                  event: T,
                  context: ProcessFunction[T, R]#Context,
                  successOutputTag: OutputTag[R],
                  duplicateOutputTag: OutputTag[R],
                  flagName: String
                 )(implicit deDupEngine: DedupEngine, metrics: Metrics): Unit = {

    if (null != key && !deDupEngine.isUniqueEvent(key)) {
      logger.debug(s"Event with mid: $key is duplicate")
      metrics.incCounter(duplicateEventMetricCount)
      context.output(duplicateOutputTag, updateFlag[T, R](event, flagName, value = true))
    } else {
      if (key != null) {
        logger.debug(s"Adding mid: $key to Redis")
        deDupEngine.storeChecksum(key)
      }
      metrics.incCounter(uniqueEventMetricCount)
      logger.debug(s"Pushing the event with mid: $key for further processing")
      context.output(successOutputTag, updateFlag[T, R](event, flagName, value = false))
    }
  }

  def updateFlag[T, R](event: T, flagName: String, value: Boolean): R = {
    val flags: util.HashMap[String, Boolean] = new util.HashMap[String, Boolean]()
    flags.put(flagName, value)
    if (event.isInstanceOf[Events]) {
      event.asInstanceOf[Events].updateFlags(flagName, value)
      event.asInstanceOf[R]
    } else if (event.isInstanceOf[String]) {
      val eventMap = gson.fromJson(event.toString, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]]
      eventMap.put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
      eventMap.asInstanceOf[R]
    } else {
      event.asInstanceOf[util.Map[String, AnyRef]].put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
      event.asInstanceOf[R]
    }
  }

  def deduplicationMetrics: List[String] = {
    List(uniqueEventMetricCount, duplicateEventMetricCount)
  }
}
