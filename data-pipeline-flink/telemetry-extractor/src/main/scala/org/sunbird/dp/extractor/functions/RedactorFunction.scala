package org.sunbird.dp.extractor.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.extractor.task.TelemetryExtractorConfig
import org.apache.flink.configuration.Configuration

import scala.collection.mutable.Map

class RedactorFunction(config: TelemetryExtractorConfig, @transient var dataCache: DataCache = null)
                      (implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]])
  extends BaseProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]](config) {

    override def metricsList(): List[String] = {
        List(config.skippedEventCount, config.cacheMissCount, config.cacheHitCount)
    }

    override def open(parameters: Configuration): Unit = {
        super.open(parameters)
        if (dataCache == null) {
            val redisConnect = new RedisConnect(config.metaRedisHost, config.metaRedisPort, config)
            dataCache = new DataCache(config, redisConnect, config.contentStore, List("questionType"))
            dataCache.init()
        }
    }

    override def close(): Unit = {
        super.close()
        dataCache.close()
    }

    override def processElement(event: util.Map[String, AnyRef],
                                context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                                metrics: Metrics): Unit = {
        var redact: Boolean = false
        val questionId = getQuestionId(event)
        if (null == questionId) {
            metrics.incCounter(config.skippedEventCount)
        }
        else {
            val questionData = getQuestionData(questionId)
            if (questionData.isEmpty) {
                metrics.incCounter(config.cacheMissCount)
            } else {
                metrics.incCounter(config.cacheHitCount)
                if (questionData.contains("questionType") && "Registration".equalsIgnoreCase(questionData("questionType").asInstanceOf[String]))
                    redact = true
            }
        }

        if (redact) {
            context.output(config.assessRawEventsOutputTag, event)
            clearUserInputData(event)
        }
        context.output(config.rawEventsOutputTag, event)
    }

    def getQuestionId(event: util.Map[String, AnyRef]): String = {
        val eid = event.get("eid").asInstanceOf[String]
        val questionId = if (eid.equalsIgnoreCase("ASSESS")) event.get("edata").asInstanceOf[util.Map[String, AnyRef]]
          .get("item").asInstanceOf[util.Map[String, AnyRef]].get("id").asInstanceOf[String]
        else event.get("edata").asInstanceOf[util.Map[String, AnyRef]]
          .get("target").asInstanceOf[util.Map[String, AnyRef]].get("id").asInstanceOf[String]

        questionId

    }

    def getQuestionData(questionId: String): Map[String, AnyRef] = {
        dataCache.getWithRetry(questionId)
    }

    def clearUserInputData(event: util.Map[String, AnyRef]): util.Map[String, AnyRef] = {
        val eid = event.get("eid").asInstanceOf[String]
        val edata = event.get("edata").asInstanceOf[util.Map[String, AnyRef]]
        if (eid.equalsIgnoreCase("ASSESS"))
            edata.put("resvalues", new util.ArrayList[AnyRef]())
        else
            edata.put("values", new util.ArrayList[AnyRef]())
        event.put("edata", edata).asInstanceOf[util.Map[String, AnyRef]]
    }
}
