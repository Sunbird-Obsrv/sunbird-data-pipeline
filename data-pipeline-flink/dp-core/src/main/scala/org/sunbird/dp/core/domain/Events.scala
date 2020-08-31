package org.sunbird.dp.core.domain

import java.util

import com.google.gson.Gson
import org.sunbird.dp.core.reader.Telemetry
import org.sunbird.dp.core.util.JSONUtil

abstract class Events(val map: util.Map[String, Any]) {

  protected var telemetry: Telemetry = new Telemetry(map)

  def getTelemetry: Telemetry = telemetry

  def kafkaKey(): String = mid()

  def getChecksum: String = {
    val checksum = id()
    if (checksum != null) return checksum
    mid()
  }

  def id(): String = telemetry.read[String](keyPath = EventsPath.CHECKSUM_PATH).orNull

  def getMap(): util.Map[String, Any] = telemetry.getMap

  // def getJson(): String = new Gson().toJson(getMap())
  def getJson(): String = JSONUtil.serialize(getMap())

  def mid(): String = telemetry.read[String](keyPath = EventsPath.MID_PATH).orNull


  def did(): String = {
    telemetry.read[String](keyPath = EventsPath.DIMENSION_DID_PATH).getOrElse(telemetry.read(keyPath = EventsPath.CONTEXT_DID_PATH).orNull)
  }

  def eid(): String = telemetry.read[String](keyPath = EventsPath.EID_PATH).orNull


  def flags(): util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]](EventsPath.FLAGS_PATH).orNull

  override def toString: String = "Event{" + "telemetry=" + telemetry + '}'

  def updateTs(value: String): Unit = telemetry.add(keyPath = EventsPath.TIMESTAMP, value)

  def pid(): String = telemetry.read(keyPath = EventsPath.CONTEXT_P_DATA_PID_PATH).orNull


  def version(): String = telemetry.read[String](keyPath = EventsPath.VERSION_KEY_PATH).orNull

  def producerId(): String = telemetry.read(keyPath = EventsPath.CONTEXT_P_DATA_ID_PATH).orNull

  final def producerPid(): String = telemetry.read(keyPath = EventsPath.CONTEXT_P_DATA_PID_PATH).orNull


  def ets(): Long = {
    telemetry.read[Long](keyPath = EventsPath.ETS_PATH).getOrElse(null).asInstanceOf[Number].longValue()
  }

  def channel(): String = {
    telemetry.read(keyPath = EventsPath.DIMENSION_CHANNEL_PATH).getOrElse(telemetry.read[String](keyPath = EventsPath.CONTEXT_CHANNEL_PATH).orNull)
  }


  def actorId(): String = {
    telemetry.read[String](keyPath = EventsPath.UID_PATH).getOrElse(telemetry.read[String](keyPath = EventsPath.ACTOR_ID_PATH).orNull)
  }

  def actorType(): String = {
    telemetry.read(EventsPath.ACTOR_TYPE_PATH).orNull
  }

  def objectID(): String = if (objectFieldsPresent()) telemetry.read[String](keyPath = EventsPath.OBJECT_ID_PATH).orNull else null

  def objectType(): String = if (objectFieldsPresent()) telemetry.read[String](keyPath = EventsPath.OBJECT_TYPEPATH).orNull else null

  def objectFieldsPresent(): Boolean = {
    val objectId = telemetry.read[String](keyPath = EventsPath.OBJECT_ID_PATH).orNull
    val objectType = telemetry.read[String](keyPath = EventsPath.OBJECT_TYPEPATH).orNull
    null != objectId && null != objectType && !objectId.isEmpty && !objectType.isEmpty
  }

  def edataType(): String = telemetry.read[String](keyPath = EventsPath.EDATA_TYPE_PATH).orNull

  def edataItems(): util.List[util.Map[String, AnyRef]] = telemetry.read[util.List[util.Map[String, AnyRef]]](keyPath = EventsPath.EDATA_ITEM).orNull

  def updateFlags(key: String, value: Boolean): Unit = {
    telemetry.addFieldIfAbsent(fieldName = EventsPath.FLAGS_PATH, new util.HashMap[String, Boolean])
    telemetry.add(keyPath = s"${EventsPath.FLAGS_PATH}." + key, value.asInstanceOf[AnyRef])
  }

  def getFlags(): util.Map[String, Boolean] = telemetry.read(keyPath = EventsPath.FLAGS_PATH).orNull

  def getTimeStamp():String = telemetry.getAtTimestamp
}
