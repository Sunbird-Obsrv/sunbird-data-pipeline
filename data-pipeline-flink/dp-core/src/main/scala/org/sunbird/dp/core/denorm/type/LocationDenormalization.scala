package org.sunbird.dp.core.denorm.`type`

import org.sunbird.dp.core.denorm.config.DenormalizationConfig
import org.sunbird.dp.core.denorm.domain.Event
import org.sunbird.dp.core.job.Metrics

class LocationDenormalization(config: DenormalizationConfig) {

  def denormalize(event: Event, metrics: Metrics): Event = {
    metrics.incCounter(config.locTotal)
    val userProfileLocation = event.getUserProfileLocation()
    val userDeclaredLocation = event.getUserDeclaredLocation()
    val ipLocation = event.getIpLocation()

    val declaredLocation = if (nonEmpty(userProfileLocation)) userProfileLocation
    else if (nonEmpty(userDeclaredLocation)) userDeclaredLocation else ipLocation

    if (nonEmpty(declaredLocation)) {
      event.addDerivedLocation(declaredLocation.get)
      metrics.incCounter(config.locCacheHit)
    } else {
      metrics.incCounter(config.locCacheMiss)
    }
    event
  }

  private def nonEmpty(loc: Option[(String, String, String)]): Boolean = {
    loc.nonEmpty && loc.get._1 != null
  }

}
