package org.sunbird.dp.denorm.`type`

import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig

class LocationDenormalization(config: DenormalizationConfig) {

  def denormalize(event: Event, metrics: Metrics) = {
    metrics.incCounter(config.locTotal)
    val userProfileLocation = event.getUserProfileLocation()
    val userDeclaredLocation = event.getUserDeclaredLocation()
    val ipLocation = event.getIpLocation()

    val declaredLocation = if (nonEmpty(userProfileLocation)) userProfileLocation
    else if (nonEmpty(userDeclaredLocation)) {
      if(userDeclaredLocation.get._1.nonEmpty) userDeclaredLocation else ipLocation
    } else ipLocation

    if (nonEmpty(declaredLocation)) {
      event.addDerivedLocation(declaredLocation.get)
      metrics.incCounter(config.locCacheHit)
    } else {
      metrics.incCounter(config.locCacheMiss)
    }
  }

  private def nonEmpty(loc: Option[(String, String, String)]): Boolean = {
    loc.nonEmpty && loc.get._1 != null
  }

}
