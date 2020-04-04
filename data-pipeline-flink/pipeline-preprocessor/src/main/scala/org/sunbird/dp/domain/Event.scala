package org.sunbird.dp.domain

import java.util

import org.apache.commons.lang3.StringUtils
import org.joda.time.format.DateTimeFormat
import org.sunbird.dp.task.PipelinePreprocessorConfig

class Event(eventMap: util.Map[String, AnyRef]) extends Events(eventMap) {

  private[this] val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC
  private val jobName = "PipelinePreprocessor"

  def schemaName: String = {
    if (eid != null) s"${eid.toLowerCase}.json"
    else "envelope.json"
  }

  def updateActorId(actorId: String): Unit = {
    telemetry.add("actor.id", actorId)
  }

  def correctDialCodeKey(): Unit = {
    val dialcodes = telemetry.read("edata.filters.dialCodes")
    if (dialcodes != null && dialcodes.value != null) {
      telemetry.add("edata.filters.dialcodes", dialcodes.value)
      telemetry.add("edata.filters.dialCodes", null)
    }
  }

  def markValidationFailure(errorMsg: String): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags.pp_validation_processed", false)
    telemetry.addFieldIfAbsent("metadata", new util.HashMap[String, AnyRef])
    if (null != errorMsg) {
      telemetry.add("metadata.validation_error", errorMsg)
      telemetry.add("metadata.src", jobName)
    }
  }

  def markSkipped(flagName: String): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add(s"flag.$flagName", true)
  }

  def markSuccess(flagName: String): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add(s"flags.$flagName", true)
    telemetry.add("type", "events")
  }

  def updateDefaults(config: PipelinePreprocessorConfig): Unit = {
    val channelString = telemetry.read[String]("context.channel").value
    val channel = StringUtils.deleteWhitespace(channelString)
    if (channel == null || channel.isEmpty) {
      telemetry.addFieldIfAbsent("context", new util.HashMap[String, AnyRef])
      telemetry.add("context.channel", config.defaultChannel)
    }
    val atTimestamp = telemetry.getAtTimestamp
    val strSyncts = telemetry.getSyncts
    if (null == atTimestamp && null == strSyncts) {
      val syncts = System.currentTimeMillis
      telemetry.addFieldIfAbsent("syncts", syncts)
      telemetry.addFieldIfAbsent("@timestamp", dateFormatter.print(syncts))
    }
    else if (atTimestamp != null) telemetry.addFieldIfAbsent("syncts", dateFormatter.parseMillis(atTimestamp))
    else if (strSyncts != null) telemetry.addFieldIfAbsent("@timestamp", strSyncts)
  }

  def edataDir: String = telemetry.read[String]("edata.dir").value

  def eventSyncTs: Long = telemetry.read[Long]("syncts").value.asInstanceOf[Number].longValue()

  def eventTags: Seq[AnyRef] = telemetry.read[Seq[AnyRef]]("tags").value

  def cdata: util.ArrayList[util.Map[String, AnyRef]] = telemetry.read[util.ArrayList[util.Map[String, AnyRef]]]("context.cdata").value

  def eventPData: util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]]("context.pdata").value

  def sessionId: String = telemetry.read[String]("context.sid").value.toString

  def env: String = telemetry.read[String]("context.env").value

  def rollup: util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]]("context.rollup").value


}
