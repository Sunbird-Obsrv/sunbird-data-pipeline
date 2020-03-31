package org.sunbird.dp.utils

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.DedupEngine

trait BaseDeduplication {
  def deDup[T](key: String, event: T, deDupEngine: DedupEngine, context:ProcessFunction[T, T],
               successOutputTag: OutputTag[T], duplicateOutputTag: OutputTag[T]): Unit = ???
}

object DedupUtil extends BaseDeduplication {
  private[this] val logger = LoggerFactory.getLogger(classOf[BaseDeduplication])

  def deDup[T](key: String,
               event: T,
               deDupEngine: DedupEngine,
               context: ProcessFunction[T, T]#Context,
               successOutputTag: OutputTag[T],
               duplicateOutputTag: OutputTag[T]
              ): Unit = {

    if (null != key && !deDupEngine.isUniqueEvent(key)) {
      logger.info(s"Duplicate Event message id is found: $key")
      context.output(duplicateOutputTag, event)
    } else {
      if (key != null) {
        logger.info(s"Adding key: $key to Redis")
        deDupEngine.storeChecksum(key)
      }
      logger.info(s"Pushing event to further process, key is: $key")
      context.output(successOutputTag, event)
    }
  }
}
