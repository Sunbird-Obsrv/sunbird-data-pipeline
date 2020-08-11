package org.sunbird.dp.usercache.functions

import com.datastax.driver.core.querybuilder.QueryBuilder
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfigV2
import org.sunbird.dp.usercache.util.UserMetadataUpdater._

import scala.collection.JavaConverters.mapAsJavaMap
import scala.collection.mutable

class UserCacheUpdaterFunctionV2(config: UserCacheUpdaterConfigV2)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserCacheUpdaterFunctionV2])
  private var dataCache: DataCache = _
  private var cassandraConnect: CassandraUtil = _
  private var custodianOrgId: String = _

  override def metricsList(): List[String] = {
    List(config.dbReadSuccessCount, config.dbReadMissCount, config.userCacheHit, config.skipCount, config.successCount, config.totalEventsCount)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config), config.userStore, config.userFields)
    dataCache.init()
    cassandraConnect = new CassandraUtil(config.cassandraHost, config.cassandraPort)
    custodianOrgId = getCustodianRootOrgId()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
    cassandraConnect.close()
  }


  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {
    metrics.incCounter(config.totalEventsCount)
    Option(event.getId).map(id => {
      Option(event.getState).map(name => {
        val userData: mutable.Map[String, AnyRef] = name.toUpperCase match {
          case "CREATE" | "CREATED" => createAction(id, event, metrics, config)
          case "UPDATE" | "UPDATED" => updateAction(id, event, metrics, config, dataCache, cassandraConnect, custodianOrgId)
          case _ => {
            logger.info(s"Invalid event state name either it should be(Create/Created/Update/Updated) but found $name for ${event.mid()}")
            metrics.incCounter(config.skipCount)
            mutable.Map[String, AnyRef]()
          }
        }
        if (!userData.isEmpty) {
          dataCache.hmSet(config.userStoreKeyPrefix + id, mapAsJavaMap(stringify(userData)))
          metrics.incCounter(config.successCount)
          metrics.incCounter(config.userCacheHit)
        } else {
          metrics.incCounter(config.skipCount)
        }
      }).getOrElse(metrics.incCounter(config.skipCount))
    }).getOrElse(metrics.incCounter(config.skipCount))
  }

  def getCustodianRootOrgId(): String = {
    val custRootOrgIdQuery = QueryBuilder.select("value").from(config.keySpace, config.systemSettingsTable)
      .where(QueryBuilder.eq("id", "custodianRootOrgId")).and(QueryBuilder.eq("field", "custodianRootOrgId")).toString
    val custRootOrgId = cassandraConnect.findOne(custRootOrgIdQuery)
    custRootOrgId.getString("value")
  }
}
