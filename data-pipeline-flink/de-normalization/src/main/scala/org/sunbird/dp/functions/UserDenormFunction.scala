package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{ DedupEngine, RedisConnect }
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core.BaseDeduplication
import org.sunbird.dp.domain.Event
import org.sunbird.dp.core.DataCache

class UserDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event]) extends ProcessFunction[Event, Event] {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserDenormFunction])

  private var dataCache: DataCache = _;

  override def open(parameters: Configuration): Unit = {

    dataCache = new DataCache(config, new RedisConnect(config), config.userStore, config.userFields)
    dataCache.init()
  }

  override def close(): Unit = {
    super.close()
    dataCache.close();
  }

  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, out: Collector[Event]): Unit = {

    Console.println("UserDenormFunction", event.getTelemetry.read("devicedata"), event.getTelemetry.read("flags"));
    val actorId = event.actorId();
    val actorType = event.actorType()
    if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) && "user".equalsIgnoreCase(actorType)) {
      val userData = dataCache.getWithRetry(actorId);
      if(userData.size == 0)
        // TODO: Increment to noCacheHitCounter
      if (!userData.contains("usersignintype"))
        userData.put("usersignintype", config.userSignInTypeDefault);
      if (!userData.contains("userlogintype"))
        userData.put("userlogintype", config.userLoginInTypeDefault);
      event.addUserData(userData);
    }

    context.output(config.withUserEventsTag, event);
  }
}
