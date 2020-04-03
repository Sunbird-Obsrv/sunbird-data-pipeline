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

  def markFailure(errorMsg: String, config: DenormalizationConfig): Unit = {

    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean]);
    telemetry.add("flags.tr_processed", false);
    telemetry.addFieldIfAbsent("metadata", new util.HashMap[String, AnyRef]);
    telemetry.add("metadata.denorm_error", errorMsg);
    telemetry.add("metadata.src", config.jobName);
  }

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
    if (!telemetry.read("userdata").isNull())
      Some(telemetry.read[String]("userdata.state").value(), telemetry.read[String]("userdata.district").value(), "user-profile")
    else
      None
  }

  def getUserDeclaredLocation(): Option[(String, String, String)] = {
    if (!telemetry.read("devicedata").isNull() && !telemetry.read("devicedata.userdeclared").isNull())
      Some(telemetry.read[String]("devicedata.userdeclared.state").value(), telemetry.read[String]("devicedata.userdeclared.district").value(), "user-declared")
    else
      None
  }

  def getIpLocation(): Option[(String, String, String)] = {
    if (!telemetry.read("userdata").isNull())
      Some(telemetry.read[String]("devicedata.state").value(), telemetry.read[String]("devicedata.district_custom").value(), "ip-resolved")
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
    if (objectFieldsPresent() && objectRollUpl1FieldsPresent()) {
      return telemetry.read[String]("object.rollup.l1").value();
    } else return null;
  }

  def objectRollUpl1FieldsPresent(): Boolean = {
    if (objectFieldsPresent()) {
      val objectrollUpl1 = telemetry.read[String]("object.rollup.l1").value();
      null != objectrollUpl1 && !objectrollUpl1.isEmpty();
    }
    false
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
    val previousData: NullableValue[util.Map[String, AnyRef]] = telemetry.read(path.collectionData());
    val collectionData = if (previousData.isNull()) new util.HashMap[String, AnyRef]() else previousData.value();
    collectionData.putAll(convertedData.asJava);
    telemetry.add(path.collectionData(), collectionData);
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
    }
    epochTs;
  }

  def setFlag(key: String, value: Boolean) {
    val telemetryFlag: NullableValue[util.Map[String, AnyRef]] = telemetry.read(path.flags());
    val flags = if (telemetryFlag.isNull()) new util.HashMap[String, AnyRef]() else telemetryFlag.value()
    if (!key.isEmpty()) {
      flags.put(key, value.asInstanceOf[AnyRef]);
    }
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
