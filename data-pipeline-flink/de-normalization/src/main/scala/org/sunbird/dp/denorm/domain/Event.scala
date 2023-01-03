package org.sunbird.dp.denorm.domain

import java.util

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}
import org.sunbird.dp.core.domain.{Events, EventsPath}
import org.sunbird.dp.denorm.task.DenormalizationConfig

import scala.collection.JavaConverters._
import scala.collection.mutable.Map
import scala.collection.mutable

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

  private[this] val df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ").withZoneUTC
  private[this] val df2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZoneUTC()
  private val jobName = "PipelinePreprocessor"

  override def kafkaKey(): String = {
    did()
  }

  def addDeviceProfile(deviceProfile: DeviceProfile): Unit = {

    val deviceMap = new util.HashMap[String, Object]()
    val userDeclaredMap = new util.HashMap[String, Object]()
    val iso3166statecode = addISOStateCodeToDeviceProfile(deviceProfile)
    val ldata = Map[String, AnyRef]("countrycode" -> deviceProfile.countryCode,
      "country" -> deviceProfile.country,
      "statecode" -> deviceProfile.stateCode,
      "state" -> deviceProfile.state,
      "city" -> deviceProfile.city,
      "statecustomcode" -> deviceProfile.stateCodeCustom,
      "statecustomname" -> deviceProfile.stateCustomName,
      "districtcustom" -> deviceProfile.districtCustom,
      "devicespec" -> deviceProfile.devicespec,
      "firstaccess" -> deviceProfile.firstaccess.asInstanceOf[AnyRef],
      "iso3166statecode" -> iso3166statecode)

    deviceMap.putAll(ldata.asJava)
    userDeclaredMap.putAll(Map[String, String]("state" -> deviceProfile.userDeclaredState, "district" -> deviceProfile.userDeclaredDistrict).asJava)
    deviceMap.put("userdeclared", userDeclaredMap)
    telemetry.add(EventsPath.DEVICE_DATA_PATH, deviceMap)
    setFlag("device_denorm", value = true)
  }

  def getUserProfileLocation(): Option[(String, String, String)] = {

    val userData: util.Map[String, AnyRef] = telemetry.read(EventsPath.USERDATA_PATH).orNull
    Option(userData).map(user => {
      (user.get("state").asInstanceOf[String], user.get("district").asInstanceOf[String], "user-profile")
    })
  }

  def getUserDeclaredLocation(): Option[(String, String, String)] = {
    val deviceData: util.Map[String, AnyRef] = telemetry.read(EventsPath.DEVICE_DATA_PATH).orNull
    Option(deviceData).map(device => {
      val userDeclaredData = device.get("userdeclared").asInstanceOf[util.Map[String, String]]
      (userDeclaredData.get("state"), userDeclaredData.get("district"), "user-declared")
    })
  }

  def getIpLocation(): Option[(String, String, String)] = {
    val deviceData: util.Map[String, AnyRef] = telemetry.read(EventsPath.DEVICE_DATA_PATH).orNull
    Option(deviceData).map(device => {
      (device.get("state").asInstanceOf[String], device.get("districtcustom").asInstanceOf[String], "ip-resolved")
    })

  }

  def addDerivedLocation(derivedData: (String, String, String)) {
    val locMap = new util.HashMap[String, String]()
    locMap.put(EventsPath.STATE_KEY_PATH, derivedData._1)
    locMap.put(EventsPath.DISTRICT_KEY_PATH, derivedData._2)
    locMap.put(EventsPath.LOCATION_DERIVED_FROM_PATH, derivedData._3)
    telemetry.add(EventsPath.DERIVED_LOCATION_PATH, locMap)
    setFlag("loc_denorm", true)
  }

  def compareAndAlterEts(): Long = {
    val eventEts = ets()
    val endTsOfCurrentDate = DateTime.now().plusDays(1).withTimeAtStartOfDay().minusMillis(1).getMillis
    if (eventEts > endTsOfCurrentDate) telemetry.add("ets", endTsOfCurrentDate)
    ets()
  }

  def isOlder(periodInMonths: Int): Boolean = {
    val eventEts = ets()
    val periodInMillis = new DateTime(DateTimeZone.UTC).minusMonths(periodInMonths).getMillis
    eventEts < periodInMillis
  }

  def objectRollUpl1ID(): String = {
    telemetry.read[String](keyPath = EventsPath.OBJECT_ROLLUP_L1).orNull
  }

  def objectRollUpl2ID(): String = {
    telemetry.read[String](keyPath = EventsPath.OBJECT_ROLLUP_L2).orNull
  }

  def objectRollUpFieldsPresent(path: String): Boolean = {

    val objectrollUpl1 = telemetry.read[String](keyPath = path).orNull
    null != objectrollUpl1 && !objectrollUpl1.isEmpty
  }

  def checkObjectIdNotEqualsRollUpId(path: String): Boolean = {
    objectRollUpFieldsPresent(path) && !objectID().equals(objectRollUpl1ID())
  }

  // def addUserData(newData: Map[String, String]) {
  def addUserData(newData: mutable.Map[String, AnyRef]) {
    val userdata: util.Map[String, AnyRef] = telemetry.read(EventsPath.USERDATA_PATH).getOrElse(new util.HashMap[String, AnyRef]())
    userdata.putAll(newData.asJava)
    telemetry.add(EventsPath.USERDATA_PATH, userdata)
    if (newData.size > 2)
      setFlag("user_denorm", true)
    else
      setFlag("user_denorm", false)
  }

  def addContentData(newData: Map[String, AnyRef]) {
    val convertedData = getEpochConvertedContentDataMap(newData)
    val contentData: util.Map[String, AnyRef] = telemetry.read(EventsPath.CONTENT_DATA_PATH).getOrElse(new util.HashMap[String, AnyRef]())
    contentData.putAll(convertedData.asJava)
    telemetry.add(EventsPath.CONTENT_DATA_PATH, contentData)
    setFlag("content_denorm", true)
  }

  def addCollectionData(newData: Map[String, AnyRef]) {
    val collectionMap = new util.HashMap[String, AnyRef]()
    val convertedData = getEpochConvertedContentDataMap(newData)
    collectionMap.putAll(convertedData.asJava)
    telemetry.add(EventsPath.COLLECTION_PATH, collectionMap)
    setFlag("coll_denorm", true)
  }

  def addL2Data(newData: Map[String, AnyRef]) {
    val l2Map = new util.HashMap[String, AnyRef]()
    l2Map.putAll(newData.asJava)
    telemetry.add(EventsPath.L2_DATA_PATH, l2Map)
    setFlag("l2_denorm", true)
  }


  def getEpochConvertedContentDataMap(data: Map[String, AnyRef]): Map[String, AnyRef] = {

    val lastSubmittedOn = data.get("lastsubmittedon")
    val lastUpdatedOn = data.get("lastupdatedon")
    val lastPublishedOn = data.get("lastpublishedon")
    if (lastSubmittedOn.nonEmpty && lastSubmittedOn.get.isInstanceOf[String]) {
      data.put("lastsubmittedon", getConvertedTimestamp(lastSubmittedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef])
    }
    if (lastUpdatedOn.nonEmpty && lastUpdatedOn.get.isInstanceOf[String]) {
      data.put("lastupdatedon", getConvertedTimestamp(lastUpdatedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef])
    }
    if (lastPublishedOn.nonEmpty && lastPublishedOn.get.isInstanceOf[String]) {
      data.put("lastpublishedon", getConvertedTimestamp(lastPublishedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef])
    }
    data
  }

  def addDialCodeData(newData: Map[String, AnyRef]) {
    val dialcodeMap = new util.HashMap[String, AnyRef]()
    dialcodeMap.putAll(getEpochConvertedDialcodeDataMap(newData).asJava)
    telemetry.add(EventsPath.DIAL_CODE_PATH, dialcodeMap)
    setFlag("dialcode_denorm", true)
  }

  private def getEpochConvertedDialcodeDataMap(data: Map[String, AnyRef]): Map[String, AnyRef] = {

    val generatedOn = data.get("generatedon")
    val publishedOn = data.get("publishedon")
    if (generatedOn.nonEmpty && generatedOn.get.isInstanceOf[String]) {
      data.put("generatedon", getConvertedTimestamp(generatedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef])
    }
    if (publishedOn.nonEmpty && publishedOn.get.isInstanceOf[String]) {
      data.put("publishedon", getConvertedTimestamp(publishedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef])
    }
    data
  }

  def getTimestamp(ts: String, df: DateTimeFormatter): Long = {
    try {
      df.parseDateTime(ts).getMillis
    } catch {
      case ex: Exception =>
        0L
    }
  }

  def getConvertedTimestamp(ts: String): Long = {
    val epochTs = getTimestamp(ts, df)
    if (epochTs == 0) {
      getTimestamp(ts, df2)
    } else {
      epochTs
    }
  }

  def setFlag(key: String, value: Boolean) {
    val telemetryFlag: util.Map[String, AnyRef] = telemetry.read(EventsPath.FLAGS_PATH).getOrElse(new util.HashMap[String, AnyRef]())
    telemetryFlag.put(key, value.asInstanceOf[AnyRef])
    telemetry.add(EventsPath.FLAGS_PATH, telemetryFlag)
  }

  def addISOStateCodeToDeviceProfile(deviceProfile: DeviceProfile): String = {
    // add new statecode field
    val statecode = deviceProfile.stateCode
    if (statecode != null && !statecode.isEmpty) {
      "IN-" + statecode
    } else ""
  }

  def isValidEventForContentDenorm(config: DenormalizationConfig, objectId: String, objectType: String, eid: String): Boolean = {
    (null != objectType && (config.permitEid.contains(eid) || !List("user", "qr", "dialcode").contains(objectType.toLowerCase())) && null != objectId)
  }

}
