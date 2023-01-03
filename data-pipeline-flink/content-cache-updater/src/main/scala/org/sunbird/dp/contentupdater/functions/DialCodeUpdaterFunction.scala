package org.sunbird.dp.contentupdater.functions

import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.contentupdater.core.util.RestUtil
import org.sunbird.dp.contentupdater.domain.Event
import org.sunbird.dp.contentupdater.task.ContentCacheUpdaterConfig
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}

import scala.collection.JavaConverters._
case class DialCodeResult(result: util.HashMap[String, Any])

class DialCodeUpdaterFunction(config: ContentCacheUpdaterConfig)
                             (implicit val mapTypeInfo: TypeInformation[Event]) extends BaseProcessFunction[Event, Event](config) {

    private[this] val logger = LoggerFactory.getLogger(classOf[DialCodeUpdaterFunction])

    private var dataCache: DataCache = _
    private val restUtil = new RestUtil()
    private lazy val headers = Map("Authorization" -> String.format("%s %s","Bearer",config.dialCodeApiToken))
    private lazy val gson = new Gson()

    override def metricsList(): List[String] = {
        List(config.dialCodeCacheHit, config.dialCodeApiMissHit, config.dialCodeApiHit, config.totaldialCodeCount)
    }

    override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        dataCache = new DataCache(config, new RedisConnect(config.dialcodeRedisHost, config.dialcodeRedisPort, config),
            config.dialcodeStore, config.dialcodeFields)
        dataCache.init()
    }

    override def close(): Unit = {
        super.close()
        dataCache.close()
    }

    override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {
        val properties = event.extractProperties()
        val dialCodesList = properties.filter(p => config.dialCodeProperties.contains(p._1)).flatMap(f => {
            f._2 match {
                case list: util.ArrayList[String] => list.asScala
                case _ => List.empty
            }
        }).filter(list => !list.isEmpty)

        dialCodesList.foreach(dc => {
            metrics.incCounter(config.totaldialCodeCount)
            if (dataCache.getWithRetry(dc).isEmpty) {
                val result = gson.fromJson[DialCodeResult](restUtil.get(String.format("%s%s",config.dialCodeApiUrl, dc), Some(headers)), classOf[DialCodeResult]).result
                if (!result.isEmpty && result.containsKey("dialcode")) {
                    metrics.incCounter(config.dialCodeApiHit)
                    dataCache.setWithRetry(dc, gson.toJson(result.get("dialcode")))
                    metrics.incCounter(config.dialCodeCacheHit)
                    logger.info(dc + " updated successfully")
                } else {
                    metrics.incCounter(config.dialCodeApiMissHit)
                }
            } else {
                metrics.incCounter(config.dialCodeCacheHit)
            }
        })

        context.output(config.withDialCodeEventsTag, event)
    }

}
