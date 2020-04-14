package org.sunbird.dp.domain


import java.util

import com.google.gson.Gson
import org.sunbird.dp.reader.Telemetry


abstract class Events(val map: util.Map[String, Any]) {

  protected var telemetry: Telemetry = new Telemetry(map)

  def getTelemetry: Telemetry = telemetry

  def kafkaKey: String = mid

  def getChecksum: String = {
    val checksum = id
    if (checksum != null) return checksum
    mid
  }

  def id: String = telemetry.read(keyPath = "metadata.checksum").value

  def getMap: util.Map[String, Any] = telemetry.getMap

  def getJson: String = new Gson().toJson(getMap)

  def mid: String = telemetry.read("mid").value


  def did: String = {
    val did = telemetry.read("dimensions.did")
    if (did.isNull) telemetry.read[String]("context.did").value
    else did.value
  }

  def eid: String = telemetry.read("eid").value

  def flags: util.Map[String, AnyRef] = telemetry.read("flags").value

  override def toString: String = "Event{" + "telemetry=" + telemetry + '}'

  def updateTs(value: String): Unit = telemetry.add("@timestamp", value)

  def pid: String = telemetry.read("context.pdata.pid").value


  def version: String = telemetry.read("ver").value.asInstanceOf[String]

  def producerId: String = telemetry.read("context.pdata.id").value

  final def producerPid: String = telemetry.read("context.pdata.pid").value


  def ets: Long = {
    val ets = telemetry.read("ets")
    if (ets.value.isInstanceOf[Double]) return ets.value.asInstanceOf[Double].longValue
    ets.value.asInstanceOf[Long]
  }

  def channel: String = {
    val channel = telemetry.read("dimensions.channel")
    if (channel.isNull) telemetry.read[String]("context.channel").value
    else channel.value
  }

  def actorId: String = {
    val actorid = telemetry.read("uid")
    if (actorid.isNull) telemetry.read[String]("actor.id").value
    else actorid.value
  }

  def actorType: String = {
    val actortype = telemetry.read("actor.type")
    actortype.value
  }

  def objectID: String = if (objectFieldsPresent) telemetry.read[String]("object.id").value
  else null

  def objectType: String = if (objectFieldsPresent) telemetry.read[String]("object.type").value
  else null

  def objectFieldsPresent: Boolean = {
    val objectId = telemetry.read[String]("object.id").value
    val objectType = telemetry.read[String]("object.type").value
    null != objectId && null != objectType && !objectId.isEmpty && !objectType.isEmpty
  }

  def edataType: String = telemetry.read[String]("edata.type").value

  def edataItems: util.List[util.Map[String, AnyRef]] = telemetry.read[util.List[util.Map[String, AnyRef]]]("edata.items").value

  def updateFlags(key: String, value: Boolean): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add("flags." + key, value)
  }

  def getFlags: util.Map[String, Boolean] = telemetry.read("flags").value
}
