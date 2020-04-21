package org.sunbird.dp.functions

import java.util

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.{DialCodeResult, RestUtil}
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.ContentCacheUpdaterConfig

class DialCodeUpdaterFunction(config: ContentCacheUpdaterConfig, restUtil: RestUtil)
                             (implicit val mapTypeInfo: TypeInformation[Event]) extends BaseProcessFunction[Event, Event](config) {

    private[this] val logger = LoggerFactory.getLogger(classOf[DialCodeUpdaterFunction])

    private var dataCache: DataCache = _


    override def metricsList(): List[String] = {
        List(config.dialCodeCacheHit, config.dialCodeApiMissHit, config.dialCodeApiHit)
    }

    override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        dataCache = new DataCache(config, new RedisConnect(config), config.dialcodeStore, config.dialcodeFields)
        dataCache.init()
    }

    override def close(): Unit = {
        super.close()
        dataCache.close()
    }

    override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {

        import scala.collection.JavaConverters._
        val gson = new Gson()
        val properties = event.extractProperties()
        val dialCodesList = properties.filter(p => p._1.equals("dialcodes") || p._1.equals("reservedDialCodes")).flatMap(f => {
            if (f._2.isInstanceOf[util.ArrayList[String]]) {
                f._2.asInstanceOf[util.ArrayList[String]].asScala
            }
            else {
                List.empty[String]
            }
        }).filter(p => !p.isEmpty)

        val headers = Map("Authorization" -> ("Bearer " + config.dialCodeApiToken))
        dialCodesList.foreach(dc => {
            if (dataCache.getWithRetry(dc).isEmpty) {
                val result = restUtil.get[DialCodeResult](config.dialCodeApiUrl + dc, Some(headers)).result
                if (!result.isEmpty && result.containsKey("dialcode")) {
                    metrics.incCounter(config.dialCodeApiHit)
                    dataCache.setWithRetry(dc, gson.toJson(result.get("dialcode")))
                    metrics.incCounter(config.dialCodeCacheHit)
                }
                else {
                    metrics.incCounter(config.dialCodeApiMissHit)
                }
            }
            else {
                metrics.incCounter(config.dialCodeCacheHit)
            }
        })

        context.output(config.withDialCodeEventsTag, event)
    }

}
