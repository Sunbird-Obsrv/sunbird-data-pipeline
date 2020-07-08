package org.sunbird.dp.usercache.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfig
import org.sunbird.dp.usercache.util.UserMetadataUpdater

import scala.collection.mutable

class UserCacheUpdaterFunction(config: UserCacheUpdaterConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserCacheUpdaterFunction])
  private[this] var userMetadataUpdater: UserMetadataUpdater[mutable.Map[String, AnyRef]] = _

  override def metricsList(): List[String] = {
    List(config.dbReadSuccessCount, config.dbReadMissCount, config.userCacheHit, config.skipCount, config.successCount, config.totalEventsCount)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    userMetadataUpdater = new UserMetadataUpdater(config)
  }

  override def close(): Unit = {
    super.close()
    userMetadataUpdater.closeCache()
  }


  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {
    userMetadataUpdater.userUpdater(event, metrics)
  }
}
