package org.sunbird.dp.core.cache

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.Duration

import com.typesafe.config.Config
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.BaseJobConfig

class RedisConnect(jobConfig: BaseJobConfig) extends java.io.Serializable {

  private val serialVersionUID = - 396824011996012513L

  val config: Config = jobConfig.config
  val redisHost: String = Option(config.getString("redis.host")).getOrElse("localhost")
  val redisPort: Int = Option(config.getInt("redis.port")).getOrElse(6379)
  private var jedisPool = new JedisPool(buildPoolConfig, redisHost, redisPort)
  private val logger = LoggerFactory.getLogger(classOf[RedisConnect])

  private def buildPoolConfig = {
    val poolConfig = new JedisPoolConfig
    poolConfig.setMaxTotal(Option(config.getInt("redis.connection.max")).getOrElse(2))
    poolConfig.setMaxIdle(Option(config.getInt("redis.connection.idle.max")).getOrElse(2))
    poolConfig.setMinIdle(Option(config.getInt("redis.connection.idle.min")).getOrElse(1))
    poolConfig.setTestOnBorrow(true)
    poolConfig.setTestOnReturn(true)
    poolConfig.setTestWhileIdle(true)
    poolConfig.setMinEvictableIdleTimeMillis(Duration.ofSeconds(Option(config.getLong("redis.connection.minEvictableIdleTimeSeconds")).getOrElse(120L)).toMillis)
    poolConfig.setTimeBetweenEvictionRunsMillis(Duration.ofSeconds(Option(config.getLong("redis.connection.timeBetweenEvictionRunsSeconds")).getOrElse(300)).toMillis)
    poolConfig.setNumTestsPerEvictionRun(3)
    poolConfig.setBlockWhenExhausted(true)
    poolConfig
  }

  def getConnection: Jedis = {
    logger.info("Obtaining new Redis connection...")
    jedisPool.getResource
  }

  def getConnection(database: Int): Jedis = {
    val conn = jedisPool.getResource
    conn.select(database)
    conn
  }

  def resetConnection(): Unit = {
    this.jedisPool.close()
    val redisHost = Option(config.getString("redis.host")).getOrElse("localhost")
    val redisPort = Option(config.getInt("redis.port")).getOrElse(6379)
    this.jedisPool = new JedisPool(buildPoolConfig, redisHost, redisPort)
  }

  def resetConnection(database: Int): Jedis = {
    resetConnection()
    getConnection(database)
  }

  def closePool(): Unit = {
    this.jedisPool.close()
  }
}
