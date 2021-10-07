package org.sunbird.dp.assessment.domain

import java.util
import org.sunbird.dp.core.domain.{Events, EventsPath}

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  private val jobName = "AssessmentAggregator"

  def assessmentEts: Long = {
    val ets = telemetry.read[Long]("assessmentTs").get
    ets.longValue()

  }

  def courseId: String = {
   telemetry.read[String]("courseId").get
  }

  def contentId: String = {
    telemetry.read[String]("contentId").get
  }

  def batchId: String = {
    telemetry.read[String]("batchId").get
  }

  def attemptId: String = {
    telemetry.read[String]("attemptId").get
  }

  def userId: String = {
    telemetry.read[String]("userId").get
  }

  def assessEvents: util.ArrayList[util.Map[String, AnyRef]] = {
    telemetry.read[util.ArrayList[util.Map[String, AnyRef]]]("events").getOrElse(null)
  }

  def markFailed(errorMsg: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.addFieldIfAbsent("metadata", new util.HashMap[String, AnyRef])
    telemetry.add("metadata.validation_error", errorMsg)
    telemetry.add("metadata.src", jobName)
  }

}
