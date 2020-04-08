package org.ekstep.dp

import java.util

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.{BaseJobConfig, DataCache}
import redis.clients.jedis.exceptions.{JedisConnectionException, JedisException}
import redis.embedded.RedisServer

class CacheTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {
  var redisServer: RedisServer = _

  override def beforeAll() {
    super.beforeAll()
    redisServer = new RedisServer(6341)
    redisServer.start()
    redisServer
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }

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

}
