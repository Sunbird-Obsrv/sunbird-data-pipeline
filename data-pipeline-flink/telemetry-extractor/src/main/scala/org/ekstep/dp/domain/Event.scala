package org.ekstep.dp.domain

import java.util

import org.ekstep.ep.samza.events.domain.Events

class Event(eventMap: util.Map[String, AnyRef]) extends Events(eventMap) {

  def markDuplicate(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dd_processed", false)
    telemetry.add("flags.dd_duplicate_event", true)
  }

  def markSuccess(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dd_processed", true)
    telemetry.add("type", "events")
  }

}
