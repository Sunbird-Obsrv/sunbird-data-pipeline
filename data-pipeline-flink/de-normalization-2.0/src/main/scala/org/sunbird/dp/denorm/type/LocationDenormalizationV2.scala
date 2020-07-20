package org.sunbird.dp.denorm.`type`

import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfigV2

class LocationDenormalizationV2(config: DenormalizationConfigV2) {

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
