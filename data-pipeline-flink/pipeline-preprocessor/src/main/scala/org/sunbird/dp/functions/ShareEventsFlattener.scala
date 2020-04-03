package org.sunbird.dp.functions

import java.util
import java.util.UUID

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.sunbird.dp.domain.{Actor, EData, Event, Object, ShareEvent}
import org.sunbird.dp.task.PipelinePreprocessorConfig

class ShareEventsFlattener(config: PipelinePreprocessorConfig)
                          (implicit val eventTypeInfo: TypeInformation[Event]) extends ProcessFunction[Event, Event] {
  override def processElement(shareEvent: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]): Unit = {
    shareEvent.edataItems().forEach(items => {
      val version = items.get("ver").asInstanceOf[String]
      val identifier = items.get("id").asInstanceOf[String]
      val contentType = items.get("type").asInstanceOf[String]
      val paramsList = Option(items.get("params").asInstanceOf[util.ArrayList[Map[String, AnyRef]]])
      paramsList.getOrElse(new util.ArrayList[util.Map[String, AnyRef]]()).forEach(param => {
        val transfers = param.asInstanceOf[util.Map[String, AnyRef]].get("transfers").toString.toDouble
        val size = param.asInstanceOf[util.Map[String, AnyRef]].get("size").toString.toDouble
        val edataType = if (transfers == 0 || transfers <= 0) "download" else "import"
        val shareItemEvent = generateShareItemEvents(shareEvent, Object(id = identifier, ver = version, `type` = contentType, Some(Map("l1" -> shareEvent.objectID()))), edataType, size)
        context.output(config.shareItemEventOutTag, new Gson().toJson(shareItemEvent))
      })
    })
    shareEvent.markSuccess("share_event_flatten")
    context.output(config.primaryRouteEventsOutputTag, shareEvent)
  }
  def generateShareItemEvents(event: Event,
                              eventObj: Object,
                              edataType: String,
                              paramSize: Double
                             ): ShareEvent = {
    ShareEvent(
      Actor(event.actorId(), event.actorType()),
      "SHARE_ITEM",
      EData(event.edataDir, event.edataType(), paramSize),
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
