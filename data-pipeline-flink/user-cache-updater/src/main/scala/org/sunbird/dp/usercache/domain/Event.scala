package org.sunbird.dp.denorm.domain

import java.util

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.sunbird.dp.core.domain.{Events, EventsPath}

import scala.collection.JavaConverters._
import scala.collection.mutable.Map

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  private[this] val df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC
  private[this] val df2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZoneUTC()
  private val jobName = "PipelinePreprocessor"

  override def kafkaKey(): String = {
    did()
  }



}
