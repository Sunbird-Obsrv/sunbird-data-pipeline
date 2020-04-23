package org.sunbird.dp.functions

import java.text.SimpleDateFormat

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.ContentCacheUpdaterConfig

class ContentUpdaterFunction(config: ContentCacheUpdaterConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

    private[this] val logger = LoggerFactory.getLogger(classOf[ContentUpdaterFunction])

    private var dataCache: DataCache = _
    private lazy val gson = new Gson()

    override def metricsList(): List[String] = {
        List(config.contentCacheHit)
    }

    override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        dataCache = new DataCache(config, new RedisConnect(config), config.contentStore, List())
        dataCache.init()
    }


    override def close(): Unit = {
        super.close()
        dataCache.close()
    }

    override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {
        import collection.JavaConverters._
        val nodeUniqueId = event.getNodeUniqueId()
        val redisData = dataCache.getWithRetry(nodeUniqueId)
        val finalProperties = event.extractProperties().filter(property => null != property._2)

        val newProperties = finalProperties.map { case (property, nv) => {
            if (config.contentDateFields.contains(property))
                (property, new SimpleDateFormat(config.contentDateFormat).parse(nv.toString).getTime)
            else if (config.contentListFields.contains(property))
                (property, List(nv.toString))
            else
                (property, nv)
        }
        }
        redisData ++= newProperties.asInstanceOf[Map[String, AnyRef]]

        if (redisData.nonEmpty) {
            dataCache.setWithRetry(event.getNodeUniqueId(), gson.toJson(redisData.asJava))
            metrics.incCounter(config.contentCacheHit)
            logger.info(nodeUniqueId + " Updated Successfully")
        }
    }
}
