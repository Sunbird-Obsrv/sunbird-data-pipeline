package org.sunbird.dp.domain


import java.util

import com.google.gson.Gson
import org.sunbird.dp.reader.Telemetry


abstract class Events(val map: util.Map[String, Any]) {

  protected var telemetry: Telemetry = new Telemetry(map)
  protected var path: Path = new Path()

  def getTelemetry: Telemetry = telemetry

  def kafkaKey(): String = mid

  def getChecksum: String = {
    val checksum = id
    if (checksum != null) return checksum
    mid
  }

  def id(): String = telemetry.read[String](keyPath = "metadata.checksum").getOrElse(null)

  def getMap(): util.Map[String, Any] = telemetry.getMap

  def getJson(): String = new Gson().toJson(getMap)

  def mid(): String = telemetry.read[String]("mid").getOrElse(null)


  def did(): String = {
    telemetry.read[String]("dimensions.did").getOrElse(telemetry.read("context.did").orNull)
  }

  def eid(): String = telemetry.read[String]("eid").getOrElse(null)


  def flags(): util.Map[String, AnyRef] = telemetry.read[util.Map[String, AnyRef]]("flags").getOrElse(null)

  override def toString: String = "Event{" + "telemetry=" + telemetry + '}'

  def updateTs(value: String): Unit = telemetry.add("@timestamp", value)

  def pid(): String = telemetry.read("context.pdata.pid").getOrElse(null)


  def version(): String = telemetry.read[String]("ver").getOrElse(null)

  def producerId(): String = telemetry.read("context.pdata.id").getOrElse(null)

  final def producerPid(): String = telemetry.read("context.pdata.pid").getOrElse(null)


  def ets(): Long = {
    telemetry.read[Long]("ets").getOrElse(null).asInstanceOf[Number].longValue()
  }

  def channel(): String = {
    telemetry.read("dimensions.channel").getOrElse(telemetry.read[String]("context.channel").getOrElse(null))
  }


  def actorId(): String = {
    telemetry.read[String]("uid").getOrElse(telemetry.read[String]("actor.id").getOrElse(null))
  }

  def actorType(): String = {
    telemetry.read("actor.type").getOrElse(null)
  }

  def objectID(): String = if (objectFieldsPresent) telemetry.read[String]("object.id").getOrElse(null) else null

  def objectType(): String = if (objectFieldsPresent) telemetry.read[String]("object.type").getOrElse(null) else null

  def objectFieldsPresent(): Boolean = {
    val objectId = telemetry.read[String]("object.id").getOrElse(null)
    val objectType = telemetry.read[String]("object.type").getOrElse(null)
    null != objectId && null != objectType && !objectId.isEmpty && !objectType.isEmpty
  }

  def edataType(): String = telemetry.read[String]("edata.type").getOrElse(null)

  def edataItems(): util.List[util.Map[String, AnyRef]] = telemetry.read[util.List[util.Map[String, AnyRef]]]("edata.items").getOrElse(null)

  def updateFlags(key: String, value: Boolean): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags." + key, value.asInstanceOf[AnyRef])
  }

  def getFlags(): util.Map[String, Boolean] = telemetry.read("flags").getOrElse(null)

  def getTimeStamp() = telemetry.getAtTimestamp
}
