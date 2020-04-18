package org.sunbird.dp.core.cache

import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.JedisException


class DedupEngine(redisConnect: RedisConnect, store: Int, expirySeconds: Int) extends Serializable {

  private val serialVersionUID = 6089562751616425354L
  private[this] var redisConnection: Jedis = redisConnect.getConnection
  redisConnection.select(store)

  @throws[JedisException]
  def isUniqueEvent(checksum: String): Boolean = {
    var unique = false
    try {
      unique = !redisConnection.exists(checksum)
    } catch {
      case ex: JedisException =>
        ex.printStackTrace()
        val redisConn = redisConnect.getConnection(this.store)
        try {
          this.redisConnection = redisConn
          this.redisConnection.select(store)
          unique = !redisConnection.exists(checksum)
        } finally {
          if (redisConn != null) redisConn.close()
        }
    }
    unique
  }

  @throws[JedisException]
  def storeChecksum(checksum: String): Unit = {
    try
      redisConnection.setex(checksum, expirySeconds, "")
    catch {
      case ex: JedisException =>
        ex.printStackTrace()
        val redisConn = redisConnect.getConnection(this.store)
        try {
          this.redisConnection = redisConn
          this.redisConnection.select(store)
          redisConnection.setex(checksum, expirySeconds, "")
        } finally {
          if (redisConn != null) redisConn.close()
        }
    }
  }

  def getRedisConnection: Jedis = redisConnection

  def closeConnectionPool(): Unit = {
    redisConnect.closePool()
  }
}
