package org.ekstep.dp.cache

import redis.clients.jedis.Jedis
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig
import java.time.Duration

import com.typesafe.config.Config
import org.ekstep.dp.core.BaseJobConfig
import org.slf4j.LoggerFactory


class RedisConnect(jobConfig: BaseJobConfig) extends java.io.Serializable {

  private val serialVersionUID = - 396824011996012513L

  val config: Config = jobConfig.config
  val redis_host: String = Option(config.getString("redis.host")).getOrElse("localhost")
  val redis_port: Int = Option(config.getInt("redis.port")).getOrElse(6379)
  private var jedisPool = new JedisPool(buildPoolConfig, redis_host, redis_port)
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
    val redis_host = Option(config.getString("redis.host")).getOrElse("localhost")
    val redis_port = Option(config.getInt("redis.port")).getOrElse(6379)
    this.jedisPool = new JedisPool(buildPoolConfig, redis_host, redis_port)
  }

  def resetConnection(database: Int): Jedis = {
    resetConnection()
    getConnection(database)
  }
}
