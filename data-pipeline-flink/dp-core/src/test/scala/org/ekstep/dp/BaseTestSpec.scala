package org.ekstep.dp

import com.typesafe.config.ConfigFactory
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.cache.RedisConnect
import org.sunbird.dp.core.BaseJobConfig
import redis.clients.jedis.exceptions.JedisConnectionException
import redis.embedded.RedisServer

class BaseTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {
  var redisServer: RedisServer = _

  override def beforeAll() {
    super.beforeAll()
    redisServer = new RedisServer(6341)
    redisServer.start()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }

  "RedisConnection" should "Should able to connect to redis" in {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
  }

  it should "Should able to reset the redis connection" in {
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
    val resetStatus = redisConnection.resetConnection(2)
    resetStatus.isConnected should be(true)
  }

  it should "Should able to close the redis connection" in intercept[JedisConnectionException]{
    val config = ConfigFactory.load("test.conf");
    val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
    val redisConnection = new RedisConnect(bsConfig)
    val status = redisConnection.getConnection(2)
    status.isConnected should be(true)
    redisConnection.closePool
    val reConnectionStatus = redisConnection.getConnection(2)
    reConnectionStatus.isConnected should be(false)
  }

}
