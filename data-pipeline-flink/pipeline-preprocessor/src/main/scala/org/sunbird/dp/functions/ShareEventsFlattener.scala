package org.sunbird.dp.functions

import java.util
import java.util.UUID

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.{Actor, EData, Event, Object, Rollup, ShareEvent}
import org.sunbird.dp.task.PipelinePreprocessorConfig

class ShareEventsFlattener(config: PipelinePreprocessorConfig)
                          (implicit val eventTypeInfo: TypeInformation[Event]) extends ProcessFunction[Event, Event] {
  private[this] val logger = LoggerFactory.getLogger(classOf[ShareEventsFlattener])

  override def processElement(shareEvent: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]): Unit = {
    shareEvent.edataItems().forEach(items => {
      val version = items.get("ver").asInstanceOf[String]
      val identifier = items.get("id").asInstanceOf[String]
      val contentType = items.get("type").asInstanceOf[String]
      val paramsList = Option(items.get("params").asInstanceOf[util.ArrayList[Map[String, AnyRef]]])
      if (paramsList.size > 0) {
        paramsList.getOrElse(new util.ArrayList[util.Map[String, AnyRef]]()).forEach(param => {
          val transfers = param.asInstanceOf[util.Map[String, AnyRef]].get("transfers").toString.toDouble
          val size = param.asInstanceOf[util.Map[String, AnyRef]].get("size").toString.toDouble
          val edataType = if (transfers == 0 || transfers <= 0) "download" else "import"
          val shareItemEvent = generateShareItemEvents(shareEvent, Object(id = identifier, ver = version, `type` = contentType, rollup = Rollup(shareEvent.objectID())), Some(edataType), Some(size))
          context.output(config.shareItemEventOutTag, new Gson().toJson(shareItemEvent))
        })
      } else {
        val shareItemEvent = generateShareItemEvents(shareEvent, Object(id = identifier, ver = version, `type` = contentType, rollup = Rollup(shareEvent.objectID())), edataType = Some(shareEvent.edataType()), paramSize = None)
        context.output(config.shareItemEventOutTag, new Gson().toJson(shareItemEvent))
      }

    })
    shareEvent.markSuccess(config.SHARE_EVENTS_FLATTEN_FLAG_NAME)
    context.output(config.primaryRouteEventsOutputTag, shareEvent)
  }

  def generateShareItemEvents(event: Event,
                              eventObj: Object,
                              edataType: Option[String],
                              paramSize: Option[Double]
                             ): ShareEvent = {
    ShareEvent(
      Actor(event.actorId(), event.actorType()),
      "SHARE_ITEM",
      EData(event.edataDir, edataType.orNull, paramSize.getOrElse(0)),
      ver = "3.0",
      syncts = event.eventSyncTs.asInstanceOf[Number].longValue,
      ets = event.ets(),
      context = org.sunbird.dp.domain.Context(event.channel(), event.env, event.sessionId, event.did(), event.eventPData, event.cdata, event.rollup),
      mid = event.mid() + "-" + UUID.randomUUID().toString,
      eventObj,
      event.eventTags
    )
  }
}
