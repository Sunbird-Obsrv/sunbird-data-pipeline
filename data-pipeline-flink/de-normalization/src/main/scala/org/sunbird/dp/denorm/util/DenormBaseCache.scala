package org.sunbird.dp.denorm.util

import java.util
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import redis.clients.jedis.Response

import scala.collection.mutable.{Map => MMap}
import scala.collection.JavaConverters._
import scala.collection.mutable

trait DenormBaseCache {

    val gson = new Gson()
    private[this] val logger = LoggerFactory.getLogger(classOf[DenormBaseCache])

    def getData(data: Response[java.util.Map[String, String]], fields: List[String]): MMap[String, String] = {
        val dataMap = data.get()
        if (dataMap.size() > 0) {
            if (fields.nonEmpty) dataMap.keySet().retainAll(fields.asJava)
            dataMap.values().removeAll(util.Collections.singleton(""))
            dataMap.asScala
        } else {
            MMap[String, String]()
        }
    }

    def getDataMap(dataStr: Response[String], fields: List[String]): MMap[String, AnyRef] = {
        val data = dataStr.get
        if (data != null && !data.isEmpty) {
            val dataMap = gson.fromJson(data, new util.HashMap[String, AnyRef]().getClass)
            if (fields.nonEmpty) dataMap.keySet().retainAll(fields.asJava)
            dataMap.values().removeAll(util.Collections.singleton(""))
            dataMap.asScala
        } else {
            MMap[String, AnyRef]()
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

    def convertToComplexDataTypes(data: mutable.Map[String, String]): MMap[String, AnyRef] = {
        val result = mutable.Map[String, AnyRef]()
        data.keys.map {
            redisKey =>
                val redisValue = data(redisKey)
                try {
                    if (isArray(redisValue)) {
                        result += redisKey -> gson.fromJson(redisValue, new util.ArrayList[AnyRef]().getClass)
                    } else if (isObject(redisValue)) {
                        result += redisKey -> gson.fromJson(redisValue, new util.HashMap[String, AnyRef]().getClass)
                    } else {
                        result += redisKey -> redisValue.replaceAll("\\\\", "")
                    }
                }
                catch {
                    case ex: Exception =>
                        logger.error("Denorm convertToComplexDataTypes failure for redis key : " + redisKey + " and value : " + redisValue)
                        logger.error("Denorm convertToComplexDataTypes failure :", ex)
                        throw ex
                }
        }
        result
    }

}
