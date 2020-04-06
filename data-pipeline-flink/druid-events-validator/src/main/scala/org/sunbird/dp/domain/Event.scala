package org.sunbird.dp.domain

import java.util

class Event(eventMap: util.Map[String, AnyRef], partition: Integer) extends Events(eventMap, partition) {

  private val jobName = "DruidValidator"

  def markValidationSuccess(): Unit = {
    this.updateFlags("dv_processed", true)
  }

  def markSkippedValidation(): Unit = {
    this.updateFlags("dv_validation_skipped", true)
  }

  def markSkippedDedup(): Unit = {
    this.updateFlags("dv_dedup_skipped", true)
  }

  def markValidationFailure(errorMsg: String): Unit = {
    this.updateFlags("dv_processed", false)
    this.updateFlags("dv_validation_failed", true)
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
