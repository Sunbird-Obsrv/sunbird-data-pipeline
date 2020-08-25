package org.sunbird.dp.preprocessor.functions

import java.util
import java.util.UUID

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.preprocessor.domain.{ActorObject, EData, Event, EventObject, Rollup, ShareEvent}
import org.sunbird.dp.preprocessor.domain.{Context => EventContext}
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig

import scala.util.Try

class ShareEventsFlattenerFunction(config: PipelinePreprocessorConfig)
                                  (implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[ShareEventsFlattenerFunction])

  override def processElement(shareEvent: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

    shareEvent.edataItems().forEach(items => {
      val version = items.get("ver").asInstanceOf[String]
      val identifier = items.get("id").asInstanceOf[String]
      val contentType = items.get("type").asInstanceOf[String]
      val paramsList = Option(items.get("params").asInstanceOf[util.ArrayList[Map[String, AnyRef]]])

      if (paramsList.size > 0) {
        paramsList.getOrElse(new util.ArrayList[util.Map[String, AnyRef]]()).forEach(param => {
          val shareEventParams = param.asInstanceOf[util.Map[String, Any]]
          val transfers = Try(shareEventParams.get("transfers").toString.toDouble).getOrElse(0d)
          val size = Try(shareEventParams.get("size").toString.toDouble).getOrElse(0d)
          val edataType = if (transfers == 0) "download" else "import"
          val shareItemEvent = generateShareItemEvents(shareEvent, EventObject(id = identifier, ver = version,
            `type` = contentType, rollup = Rollup(shareEvent.objectID())), Some(edataType), Some(size))
          context.output(config.shareItemEventOutputTag, new Event(new Gson().fromJson(shareItemEvent, new util.LinkedHashMap[String, Any]().getClass)))
        })
      } else {
        val shareItemEvent = generateShareItemEvents(shareEvent, EventObject(id = identifier, ver = version,
          `type` = contentType, rollup = Rollup(shareEvent.objectID())), edataType = Some(shareEvent.edataType()))
        context.output(config.shareItemEventOutputTag, new Event(new Gson().fromJson(shareItemEvent, new util.LinkedHashMap[String, Any]().getClass)))
      }
      metrics.incCounter(config.shareItemEventsMetircsCount)
    })

    shareEvent.markSuccess(config.SHARE_EVENTS_FLATTEN_FLAG_NAME)
    context.output(config.primaryRouteEventsOutputTag, shareEvent)
  }

  def generateShareItemEvents(event: Event,
                              eventObj: EventObject,
                              edataType: Option[String],
                              paramSize: Option[Double] = None
                             ): String = {
    val shareItemEvent = ShareEvent(
      ActorObject(event.actorId(), event.actorType()),
      "SHARE_ITEM",
      EData(event.edataDir, edataType.orNull, paramSize.getOrElse(0)),
      ver = "3.0",
      syncts = event.eventSyncTs.asInstanceOf[Number].longValue,
      ets = event.ets(),
      context = EventContext(event.channel(), event.env, event.sessionId, event.did(), event.eventPData, event.cdata, event.rollup),
      mid = event.mid() + "-" + UUID.randomUUID().toString,
      eventObj,
      event.eventTags
    )

    new Gson().toJson(shareItemEvent)
  }

  override def metricsList(): List[String] = {
    List(config.shareItemEventsMetircsCount)
  }
}
