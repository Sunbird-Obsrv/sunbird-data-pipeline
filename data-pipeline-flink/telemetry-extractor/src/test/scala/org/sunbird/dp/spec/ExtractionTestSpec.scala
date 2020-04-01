package org.sunbird.dp.spec

import java.util
import java.util.Date

import com.google.gson.Gson
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
import org.sunbird.dp.task.ExtractionConfig
import redis.embedded.RedisServer
import com.typesafe.config.ConfigFactory

class ExtractionTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  val gson = new Gson()
  var redisServer: RedisServer = _
  var redisConnect: RedisConnect = _
  var dedupEngine: DedupEngine = _
  val mockConfig: ExtractionConfig = Mockito.spy(new ExtractionConfig(ConfigFactory.load()))
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
    when(mockBaseConfig.getBoolean("task.dedup.validation.required")).thenReturn(true)
    when(mockBaseConfig.getInt("task.dedup.parallelism")).thenReturn(1)
    when(mockBaseConfig.getInt("task.extraction.parallelism")).thenReturn(1)

    when(mockConfig.config).thenReturn(mockBaseConfig)

    redisConnect = Mockito.spy(new RedisConnect(mockConfig))
    dedupEngine = Mockito.spy(new DedupEngine(redisConnect = redisConnect, store = 12, expirySeconds = 3600))
  }

  "Unique events" should "be sent to unique SideOutput" in {

    implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(true)
    val deduplicationFunction = new DeduplicationFunction(mockConfig)(mapTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    val eventData = gson.fromJson(EventFixture.EVENT_WITH_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    harness.processElement(eventData, new Date().getTime)
    val uniqueEvents = harness.getSideOutput(new OutputTag(mockConfig.UNIQUE_EVENTS_OUTPUT_TAG))
    uniqueEvents.size() should be(1)
  }

  "Duplicate events" should "be sent to duplicate SideOutput" in {

    implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(false)
    val deduplicationFunction = new DeduplicationFunction(mockConfig)(mapTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction)
    val eventData = gson.fromJson(EventFixture.EVENT_WITH_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    harness.processElement(eventData, new Date().getTime)
    val uniqueEvents = harness.getSideOutput(new OutputTag(mockConfig.DUPLICATE_EVENTS_OUTPUT_TAG))
    uniqueEvents.size() should be(1)
  }


  "Event" should "be sent to unique SideOutput when mid is not defined" in {

    implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    when(dedupEngine.isUniqueEvent("321a6f0c-10c6-4cdc-9893-207bb64fea50")).thenReturn(true)
    val deduplicationFunction = new DeduplicationFunction(mockConfig, dedupEngine)(mapTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(deduplicationFunction);
    val eventData = gson.fromJson(EventFixture.EVENT_WITHOUT_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    harness.processElement(eventData, new Date().getTime)
    val uniqueEvents = harness.getSideOutput(OutputTag(mockConfig.UNIQUE_EVENTS_OUTPUT_TAG))
    uniqueEvents.size() should be(1)
  }

  "Extracted telemetry events" should "Audit event should have proper did, mid, pdata and all extractted events should stamp with syncts " in {
    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    val extractFunction = new ExtractionFunction(mockConfig)(stringTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(extractFunction)
    val eventData = gson.fromJson(EventFixture.MISSING_FIELDS_BATCH, new util.LinkedHashMap[String, AnyRef]().getClass)

    harness.processElement(eventData, new Date().getTime)
    val extractedEvents = harness.getSideOutput(OutputTag(mockConfig.RAW_EVENTS_OUTPUT_TAG))
    val extractedEventsList = gson.fromJson(gson.toJson(extractedEvents), (new util.ArrayList[util.Map[String, AnyRef]]()).getClass)
    // All Extracted Events SyncTs should be same as Batch Event SyncTs
    extractedEventsList.forEach(event => {
      val eventObj = event.asInstanceOf[util.Map[String, AnyRef]].get("value").asInstanceOf[String]
      val eventMap = gson.fromJson(eventObj, new util.LinkedHashMap[String, AnyRef]().getClass)
      val extractedEventSyncTs = eventMap.get("syncts")
      extractedEventSyncTs should not be(null)

    })
    // Log event size should be one
    val log = harness.getSideOutput(new OutputTag(mockConfig.LOG_EVENTS_OUTPUT_TAG))
    log.size() should be(1)
    val auditEventList = gson.fromJson(gson.toJson(log), (new util.ArrayList[AnyRef]()).getClass)
    val logEvents = Option(auditEventList.get(0)).map(x => x.asInstanceOf[util.Map[String, AnyRef]]).get.get("value")
    val total_events_count = logEvents.asInstanceOf[util.Map[String, AnyRef]].get("edata")
      .asInstanceOf[util.Map[String, AnyRef]].get("params").asInstanceOf[util.ArrayList[util.Map[String, AnyRef]]].get(0).get("events_count").asInstanceOf[Double]
    // Total Events count in the batch should be 20 from the audit event
    total_events_count should be(20.0)
    val logEventsMap = gson.fromJson(gson.toJson(logEvents), new util.LinkedHashMap[String, AnyRef]().getClass)
    val logEventContext = Option(logEventsMap.get("context").asInstanceOf[util.Map[String, AnyRef]])
    val logEventDeviceId = logEventContext map {
      event => event.get("did")
    }
    val channel = logEventContext map {
      event => event.get("channel")
    }
    // log event mid should be batch event mid
    logEventsMap.get("mid") should be("5734970")
    // Log event device id should be batch event device id
    logEventDeviceId.get should be("0958743690")
    // Log event consumer id should be batch event channel
    channel.get should be("98347593475834")
    // Total Extracted events size should be 20
    extractedEvents.size() should be(20)


  }

  "Extracted telemetry events" should "be sent to raw SideOutput and should validate the syncTs and EventsCount" in {

    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    val extractFunction = new ExtractionFunction(mockConfig)(stringTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(extractFunction)
    val eventData = gson.fromJson(EventFixture.EVENT_WITH_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    val batchEventSyncTs = eventData.get("syncts").asInstanceOf[Number].longValue()
    harness.processElement(eventData, new Date().getTime)
    val extractedEvents = harness.getSideOutput(OutputTag(mockConfig.RAW_EVENTS_OUTPUT_TAG))
    val extractedEventsList = gson.fromJson(gson.toJson(extractedEvents), (new util.ArrayList[util.Map[String, AnyRef]]()).getClass)
    // All Extracted Events SyncTs should be same as Batch Event SyncTs
    extractedEventsList.forEach(event => {
      val eventObj = event.asInstanceOf[util.Map[String, AnyRef]].get("value").asInstanceOf[String]
      val eventMap = gson.fromJson(eventObj, new util.LinkedHashMap[String, AnyRef]().getClass)
      val extractedEventSyncTs = eventMap.get("syncts").asInstanceOf[Number].longValue()
      val flags = eventMap.get("flags").asInstanceOf[util.Map[String, AnyRef]]
      flags.get("ex_processed").asInstanceOf[Boolean] should be(true)
      batchEventSyncTs should be(extractedEventSyncTs)
    })
    // Log event size should be one
    val log = harness.getSideOutput(new OutputTag(mockConfig.LOG_EVENTS_OUTPUT_TAG))
    log.size() should be(1)
    val auditEventList = gson.fromJson(gson.toJson(log), (new util.ArrayList[AnyRef]()).getClass)
    val total_events = Option(auditEventList.get(0)).map(x => x.asInstanceOf[util.Map[String, AnyRef]]).get.get("value")
      .asInstanceOf[util.Map[String, AnyRef]].get("edata")
      .asInstanceOf[util.Map[String, AnyRef]].get("params").asInstanceOf[util.ArrayList[util.Map[String, AnyRef]]].get(0).get("events_count").asInstanceOf[Double]
    // Total Events count in the batch should be 20 from the audit event
    total_events should be(20.0)
    // Total Extracted events size should be 20
    extractedEvents.size() should be(20)


  }

  "Failed extracted events" should "be sent to failed SideOutput" in {

    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    when(mockConfig.eventMaxSize).thenReturn(500L)
    val extractFunction = new ExtractionFunction(mockConfig)(stringTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(extractFunction)
    val eventData = gson.fromJson(EventFixture.EVENT_WITH_MESSAGE_ID, new util.LinkedHashMap[String, AnyRef]().getClass)
    harness.processElement(eventData, new Date().getTime)
    val log = harness.getSideOutput(new OutputTag(mockConfig.LOG_EVENTS_OUTPUT_TAG))
    log.size() should be(1)
    val auditEventList = gson.fromJson(gson.toJson(log), (new util.ArrayList[AnyRef]()).getClass)
    val total_events = Option(auditEventList.get(0)).map(x => x.asInstanceOf[util.Map[String, AnyRef]]).get.get("value")
      .asInstanceOf[util.Map[String, AnyRef]].get("edata")
      .asInstanceOf[util.Map[String, AnyRef]].get("params").asInstanceOf[util.ArrayList[util.Map[String, AnyRef]]].get(0).get("events_count").asInstanceOf[Double]
    total_events should be(20)

    val failedEvent = gson.fromJson(gson.toJson(harness.getSideOutput(new OutputTag(mockConfig.FAILED_EVENTS_OUTPUT_TAG))), (new util.ArrayList[AnyRef]()).getClass)
    getFlags(failedEvent) should be(false)
    // Should get the falgs are present or not.
    // ex_processed flag should be flase
    failedEvent.size() should be(20)
  }

  "When Events Are Empty in the Batch" should "not fail" in {
    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    when(mockConfig.eventMaxSize).thenReturn(500L)
    val extractFunction = new ExtractionFunction(mockConfig)(stringTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(extractFunction)
    val eventData = gson.fromJson(EventFixture.EMPTY_BATCH_EVENTS, new util.LinkedHashMap[String, AnyRef]().getClass)
    harness.processElement(eventData, new Date().getTime)
    val failedEvent = harness.getSideOutput(new OutputTag(mockConfig.FAILED_EVENTS_OUTPUT_TAG))
    failedEvent should be(null)
    val extractedEvents = harness.getSideOutput(OutputTag(mockConfig.RAW_EVENTS_OUTPUT_TAG))
    extractedEvents should be(null)
    val log = harness.getSideOutput(new OutputTag(mockConfig.LOG_EVENTS_OUTPUT_TAG))
    log.size() should be(1)
    val auditEventList = gson.fromJson(gson.toJson(log), (new util.ArrayList[AnyRef]()).getClass)
    val total_events = Option(auditEventList.get(0)).map(x => x.asInstanceOf[util.Map[String, AnyRef]]).get.get("value")
      .asInstanceOf[util.Map[String, AnyRef]].get("edata")
      .asInstanceOf[util.Map[String, AnyRef]].get("params").asInstanceOf[util.ArrayList[util.Map[String, AnyRef]]].get(0).get("events_count").asInstanceOf[Double]
    total_events should be(0)
  }

  "When Events Are Undefined in the Batch" should "fail the job" in {

    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    when(mockConfig.eventMaxSize).thenReturn(500L)
    val extractFunction = new ExtractionFunction(mockConfig)(stringTypeInfo)
    val harness = ProcessFunctionTestHarnesses.forProcessFunction(extractFunction)
    val eventData = gson.fromJson(EventFixture.UNDEFINED_EVENTS_IN_BATCH, new util.LinkedHashMap[String, AnyRef]().getClass)
    harness.processElement(eventData, new Date().getTime)
    // Failed event count should be zero
    val failedEvent = harness.getSideOutput(new OutputTag(mockConfig.FAILED_EVENTS_OUTPUT_TAG))
    failedEvent should be(null)
    // Raw event count should be zero
    val extractedEvents = harness.getSideOutput(OutputTag(mockConfig.RAW_EVENTS_OUTPUT_TAG))
    extractedEvents should be(null)
    // Log event count should be one
    val log = harness.getSideOutput(new OutputTag(mockConfig.LOG_EVENTS_OUTPUT_TAG))
    log.size() should be(1)

    val auditEventList = gson.fromJson(gson.toJson(log), (new util.ArrayList[AnyRef]()).getClass)
    val total_events = Option(auditEventList.get(0)).map(x => x.asInstanceOf[util.Map[String, AnyRef]]).get.get("value")
      .asInstanceOf[util.Map[String, AnyRef]].get("edata")
      .asInstanceOf[util.Map[String, AnyRef]].get("params").asInstanceOf[util.ArrayList[util.Map[String, AnyRef]]].get(0).get("events_count").asInstanceOf[Double]
    // Total Events in the batch should be Zero
    total_events should be(0)
  }



  def getFlags(failedEvent: util.ArrayList[AnyRef]): Boolean = {
    gson.fromJson(Option(failedEvent.get(0)).map(x => x.asInstanceOf[util.Map[String, AnyRef]])
      .get.get("value").toString, new util.LinkedHashMap[String, AnyRef]().getClass).get("flags").asInstanceOf[util.Map[String, Boolean]].get("ex_processed")
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }

}