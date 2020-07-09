package org.sunbird.dp.usercache.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.DataCache
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfigV2
import org.sunbird.dp.usercache.util.UserMetadataUpdater

class UserCacheUpdaterFunctionV2(config: UserCacheUpdaterConfigV2)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserCacheUpdaterFunctionV2])
  private var dataCache: DataCache = _
  private var cassandraConnect: CassandraUtil = _
  private[this] var userMetadataUpdater: UserMetadataUpdater = _

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
