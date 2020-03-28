package org.sunbird.dp.functions

import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.joda.time.format.DateTimeFormat
import org.sunbird.dp.domain.LogEventGeneration
import org.sunbird.dp.task.DeduplicationConfig

class ExtractionFunction(config: DeduplicationConfig)(implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]]) extends ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]] {
  lazy val rawEventOutPut: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "raw-events")
  lazy val failedEventsOutPut: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "failed-events")
  lazy val logEventOutPut: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "log-events")


  override def processElement(batchEvent: util.Map[String, AnyRef], context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context, collector: Collector[util.Map[String, AnyRef]]): Unit = {
    val gson = new Gson();
    val events = getEventsList(batchEvent)
    events.foreach(event => {
      val eventJson = gson.toJson(event)
      val eventSize = eventJson.getBytes("UTF-8").length;
      val eventData = updateEvent(batchEvent, gson.fromJson(eventJson, (new util.HashMap[String, AnyRef]()).getClass))
      if (eventSize > config.rawEventSize) {
        context.output(failedEventsOutPut, eventData)
      } else {
        context.output(rawEventOutPut, eventData)
      }
    })
    context.output(logEventOutPut, gson.fromJson(gson.toJson(LogEventGeneration.generate(batchEvent)), (new util.HashMap[String, AnyRef]()).getClass))
  }

  def getEventsList(event: util.Map[String, AnyRef]): Array[AnyRef] = {
    val gson = new Gson();
    val events = event.get("events")
    gson.fromJson(gson.toJson(events), (new util.ArrayList[String]()).getClass).toArray
  }

  def updateEvent(batchEvent: util.Map[String, AnyRef], event: util.Map[String, AnyRef]): util.Map[String, AnyRef] = {
    val syncTs: Long = batchEvent.get("syncts").asInstanceOf[Number].longValue()
    val timeStamp: String = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC.print(syncTs)
    event.put("syncts", syncTs.asInstanceOf[AnyRef])
    event.put("@timestamp", timeStamp.asInstanceOf[AnyRef])
    event
  }
}
