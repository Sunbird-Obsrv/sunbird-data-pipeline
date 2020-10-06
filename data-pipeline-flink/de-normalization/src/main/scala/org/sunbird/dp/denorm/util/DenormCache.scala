package org.sunbird.dp.denorm.util

import java.util
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import redis.clients.jedis.Jedis
import redis.clients.jedis.exceptions.{ JedisConnectionException, JedisException }

import scala.collection.JavaConverters._
import scala.collection.mutable
import scala.collection.mutable.Map
import scala.collection.immutable
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.denorm.domain.Event
import redis.clients.jedis.Pipeline
import scala.collection.mutable.ArrayBuffer
import org.sunbird.dp.denorm.task.DenormalizationConfig
import org.sunbird.dp.core.domain.EventsPath
import redis.clients.jedis.Response

case class CacheData(content: Map[String, AnyRef], collection: Map[String, AnyRef], l2data: Map[String, AnyRef], device: Map[String, AnyRef],
  dialCode: Map[String, AnyRef], user: Map[String, AnyRef])

class DenormCache(val config: DenormalizationConfig, val redisConnect: RedisConnect) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DenormCache])
  private var pipeline: Pipeline = _
  val gson = new Gson()

  def init() {
    this.pipeline = redisConnect.getConnection(0).pipelined()
  }

  def close() {
    this.pipeline.close()
  }

  def getDenormData(event: Event): CacheData = {
    this.pipeline.clear();
    val responses = scala.collection.mutable.Map[String, AnyRef]();
    getContentCache(event, responses)
    getDeviceCache(event, responses);
    getDialcodeCache(event, responses);
    getUserCache(event, responses);
    this.pipeline.sync()
    parseResponses(responses);
  }

  private def getContentCache(event: Event, responses: scala.collection.mutable.Map[String, AnyRef]) {
    this.pipeline.select(config.contentStore)
    val objectType = event.objectType()
    val objectId = event.objectID()
    if (!List("user", "qr", "dialcode").contains(objectType) && null != objectId) {
      responses.put("content", this.pipeline.get(objectId).asInstanceOf[AnyRef])

      if (event.checkObjectIdNotEqualsRollUpId(EventsPath.OBJECT_ROLLUP_L1)) {
        responses.put("collection", this.pipeline.get(event.objectRollUpl1ID()).asInstanceOf[AnyRef])
      }
      if (event.checkObjectIdNotEqualsRollUpId(EventsPath.OBJECT_ROLLUP_L2)) {
        responses.put("l2data", this.pipeline.get(event.objectRollUpl2ID()).asInstanceOf[AnyRef])
      }
    }
  }

  private def getDialcodeCache(event: Event, responses: scala.collection.mutable.Map[String, AnyRef]) {
    this.pipeline.select(config.dialcodeStore)
    if (null != event.objectType() && List("dialcode", "qr").contains(event.objectType().toLowerCase())) {
      responses.put("dialcode", this.pipeline.get(event.objectID().toUpperCase()).asInstanceOf[AnyRef])
    }
  }

  private def getDeviceCache(event: Event, responses: scala.collection.mutable.Map[String, AnyRef]) {
    this.pipeline.select(config.deviceStore)
    if (null != event.did() && event.did().nonEmpty) {
      responses.put("device", this.pipeline.hgetAll(event.did()).asInstanceOf[AnyRef])
    }
  }

  private def getUserCache(event: Event, responses: scala.collection.mutable.Map[String, AnyRef]) {
    this.pipeline.select(config.userStore)

    val actorId = event.actorId()
    val actorType = event.actorType()
    if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) && ("user".equalsIgnoreCase(Option(actorType).getOrElse("")) || "ME_WORKFLOW_SUMMARY".equals(event.eid()))) {
      responses.put("user", this.pipeline.hgetAll(config.userStoreKeyPrefix + actorId).asInstanceOf[AnyRef])
    }
  }
  
  private def parseResponses(responses: scala.collection.mutable.Map[String, AnyRef]) : CacheData = {
    
    val userData = responses.get("user").map(data => {
      convertToComplexDataTypes(getData(data.asInstanceOf[Response[java.util.Map[String, String]]], config.userFields))
    }).getOrElse(mutable.Map[String, AnyRef]())
    
    val deviceData = responses.get("device").map(data => {
      convertToComplexDataTypes(getData(data.asInstanceOf[Response[java.util.Map[String, String]]], config.deviceFields))
    }).getOrElse(mutable.Map[String, AnyRef]())
    
    val contentData = responses.get("content").map(data => {
      getDataMap(data.asInstanceOf[Response[String]], config.contentFields)
    }).getOrElse(mutable.Map[String, AnyRef]())
    
    val collectionData = responses.get("collection").map(data => {
      getDataMap(data.asInstanceOf[Response[String]], config.contentFields)
    }).getOrElse(mutable.Map[String, AnyRef]())
    
    val l2Data = responses.get("l2data").map(data => {
      getDataMap(data.asInstanceOf[Response[String]], config.contentFields)
    }).getOrElse(mutable.Map[String, AnyRef]())
    
    val dialData = responses.get("dialcode").map(data => {
      getDataMap(data.asInstanceOf[Response[String]], config.dialcodeFields)
    }).getOrElse(mutable.Map[String, AnyRef]())
    
    CacheData(contentData, collectionData, l2Data, deviceData, dialData, userData);
  }
  
  private def getData(data: Response[java.util.Map[String, String]], fields: List[String]): mutable.Map[String, String] = {
    val dataMap = data.get
    if (dataMap.size() > 0) {
      if (fields.nonEmpty) dataMap.keySet().retainAll(fields.asJava)
      dataMap.values().removeAll(util.Collections.singleton(""))
      dataMap.asScala
    } else {
      mutable.Map[String, String]()
    }
  }
  
  private def getDataMap(dataStr: Response[String], fields: List[String]): mutable.Map[String, AnyRef] = {
    val data = dataStr.get
    if (data != null && !data.isEmpty) {
      val dataMap = gson.fromJson(data, new util.HashMap[String, AnyRef]().getClass)
      if (fields.nonEmpty) dataMap.keySet().retainAll(fields.asJava)
      dataMap.values().removeAll(util.Collections.singleton(""))
      dataMap.asScala
    } else {
      mutable.Map[String, AnyRef]()
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
        if (isArray(redisValue)) {
          result += redisKey -> gson.fromJson(redisValue, new util.ArrayList[AnyRef]().getClass)
        } else if (isObject(redisValue)) {
          result += redisKey -> gson.fromJson(redisValue, new util.HashMap[String, AnyRef]().getClass)
        } else {
          result += redisKey -> redisValue
        }
    }
    result
  }

}

// $COVERAGE-ON$