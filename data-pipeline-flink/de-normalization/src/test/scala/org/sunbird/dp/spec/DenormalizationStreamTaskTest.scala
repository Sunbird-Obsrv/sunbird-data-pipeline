package org.sunbird.dp.spec

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.FlatSpec
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.core.FlinkKafkaConnector
import org.sunbird.dp.domain.Event
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.task.DenormalizationStreamTask

import com.google.gson.Gson
import com.typesafe.config.ConfigFactory

import redis.embedded.RedisServer
import org.joda.time.DateTime
import org.sunbird.dp.cache.RedisConnect
import collection.JavaConverters._

class DenormalizationStreamTaskTest extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(2)
    .setNumberTaskManagers(1)
    .build)
  var redisServer: RedisServer = _
  val config = ConfigFactory.load("test.conf");
  val extConfig: DenormalizationConfig = new DenormalizationConfig(config);
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson();

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6340)
    redisServer.start()

    setupRedisTestData();
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    flinkCluster.after()
  }
  
  def setupRedisTestData() {
    
    val redisConnect = new RedisConnect(extConfig)
    
    // Insert device test data
    var jedis = redisConnect.getConnection(extConfig.deviceStore)
    jedis.hmset("264d679186d4b0734d858d4e18d4d31e", gson.fromJson(EventFixture.deviceCacheData1, new util.HashMap[String, String]().getClass))
    jedis.hmset("45f32f48592cb9bcf26bef9178b7bd20abe24932", gson.fromJson(EventFixture.deviceCacheData2, new util.HashMap[String, String]().getClass))
    jedis.close();
    
    // Insert user test data
    jedis = redisConnect.getConnection(extConfig.userStore)
    jedis.set("b7470841-7451-43db-b5c7-2dcf4f8d3b23", EventFixture.userCacheData1)
    jedis.set("610bab7d-1450-4e54-bf78-c7c9b14dbc81", EventFixture.userCacheData2)
    jedis.close();
    
    // Insert dialcode test data
    jedis = redisConnect.getConnection(extConfig.dialcodeStore)
    jedis.set("GWNI38", EventFixture.dialcodeCacheData1)
    jedis.set("PCZKA3", EventFixture.dialcodeCacheData2)
    jedis.close();
    
    // Insert content test data
    jedis = redisConnect.getConnection(extConfig.contentStore)
    jedis.set("do_31249064359802470412856", EventFixture.contentCacheData1)
    jedis.set("do_312526125187809280139353", EventFixture.contentCacheData2)
    jedis.set("do_312526125187809280139355", EventFixture.contentCacheData3)
    jedis.close();

  }

  "Extraction job pipeline" should "extract events" in {

    when(mockKafkaUtil.kafkaEventSource[Event](extConfig.inputTopic)).thenReturn(new InputSource)
    when(mockKafkaUtil.kafkaEventSink[Event](extConfig.denormSuccessTopic)).thenReturn(new DenormEventsSink)
    when(mockKafkaUtil.kafkaStringSink(extConfig.metricsTopic)).thenReturn(new MetricsEventsSink)
    val task = new DenormalizationStreamTask(extConfig, mockKafkaUtil);
    task.process()
    Thread.sleep(extConfig.metricsWindowSize + 2000); // Wait for metrics to be triggered
    DenormEventsSink.values.size should be (10)
    DenormEventsSink.values.get("mid10") should be (None)
    
    var event = DenormEventsSink.values.get("mid1").get;
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("dialcode_denorm")) should be (None)
    Option(event.flags().get("content_denorm")) should be (None)
    Option(event.flags().get("location_denorm")) should be (None)
    
    event = DenormEventsSink.values.get("mid2").get;
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("dialcode_denorm")) should be (None)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("coll_denorm")) should be (None)
    
    event = DenormEventsSink.values.get("mid3").get;
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("coll_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    
    event = DenormEventsSink.values.get("mid4").get;
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("dialcode_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("content_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("coll_denorm")) should be (None)
    
    event = DenormEventsSink.values.get("mid5").get;
    event.flags().get("device_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("user_denorm").asInstanceOf[Boolean] should be (false)
    event.flags().get("loc_denorm").asInstanceOf[Boolean] should be (true)
    event.flags().get("dialcode_denorm").asInstanceOf[Boolean] should be (true)
    Option(event.flags().get("content_denorm")) should be (None)
    Option(event.flags().get("location_denorm")) should be (None)

    // TODO: Complete the assertions
    event = DenormEventsSink.values.get("mid6").get;
    event = DenormEventsSink.values.get("mid7").get;
    event = DenormEventsSink.values.get("mid8").get;
    event = DenormEventsSink.values.get("mid9").get;
    
    MetricsEventsSink.values.size should be (5)
    val metricsMap: scala.collection.mutable.Map[String, Double] = scala.collection.mutable.Map[String, Double]();
    MetricsEventsSink.values.foreach(metricJson => {
      val metricEvent = gson.fromJson(metricJson, new util.HashMap[String, AnyRef]().getClass);
      val list = metricEvent.get("metrics").asInstanceOf[util.List[util.Map[String, AnyRef]]].asScala
      for(metric <- list) {
        metricsMap.put(metric.get("id").asInstanceOf[String], metric.get("value").asInstanceOf[Double])
      }
    })
    
    metricsMap.get("loc-cache-hit").get should be (7)
    metricsMap.get("event-expired").get should be (1)
    
    metricsMap.get("content-cache-miss").get should be (3)
    metricsMap.get("device-cache-miss").get should be (2)
    metricsMap.get("dialcode-total").get should be (3)
    metricsMap.get("dialcode-cache-miss").get should be (1)
    metricsMap.get("content-total").get should be (7)
    metricsMap.get("loc-cache-miss").get should be (3)
    metricsMap.get("user-cache-miss").get should be (4)
    metricsMap.get("dialcode-cache-hit").get should be (2)
    
    metricsMap.get("content-cache-hit").get should be (4)
    metricsMap.get("loc-total").get should be (10)
    metricsMap.get("device-cache-hit").get should be (7)
    metricsMap.get("user-cache-hit").get should be (3)
    metricsMap.get("user-total").get should be (7)
    metricsMap.get("device-total").get should be (9)

  }
  
  it should " test the optional fields in denorm config " in {
    val config = ConfigFactory.load("test2.conf");
    val extConfig: DenormalizationConfig = new DenormalizationConfig(config);
    extConfig.ignorePeriodInMonths should be (6)
    extConfig.userLoginInTypeDefault should be ("Google")
    extConfig.userSignInTypeDefault should be ("Default")
    extConfig.summaryFilterEvents.size should be (2)
    extConfig.summaryFilterEvents.contains("ME_WORKFLOW_SUMMARY") should be (true)
    extConfig.summaryFilterEvents.contains("ME_RANDOM_SUMMARY") should be (true)
  }

}

class InputSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {

    val gson = new Gson()
    EventFixture.telemetrEvents.foreach(f => {
      val eventMap = gson.fromJson(f, new util.HashMap[String, AnyRef]().getClass)
      ctx.collect(new Event(eventMap, 0));  
    })
    
  }

  override def cancel() = {

  }

}

class DerivedEventSource extends SourceFunction[Event] {

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    
  }

  override def cancel() = {

  }

}

class DenormEventsSink extends SinkFunction[Event] {

  override def invoke(value: Event): Unit = {
    synchronized {
      DenormEventsSink.values.put(value.mid(), value);
    }
  }
}

object DenormEventsSink {
  val values: scala.collection.mutable.Map[String, Event] = scala.collection.mutable.Map[String, Event]()
}

class MetricsEventsSink extends SinkFunction[String] {

  override def invoke(value: String): Unit = {
    synchronized {
      MetricsEventsSink.values.append(value);
    }
  }
}

object MetricsEventsSink {
  val values: scala.collection.mutable.Buffer[String] = scala.collection.mutable.Buffer[String]()
}