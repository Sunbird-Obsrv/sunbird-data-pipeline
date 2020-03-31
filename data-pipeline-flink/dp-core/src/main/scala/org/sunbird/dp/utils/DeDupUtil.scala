package org.sunbird.dp.utils

import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.DedupEngine

trait BaseDeDup {
  def deDup[T](key: String, event: T, deDupEngine: DedupEngine, context:ProcessFunction[T, T], successOutPutTag: OutputTag[T], deDupOutput: OutputTag[T]): Unit = ???
}

object DeDupUtil extends BaseDeDup {
  private[this] val logger = LoggerFactory.getLogger(classOf[BaseDeDup])

  def deDup[T](key: String,
               event: T,
               deDupEngine: DedupEngine,
               context: ProcessFunction[T, T]#Context,
               successOutPutTag: OutputTag[T],
               deDupOutput: OutputTag[T]
              ): Unit = {

    if (null != key && !deDupEngine.isUniqueEvent(key)) {
      logger.info(s"Duplicate Event message id is found: ${key}")
      context.output(deDupOutput, event)
    } else {
      if (key != null) {
        logger.info(s"Adding key: ${key} to Redis")
        deDupEngine.storeChecksum(key)
      }
      logger.info(s"Pushing event to further process, key is: ${key}")
      context.output(successOutPutTag, event)
    }
  }
}
