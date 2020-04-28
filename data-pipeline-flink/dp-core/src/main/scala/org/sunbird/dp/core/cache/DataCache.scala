package org.sunbird.dp.core.cache

import java.util

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.BaseJobConfig
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.{JedisConnectionException, JedisException}

import scala.collection.JavaConverters._
import scala.collection.mutable.Map

class DataCache(val config: BaseJobConfig, val redisConnect: RedisConnect, val dbIndex: Int, val fields: List[String]) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DataCache])
  private var redisConnection: Jedis = _
  val gson = new Gson()

  def init() {
    this.redisConnection = redisConnect.getConnection(dbIndex)
  }

  def close() {
    this.redisConnection.close()
    redisConnect.closePool()
  }

  def hgetAllWithRetry(key: String): Map[String, String] = {
    try {
      hgetAll(key)
    } catch {
      case ex: JedisException =>
        logger.error("Exception when retrieving data from redis cache", ex)
        redisConnect.resetConnection()
        this.redisConnection = redisConnect.getConnection(dbIndex)
        hgetAll(key)
    }

  }

  private def hgetAll(key: String): Map[String, String] = {
    val dataMap = redisConnection.hgetAll(key)
    if (dataMap.size() > 0) {
      dataMap.keySet().retainAll(fields.asJava)
      dataMap.values().removeAll(util.Collections.singleton(""))
      dataMap.asScala
    } else {
      Map[String, String]()
    }
  }

  def getWithRetry(key: String): Map[String, AnyRef] = {
    try {
      get(key)
    } catch {
      case ex: JedisException =>
        logger.error("Exception when retrieving data from redis cache", ex)
        redisConnect.resetConnection()
        this.redisConnection = redisConnect.getConnection(dbIndex)
        get(key)
    }

  }

  private def get(key: String): Map[String, AnyRef] = {
    val data = redisConnection.get(key)
    if (data != null && !data.isEmpty()) {
      val dataMap = gson.fromJson(data, new util.HashMap[String, AnyRef]().getClass)
      dataMap.keySet().retainAll(fields.asJava)
      dataMap.values().removeAll(util.Collections.singleton(""))
      val map = dataMap.asScala
      map.map(f => {
        (f._1.toLowerCase().replace("_", ""), f._2)
      })
    } else {
      Map[String, AnyRef]()
    }
  }

  def getMultipleWithRetry(keys: List[String]): List[Map[String, AnyRef]] = {
    for (key <- keys) yield {
      getWithRetry(key)
    }
  }

  def isExists(key: String): Boolean = {
    redisConnection.exists(key)
  }

  def hmSet(key: String, value: util.Map[String, String]): Unit = {
    try {
      redisConnection.hmset(key, value)
    } catch {
      // Write testcase for catch block
      // $COVERAGE-OFF$ Disabling scoverage
      case ex: JedisException => {
        println("dataCache")
        logger.error("Exception when inserting data to redis cache", ex)
        redisConnect.resetConnection()
        this.redisConnection = redisConnect.getConnection(dbIndex)
        this.redisConnection.hmset(key, value)
      }
    }
  }

  def setWithRetry(key: String, value: String): Unit = {
    try {
      set(key, value);
    } catch {
      case ex @ (_ : JedisConnectionException | _ : JedisException) =>
        logger.error("Exception when update data to redis cache", ex)
        redisConnect.resetConnection();
        this.redisConnection = redisConnect.getConnection(dbIndex);
        set(key, value)
    }
  }

  def set(key: String, value: String): Unit = {
    redisConnection.set(key, value)
  }

}
// $COVERAGE-ON$