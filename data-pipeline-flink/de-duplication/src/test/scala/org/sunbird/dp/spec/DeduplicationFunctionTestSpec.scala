package org.sunbird.dp.spec

import java.lang.reflect.Type
import java.util
import java.util.Date

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.domain.Event
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.functions.DeduplicationFunction
import org.sunbird.dp.task.DeduplicationConfig
import redis.embedded.RedisServer

import scala.collection.JavaConverters._

class DeduplicationFunctionTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  val gson = new Gson()
  val mapType: Type = new TypeToken[util.Map[String, AnyRef]](){}.getType
  var redisServer: RedisServer = _
  var redisConnect: RedisConnect = _
  var dedupEngine: DedupEngine = _
  val mockConfig: DeduplicationConfig = Mockito.spy(new DeduplicationConfig)
  val mockBaseConfig: Config = mock[Config]

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    when(mockConfig.includedProducersForDedup).thenReturn(List("sunbird.app", "sunbird.portal"))
    when(mockConfig.dedupStore).thenReturn(6)
    when(mockConfig.cacheExpirySeconds).thenReturn(300)

    redisServer = new RedisServer(6340)
    redisServer.start()

    when(mockBaseConfig.getString("redis.host")).thenReturn("localhost")
    when(mockBaseConfig.getInt("redis.port")).thenReturn(6340)
    when(mockBaseConfig.getInt("redis.connection.max")).thenReturn(2)
    when(mockBaseConfig.getInt("redis.connection.idle.max")).thenReturn(2)
    when(mockBaseConfig.getInt("redis.connection.idle.min")).thenReturn(1)
    when(mockBaseConfig.getInt("redis.connection.minEvictableIdleTimeSeconds")).thenReturn(120)
    when(mockBaseConfig.getInt("redis.connection.timeBetweenEvictionRunsSeconds")).thenReturn(300)

    when(mockConfig.config).thenReturn(mockBaseConfig)
    redisConnect = Mockito.spy(new RedisConnect(mockConfig))
    dedupEngine = Mockito.spy(new DedupEngine(redisConnect = redisConnect, mockConfig.dedupStore, mockConfig.cacheExpirySeconds))
  }

  "Unique events" should "be sent to unique SideOutput" in {

    val deduplicationFunction = new DeduplicationFunction(mockConfig, dedupEngine)
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))
    
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    harness.processElement(event, new Date().getTime)
    verify(dedupEngine, times(1)).storeChecksum("321a6f0c-10c6-4cdc-9893-207bb64fea50")
    val uniqueEventStream = harness.getSideOutput(new OutputTag("unique-events"))
    uniqueEventStream.size() should be (1)
    val uniqueEvent = uniqueEventStream.asScala.head.getValue
    uniqueEvent.mid() should be ("321a6f0c-10c6-4cdc-9893-207bb64fea50")
  }

  "Duplicate events" should "be sent to duplicate SideOutput" in {

    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(false)

    val deduplicationFunction = new DeduplicationFunction(mockConfig, dedupEngine)
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))

    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    harness.processElement(event, new Date().getTime)
    val duplicateEventStream = harness.getSideOutput(new OutputTag("duplicate-events"))
    duplicateEventStream.size() should be (1)

    val uniqueEventStream = harness.getSideOutput(new OutputTag("unique-events"))
    uniqueEventStream should be (null)

    val duplicateEvent = duplicateEventStream.asScala.head.getValue
    duplicateEvent.mid() should be ("321a6f0c-10c6-4cdc-9893-207bb64fea50")
  }

  "Duplicate check required " should "return true if producer id is defined in the inclusion list" in {
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))
    val deduplicationFunction = new DeduplicationFunction(mockConfig, dedupEngine)
    val isDuplicationCheckRequired = deduplicationFunction.isDuplicateCheckRequired(event)
    isDuplicationCheckRequired should be (true)
  }

  "Duplicate check required " should "return false if producer id is not defined in the inclusion list" in {

    when(mockConfig.includedProducersForDedup).thenReturn(List("producer.id.not.included"))
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))
    val deduplicationFunction = new DeduplicationFunction(mockConfig, dedupEngine)
    val isDuplicationCheckRequired = deduplicationFunction.isDuplicateCheckRequired(event)
    isDuplicationCheckRequired should be (false)
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }
}
