package org.sunbird.dp.denorm.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.denorm.`type`._
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig

class DenormalizationFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[DenormalizationFunction])

  private[this] var deviceDenormalization: DeviceDenormalization = _
  private[this] var userDenormalization: UserDenormalization = _
  private[this] var dialcodeDenormalization: DialcodeDenormalization = _
  private[this] var contentDenormalization: ContentDenormalization = _
  private[this] var locationDenormalization: LocationDenormalization = _


  override def metricsList(): List[String] = {
    List(config.eventsExpired, config.userTotal, config.userCacheHit, config.userCacheMiss,
      config.contentTotal, config.contentCacheHit, config.contentCacheMiss, config.deviceTotal,
      config.deviceCacheHit, config.deviceCacheMiss, config.eventsExpired, config.dialcodeTotal,
      config.dialcodeCacheHit, config.dialcodeCacheMiss, config.locTotal, config.locCacheHit, config.locCacheMiss)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    deviceDenormalization = new DeviceDenormalization(config)
    userDenormalization = new UserDenormalization(config)
    dialcodeDenormalization = new DialcodeDenormalization(config)
    contentDenormalization = new ContentDenormalization(config)
    locationDenormalization = new LocationDenormalization(config)
  }

  override def close(): Unit = {
    super.close()
    deviceDenormalization.closeDataCache()
    userDenormalization.closeDataCache()
    dialcodeDenormalization.closeDataCache()
    contentDenormalization.closeDataCache()
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {
    if (event.isOlder(config.ignorePeriodInMonths)) { // Skip events older than configured value (default: 3 months)
      metrics.incCounter(config.eventsExpired)
    } else {
      if ("ME_WORKFLOW_SUMMARY" == event.eid() || !event.eid().contains("SUMMARY")) {
        val deviceDenormEvent = deviceDenormalization.denormalize(event, metrics)
        val userDenormEvent = userDenormalization.denormalize(deviceDenormEvent, metrics)
        val dialcodeDenormEvent = dialcodeDenormalization.denormalize(userDenormEvent, metrics)
        val contentDenormEvent = contentDenormalization.denormalize(dialcodeDenormEvent, metrics)
        val locationDenormEvent = locationDenormalization.denormalize(contentDenormEvent, metrics)
        context.output(config.denormEventsTag, locationDenormEvent)
      }
    }
  }
}
