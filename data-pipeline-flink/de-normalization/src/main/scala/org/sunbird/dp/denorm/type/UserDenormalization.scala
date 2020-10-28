package org.sunbird.dp.denorm.`type`

import scala.collection.mutable.Map
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig
import org.sunbird.dp.denorm.util.CacheResponseData

import scala.collection.mutable

class UserDenormalization(config: DenormalizationConfig) {

  def denormalize(event: Event, cacheData: CacheResponseData, metrics: Metrics) = {
    val actorId = event.actorId()
    val actorType = event.actorType()
    if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) &&
      ("user".equalsIgnoreCase(Option(actorType).getOrElse("")) || "ME_WORKFLOW_SUMMARY".equals(event.eid()))) {

      metrics.incCounter(config.userTotal)
      val userData: mutable.Map[String, AnyRef] =
        cacheData.user.map(f => {(f._1.toLowerCase().replace("_", ""), f._2)})

      if (userData.isEmpty) {
        metrics.incCounter(config.userCacheMiss)
      } else {
        metrics.incCounter(config.userCacheHit)
      }
      if (!userData.contains("usersignintype"))
        userData += "usersignintype" -> config.userSignInTypeDefault
      if (!userData.contains("userlogintype"))
        userData += "userlogintype" -> config.userLoginInTypeDefault
      event.addUserData(userData)
    }
  }

}
