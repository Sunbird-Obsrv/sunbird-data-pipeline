package org.sunbird.dp.spec

import java.lang.reflect.Type
import java.util
import java.util.Date

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses
import org.sunbird.dp.cache.DedupEngine
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.domain.Event
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.functions.DeduplicationFunction
import org.sunbird.dp.task.DeduplicationConfig

import scala.collection.JavaConverters._
import com.typesafe.config.ConfigFactory

class DeduplicationFunctionTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  val gson = new Gson()
  val mapType: Type = new TypeToken[util.Map[String, AnyRef]](){}.getType
  val dedupEngine: DedupEngine = mock[DedupEngine]
  val dedupConfig: DeduplicationConfig = new DeduplicationConfig(ConfigFactory.load("test.conf"))

  "Unique events" should "be sent to unique SideOutput" in {

    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(true)

    val deduplicationFunction = new DeduplicationFunction(dedupConfig, dedupEngine)
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))

    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    harness.processElement(event, new Date().getTime)
    val uniqueEventStream = harness.getSideOutput(dedupConfig.uniqueEventsOutputTag)
    uniqueEventStream.size() should be (1)
    val uniqueEvent = uniqueEventStream.asScala.head.getValue
    uniqueEvent.mid() should be ("321a6f0c-10c6-4cdc-9893-207bb64fea50")
  }

  "Duplicate events" should "be sent to duplicate SideOutput" in {

    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(false)

    val deduplicationFunction = new DeduplicationFunction(dedupConfig, dedupEngine)
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))

    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    harness.processElement(event, new Date().getTime)
    val duplicateEventStream = harness.getSideOutput(dedupConfig.duplicateEventsOutputTag)
    duplicateEventStream.size() should be (1)

    val uniqueEventStream = harness.getSideOutput(dedupConfig.uniqueEventsOutputTag)
    uniqueEventStream should be (null)

    val duplicateEvent = duplicateEventStream.asScala.head.getValue
    duplicateEvent.mid() should be ("321a6f0c-10c6-4cdc-9893-207bb64fea50")
  }

  "Duplicate check required " should "return true if producer id is defined in the inclusion list" in {
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))
    val deduplicationFunction = new DeduplicationFunction(dedupConfig, dedupEngine)
    val isDuplicationCheckRequired = deduplicationFunction.isDuplicateCheckRequired(event)
    isDuplicationCheckRequired should be (true)
  }

  "Duplicate check required " should "return false if producer id is not defined in the inclusion list" in {

    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.NON_INCLUDED_PDATA_ID_EVENT, mapType))
    val deduplicationFunction = new DeduplicationFunction(dedupConfig, dedupEngine)
    val isDuplicationCheckRequired = deduplicationFunction.isDuplicateCheckRequired(event)
    isDuplicationCheckRequired should be (false)
  }

}
