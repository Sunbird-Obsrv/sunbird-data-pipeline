package org.ekstep.dp.functions


import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.ekstep.dp.task.DeduplicationConfig
import org.slf4j.{Logger, LoggerFactory}

class ExtractionFunction(config: DeduplicationConfig)(implicit val eventTypeInfo: TypeInformation[AnyRef]) extends ProcessFunction[Map[String, AnyRef], Map[String, AnyRef]] {
  val logger: Logger = LoggerFactory.getLogger(classOf[ExtractionFunction])
  //implicit val feventTypeInfo: TypeInformation[AnyRef]
  lazy val rawEventOutPut: OutputTag[AnyRef] = new OutputTag[AnyRef](id = "raw-events")
  lazy val failedEventsOutPut: OutputTag[AnyRef] = new OutputTag[AnyRef](id = "failed-events")
  val gson = new Gson();

  override def processElement(batchEvent: Map[String, AnyRef], context: ProcessFunction[Map[String, AnyRef], Map[String, AnyRef]]#Context, collector: Collector[Map[String, AnyRef]]): Unit = {
    val events = batchEvent.get("events")
    events.foreach(event => {
      context.output(rawEventOutPut, event)
      if (gson.toJson(event).getBytes == config.rawEventSize) {
        context.output(failedEventsOutPut, event)
      } else {
        context.output(rawEventOutPut, event)
      }
    })
  }

  //  def updateEvent(event: Event, mapfields: Map[String, String]): Unit = {
  //    event.markDuplicate()
  //  }

  //  def getSyncTS(batchEvent: Map[String, AnyRef]):Long ={
  //
  //    1000
  //  }
}
