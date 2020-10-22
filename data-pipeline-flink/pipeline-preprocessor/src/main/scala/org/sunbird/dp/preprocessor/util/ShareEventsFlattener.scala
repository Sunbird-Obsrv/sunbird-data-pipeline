package org.sunbird.dp.preprocessor.util

import java.util
import java.util.UUID

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.sunbird.dp.core.util.JSONUtil
// import com.google.gson.Gson
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.preprocessor.domain._
import org.sunbird.dp.preprocessor.task.PipelinePreprocessorConfig
import org.sunbird.dp.preprocessor.domain.{Context => EventContext}

import scala.util.Try

class ShareEventsFlattener(config: PipelinePreprocessorConfig) extends java.io.Serializable {

  private val serialVersionUID = 1167435095740381669L
  val objectMapper = new ObjectMapper()

  def flatten(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics) = {

    event.edataItems().forEach(items => {
      val version = items.get("ver").asInstanceOf[String]
      val identifier = items.get("id").asInstanceOf[String]
      val contentType = items.get("type").asInstanceOf[String]
      val paramsList = Option(items.get("params").asInstanceOf[util.ArrayList[Map[String, AnyRef]]])

      if (paramsList.isDefined) {
        paramsList.getOrElse(new util.ArrayList[util.Map[String, AnyRef]]()).forEach(
          param => {
            val shareEventParams = param.asInstanceOf[util.Map[String, Any]]
            val transfers = Try(shareEventParams.get("transfers").toString.toDouble).getOrElse(0d)
            val size = Try(shareEventParams.get("size").toString.toDouble).getOrElse(0d)
            val edataType = if (transfers == 0) "download" else "import"
            val shareItemStr = generateShareItemEvents(event, EventObject(id = identifier, ver = version,
              `type` = contentType, rollup = Rollup(event.objectID())), Some(edataType), Some(size))
            // context.output(config.shareItemEventOutputTag, new Event(new Gson().fromJson(shareItemEvent, new util.LinkedHashMap[String, Any]().getClass)))
            val shareItemEvent = new Event(JSONUtil.deserialize[util.LinkedHashMap[String, Any]](shareItemStr))
            context.output(config.shareItemEventOutputTag, shareItemEvent)
            routeShareItemEvents(shareItemEvent, context, metrics)
          }
        )
      } else {
        val shareItemStr = generateShareItemEvents(event, EventObject(id = identifier, ver = version,
          `type` = contentType, rollup = Rollup(event.objectID())), edataType = Some(event.edataType()))
        // context.output(config.shareItemEventOutputTag, new Event(new Gson().fromJson(shareItemEvent, new util.LinkedHashMap[String, Any]().getClass)))
        val shareItemEvent = new Event(JSONUtil.deserialize[util.LinkedHashMap[String, Any]](shareItemStr))
        context.output(config.shareItemEventOutputTag, shareItemEvent)
        routeShareItemEvents(shareItemEvent, context, metrics)
      }
      metrics.incCounter(config.shareItemEventsMetircsCount)
    })

    event.markSuccess(config.SHARE_EVENTS_FLATTEN_FLAG_NAME)
    context.output(config.primaryRouteEventsOutputTag, event)

  }


  def generateShareItemEvents(event: Event, eventObj: EventObject, edataType: Option[String], paramSize: Option[Double] = None): String = {
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

  def routeShareItemEvents(shareItem: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics) = {
    if (config.secondaryEvents.contains("SHARE_ITEM")) {
      context.output(config.denormSecondaryEventsRouteOutputTag, shareItem)
      metrics.incCounter(metric = config.denormSecondaryEventsRouterMetricsCount)
    }
    else {
      context.output(config.denormPrimaryEventsRouteOutputTag, shareItem)
      metrics.incCounter(metric = config.denormPrimaryEventsRouterMetricsCount)
    }
  }

}
