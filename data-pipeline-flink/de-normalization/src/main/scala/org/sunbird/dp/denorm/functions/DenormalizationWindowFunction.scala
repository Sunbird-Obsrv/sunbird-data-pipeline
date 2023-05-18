package org.sunbird.dp.denorm.functions

import java.lang

import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.{GlobalWindow, TimeWindow}
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.domain.EventsPath
import org.sunbird.dp.core.job.{Metrics, WindowBaseProcessFunction}
import org.sunbird.dp.denorm.`type`._
import org.sunbird.dp.denorm.domain.Event
import org.sunbird.dp.denorm.task.DenormalizationConfig
import org.sunbird.dp.denorm.util.{CacheResponseData, DenormCache, DenormWindowCache}
import org.apache.flink.util.Collector

import scala.collection.mutable.{Map => MMap}
import scala.collection.JavaConverters._

case class EventCache(event: Event, contentId: Option[String], collectionId: Option[String], l2Id: Option[String], deviceId: Option[String], userId: Option[String], dialcodeId: Option[String])

case class EventsMetadata(contentMap: MMap[String, AnyRef], collectionMap: MMap[String, AnyRef], l2RollupMap: MMap[String, AnyRef],
                          deviceMap: MMap[String, AnyRef], userMap: MMap[String, AnyRef], dialcodeMap: MMap[String, AnyRef])

