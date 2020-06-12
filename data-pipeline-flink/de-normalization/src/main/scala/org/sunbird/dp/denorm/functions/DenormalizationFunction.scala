package org.sunbird.dp.denorm.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.denorm.domain.{DeviceProfile, Event}
import org.sunbird.dp.denorm.task.DenormalizationConfig

class DenormalizationFunction (config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserDenormFunction])
  private var deviceDataCache: DataCache = _
  private var userDataCache: DataCache = _
  private var contentDataCache: DataCache = _
  private var dialcodeDataCache: DataCache = _


  override def metricsList(): List[String] = {
    List(config.eventsExpired, config.userTotal, config.userCacheHit, config.userCacheMiss,
      config.contentTotal, config.contentCacheHit, config.contentCacheMiss, config.deviceTotal,
      config.deviceCacheHit, config.deviceCacheMiss, config.eventsExpired, config.dialcodeTotal,
      config.dialcodeCacheHit, config.dialcodeCacheMiss, config.locTotal, config.locCacheHit, config.locCacheMiss)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    deviceDataCache = new DataCache(config, new RedisConnect(config), config.deviceStore, config.deviceFields)
    deviceDataCache.init()
    contentDataCache = new DataCache(config, new RedisConnect(config), config.contentStore, config.contentFields)
    contentDataCache.init()
    userDataCache = new DataCache(config, new RedisConnect(config), config.userStore, config.userFields)
    userDataCache.init()
    dialcodeDataCache = new DataCache(config, new RedisConnect(config), config.dialcodeStore, config.dialcodeFields)
    dialcodeDataCache.init()
  }

  override def close(): Unit = {
    super.close()
    userDataCache.close()
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {
    if (event.isOlder(config.ignorePeriodInMonths)) { // Skip events older than configured value (default: 3 months)
      metrics.incCounter(config.eventsExpired)
    } else {
      val deviceDenormEvent = deviceDenorm(event, metrics)
      val userDenormEvent = userDenorm(deviceDenormEvent, metrics)
      val dialcodeDenormEvent = dialcodeDenorm(userDenormEvent, metrics)
      val contentDenormEvent = contentDenorm(dialcodeDenormEvent, metrics)
      val locationDenormEvent = locationDenorm(contentDenormEvent, metrics)
      context.output(config.denormEventsTag, deviceDenormEvent)
    }
  }

  def deviceDenorm(event: Event, metrics: Metrics): Event = {
    event.compareAndAlterEts() // Reset ets to today's date if we get future value
    val did = event.did()
    if (null != did && did.nonEmpty) {
      metrics.incCounter(config.deviceTotal)
      val deviceDetails = deviceDataCache.hgetAllWithRetry(did)
      if (deviceDetails.nonEmpty) {
        metrics.incCounter(config.deviceCacheHit)
        event.addDeviceProfile(DeviceProfile.apply(deviceDetails))
      } else {
        metrics.incCounter(config.deviceCacheMiss)
        event.setFlag("device_denorm", value = false)
      }
    }
    event
  }

  def userDenorm(event: Event, metrics: Metrics): Event = {
    val actorId = event.actorId()
    val actorType = event.actorType()
    if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) && "user".equalsIgnoreCase(actorType)) {
      metrics.incCounter(config.userTotal)
      val userData = userDataCache.getWithRetry(actorId)

      if (userData.isEmpty) {
        metrics.incCounter(config.userCacheMiss)
      } else {
        metrics.incCounter(config.userCacheHit)
      }
      if (!userData.contains("usersignintype"))
        userData.put("usersignintype", config.userSignInTypeDefault)
      if (!userData.contains("userlogintype"))
        userData.put("userlogintype", config.userLoginInTypeDefault)
      event.addUserData(userData)
    }
    event
  }

  def contentDenorm(event: Event, metrics: Metrics): Event = {
    val objectType = event.objectType()
    val objectId = event.objectID()
    if (!List("user", "qr", "dialcode").contains(objectType) && null != objectId) {
      metrics.incCounter(config.contentTotal)
      val contentData = contentDataCache.getWithRetry(objectId)

      if (contentData.nonEmpty) {
        metrics.incCounter(config.contentCacheHit)
        event.addContentData(contentData)
      } else {
        metrics.incCounter(config.contentCacheMiss)
        event.setFlag("content_denorm", value = false)
      }

      if (event.checkObjectIdNotEqualsRollUpl1Id()) {
        event.addCollectionData(contentDataCache.getWithRetry(event.objectRollUpl1ID()))
      }
    }
    event
  }

  def dialcodeDenorm(event: Event, metrics: Metrics): Event = {
    if (null != event.objectType() && List("dialcode", "qr").contains(event.objectType().toLowerCase())) {
      metrics.incCounter(config.dialcodeTotal)
      val dialcodeData = dialcodeDataCache.getWithRetry(event.objectID().toUpperCase())

      if (dialcodeData.nonEmpty) {
        metrics.incCounter(config.dialcodeCacheHit)
        event.addDialCodeData(dialcodeData)
      } else {
        metrics.incCounter(config.dialcodeCacheMiss)
        event.setFlag("dialcode_denorm", value = false)
      }
    }
    event
  }

  def locationDenorm(event: Event, metrics: Metrics): Event = {
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
