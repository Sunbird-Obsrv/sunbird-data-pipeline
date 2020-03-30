package org.sunbird.dp.functions

import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.task.ExtractionConfig

class DeduplicationFunction(config: ExtractionConfig, @transient var dedupEngine: DedupEngine = null)(implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]])
  extends ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  /**
   * Tag to hold all duplicate batch events based on the msgId
   * (Note: De-dup validation only if batch event has "msgId" )
   */
  lazy val duplicateEventOutput: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "duplicate-events")

  /**
   * OutPutTag to hold all unique batch events.
   */
  lazy val uniqueEventOutput: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "unique-events")

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

  override def processElement(
                               event: util.Map[String, AnyRef],
                               context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                               out: Collector[util.Map[String, AnyRef]]
                             ): Unit = {

    if (config.isDuplicationCheckRequired) {
      val msgId = getMsgIdentifier(event)
      if (null != msgId) {
        if (!dedupEngine.isUniqueEvent(msgId)) {
          logger.info(s"Duplicate Event message id: ${msgId}")
          context.output(duplicateEventOutput, event)
        } else {
          logger.info(s"Adding mid: ${msgId} to Redis")
          dedupEngine.storeChecksum(msgId)
          context.output(uniqueEventOutput, event)
        }
      } else {
        context.output(uniqueEventOutput, event)
      }
    } else {
      context.output(uniqueEventOutput, event)
    }
  }

  def getMsgIdentifier(event: util.Map[String, AnyRef]): String = {
    val gson = new Gson();
    val paramsObj = event.get("params")
    if (null != paramsObj) {
      val messageId = gson.fromJson(gson.toJson(paramsObj), (new util.LinkedHashMap[String, AnyRef]()).getClass).get("msgid")
      if (null != messageId)
        messageId.toString
      else null
    }
    else null
  }
}
