package org.sunbird.dp.cbpreprocessor.domain

import java.util

case class ActorObject(id: String, `type`: String)

case class Context(channel: String,
                   env: String,
                   sid: String,
                   did: String,
                   pdata: util.Map[String, AnyRef],
                   cdata: util.ArrayList[util.Map[String, AnyRef]],
                   rollup: util.Map[String, AnyRef])

case class EData(dir: String, `type`: String, size: Double)

case class Rollup(l1: String)

case class EventObject(id: String, ver: String, `type`: String, rollup: Rollup)

case class CBObject(id: String, ver: String, `type`: String, name: String, org: String, sub_type: String, rollup: Rollup)

case class CBData(id: String,
                  name: String,
                  status: String,
                  users: util.ArrayList[util.Map[String, AnyRef]])
