package org.ekstep.dp.spec

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses
import org.ekstep.dp.domain.Event
import org.ekstep.dp.functions.DeduplicationFunction
import org.ekstep.dp.task.DeduplicationConfig
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import java.util
import java.util.Date

import com.google.gson.reflect.TypeToken
import com.typesafe.config.Config
import org.apache.flink.streaming.api.scala.OutputTag
import org.ekstep.dp.cache.{DedupEngine, RedisConnect}
import org.ekstep.dp.fixture.EventFixture
import org.mockito.Mockito
import redis.embedded.RedisServer
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar

import scala.collection.JavaConverters._

class DeduplicationFunctionTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  val gson = new Gson()
  val mapType = new TypeToken[util.Map[String, AnyRef]](){}.getType
  var redisServer: RedisServer = _
  var redisConnect: RedisConnect = _
  var dedupEngine: DedupEngine = _
  val mockConfig: DeduplicationConfig = Mockito.spy(new DeduplicationConfig)
  val mockBaseConfig: Config = mock[Config]

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    when(mockConfig.includedProducersForDedup).thenReturn(List("sunbird.app", "sunbird.portal"))
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
    dedupEngine = Mockito.spy(new DedupEngine(redisConnect = redisConnect, store = 12, expirySeconds = 3600))
  }



  "Unique events" should "be sent to unique SideOutput" in {
    
    val deduplicationFunction = new DeduplicationFunction(mockConfig)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))
    harness.processElement(event, new Date().getTime)
    // verify(dedupEngine.storeChecksum("321a6f0c-10c6-4cdc-9893-207bb64fea50"), atMostOnce())
    val uniqueEventStream = harness.getSideOutput(new OutputTag("unique-events"))
    uniqueEventStream.size() should be (1)
    val uniqueEvent = uniqueEventStream.asScala.head.getValue
    uniqueEvent.mid() should be ("321a6f0c-10c6-4cdc-9893-207bb64fea50")
  }

  "Duplicate events" should "be sent to duplicate SideOutput" in {

    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(false)
    val deduplicationFunction = new DeduplicationFunction(mockConfig)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType))
    harness.processElement(event, new Date().getTime)
    val duplicateEventStream = harness.getSideOutput(new OutputTag("duplicate-events"))
    duplicateEventStream.size() should be (1)
    val duplicateEvent = duplicateEventStream.asScala.head.getValue
    duplicateEvent.mid() should be ("321a6f0c-10c6-4cdc-9893-207bb64fea50")
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }
}
