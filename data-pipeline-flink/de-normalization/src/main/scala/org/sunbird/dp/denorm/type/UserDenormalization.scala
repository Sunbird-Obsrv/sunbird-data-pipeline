package org.sunbird.dp.denorm.`type`

import scala.collection.mutable.Map
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig

class UserDenormalization(config: DenormalizationConfig) {

  private val userDataCache =
    new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config),
      config.userStore, config.userFields)
  userDataCache.init()

  def denormalize(event: Event, metrics: Metrics): Event = {
    val actorId = event.actorId()
    val actorType = event.actorType()
    if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) && "user".equalsIgnoreCase(actorType)) {
      metrics.incCounter(config.userTotal)

      val userData = if (config.userDenormVersion.equalsIgnoreCase("v2")) {
        userDataCache.hgetAllWithRetry(actorId).map(f => {(f._1.toLowerCase().replace("_", ""), f._2)})
      } else {
        userDataCache.getWithRetry(actorId).map(f => {(f._1.toLowerCase().replace("_", ""), f._2)}).asInstanceOf[Map[String, String]]
      }

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
    event
  }

  def closeDataCache() = {
    userDataCache.close()
  }

}
