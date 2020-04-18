package org.sunbird.dp.functions


import org.apache.flink.api.common.typeinfo.TypeInformation
import org.slf4j.LoggerFactory
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.domain.Event
import org.apache.flink.streaming.api.functions.ProcessFunction

class LocationDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[LocationDenormFunction])

  override def metricsList(): List[String] = {
    List(config.locTotal, config.locCacheHit, config.locCacheMiss)
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

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

    context.output(config.withLocationEventsTag, event)
  }

  private def nonEmpty(loc: Option[(String, String, String)]): Boolean = {
    loc.nonEmpty && loc.get._1 != null
  }
}
