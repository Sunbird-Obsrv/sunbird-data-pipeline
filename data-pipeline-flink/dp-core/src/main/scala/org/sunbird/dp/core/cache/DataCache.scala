package org.sunbird.dp.core.cache

import java.util

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.BaseJobConfig
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.{JedisConnectionException, JedisException}

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.Map
import scala.collection.immutable

class DataCache(val config: BaseJobConfig, val redisConnect: RedisConnect, val dbIndex: Int, val fields: List[String]) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DataCache])
  private var redisConnection: Jedis = _
  val gson = new Gson()

  def init() {
    this.redisConnection = redisConnect.getConnection(dbIndex)
  }

  def close() {
    this.redisConnection.close()
  }

  def hgetAllWithRetry(key: String): mutable.Map[String, AnyRef] = {
    try {
      convertToComplexDataTypes(hgetAll(key))
    } catch {
      case ex: JedisException =>
        logger.error("Exception when retrieving data from redis cache", ex)
        this.redisConnection.close()
        this.redisConnection = redisConnect.getConnection(dbIndex)
        convertToComplexDataTypes(hgetAll(key))
    }
  }

  def isArray(value: String): Boolean = {
    val redisValue = value.trim
    redisValue.length > 0 && redisValue.startsWith("[")
  }

   def isObject(value: String) = {
     val redisValue = value.trim
     redisValue.length > 0 && redisValue.startsWith("{")
   }

  def convertToComplexDataTypes(data: mutable.Map[String, String]): mutable.Map[String, AnyRef] = {
    val result = mutable.Map[String, AnyRef]()
    data.keys.map {
      redisKey =>
        val redisValue = data(redisKey)
        if(isArray(redisValue)) {
          result += redisKey -> gson.fromJson(redisValue, new util.ArrayList[AnyRef]().getClass)
        } else if (isObject(redisValue)) {
          result += redisKey -> gson.fromJson(redisValue, new util.HashMap[String, AnyRef]().getClass)
        } else {
          result += redisKey -> redisValue
        }
    }
    result
  }

  private def hgetAll(key: String): mutable.Map[String, String] = {
    val dataMap = redisConnection.hgetAll(key)
    if (dataMap.size() > 0) {
      if(fields.nonEmpty) dataMap.keySet().retainAll(fields.asJava)
      dataMap.values().removeAll(util.Collections.singleton(""))
      dataMap.asScala
    } else {
      mutable.Map[String, String]()
    }
  }

  def getWithRetry(key: String): mutable.Map[String, AnyRef] = {
    try {
      get(key)
    } catch {
      case ex: JedisException =>
        logger.error("Exception when retrieving data from redis cache", ex)
        this.redisConnection.close()
        this.redisConnection = redisConnect.getConnection(dbIndex)
        get(key)
    }

  }

  private def get(key: String): mutable.Map[String, AnyRef] = {
    val data = redisConnection.get(key)
    if (data != null && !data.isEmpty) {
      val dataMap = gson.fromJson(data, new util.HashMap[String, AnyRef]().getClass)
      if(fields.nonEmpty) dataMap.keySet().retainAll(fields.asJava)
      dataMap.values().removeAll(util.Collections.singleton(""))
      dataMap.asScala
    } else {
      mutable.Map[String, AnyRef]()
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
        this.redisConnection.close()
        this.redisConnection = redisConnect.getConnection(dbIndex)
        this.redisConnection.hmset(key, value)
      }
    }
  }

  def setWithRetry(key: String, value: String): Unit = {
    try {
      set(key, value);
    } catch {
      case ex@(_: JedisConnectionException | _: JedisException) =>
        logger.error("Exception when update data to redis cache", ex)
        this.redisConnection.close()
        this.redisConnection = redisConnect.getConnection(dbIndex);
        set(key, value)
    }
  }

  def set(key: String, value: String): Unit = {
    redisConnection.set(key, value)
  }

  def sMembers(key: String): util.Set[String] = {
    redisConnection.smembers(key)
  }
  def getKeyMembers(key: String): util.Set[String] = {
    try {
      sMembers(key)
    } catch {
      case ex: JedisException =>
        logger.error("Exception when retrieving data from redis cache", ex)
        this.redisConnection.close()
        this.redisConnection = redisConnect.getConnection(dbIndex)
        sMembers(key)
    }
  }

}

// $COVERAGE-ON$