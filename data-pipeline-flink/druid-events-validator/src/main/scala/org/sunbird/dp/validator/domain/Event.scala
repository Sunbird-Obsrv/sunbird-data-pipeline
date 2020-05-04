package org.sunbird.dp.validator.domain

import java.util

import org.sunbird.dp.core.domain.Events

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  private val jobName = "DruidValidator"
  
  def markValidationSuccess(): Unit = {
    this.updateFlags("dv_processed", true)
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

}
