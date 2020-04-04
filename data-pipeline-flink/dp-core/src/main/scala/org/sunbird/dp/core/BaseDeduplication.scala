package org.sunbird.dp.core

import java.util

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.DedupEngine
import org.sunbird.dp.domain.Events

trait BaseDeduplication {

  private[this] val logger = LoggerFactory.getLogger(classOf[BaseDeduplication])

  def deDup[T](key: String,
               event: T,
               context: ProcessFunction[T, T]#Context,
               successOutputTag: OutputTag[T],
               duplicateOutputTag: OutputTag[T],
               flagName: String
              )(implicit deDupEngine: DedupEngine): Unit = {

    if (null != key && !deDupEngine.isUniqueEvent(key)) {
      logger.info(s"Duplicate Event message id is found: $key")
      context.output(duplicateOutputTag, markDuplicate(event, flagName))
    } else {
      if (key != null) {
        logger.info(s"Adding key: $key to Redis")
        deDupEngine.storeChecksum(key)
      }
      logger.info(s"Pushing event to further process, key is: $key")
      context.output(successOutputTag, event)
    }
  }

  def markDuplicate[T](event: T, flagName: String): T = {
    if (event.isInstanceOf[Events]) {
      event.asInstanceOf[Events].updateFlags("flags." + flagName, true)
    }
    else {
      val flags: util.HashMap[String, Boolean] = new util.HashMap[String, Boolean]()
      flags.put(flagName, true)
      event.asInstanceOf[util.Map[String, AnyRef]].put("flags", flags.asInstanceOf[util.HashMap[String, AnyRef]])
    }
    event.asInstanceOf[T]
  }
}
