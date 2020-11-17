package org.sunbird.dp.denorm.util

import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.denorm.functions.EventsMetadata
import org.sunbird.dp.denorm.task.DenormalizationConfig
import redis.clients.jedis.Response

import scala.collection.mutable.{Map => MMap}

class DenormWindowCache(config: DenormalizationConfig, contentRedis: RedisConnect, deviceRedis: RedisConnect,
                        userRedis: RedisConnect, dialcodeRedis: RedisConnect) extends DenormBaseCache {

    private val contentPipeline = contentRedis.getConnection(config.contentStore).pipelined()
    private val devicePipeline = deviceRedis.getConnection(config.deviceStore).pipelined()
    private val userPipeline = userRedis.getConnection(config.userStore).pipelined()
    private val dialcodePipeline = dialcodeRedis.getConnection(config.dialcodeStore).pipelined()

    def close(): Unit = {
        contentPipeline.close()
        devicePipeline.close()
        userPipeline.close()
        dialcodePipeline.close()
    }

    def getDenormData(eventsMeta: EventsMetadata): EventsMetadata = {
        contentPipeline.clear()
        devicePipeline.clear()
        userPipeline.clear()
        dialcodePipeline.clear()
        getContentCacheData(eventsMeta.contentMap, eventsMeta.collectionMap, eventsMeta.l2RollupMap)
        getDeviceCacheData(eventsMeta.deviceMap)
        getUserCacheData(eventsMeta.userMap)
        getDialcodeCacheData(eventsMeta.dialcodeMap)
        contentPipeline.sync()
        devicePipeline.sync()
        userPipeline.sync()
        dialcodePipeline.sync()
        parseResponses(eventsMeta)
        eventsMeta
    }

    private def getContentCacheData(contentMap: MMap[String, AnyRef], collectionMap: MMap[String, AnyRef], l2rollupMap: MMap[String, AnyRef]) = {
        contentMap.keySet.foreach {
            contentId => contentMap.put(contentId, contentPipeline.get(contentId))
        }

        collectionMap.keySet.foreach {
            collectionId => collectionMap.put(collectionId, contentPipeline.get(collectionId))
        }

        l2rollupMap.keySet.foreach {
            l2RollupId => l2rollupMap.put(l2RollupId, contentPipeline.get(l2RollupId))
        }
    }

    private def getDeviceCacheData(deviceMap: MMap[String, AnyRef]) = {
        deviceMap.keySet.foreach {
            deviceId => deviceMap.put(deviceId, devicePipeline.hgetAll(deviceId))
        }
    }

    private def getUserCacheData(userMap: MMap[String, AnyRef]) = {
        userMap.keySet.foreach {
            userId => userMap.put(userId, userPipeline.hgetAll(userId))
        }
    }

    private def getDialcodeCacheData(dialcodeMap: MMap[String, AnyRef]) = {
        dialcodeMap.keySet.foreach {
            dialcodeId => dialcodeMap.put(dialcodeId, dialcodePipeline.get(dialcodeId))
        }
    }

    def parseResponses(eventsMeta: EventsMetadata) = {
        eventsMeta.contentMap.foreach {
            case (contentId, contentResponse) =>
                eventsMeta.contentMap.put(contentId, getDataMap(contentResponse.asInstanceOf[Response[String]], config.contentFields))
        }

        eventsMeta.collectionMap.foreach {
            case (collectionId, contentResponse) =>
                eventsMeta.collectionMap.put(collectionId, getDataMap(contentResponse.asInstanceOf[Response[String]], config.contentFields))
        }

        eventsMeta.l2RollupMap.foreach {
            case (l2RollupId, contentResponse) =>
                eventsMeta.l2RollupMap.put(l2RollupId, getDataMap(contentResponse.asInstanceOf[Response[String]], config.contentFields))
        }

        eventsMeta.deviceMap.foreach {
            case (deviceId, deviceResponse) =>
                eventsMeta.deviceMap.put(deviceId, convertToComplexDataTypes(getData(deviceResponse.asInstanceOf[Response[java.util.Map[String, String]]], config.deviceFields)))
        }

        eventsMeta.userMap.foreach {
            case (userId, userResponse) =>
                eventsMeta.userMap.put(userId, convertToComplexDataTypes(getData(userResponse.asInstanceOf[Response[java.util.Map[String, String]]], config.userFields)))
        }

        eventsMeta.dialcodeMap.foreach {
            case (dialcodeId, dialcodeResponse) =>
                eventsMeta.dialcodeMap.put(dialcodeId, getDataMap(dialcodeResponse.asInstanceOf[Response[String]], config.dialcodeFields))
        }

    }
}
