package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.RedisConnect
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core._
import org.sunbird.dp.domain.Event
import org.apache.flink.streaming.api.functions.ProcessFunction

class UserDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserDenormFunction])
  private var dataCache: DataCache = _

  override def metricsList(): List[String] = {
    List(config.userTotal, config.userCacheHit, config.userCacheMiss)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config), config.userStore, config.userFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

    val actorId = event.actorId()
    val actorType = event.actorType()
    if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) && "user".equalsIgnoreCase(actorType)) {
      metrics.incCounter(config.userTotal)
      val userData = dataCache.getWithRetry(actorId)

      if (userData.isEmpty) {
        metrics.incCounter(config.userCacheMiss)
      } else {
        metrics.incCounter(config.userCacheHit)
      }
      if (!userData.contains("usersignintype"))
        userData.put("usersignintype", config.userSignInTypeDefault)
      if (!userData.contains("userlogintype"))
        userData.put("userlogintype", config.userLoginInTypeDefault)
      event.addUserData(userData)
    }

    context.output(config.withUserEventsTag, event)
  }
}
