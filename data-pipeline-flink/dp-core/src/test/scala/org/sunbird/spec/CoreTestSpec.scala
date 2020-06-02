package org.sunbird.spec

import java.util

import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.contentupdater.core.util.RestUtil
import org.sunbird.dp.core.cache.{DataCache, DedupEngine, RedisConnect}
import org.sunbird.dp.core.domain.Events
import org.sunbird.dp.core.job.{BaseDeduplication, BaseJobConfig}
import org.sunbird.dp.core.serde._
import org.sunbird.dp.core.util.FlinkUtil
import org.sunbird.fixture.EventFixture
import redis.clients.jedis.exceptions.{JedisConnectionException, JedisDataException, JedisException}

class CoreTestSpec extends BaseSpec with Matchers with MockitoSugar {

  val config: Config = ConfigFactory.load("base-test.conf")
  val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job")

  "RedisConnect functionality" should "be able to connect to redis" in {
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
  }

  it should "be able to reset the redis connection" in {
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
    val resetStatus = redisConnection.resetConnection(2)
    resetStatus.isConnected should be(true)
  }

  it should "be able to close the redis connection" in intercept[JedisConnectionException] {
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
    redisConnection.closePool()
    val reConnectionStatus = redisConnection.getConnection(2)
    reConnectionStatus.isConnected should be(false)
  }

  "DedupEngine functionality" should "be able to identify if the key is unique or duplicate & it should able throw jedis excption for invalid action" in  intercept[JedisException] {
    val redisConnection = new RedisConnect(bsConfig)
    val dedupEngine = new DedupEngine(redisConnection, 2, 200)
    dedupEngine.getRedisConnection should not be (null)
    dedupEngine.isUniqueEvent("key-1") should be(true)
    dedupEngine.storeChecksum("key-1")
    dedupEngine.isUniqueEvent("key-1") should be(false)
    dedupEngine.isUniqueEvent(null)
  }

  it should "be able to reconnect when a jedis exception for invalid action is thrown" in intercept[JedisException] {
    val redisConnection = new RedisConnect(bsConfig)
    val dedupEngine = new DedupEngine(redisConnection, 0, 4309535)
    dedupEngine.isUniqueEvent("event-id-3") should be(true)
    dedupEngine.storeChecksum(null)
    dedupEngine.getRedisConnection should not be(null)
  }

  "DataCache hgetAllWithRetry function" should "be able to retrieve the map data from Redis" in {
    val redisConnection = new RedisConnect(bsConfig)
    val map = new util.HashMap[String, String]()
    map.put("country_code", "IN")
    map.put("country", "INDIA")
    redisConnection.getConnection(2).hmset("5d8947598347589fsdlh43y8", map)
    redisConnection.getConnection(2).set("56934dufhksjd8947hdskj", "{\"actor\":{\"type\":\"User\",\"id\":\"4c4530df-0d4f-42a5-bd91-0366716c8c24\"}}")

    val deviceFields = List("country_code", "country", "state_code", "st" +
      "ate", "city", "district_custom", "state_code_custom",
      "state_custom", "user_declared_state", "user_declared_district", "devicespec", "firstaccess")
    val dataCache = new DataCache(bsConfig, new RedisConnect(bsConfig), 2, deviceFields)
    dataCache.init()
    val hGetResponse = dataCache.hgetAllWithRetry("5d8947598347589fsdlh43y8")
    val hGetResponse2 = dataCache.getMultipleWithRetry(List("56934dufhksjd8947hdskj"))
    hGetResponse2.size should be(1)
    hGetResponse.size should be(2)
    hGetResponse("country_code") should be("IN")
    hGetResponse("country") should be("INDIA")
    dataCache.close()
  }

  "BaseDedup functionality" should "be able to identify if an event is unique or duplicate" in {
    val deDup = new BaseDeduplication {}
    val metrics = deDup.deduplicationMetrics
    metrics.size should be(2)
    val event = new util.HashMap[String, AnyRef]()
    event.put("country_code", "IN")
    event.put("country", "INDIA")
    deDup.updateFlag[util.Map[String, AnyRef], util.Map[String, AnyRef]](event, "test-failed", true)
    val redisConnection = new RedisConnect(bsConfig)
    val dedupEngine = new DedupEngine(redisConnection, 2, 200)
    dedupEngine.storeChecksum("key-1")
    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    val dedupTag: OutputTag[String] = OutputTag[String]("test-de-dup-tag")
    val uniqueTag: OutputTag[String] = OutputTag[String]("test-unique-tag")
  }

