package org.sunbird.dp.spec

import java.lang.reflect.Type
import java.util
import java.util.Date

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.streaming.util.ProcessFunctionTestHarnesses
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.domain.Event
import org.sunbird.dp.fixture.EventFixtures
import org.sunbird.dp.functions.{TelemetryRouterFunction, TelemetryValidationFunction}
import org.sunbird.dp.task.PipelinePreprocessorConfig
import redis.embedded.RedisServer

class PipelineProcessorTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  val gson = new Gson()
  var redisServer: RedisServer = _
  var redisConnect: RedisConnect = _
  var dedupEngine: DedupEngine = _
  val mockConfig: PipelinePreprocessorConfig = Mockito.spy(new PipelinePreprocessorConfig(ConfigFactory.load()))
  val mockBaseConfig: Config = mock[Config]

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    redisServer = new RedisServer(6340)
    redisServer.start()

    when(mockBaseConfig.getString("redis.host")).thenReturn("localhost")
    when(mockBaseConfig.getInt("redis.port")).thenReturn(6340)
    when(mockBaseConfig.getInt("redis.connection.max")).thenReturn(2)
    when(mockBaseConfig.getInt("redis.connection.idle.max")).thenReturn(2)
    when(mockBaseConfig.getInt("redis.connection.idle.min")).thenReturn(1)
    when(mockBaseConfig.getInt("redis.connection.minEvictableIdleTimeSeconds")).thenReturn(120)
    when(mockBaseConfig.getInt("redis.connection.timeBetweenEvictionRunsSeconds")).thenReturn(300)
    when(mockBaseConfig.getInt("task.validation.parallelism")).thenReturn(1)
    when(mockBaseConfig.getInt("task.router.parallelism")).thenReturn(1)
    when(mockBaseConfig.getInt("task.flattener.parallelism")).thenReturn(1)

    when(mockConfig.config).thenReturn(mockBaseConfig)

    redisConnect = Mockito.spy(new RedisConnect(mockConfig))
    dedupEngine = Mockito.spy(new DedupEngine(redisConnect = redisConnect, store = 12, expirySeconds = 3600))
  }

//  "Unique events" should "be sent to unique SideOutput" in {
//
//    implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
//    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(true)
//    val telemetryValidationFunc = new TelemetryValidationFunction(mockConfig)(mapTypeInfo)
//    val harness = ProcessFunctionTestHarnesses.forProcessFunction(telemetryValidationFunc)
//    val mapType: Type = new TypeToken[util.Map[String, AnyRef]](){}.getType
//    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixtures.EVENT_WITH_MID, mapType))
//    harness.processElement(event, new Date().getTime)
//    val validEvents = harness.getSideOutput(mockConfig.validEventsOutputTag)
//    println(validEvents)
//  }

  "Unique events" should "be sent to unique SideOutput" in {

    implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    val telemetryValidationFunc = new TelemetryRouterFunction(mockConfig)(mapTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(telemetryValidationFunc)
    val mapType: Type = new TypeToken[util.Map[String, AnyRef]](){}.getType
    val event = new Event(gson.fromJson[util.Map[String, AnyRef]](EventFixtures.EVENT_WITH_MID, mapType))
    harness.processElement(event, new Date().getTime)
    val validEvents = harness.getSideOutput(mockConfig.secondaryRouteEventsOutputTag)
    println(validEvents)
  }


  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }

}