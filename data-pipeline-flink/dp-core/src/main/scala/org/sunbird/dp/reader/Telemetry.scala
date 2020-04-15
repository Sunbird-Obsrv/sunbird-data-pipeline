package org.sunbird.dp.reader

import java.io.Serializable
import java.text.MessageFormat.format
import java.text.SimpleDateFormat
import java.util
import java.util.Date
import java.util.logging.Logger



@SerialVersionUID(8132821816689744470L)
class Telemetry(var map: util.Map[String, Any]) extends Serializable {
 // private val logger = new Logger(this.getClass)

  def add(keyPath: String, value: Any): Boolean = {
    try {
      val lastParent = lastParentMap(map, keyPath)
      lastParent.addChild(value)
      return true
    } catch {
      case e: Exception =>
        //logger.error("", format("Couldn't add value:{0} at  key: {0} for event:{1}", value, keyPath, map), e)
    }
    false
  }

  def getMap: util.Map[String, Any] = map

  def read[T <: Any](keyPath: String): NullableValue[T] = try {
    val parentMap = lastParentMap(map, keyPath)
    new NullableValue[T](parentMap.readChild.get.asInstanceOf[T])
  } catch {
    case e: Exception =>
      //logger.error("", format("Couldn't get key: {0} from event:{1}", keyPath, map), e)
      new NullableValue[T](null.asInstanceOf[T])
  }

  def readOrDefault[T](keyPath: String, defaultValue: T): NullableValue[T] = if (read(keyPath).isNull) new NullableValue[T](defaultValue)
  else read(keyPath).asInstanceOf[NullableValue[T]]

  @throws[TelemetryReaderException]
  def mustReadValue[T](keyPath: String): T = {
    val readValue = read(keyPath)
    if (readValue.isNull) {
      val eid = read("eid")
      var message = keyPath + " is not available in the event"
      if (!eid.isNull) message = keyPath + " is not available in " + eid.value
      throw new TelemetryReaderException(message)
    }
    readValue.value
  }

  private def lastParentMap(map: util.Map[String, Any], keyPath: String) = {
    var parent = map
    val keys = keyPath.split("\\.")
    val lastIndex = keys.length - 1
    if (keys.length > 1) {
      var i = 0
      while ( {
        i < lastIndex && parent != null
      }) {
        var o = null
        if (parent.isInstanceOf[util.Map[_, _]]) o = new ParentMap(parent, keys(i)).readChild.get
        else if (parent.isInstanceOf[util.List[_]]) o = new ParentListOfMap(parent.asInstanceOf[util.List[util.Map[String, Any]]], keys(i)).readChild.get
        else o = new NullParent(parent, keys(i)).readChild.get
        parent = o
        i += 1
      }
    }
    val lastKeyInPath = keys(lastIndex)
    if (parent.isInstanceOf[util.Map[_, _]]) new ParentMap(parent, lastKeyInPath)
    else if (parent.isInstanceOf[util.List[_]]) new ParentListOfMap(parent.asInstanceOf[util.List[util.Map[String, Any]]], lastKeyInPath)
    else new NullParent(parent, lastKeyInPath)
  }

  override def toString: String = "Telemetry{" + "map=" + map + '}'

  override def equals(o: Any): Boolean = {
    if (this.equals(o)) return true
    if (o == null || (this.getClass ne o.getClass)) return false
    val telemetry = o.asInstanceOf[Telemetry]
    if (map != null) map == telemetry.map
    else telemetry.map == null
  }

  override def hashCode: Int = if (map != null) map.hashCode
  else 0

  def id: String = this.read[String]("metadata.checksum").value

  def addFieldIfAbsent[T](fieldName: String, value: T): Unit = {
    if (read(fieldName).isNull) add(fieldName, value.asInstanceOf[AnyRef])
  }

  @throws[TelemetryReaderException]
  def getEts: Long = {
    val ets = this.mustReadValue[Double]("ets")
    ets.toLong
  }

  def getAtTimestamp: String = {
    val timestamp = this.read("@timestamp").value
    val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    if (timestamp.isInstanceOf[Number]) {
      val date = new Date(timestamp.asInstanceOf[Number].longValue)
      simpleDateFormat.format(date)
    }
    else this.read[String]("@timestamp").value
  }

  def getSyncts: String = {
    val timestamp = this.read("syncts").value
    val simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    if (timestamp.isInstanceOf[Number]) {
      val date = new Date(timestamp.asInstanceOf[Number].longValue)
      simpleDateFormat.format(date)
    }
    else this.read[String]("syncts").value
  }
}
