package org.sunbird.dp.functions

import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.util.Collector
import org.joda.time.format.DateTimeFormat
import org.sunbird.dp.task.ExtractionConfig
import org.sunbird.dp.domain._
import java.util.UUID

class ExtractionFunction(config: ExtractionConfig)(implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]]) extends ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]] {

  /**
   * Method to process the events extraction from the batch
   *
   * @param batchEvent - Batch of telemetry events
   * @param context
   * @param collector
   */
  override def processElement(batchEvent: util.Map[String, AnyRef], context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context, collector: Collector[util.Map[String, AnyRef]]): Unit = {
    val gson = new Gson();
    val eventsList = getEventsList(batchEvent)
    eventsList.foreach(event => {
      val eventJson = gson.toJson(event)
      val eventSize = eventJson.getBytes("UTF-8").length;
      val eventData = updateEvent(batchEvent, gson.fromJson(eventJson, (new util.HashMap[String, AnyRef]()).getClass))
      if (eventSize > config.eventMaxSize) {
        context.output(config.failedEventsOutputTag, eventData)
      } else {
        context.output(config.rawEventsOutputTag, eventData)
      }
    })

    /**
     * Generating Audit events to compute the number of events in the batch.
     */
    context.output(config.logEventsOutputTag, gson.fromJson(gson.toJson(generateAuditEvents(eventsList.length, batchEvent)), (new util.HashMap[String, AnyRef]()).getClass))
  }

  /**
   * Method to get the events from the batch.
   *
   * @param batchEvent - Batch of telemetry event.
   * @return Array[AnyRef] - List of telemetry events.
   */
  def getEventsList(batchEvent: util.Map[String, AnyRef]): Array[AnyRef] = {
    val gson = new Gson();
    gson.fromJson(gson.toJson(batchEvent.get("events")), (new util.ArrayList[String]()).getClass).toArray
  }

  /**
   * Method to update the "SyncTS", "@TimeStamp" fileds of batch events into Events Object
   *
   * @param batchEvent - Batch of Telemetry Events.
   * @param event      - Telemetry Event
   * @return - util.Map[String, AnyRef] Updated Telemetry Event
   */
  def updateEvent(batchEvent: util.Map[String, AnyRef], event: util.Map[String, AnyRef]): util.Map[String, AnyRef] = {
    val syncTs: Long = batchEvent.get("syncts").asInstanceOf[Number].longValue()
    val timeStamp: String = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC.print(syncTs)
    event.put("syncts", syncTs.asInstanceOf[AnyRef])
    event.put("@timestamp", timeStamp.asInstanceOf[AnyRef])
    event
  }

  /**
   * Method to Generate the LOG Event to Determine the Number of events has extracted.
   */
  def generateAuditEvents(totalEvents: Int, batchEvents: util.Map[String, AnyRef]): LogEvent = {
    LogEvent(
      actor = Actor("sunbird.telemetry", "telemetry-sync"),
      eid = "LOG",
      edata = EData(level = "INFO", "telemetry_audit", message = "telemetry sync", Array(Params("3.0", totalEvents, "SUCCESS"), Params("3.0", totalEvents, "SUCCESS"))),
      syncts = System.currentTimeMillis(),
      ets = System.currentTimeMillis(),
      context = org.sunbird.dp.domain.Context(channel = "in.sunbird", env = "data-pipeline",
        sid = UUID.randomUUID().toString,
        did = UUID.randomUUID().toString,
        pdata = Pdata("3.0", "telemetry-extractor", "data-pipeline"),
        cdata = null),
      mid = UUID.randomUUID().toString,
      `object` = Object(UUID.randomUUID().toString, "3.0", "telemetry-events", None),
      tags = null)
  }
}
