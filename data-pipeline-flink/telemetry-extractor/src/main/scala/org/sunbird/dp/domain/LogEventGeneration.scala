package org.sunbird.dp.domain

import java.util

case class Actor(id: String = "sunbird.telemetry", `type`: String = "telemetry-sync")

case class Context(channel: String, env: String, sid: String, did: String, pdata: Pdata, cdata: Seq[AnyRef])

case class Edata(level: String ="INFO", `type`: String = "telemetry_audit", message: String = "Telemetry Sync", pageid: String, params: Seq[Params])

case class Params(consumer_id: String, ver: String, events_count: Int, sync_status: String)

case class Pdata(ver: String, pid: String, id: String = "pipeline")

case class LogEvent(actor: Option[Actor], eid: String = "LOG", edata: Option[Edata], ver: String = "3.0", metadata: Option[String], syncts: Long = System.currentTimeMillis(), `@timestamp`: Option[String], ets: Long = System.currentTimeMillis(), context: Option[Context], mid: Option[String], `object`: Option[Actor], tags: Option[Seq[AnyRef]])


object LogEventGeneration {
  def generate(eventMap: util.Map[String, AnyRef]): LogEvent = {
    LogEvent(None, "LOG", None, "3.0", None, 97809, None, 839428, None, None, None, None)
  }


}
