package org.sunbird.dp.functions

import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.ekstep.dp.cache.{DedupEngine, RedisConnect}
import org.slf4j.LoggerFactory
import org.sunbird.dp.task.DeduplicationConfig

class DeduplicationFunction(config: DeduplicationConfig)(implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]])
  extends ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]] {

  private[this] val logger = LoggerFactory.getLogger(classOf[DeduplicationFunction])

  lazy val duplicateEventOutput: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "duplicate-events")
  lazy val uniqueEventOuput: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "unique-events")

  lazy val redisConnect = new RedisConnect(config)
  lazy val dedupEngine = new DedupEngine(redisConnect, config.dedupStore, config.cacheExpirySeconds)

  override def processElement(
                               event: util.Map[String, AnyRef],
                               context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                               out: Collector[util.Map[String, AnyRef]]): Unit = {

    if (config.isDuplicationCheckRequired) {
      val msgId = getMsgIdentifier(event)
      if (!dedupEngine.isUniqueEvent(msgId)) {
        logger.info(s"Duplicate Event message id: ${msgId}")
        context.output(duplicateEventOutput, event)
      } else {
        logger.info(s"Adding mid: ${msgId} to Redis")
        dedupEngine.storeChecksum(msgId)
        context.output(uniqueEventOuput, event)
      }
    } else {
      context.output(uniqueEventOuput, event)
    }
  }

  def getMsgIdentifier(event: util.Map[String, AnyRef]): String = {
    val gson = new Gson();
    val params = event.get("params")
    gson.fromJson(gson.toJson(params), (new util.LinkedHashMap[String, AnyRef]()).getClass).get("msgid").toString
  }
}
