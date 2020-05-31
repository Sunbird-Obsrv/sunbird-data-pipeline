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

  def deDup[T, R](key: String,
                  event: T,
                  context: ProcessFunction[T, R]#Context,
                  successOutputTag: OutputTag[R],
                  duplicateOutputTag: OutputTag[R],
                  flagName: String
                 )(implicit deDupEngine: DedupEngine, metrics: Metrics): Unit = {

    if (null != key && !deDupEngine.isUniqueEvent(key)) {
      logger.info(s"Duplicate Event message id is found: $key")
      metrics.incCounter(duplicateEventMetricCount)
      val data = updateFlag[T, R](event, flagName, value = true)
      println("data" + data)
      context.output(duplicateOutputTag, data)
    } else {
      if (key != null) {
        logger.info(s"Adding key: $key to Redis")
        deDupEngine.storeChecksum(key)
        metrics.incCounter(uniqueEventMetricCount)
      }
      logger.info(s"Pushing event to further process, key is: $key")
      val d = updateFlag[T, R](event, flagName, value = false)
      println("d" + d)
      context.output(successOutputTag, d)
    }
  }

  def updateFlag[T, R](event: T, flagName: String, value: Boolean): R = {
    val flags: util.HashMap[String, Boolean] = new util.HashMap[String, Boolean]()
    flags.put(flagName, value)
    if (event.isInstanceOf[Events]) {
      event.asInstanceOf[Events].updateFlags(flagName, value)
    }
    if (event.isInstanceOf[String]) {
      val evntdata = new Gson().fromJson(event.toString, new util.LinkedHashMap[String, AnyRef]().getClass).asInstanceOf[util.Map[String, AnyRef]]
      evntdata.asInstanceOf[util.Map[String, AnyRef]].put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
      evntdata.asInstanceOf[R]
    } else {
      event.asInstanceOf[util.Map[String, AnyRef]].put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
      event.asInstanceOf[R]
    }
  }

  def deduplicationMetrics: List[String] = {
    List(uniqueEventMetricCount, duplicateEventMetricCount)
  }
}
