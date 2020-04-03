package org.sunbird.dp.domain

import java.util

class Event(eventMap: util.Map[String, AnyRef]) extends Events(eventMap) {

  private val jobName = "DruidValidator"

  def markValidationSuccess(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dv_processed", true)
  }

  def markSkippedValidation(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dv_validation_skipped", true)
  }

  def markSkippedDedup(): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.dv_dedup_skipped", true)
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

  def isSummaryEvent: Boolean = "ME_WORKFLOW_SUMMARY".equalsIgnoreCase(eid)

  def isSearchEvent: Boolean = "SEARCH".equalsIgnoreCase(eid)

  def isLogEvent: Boolean = "LOG".equalsIgnoreCase(eid)

  def isErrorEvent: Boolean = "ERROR".equalsIgnoreCase(eid)

}
