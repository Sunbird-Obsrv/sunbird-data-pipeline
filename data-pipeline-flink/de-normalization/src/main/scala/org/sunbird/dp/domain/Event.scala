package org.sunbird.dp.domain

import java.util

import org.apache.commons.lang3.StringUtils
import org.joda.time.format.DateTimeFormat
import org.sunbird.dp.task.DenormalizationConfig
import org.joda.time.DateTime
import org.joda.time.LocalDate
import org.sunbird.dp.reader.NullableValue
import org.joda.time.format.DateTimeFormatter
import scala.collection.mutable.Map
import collection.JavaConverters._

class Event(eventMap: util.Map[String, AnyRef]) extends Events(eventMap) {

  private[this] val df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZoneUTC
  private[this] val df2 = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZoneUTC();
  private val jobName = "PipelinePreprocessor"

  def addDeviceProfile(deviceProfile: DeviceProfile): Unit = {

    val iso3166statecode = addISOStateCodeToDeviceProfile(deviceProfile);
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
      "iso3166statecode" -> iso3166statecode,
      "userdeclared" -> Map[String, String]("state" -> deviceProfile.userDeclaredState, "district" -> deviceProfile.userDeclaredDistrict));

    telemetry.add(path.deviceData(), ldata);
    setFlag("device_denorm", true);
  }

  def getUserProfileLocation(): Option[(String, String, String)] = {

    val userData: NullableValue[util.Map[String, AnyRef]] = telemetry.read(path.userData());
    if (!userData.isNull())
      Some(userData.value().get("state").asInstanceOf[String], userData.value().get("district").asInstanceOf[String], "user-profile")
    else
      None
  }

  def getUserDeclaredLocation(): Option[(String, String, String)] = {

    val deviceData: NullableValue[Map[String, AnyRef]] = telemetry.read(path.deviceData());
    if (!deviceData.isNull() && deviceData.value().get("userdeclared").nonEmpty) {
      val userDeclared = deviceData.value().get("userdeclared").get.asInstanceOf[Map[String, String]]
      Some(userDeclared.get("state").getOrElse(null), userDeclared.get("district").getOrElse(null), "user-declared")
    } else
      None
  }

  def getIpLocation(): Option[(String, String, String)] = {
    val deviceData: NullableValue[Map[String, AnyRef]] = telemetry.read(path.deviceData());
    if (!deviceData.isNull())
      Some(deviceData.value().get("state").get.asInstanceOf[String], deviceData.value().get("districtcustom").get.asInstanceOf[String], "ip-resolved")
    else
      None
  }

  def addDerivedLocation(derivedData: (String, String, String)) {
    val locMap = Map(path.stateKey() -> derivedData._1, path.districtKey() -> derivedData._2, path.locDerivedFromKey() -> derivedData._3)
    telemetry.add(path.derivedLocationData(), derivedData);
    setFlag("loc_denorm", true);
  }

  def compareAndAlterEts(): Long = {
    val eventEts = ets();
    val endTsOfCurrentDate = DateTime.now().plusDays(1).withTimeAtStartOfDay().minusMillis(1).getMillis;
    if (eventEts > endTsOfCurrentDate) telemetry.add("ets", endTsOfCurrentDate);
    ets();
  }

  def isOlder(periodInMonths: Int): Boolean = {
    val eventEts = ets();
    val periodInMillis = new DateTime().minusMonths(periodInMonths).getMillis();
    eventEts < periodInMillis
  }

  def objectRollUpl1ID(): String = {
    telemetry.read[String]("object.rollup.l1").value();
  }

  def objectRollUpl1FieldsPresent(): Boolean = {

    val objectrollUpl1 = telemetry.read[String]("object.rollup.l1").value();
    null != objectrollUpl1 && !objectrollUpl1.isEmpty();
  }

  def checkObjectIdNotEqualsRollUpl1Id(): Boolean = {
    objectRollUpl1FieldsPresent() && !(objectID().equals(objectRollUpl1ID()))
  }

  def addUserData(newData: Map[String, AnyRef]) {
    val previousData: NullableValue[util.Map[String, AnyRef]] = telemetry.read(path.userData());
    val userdata = if (previousData.isNull()) new util.HashMap[String, AnyRef]() else previousData.value();
    userdata.putAll(newData.asJava);
    telemetry.add(path.userData(), userdata);
    if (newData.size > 2)
      setFlag("user_denorm", true);
    else
      setFlag("user_denorm", false);
  }

  def addContentData(newData: Map[String, AnyRef]) {
    val convertedData = getEpochConvertedContentDataMap(newData);
    val previousData: NullableValue[util.Map[String, AnyRef]] = telemetry.read(path.contentData());
    val contentData = if (previousData.isNull()) new util.HashMap[String, AnyRef]() else previousData.value();
    contentData.putAll(convertedData.asJava);
    telemetry.add(path.contentData(), contentData);
    setFlag("content_denorm", true);
  }

  def addCollectionData(newData: Map[String, AnyRef]) {
    val convertedData = getEpochConvertedContentDataMap(newData);
    telemetry.add(path.collectionData(), convertedData);
    setFlag("coll_denorm", true);
  }

  def getEpochConvertedContentDataMap(data: Map[String, AnyRef]): Map[String, AnyRef] = {

    val lastSubmittedOn = data.get("lastsubmittedon");
    val lastUpdatedOn = data.get("lastupdatedon");
    val lastPublishedOn = data.get("lastpublishedon");
    if (lastSubmittedOn.nonEmpty && lastSubmittedOn.get.isInstanceOf[String]) {
      data.put("lastsubmittedon", getConvertedTimestamp(lastSubmittedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef]);
    }
    if (lastUpdatedOn.nonEmpty && lastUpdatedOn.get.isInstanceOf[String]) {
      data.put("lastupdatedon", getConvertedTimestamp(lastUpdatedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef]);
    }
    if (lastPublishedOn.nonEmpty && lastPublishedOn.get.isInstanceOf[String]) {
      data.put("lastpublishedon", getConvertedTimestamp(lastPublishedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef]);
    }
    data;
  }

  def addDialCodeData(newData: Map[String, AnyRef]) {
    telemetry.add(path.dialCodeData(), getEpochConvertedDialcodeDataMap(newData));
    setFlag("dialcode_denorm", true);
  }

  private def getEpochConvertedDialcodeDataMap(data: Map[String, AnyRef]): Map[String, AnyRef] = {

    val generatedOn = data.get("generatedon");
    val publishedOn = data.get("publishedon");
    if (generatedOn.nonEmpty && generatedOn.get.isInstanceOf[String]) {
      data.put("generatedon", getConvertedTimestamp(generatedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef])
    }
    if (publishedOn.nonEmpty && publishedOn.get.isInstanceOf[String]) {
      data.put("publishedon", getConvertedTimestamp(publishedOn.get.asInstanceOf[String]).asInstanceOf[AnyRef]);
    }
    data
  }

  def getTimestamp(ts: String, df: DateTimeFormatter): Long = {
    try {
      df.parseDateTime(ts).getMillis();
    } catch {
      case ex: Exception =>
        0L;
    }
  }

  def getConvertedTimestamp(ts: String): Long = {
    val epochTs = getTimestamp(ts, df);
    if (epochTs == 0) {
      getTimestamp(ts, df2);
    } else {
      epochTs;
    }
  }

  def setFlag(key: String, value: Boolean) {
    val telemetryFlag: NullableValue[util.Map[String, AnyRef]] = telemetry.read(path.flags());
    val flags = if (telemetryFlag.isNull()) new util.HashMap[String, AnyRef]() else telemetryFlag.value()
    flags.put(key, value.asInstanceOf[AnyRef]);
    telemetry.add(path.flags(), flags);
  }

  def addISOStateCodeToDeviceProfile(deviceProfile: DeviceProfile): String = {
    // add new statecode field
    val statecode = deviceProfile.stateCode;
    if (statecode != null && !statecode.isEmpty()) {
      return "IN-" + statecode;
    } else return "";
  }

}
