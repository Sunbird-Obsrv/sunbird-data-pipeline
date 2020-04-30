package org.sunbird.spec

import java.util

import com.google.gson.Gson
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.core.domain.EventsPath
import org.sunbird.dp.core.reader.{Telemetry, TelemetryReaderException}
import org.sunbird.fixture.EventFixture

class TelemetryEventReaderSpec extends BaseSpec with Matchers with MockitoSugar {
  val gson = new Gson()

  "Telemetry Reader" should "Able to read the particular key from the event" in {

    val telemetryEvent = new Event(gson.fromJson(EventFixture.SAMPLE_EVENT_1, new util.LinkedHashMap[String, Any]().getClass))
    telemetryEvent.eid() should be("INTERACT")
    telemetryEvent.mid() should be("321a6f0c-10c6-4cdc-9893-207bb64fea50")
    telemetryEvent.did() should be("758e054a400f20f7677f2def76427dc13ad1f837")
    telemetryEvent.pid() should be("sunbird.app")
    telemetryEvent.producerId() should be("prod.sunbird.portal")
    telemetryEvent.producerPid() should be("sunbird.app")
    telemetryEvent.actorId() should be("bc3be7ae-ad2b-4dee-ac4c-220c7db146b2")
    telemetryEvent.actorType() should be("User")
    telemetryEvent.flags() should be(null)
    telemetryEvent.version() should be("3.0")
    telemetryEvent.ets() should be(1579143065071L)
    telemetryEvent.channel() should be("505c7c48ac6dc1edc9b08f21db5a571d")
    telemetryEvent.objectID() should be("do_9574")
    telemetryEvent.objectType() should be("content")
    telemetryEvent.edataType() should be("OTHER")
    telemetryEvent.edataItems() should be(null)
    telemetryEvent.getFlags() should be(null)
    telemetryEvent.getTimeStamp() should be("2020-01-21T00:02:54.098Z")
  }

  it should "Able to read the default, nested and update the particular key and value" in {
    val eventMap = gson.fromJson(EventFixture.SAMPLE_EVENT_2, new util.LinkedHashMap[String, Any]().getClass)
    val telemetryEvent = new Event(eventMap)
    // read
    telemetryEvent.edataItems() should not be (null)
    telemetryEvent.edataItems().size() should be(3)
    telemetryEvent.kafkaKey() should be("02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84")


    telemetryEvent.updateTs("9543785")
    telemetryEvent.getTimeStamp() should be("9543785")
    telemetryEvent.getTelemetry.isInstanceOf[Telemetry] should be(true)

    telemetryEvent.toString should not be (null)
    telemetryEvent.id should be(null)
    telemetryEvent.getChecksum should be("02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84")


    // Update
    val telemetryReader: Telemetry = new Telemetry(eventMap)
    telemetryReader.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetryReader.add(s"flags.testflag", false)
    val flags = telemetryReader.read[util.HashMap[String, Boolean]](EventsPath.FLAGS_PATH).getOrElse(null)
    flags.get("testflag") should be(false)

    // Nested
    val producerPid = telemetryReader.read[String](EventsPath.CONTEXT_P_DATA_PID_PATH).getOrElse(null)
    producerPid should not be (null)
    producerPid should be("sunbird.app")


  }

  it should "Not fail when the particular key/nested key or value is not found" in intercept[TelemetryReaderException] {
    val eventMap = gson.fromJson(EventFixture.SAMPLE_EVENT_2, new util.LinkedHashMap[String, Any]().getClass)
    val telemetryReader: Telemetry = new Telemetry(eventMap)
    val actorName = telemetryReader.read[String]("actor.id.name").getOrElse(null)
    actorName should be(null)

    val userKey = telemetryReader.read[String]("user").getOrElse(null)
    userKey should be(null)

    val syncTs = telemetryReader.getSyncts
    syncTs should not be (null)

    val timeStamp = telemetryReader.getAtTimestamp
    timeStamp should not be (null)

    telemetryReader.getEts should be(1577278681178L)
    telemetryReader.readOrDefault(s"${EventsPath.CONTEXT_PATH}.id", "context_id") should be("context_id")
    telemetryReader.readOrDefault(EventsPath.CONTEXT_CHANNEL_PATH, "context_id") should be("505c7c48ac6dc1edc9b08f21db5a571d")

    // Should throw an exception when mustRead value doesn't find any value for the key
    telemetryReader.mustReadValue("invlid_key")
    telemetryReader.id should be(null)
    telemetryReader.toString should not be (null)
  }

  it should "Take the default value when the timestamp and ets are not defined" in {
    // When ets and @timeStamp is null
    val eventMap = gson.fromJson(EventFixture.SAMPLE_EVENT_3, new util.LinkedHashMap[String, Any]().getClass)
    val telemetryReader: Telemetry = new Telemetry(eventMap)
    telemetryReader.read("ets") should not be (null)
    telemetryReader.read("@timestamp") should not be (null)
    telemetryReader.getSyncts should not be (null)
    telemetryReader.getAtTimestamp should not be (null)
    telemetryReader.id should be(null)
  }

  it should "not able to add null values into telemetry" in {
    val eventMap = gson.fromJson(EventFixture.SAMPLE_EVENT_3, new util.LinkedHashMap[String, Any]().getClass)
    val telemetryReader: Telemetry = new Telemetry(eventMap)
    telemetryReader.add(null, null) should be(false)

  }


}