  "StringSerialization functionality" should "be able to serialize the input data as String" in {
    val topic: String = "topic-test"
    val partition: Int = 0
    val offset: Long = 1
    val group: String = "group-test"
    val key: Array[Byte] = null
    val value: Array[Byte] = Array[Byte](1)
    val stringDeSerialization = new StringDeserializationSchema()
    val stringSerialization = new StringSerializationSchema(topic, Some("kafka-key"))
    val eventSerialization = new EventSerializationSchema[Events](topic)
    val eventDeSerialization = new EventDeserializationSchema[Events]
    val mapSerialization: MapSerializationSchema = new MapSerializationSchema(topic, Some("kafka-key"))
    val mapDeSerialization = new MapDeserializationSchema()
    import org.apache.kafka.clients.consumer.ConsumerRecord
    val cRecord: ConsumerRecord[Array[Byte], Array[Byte]] = new ConsumerRecord[Array[Byte], Array[Byte]](topic, partition, offset, key, value)
    stringDeSerialization.deserialize(cRecord)
    val event = new Event(new Gson().fromJson(EventFixture.SAMPLE_EVENT_1, new util.LinkedHashMap[String, AnyRef]().getClass))
    eventSerialization.serialize(event, System.currentTimeMillis())
    eventDeSerialization.getProducedType
    stringSerialization.serialize("test", System.currentTimeMillis())
    stringDeSerialization.isEndOfStream("") should be(false)
    val map = new util.HashMap[String, AnyRef]()
    map.put("country_code", "IN")
    map.put("country", "INDIA")
    mapSerialization.serialize(map, System.currentTimeMillis())
  }

  "DataCache" should "be able to add the data into redis" in intercept[JedisDataException]{
    val redisConnection = new RedisConnect(bsConfig)
    val deviceData = new util.HashMap[String, String]()
    deviceData.put("country_code", "IN")
    deviceData.put("country", "INDIA")

    val deviceFields = List("country_code", "country", "state_code", "st" +
      "ate", "city", "district_custom", "state_code_custom",
      "state_custom", "user_declared_state", "user_declared_district", "devicespec", "firstaccess")
    val dataCache = new DataCache(bsConfig, redisConnection, 2, deviceFields)
    dataCache.init()
    dataCache.isExists("device_10") should be(false)
    dataCache.hmSet("device_1", deviceData)
    val redisData = dataCache.hgetAllWithRetry("device_1")
    redisData.size should be(2)
    redisData should not be (null)
    redisConnection.closePool()
    dataCache.hmSet("device_1", deviceData)
    dataCache.getWithRetry(null)
  }


  "DataCache" should "thorw an jedis exception when invalid action happen" in  intercept[JedisDataException]{
    val redisConnection = new RedisConnect(bsConfig)
    val dataCache = new DataCache(bsConfig, redisConnection, 2, List())
    dataCache.init()
    dataCache.hgetAllWithRetry(null)
  }

  "DataCache setWithRetry function" should "be able to set the data from Redis" in {
    val redisConnection = new RedisConnect(bsConfig)
    val dataCache = new DataCache(bsConfig, redisConnection, 4, List("identifier"))
    dataCache.init()
    dataCache.setWithRetry("key", "{\"test\": \"value\"}")
    redisConnection.getConnection(4).get("key") should equal("{\"test\": \"value\"}")
  }

  "RestUtil functionality" should "be able to return response" in {
    val restUtil = new RestUtil()
    val url = "https://httpbin.org/json";
    val response = restUtil.get(url);
    response should not be null
  }

  "FilnkUtil" should "get the flink util context" in {
    val config = ConfigFactory.empty()
    config.entrySet()

    val customConf =
      ConfigFactory.parseString(EventFixture.customConfig)

    val flinkConfig: BaseJobConfig = new BaseJobConfig(customConf, "base-job")
    val context: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(flinkConfig)
    context should not be (null)
  }

}

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {
  def markSuccess(flagName: String): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add(s"flags.$flagName", true)
    telemetry.add("type", "events")
  }

  override def kafkaKey(): String = {
    super.kafkaKey()
  }
}
