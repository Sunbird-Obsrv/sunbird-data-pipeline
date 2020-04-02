package org.sunbird.dp.domain

import java.util

class Event(eventMap: util.Map[String, AnyRef]) extends Events(eventMap) {

  private val jobName = "DruidValidator"

  def markDuplicate(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dv_processed", false)
    telemetry.add("flags.dv_duplicate_event", true)
  }

  def markSuccess(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dv_processed", true)
  }

  def markSkipped(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dv_skipped", true)
  }

  def markValidationFailure(errorMsg: String): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dv_processed", false)
    telemetry.addFieldIfAbsent("metadata", new util.HashMap[String, AnyRef])
    if (null != errorMsg) {
      telemetry.add("metadata.dv_error", errorMsg)
      telemetry.add("metadata.src", jobName)
    }
  }

  def isSummaryEvent: Boolean = eid != null && eid == "ME_WORKFLOW_SUMMARY"

  def isSearchEvent: Boolean = "SEARCH".equalsIgnoreCase(eid)

  def isLogEvent: Boolean = "LOG".equalsIgnoreCase(eid)

  def isErrorEvent: Boolean = "ERROR".equalsIgnoreCase(eid)

}
