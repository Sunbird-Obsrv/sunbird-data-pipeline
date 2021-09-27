package org.sunbird.dp.usercache.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.contentupdater.core.util.RestUtil
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.JSONUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfigV2
import org.sunbird.dp.usercache.util.UserMetadataUpdater

import scala.collection.JavaConverters.mapAsJavaMap
import scala.collection.mutable

class UserCacheUpdaterFunctionV2(config: UserCacheUpdaterConfigV2)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserCacheUpdaterFunctionV2])
  private var dataCache: DataCache = _

  private var restUtil: RestUtil = _

  override def metricsList(): List[String] = {
    List(config.userCacheHit, config.skipCount, config.successCount, config.totalEventsCount, config.apiReadMissCount, config.apiReadSuccessCount)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config), config.userStore, config.userFields)
    dataCache.init()
    restUtil = new RestUtil()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
  }

  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {
    metrics.incCounter(config.totalEventsCount)
    val userId = event.getId
    try {
      Option(userId).map(id => {
        Option(event.getState).map(name => {
          val userData: mutable.Map[String, AnyRef] = name.toUpperCase match {
            case "CREATE" | "CREATED" | "UPDATE" | "UPDATED" => {
              UserMetadataUpdater.execute(id, event, metrics, config, dataCache, restUtil)
            }
            case _ => {
              logger.info(s"Invalid event state name either it should be(Create/Created/Update/Updated) but found $name for ${event.mid()}")
              metrics.incCounter(config.skipCount)
              mutable.Map[String, AnyRef]()
            }
          }
          if (!userData.isEmpty) {
            if (config.regdUserProducerPid.equals(event.producerPid())) UserMetadataUpdater.removeEmptyFields(config.userStoreKeyPrefix + id, dataCache, userData)
            dataCache.hmSet(config.userStoreKeyPrefix + id, mapAsJavaMap(UserMetadataUpdater.stringify(userData)))
            logger.info(s"Data inserted into cache for user: ${userId} having mid: ${event.mid()}")
            metrics.incCounter(config.successCount)
            metrics.incCounter(config.userCacheHit)
          } else {
            logger.info(s"User Data to be updated is empty for user: ${userId}")
            metrics.incCounter(config.skipCount)
          }
        }).getOrElse(metrics.incCounter(config.skipCount))
      }).getOrElse(metrics.incCounter(config.skipCount))
    } catch {
      case ex: Exception => {
        ex.printStackTrace()
        logger.info(s"Processing event for user: ${userId} having mid: ${event.mid()}")
        logger.info("Event throwing exception: ", JSONUtil.serialize(event))
        throw ex
      }
    }
  }
}

