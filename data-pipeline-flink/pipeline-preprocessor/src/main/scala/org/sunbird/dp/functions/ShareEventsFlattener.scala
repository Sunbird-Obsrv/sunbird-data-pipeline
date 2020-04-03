package org.sunbird.dp.functions

import java.util
import java.util.UUID

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.sunbird.dp.domain.{Actor, EData, Event, Object, ShareEvent}
import org.sunbird.dp.task.PipelinePreprocessorConfig

class ShareEventsFlattener(config: PipelinePreprocessorConfig)
                          (implicit val eventTypeInfo: TypeInformation[Event]) extends ProcessFunction[Event, Event] {
  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              out: Collector[Event]): Unit = {
    event.edataItems().forEach(items => {
      val version = items.asInstanceOf[util.Map[String, AnyRef]].get("ver").asInstanceOf[String]
      val identifier = items.asInstanceOf[util.Map[String, AnyRef]].get("id").asInstanceOf[String]
      val contentType = items.asInstanceOf[util.Map[String, AnyRef]].get("type").asInstanceOf[String]

      val paramsList = Option(items.asInstanceOf[util.Map[String, AnyRef]].get("params").asInstanceOf[util.ArrayList[Map[String, AnyRef]]])

      paramsList.getOrElse(new util.ArrayList[util.Map[String, AnyRef]]()).forEach(param => {
        val transfers = param.asInstanceOf[util.Map[String, AnyRef]].get("transfers").toString.toDouble
        val size = param.asInstanceOf[util.Map[String, AnyRef]].get("size").toString.toDouble
        val edataType = if (transfers == 0 || transfers <= 0) "download" else "import"
        val shareItemEvent = generateShareItemEvents(event,
          Object(id = identifier, ver = version, `type` = contentType, None),
          edataType,
          size
        )
        println(shareItemEvent)
      })
    })
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
      context = event.eventContext,
      mid = event.mid() + "-" +UUID.randomUUID().toString,
      eventObj,
      event.eventTags
    )

  }


}
