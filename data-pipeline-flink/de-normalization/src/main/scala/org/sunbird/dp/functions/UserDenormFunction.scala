package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core._
import org.sunbird.dp.domain.Event
import org.apache.flink.streaming.api.functions.KeyedProcessFunction

class UserDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event]) extends BaseProcessKeyedFunction[Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserDenormFunction])

  private var dataCache: DataCache = _;

  val total = "user-total";
  val cacheHit = "user-cache-hit";
  val cacheMiss = "user-cache-miss";

  override def getMetricsList(): List[String] = {
    List(total, cacheHit, cacheMiss)
  }

  override def open(parameters: Configuration): Unit = {

    dataCache = new DataCache(config, new RedisConnect(config), config.userStore, config.userFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close();
  }

  override def processElement(event: Event, context: KeyedProcessFunction[Integer, Event, Event]#Context, metrics: Metrics): Unit = {

    val actorId = event.actorId();
    val actorType = event.actorType()
    if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) && "user".equalsIgnoreCase(actorType)) {
      metrics.incCounter(total)
      val userData = dataCache.getWithRetry(actorId);
      if (userData.size == 0) {
        metrics.incCounter(cacheMiss)
      } else {
        metrics.incCounter(cacheHit)
      }
      if (!userData.contains("usersignintype"))
        userData.put("usersignintype", config.userSignInTypeDefault);
      if (!userData.contains("userlogintype"))
        userData.put("userlogintype", config.userLoginInTypeDefault);
      event.addUserData(userData);
    }

    context.output(config.withUserEventsTag, event);
  }
}
