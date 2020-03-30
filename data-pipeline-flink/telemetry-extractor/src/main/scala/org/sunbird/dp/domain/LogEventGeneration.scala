package org.sunbird.dp.domain

/**
 * Service to Generate a LOG Events to know the number of events are processed
 */

import java.util
import java.util.UUID.randomUUID


case class Actor(id: String, `type`: String)

case class Context(channel: String, env: String, sid: String, did: String, pdata: Pdata, cdata: Seq[AnyRef])

case class Edata(level: String = "INFO", `type`: String, message: String, params: Array[Params])

case class Params(ver: String, events_count: Int, sync_status: String)

case class Pdata(ver: String, pid: String, id: String = "data-pipeline")

case class LogEvent(actor: Actor,
                    eid: String,
                    edata: Edata,
                    ver: String = "3.0",
                    syncts: Long,
                    ets: Long = System.currentTimeMillis(),
                    context: Context,
                    mid: String,
                    `object`: Actor,
                    tags: Seq[AnyRef]
                   )

/**
 * Method to Generate the LOG Event to Determine the Number of events has extracted.
 */
object LogEventGeneration {
  def generate(totalEvents: Int, batchEvents: util.Map[String, AnyRef]): LogEvent = {
    LogEvent(
      actor = Actor("sunbird.telemetry", "telemetry-sync"),
      eid = "LOG",
      edata = Edata(level = "INFO", "telemetry_audit", message = "telemetry sync", Array(Params("3.0", totalEvents, "SUCCESS"),Params("3.0", totalEvents, "SUCCESS"))),
      syncts = System.currentTimeMillis(),
      ets = System.currentTimeMillis(),
      context = Context(channel = "in.sunbird", env = "data-pipeline",
        sid = randomUUID().toString,
        did = randomUUID().toString,
        pdata = Pdata("3.0", "telemetry-extractor", "data-pipeline"),
        cdata = null),
      mid = randomUUID().toString,
      `object` = Actor("sunbird.telemetry", "event"),
      tags = null)
  }
}
