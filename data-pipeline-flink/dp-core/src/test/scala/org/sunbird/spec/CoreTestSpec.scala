package org.sunbird.spec

import java.util

import com.google.gson.Gson
import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.{BaseDeduplication, BaseJobConfig, DataCache}
import org.sunbird.dp.domain.Events
import org.sunbird.dp.serde._
import org.sunbird.fixture.EventFixture
import redis.clients.jedis.exceptions.{JedisConnectionException, JedisException}

class CoreTestSpec extends BaseSpec with Matchers with MockitoSugar {

  "RedisConnection" should "Able to connect to redis" in {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
  }

  it should "Able to reset the redis connection" in {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
    val resetStatus = redisConnection.resetConnection(2)
    resetStatus.isConnected should be(true)
  }

  it should "Able to close the redis connection" in intercept[JedisConnectionException] {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
    redisConnection.closePool
    val reConnectionStatus = redisConnection.getConnection(2)
    reConnectionStatus.isConnected should be(false)
  }

  "De-DupEngine" should "Able to detect the key is unique or not" in {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)

    val dedupEngine = new DedupEngine(redisConnection, 2, 200)
    dedupEngine.isUniqueEvent("key-1") should be(true)
    dedupEngine.storeChecksum("key-1")
    dedupEngine.isUniqueEvent("key-1") should be(false)

  }

  it should "Able to reConnect when the jedis exception thrown" in intercept[JedisException] {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    redisConnection.closePool()
    val dedupEngine = new DedupEngine(redisConnection, 0, 4309535)
    dedupEngine.isUniqueEvent("event-id-3") should be(true)
  }
  "DataCache" should "hgetAllWithRetry method should bble to get the map data" in {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
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
    hGetResponse.get("country_code").get should be("IN")
    hGetResponse.get("country").get should be("INDIA")
    dataCache.close()
  }

  "BaseDedup" should "Should able to deDup the event" in {
    val deDup = new BaseDeduplication {}
    val metrics = deDup.deduplicationMetrics
    metrics.size should be(2)
    val event = new util.HashMap[String, AnyRef]()
    event.put("country_code", "IN")
    event.put("country", "INDIA")
    deDup.updateFlag[util.Map[String, AnyRef]](event, "test-failed", true)
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    val dedupEngine = new DedupEngine(redisConnection, 2, 200)
    dedupEngine.storeChecksum("key-1")
    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    val dedupTag: OutputTag[String] = OutputTag[String]("test-de-dup-tag")
    val uniqueTag: OutputTag[String] = OutputTag[String]("test-unique-tag")
    // deDup.deDup[String]("key-1","test",null, dedupTag, uniqueTag, "test")(dedupEngine, JobMetrics.apply(List("success-count")))
  }
  "StringSerialization" should "Able to serialize the data" in {
    val topic: String = "topic-test"
    val partition: Int = 0
    val offset: Long = 1
    val group: String = "group-test"
    val key: Array[Byte] = null
    val value: Array[Byte] = Array[Byte](1)
    val stringDeSerialization = new StringDeserializationSchema()
    val stringSerialization = new StringSerializationSchema(topic)
    val eventSerialization = new EventSerializationSchema[Events](topic)
    val eventDeSerialization = new EventDeserializationSchema[Events]
    val mapSerialization: MapSerializationSchema = new MapSerializationSchema(topic)
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
}

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {
  def markSuccess(flagName: String): Unit = {
    telemetry.addFieldIfAbsent("flags", new util.HashMap[String, Boolean])
    telemetry.add(s"flags.$flagName", true)
    telemetry.add("type", "events")
  }
}