class DenormalizationWindowFunction(config: DenormalizationConfig)(implicit val eventTypeInfo: TypeInformation[Event])
  extends WindowBaseProcessFunction[Event, Event, Int](config) {

    private[this] val logger = LoggerFactory.getLogger(classOf[DenormalizationWindowFunction])

    private[this] var deviceDenormalization: DeviceDenormalization = _
    private[this] var userDenormalization: UserDenormalization = _
    private[this] var dialcodeDenormalization: DialcodeDenormalization = _
    private[this] var contentDenormalization: ContentDenormalization = _
    private[this] var locationDenormalization: LocationDenormalization = _
    private[this] var denormCache: DenormWindowCache = _

    override def metricsList(): List[String] = {
        List(config.eventsExpired, config.userTotal, config.userCacheHit, config.userCacheMiss,
            config.contentTotal, config.contentCacheHit, config.contentCacheMiss, config.deviceTotal,
            config.deviceCacheHit, config.deviceCacheMiss, config.dialcodeTotal,
            config.dialcodeCacheHit, config.dialcodeCacheMiss,
            config.locTotal, config.locCacheHit, config.locCacheMiss, config.eventsSkipped)
    }

    override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        denormCache = new DenormWindowCache(config,
            new RedisConnect(config.contentRedisHost, config.contentRedisPort, config),
            new RedisConnect(config.deviceRedisHost, config.deviceRedisPort, config),
            new RedisConnect(config.userRedisHost, config.userRedisPort, config),
            new RedisConnect(config.dialcodeRedisHost, config.dialcodeRedisPort, config)
        )
        deviceDenormalization = new DeviceDenormalization(config)
        userDenormalization = new UserDenormalization(config)
        dialcodeDenormalization = new DialcodeDenormalization(config)
        contentDenormalization = new ContentDenormalization(config)
        locationDenormalization = new LocationDenormalization(config)
    }

    override def close(): Unit = {
        super.close()
        denormCache.close()
    }

    override def process(key: Int, context: ProcessWindowFunction[Event, Event, Int, GlobalWindow]#Context, elements: lang.Iterable[Event], metrics: Metrics): Unit = {

        val summaryEventsList = List("ME_WORKFLOW_SUMMARY", "SUMMARY")
        val eventsList = elements.asScala.toList
        val filteredEventsList: List[Event] = eventsList.filter { event =>
            if (event.isOlder(config.ignorePeriodInMonths)) { // Skip events older than configured value (default: 3 months)
                logger.info(s"Event Dropped: Event older than configured value (default: 3 months)")
                metrics.incCounter(config.eventsExpired)
                false
            } else {
                if (summaryEventsList.contains(event.eid()) || !(event.eid().contains("SUMMARY") || config.eventsToskip.contains(event.eid()))) {
                    true
                } else {
                    metrics.incCounter(config.eventsSkipped)
                    false
                }
            }
        }
        denormalize(filteredEventsList, context, metrics)
    }

    def denormalize(events: List[Event], context: ProcessWindowFunction[Event, Event, Int, GlobalWindow]#Context, metrics: Metrics) = {

        val eventCacheData = parseLookupIds(events)
        val eventsDenormMeta = denormCache.getDenormData(eventCacheData._2)

        eventCacheData._1.foreach {
            eventData =>
                val event = eventData.event
                val cacheData = CacheResponseData(
                    content = eventsDenormMeta.contentMap.getOrElse(eventData.contentId.getOrElse(""), MMap[String, AnyRef]()).asInstanceOf[MMap[String, AnyRef]],
                    collection = eventsDenormMeta.collectionMap.getOrElse(eventData.collectionId.getOrElse(""), MMap[String, AnyRef]()).asInstanceOf[MMap[String, AnyRef]],
                    l2data = eventsDenormMeta.l2RollupMap.getOrElse(eventData.l2Id.getOrElse(""), MMap[String, AnyRef]()).asInstanceOf[MMap[String, AnyRef]],
                    device = eventsDenormMeta.deviceMap.getOrElse(eventData.deviceId.getOrElse(""), MMap[String, AnyRef]()).asInstanceOf[MMap[String, AnyRef]],
                    user = eventsDenormMeta.userMap.getOrElse(eventData.userId.getOrElse(""), MMap[String, AnyRef]()).asInstanceOf[MMap[String, AnyRef]],
                    dialCode = eventsDenormMeta.dialcodeMap.getOrElse(eventData.dialcodeId.getOrElse(""), MMap[String, AnyRef]()).asInstanceOf[MMap[String, AnyRef]]
                )
                deviceDenormalization.denormalize(event, cacheData, metrics)
                userDenormalization.denormalize(event, cacheData, metrics)
                dialcodeDenormalization.denormalize(event, cacheData, metrics)
                contentDenormalization.denormalize(event, cacheData, metrics)
                locationDenormalization.denormalize(event, metrics)
                context.output(config.denormEventsTag, event)
        }
    }

    def parseLookupIds(events: List[Event]): (List[EventCache], EventsMetadata) = {
        val contentMap = MMap[String, AnyRef]()
        val collectionMap = MMap[String, AnyRef]()
        val l2RollupMap = MMap[String, AnyRef]()
        val dialcodeMap = MMap[String, AnyRef]()
        val deviceMap = MMap[String, AnyRef]()
        val userMap = MMap[String, AnyRef]()

        val eventsCache = events.map { event =>
            val objectType = event.objectType()
            val objectId = event.objectID()
            val deviceId = if (null != event.did()) Some(event.did()) else None
            val actorId = event.actorId()
            val actorType = event.actorType()

            val contentIds = if (event.isValidEventForContentDenorm(config, objectId, objectType, event.eid())) {
                contentMap.put(objectId, null)
                val collectionId = if (event.checkObjectIdNotEqualsRollUpId(EventsPath.OBJECT_ROLLUP_L1)) {
                    collectionMap.put(event.objectRollUpl1ID(), null)
                    Some(event.objectRollUpl1ID())
                } else None

                val l2RollupId = if (event.checkObjectIdNotEqualsRollUpId(EventsPath.OBJECT_ROLLUP_L2)) {
                    l2RollupMap.put(event.objectRollUpl2ID(), null)
                    Some(event.objectRollUpl2ID())
                } else None

                Some((objectId, collectionId, l2RollupId))
            } else {
                None
            }

            val dialcodeId = if (null != objectType && List("dialcode", "qr").contains(objectType.toLowerCase())) {
                dialcodeMap.put(objectId.toUpperCase(), null)
                Some(objectId.toUpperCase)
            } else None

            deviceId.map(did => deviceMap.put(did, null))

            val userId = if (null != actorId && actorId.nonEmpty && !"anonymous".equalsIgnoreCase(actorId) && ("user".equalsIgnoreCase(Option(actorType).getOrElse("")) || "ME_WORKFLOW_SUMMARY".equals(event.eid()))) {
                userMap.put(config.userStoreKeyPrefix + actorId, null)
                Some(config.userStoreKeyPrefix + actorId)
            } else None

            EventCache(event, contentId = contentIds.map(_._1), collectionId = contentIds.flatMap(_._2), l2Id = contentIds.flatMap(_._3), deviceId = deviceId, userId = userId, dialcodeId = dialcodeId)
        }
        val eventsMetaCache = EventsMetadata(contentMap, collectionMap, l2RollupMap, deviceMap, userMap, dialcodeMap)
        (eventsCache, eventsMetaCache)
    }
}