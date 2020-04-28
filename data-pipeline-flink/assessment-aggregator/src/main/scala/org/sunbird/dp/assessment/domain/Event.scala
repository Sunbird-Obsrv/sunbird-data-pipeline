package org.sunbird.dp.assessment.domain

import java.util

import com.google.gson.internal.LinkedTreeMap
import org.sunbird.dp.core.domain.{Events, EventsPath}

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  private val jobName = "AssessmentAggregator"

  def assessmentEts: Long = {
    val ets = telemetry.read[Double]("assessmentTs").get
    ets.asInstanceOf[Double].longValue

  }


  def courseId: String = {
   telemetry.read("courseId").get
  }

  def contentId: String = {
    telemetry.read("contentId").get
  }

  def batchId: String = {
    telemetry.read("batchId").get
  }

  def attemptId: String = {
    telemetry.read("attemptId").get
  }

  def userId: String = {
    telemetry.read("userId").get
  }

  def assessEvents: util.List[LinkedTreeMap[String, AnyRef]] = {
    telemetry.read("events").get
  }

  def markFailed(errorMsg: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
      telemetry.addFieldIfAbsent("metadata", new util.HashMap[String, AnyRef])
    telemetry.add("metadata.validation_error", errorMsg)
    telemetry.add("metadata.src", jobName)

  }

}
