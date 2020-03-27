package org.ekstep.dp.functions


import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.ekstep.dp.task.DeduplicationConfig
import org.slf4j.{Logger, LoggerFactory}

import scala.collection.mutable

class ExtractionFunction(config: DeduplicationConfig)(implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]]) extends ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]] {
  val logger: Logger = LoggerFactory.getLogger(classOf[ExtractionFunction])
  lazy val rawEventOutPut: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "raw-events")
  lazy val failedEventsOutPut: OutputTag[util.Map[String, AnyRef]] = new OutputTag[util.Map[String, AnyRef]](id = "failed-events")
  val gson = new Gson();

  override def processElement(batchEvent: util.Map[String, AnyRef], context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context, collector: Collector[util.Map[String, AnyRef]]): Unit = {
    val events = getEventsList(batchEvent)
    events.toArray().foreach(event => {
      val eventJson = gson.toJson(event)
      val eventSize = eventJson.getBytes("UTF-8").length;
      val data = gson.fromJson(eventJson, (new util.LinkedHashMap[String, AnyRef]()).getClass)
      if (eventSize > config.rawEventSize) {
        context.output(failedEventsOutPut, data)
      } else {
        context.output(rawEventOutPut, data)
      }
    })
  }

  def getEventsList(event: util.Map[String, AnyRef]): util.ArrayList[String] = {
    val gson = new Gson();
    val events = event.get("events")
    gson.fromJson(gson.toJson(events), (new util.ArrayList[String]()).getClass)
  }
}
