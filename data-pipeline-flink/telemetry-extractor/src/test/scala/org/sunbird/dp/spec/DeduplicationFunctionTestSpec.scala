package org.sunbird.dp.spec

import java.util
import java.util.Date

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.functions.{DeduplicationFunction, ExtractionFunction}
import org.sunbird.dp.task.DeduplicationConfig
import redis.embedded.RedisServer

class DeduplicationFunctionTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val eventTypeInfo: TypeInformation[Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[Map[String, AnyRef]])
  val gson = new Gson()
  val mapType = new TypeToken[Map[String, Any]]() {}.getType
  var redisServer: RedisServer = _
  var redisConnect: RedisConnect = _
  var dedupEngine: DedupEngine = _
  val mockConfig: DeduplicationConfig = Mockito.spy(new DeduplicationConfig)
  val mockBaseConfig: Config = mock[Config]

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    when(mockConfig.isDuplicationCheckRequired).thenReturn(true)
    redisServer = new RedisServer(6340)
    redisServer.start()

    when(mockBaseConfig.getString("redis.host")).thenReturn("localhost")
    when(mockBaseConfig.getInt("redis.port")).thenReturn(6340)
    when(mockBaseConfig.getLong("kafka.event.max.size")).thenReturn(2000L)
    when(mockBaseConfig.getInt("redis.connection.max")).thenReturn(2)
    when(mockBaseConfig.getInt("redis.connection.idle.max")).thenReturn(2)
    when(mockBaseConfig.getInt("redis.connection.idle.min")).thenReturn(1)
    when(mockBaseConfig.getInt("redis.connection.minEvictableIdleTimeSeconds")).thenReturn(120)
    when(mockBaseConfig.getInt("redis.connection.timeBetweenEvictionRunsSeconds")).thenReturn(300)

    when(mockConfig.config).thenReturn(mockBaseConfig)
    redisConnect = Mockito.spy(new RedisConnect(mockConfig))
    dedupEngine = Mockito.spy(new DedupEngine(redisConnect = redisConnect, store = 12, expirySeconds = 3600))
  }


  //  "Unique events" should "be sent to unique SideOutput" in {
  //    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  //    val deduplicationFunction = new DeduplicationFunction(mockConfig)
  //    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
  //    val event = gson.fromJson[Map[String, AnyRef]](EventFixture.EVENT_WITH_MID, mapType)
  //    harness.processElement(event, new Date().getTime)
  //    // verify(dedupEngine.storeChecksum("321a6f0c-10c6-4cdc-9893-207bb64fea50"), atMostOnce())
  //    val uniqueEventStream = harness.getSideOutput(new OutputTag("unique-events"))
  //    uniqueEventStream.size() should be (1)
  //    val uniqueEvent = uniqueEventStream.asScala.head.getValue
  //   // uniqueEvent.mid() should be ("321a6f0c-10c6-4cdc-9893-207bb64fea50")
  //  }

  "De-Dup" should "be sent to uniq SideOutput" in {
    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(true)
    val deduplicationFunction = new DeduplicationFunction(mockConfig)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction);
    val ress = gson.fromJson(EventFixture.EVENT_WITH_MID, (new util.LinkedHashMap[String, AnyRef]()).getClass);
    harness.processElement(ress, new Date().getTime)
    val uniquEvents = harness.getSideOutput(new OutputTag("unique-events"))
    uniquEvents.size() should be(1)
  }
  "De-Dup" should "be sent to duplicate SideOutput" in {
    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(false)
    val deduplicationFunction = new DeduplicationFunction(mockConfig)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction);
    val ress = gson.fromJson(EventFixture.EVENT_WITH_MID, (new util.LinkedHashMap[String, AnyRef]()).getClass);
    harness.processElement(ress, new Date().getTime)
    val uniquEvents = harness.getSideOutput(new OutputTag("duplicate-events"))
    uniquEvents.size() should be(1)
  }
  "Extract events" should "be sent to extract SideOutput" in {

    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(true)
    val extractFunction = new ExtractionFunction(mockConfig)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(extractFunction);
    val ress = gson.fromJson(EventFixture.EVENT_WITH_MID, (new util.LinkedHashMap[String, AnyRef]()).getClass);
    harness.processElement(ress, new Date().getTime)
    val extractedEvents = harness.getSideOutput(new OutputTag("raw-events"))
    val log = harness.getSideOutput(new OutputTag("log-events"))
    println(gson.toJson(log))
    extractedEvents.size() should be(20)
  }
  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }
}