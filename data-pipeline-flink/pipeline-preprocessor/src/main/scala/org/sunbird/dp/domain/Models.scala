package org.sunbird.dp.domain

import java.util

case class Actor(id: String, `type`: String)

case class Context(channel: String, env: String, sid: String, did: String, pdata: Pdata, cdata: Seq[AnyRef])

case class EData(dir:String, `type`:String, size:Double)

case class Params(ver: String, events_count: Int, sync_status: String)

case class Pdata(ver: String, pid: String, id: String = "data-pipeline")

case class Object(id: String, ver: String, `type`: String, rollup: Option[Map[String, String]])

case class ShareEvent(actor: Actor,
                      eid: String,
                      edata: EData,
                      ver: String = "3.0",
                      syncts: Long,
                      ets: Long = System.currentTimeMillis(),
                      context: util.Map[String, AnyRef],
                      mid: String,
                      `object`: Object,
                      tags: Seq[AnyRef]
                     )