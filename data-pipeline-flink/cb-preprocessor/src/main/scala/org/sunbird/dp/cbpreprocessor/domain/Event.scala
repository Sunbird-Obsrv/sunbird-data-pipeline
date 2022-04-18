package org.sunbird.dp.cbpreprocessor.domain

import java.util

import org.apache.commons.lang3.StringUtils
import org.joda.time.format.DateTimeFormat
import org.sunbird.dp.core.domain.{Events, EventsPath}
import org.sunbird.dp.cbpreprocessor.task.CBPreprocessorConfig

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  private[this] val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC
  private val jobName = "CBPreprocessor"

  private val CB_OBJECT_TYPE_PATH = s"${EventsPath.EDATA_PATH}.cb_object.type"

  override def kafkaKey(): String = {
    did()
  }

  def schemaName: String = {
    if (eid != null) s"${eid().toLowerCase}.json"
    else "envelope.json"
  }

  def updateActorId(actorId: String): Unit = {
    telemetry.add(EventsPath.ACTOR_ID_PATH, actorId)
  }

  def correctDialCodeKey(): Unit = {
    val dialcodes = telemetry.read(s"${EventsPath.EDTA_FILTERS}.dialCodes").getOrElse("")
    if (!dialcodes.isEmpty) {
      telemetry.add(s"${EventsPath.EDTA_FILTERS}.dialcodes", dialcodes)
      telemetry.add(s"${EventsPath.EDTA_FILTERS}.dialCodes", null)
    }
  }

  def correctDialCodeValue(): Unit = {
    val dialcode = telemetry.read[String](EventsPath.OBJECT_ID_PATH).getOrElse(null)
    if (dialcode != null) telemetry.add(EventsPath.OBJECT_ID_PATH, dialcode.toUpperCase)
  }

  def markValidationFailure(errorMsg: String, flagName: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.add(s"${EventsPath.FLAGS_PATH}.$flagName", false)
    telemetry.addFieldIfAbsent("metadata", new util.HashMap[String, AnyRef])
    if (null != errorMsg) {
      telemetry.add("metadata.validation_error", errorMsg)
      telemetry.add("metadata.src", jobName)
    }
  }

  def markSkipped(flagName: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.add(s"${EventsPath.FLAGS_PATH}.$flagName", true)
  }

  def markSuccess(flagName: String): Unit = {
    telemetry.addFieldIfAbsent(EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.add(s"${EventsPath.FLAGS_PATH}.$flagName", true)
    telemetry.add("type", "events")
  }

  def updateDefaults(config: CBPreprocessorConfig): Unit = {
    val channelString = telemetry.read[String](EventsPath.CONTEXT_CHANNEL_PATH).getOrElse(null)
    val channel = StringUtils.deleteWhitespace(channelString)
    if (channel == null || channel.isEmpty) {
      telemetry.addFieldIfAbsent(EventsPath.CONTEXT_PATH, new util.HashMap[String, AnyRef])
      telemetry.add(EventsPath.CONTEXT_CHANNEL_PATH, config.defaultChannel)
    }
    telemetry.addFieldIfAbsent(EventsPath.SYNC_TS_PATH, dateFormatter.parseMillis(telemetry.getAtTimestamp))
    telemetry.addFieldIfAbsent(EventsPath.TIMESTAMP, telemetry.getSyncts)
  }

  def edataDir: String = telemetry.read[String](EventsPath.EDATA_DIR_PATH).orNull

  def eventSyncTs: Long = telemetry.read[Long](EventsPath.SYNC_TS_PATH).getOrElse(System.currentTimeMillis()).asInstanceOf[Number].longValue()

  def eventTags: util.List[AnyRef] = telemetry.read[util.List[AnyRef]](EventsPath.TAGS_PATH).orNull

  def cdata: util.ArrayList[util.Map[String, AnyRef]] = telemetry.read[util.ArrayList[util.Map[String, AnyRef]]](EventsPath.CONTEXT_CDATA).orNull

  def eventPData: util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]](EventsPath.CONTEXT_P_DATA_PATH).orNull

  def sessionId: String = telemetry.read[String](EventsPath.CONTEXT_SID_PATH).orNull

  def env: String = telemetry.read[String](EventsPath.CONTEXT_ENV_PATH).orNull

  def rollup: util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]](EventsPath.CONTEXT_ROLLUP_PATH).orNull

  def cbObject: util.Map[String, Any] = telemetry.read[util.Map[String, Any]](s"${EventsPath.EDATA_PATH}.cb_object").orNull

  def cbData: util.Map[String, Any] = telemetry.read[util.Map[String, Any]](s"${EventsPath.EDATA_PATH}.cb_data").orNull

  def cbObjectType: String = telemetry.read[String](CB_OBJECT_TYPE_PATH).orNull

  def isWorkOrder: Boolean = telemetry.read[String](CB_OBJECT_TYPE_PATH).orNull == "WorkOrder"

  def isPublishedWorkOrder: Boolean = telemetry.read[String](s"${EventsPath.EDATA_PATH}.cb_data.data.status").orNull == "Published"

  def cbUid: String = s"CB:${mid()}"

  def correctCbObjectOrg(): Unit = {
    if (cbObjectType == "Competency") {
      val keyPath = s"${EventsPath.EDATA_PATH}.cb_object.org"
      var org : String = telemetry.read[String](keyPath).orNull
      if (org != null) org = org.trim()
      if (org == null || org == "") {
        telemetry.add(keyPath, "FRAC Department")
      }
    }
  }

}
