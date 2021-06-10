package org.sunbird.dp.contentupdater.functions

import java.text.SimpleDateFormat
import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.contentupdater.domain.Event
import org.sunbird.dp.contentupdater.task.ContentCacheUpdaterConfig
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}

import scala.collection.JavaConverters._

class ContentUpdaterFunction(config: ContentCacheUpdaterConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

    private[this] val logger = LoggerFactory.getLogger(classOf[ContentUpdaterFunction])

    private var dataCache: DataCache = _
    private lazy val gson = new Gson()

    override def metricsList(): List[String] = {
        List(config.contentCacheHit, config.skippedEventCount)
    }

    override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        dataCache = new DataCache(config, new RedisConnect(config.contentRedisHost, config.contentRedisPort, config), config.contentStore, List())
        dataCache.init()
    }


    override def close(): Unit = {
        super.close()
        dataCache.close()
    }

    override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {
        val nodeUniqueId = event.getNodeUniqueId()
        if (null != nodeUniqueId) {
            val redisData = dataCache.getWithRetry(nodeUniqueId)
            val finalProperties = event.extractProperties().filter(property => null != property._2)

            val newProperties = finalProperties.map { case (property, nv) =>
                if (config.contentDateFields.contains(property) && null != nv && nv.asInstanceOf[String].nonEmpty)
                    (property, new SimpleDateFormat(config.contentDateFormat).parse(nv.toString).getTime)
                else if (config.contentListFields.contains(property))
                    (property, nv match {
                        case _: String => List(nv)
                        case _: util.ArrayList[String] => nv
                    })
                else
                    (property, nv)
            }.filter(map => None != map._2)
            redisData ++= newProperties.asInstanceOf[Map[String, AnyRef]]

            if (redisData.nonEmpty) {
                dataCache.setWithRetry(event.getNodeUniqueId(), gson.toJson(redisData.asJava))
                metrics.incCounter(config.contentCacheHit)
                logger.info(nodeUniqueId + " Updated Successfully")
            }

            if (finalProperties.exists(p => config.dialCodeProperties.contains(p._1)))
                context.output(config.withContentDailCodeEventsTag, event)
        }
        else{
            metrics.incCounter(config.skippedEventCount)
            logger.info("Skipping as nodeUniqueId retrieved is null from event. Event json might be invalid")
        }
    }
}
