package org.sunbird.dp.contentupdater.domain

import java.util

import com.google.gson.internal.LinkedTreeMap
import org.sunbird.dp.core.domain.Events

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

    private val jobName = "ContentCacheUpdater"
    import scala.collection.JavaConverters._
    def extractProperties(): Map[String, Any] = {
        val properties = telemetry.read[LinkedTreeMap[String, Any]]("transactionData.properties")
        properties.map { propertySet =>
            propertySet.asScala.map {
                case (key, value) => key -> value.asInstanceOf[LinkedTreeMap[String, Any]].getOrDefault("nv", None)
            }.toMap
        }.getOrElse(Map.empty[String, Any])
    }


    def getNodeUniqueId(): String = {
        telemetry.read[String]("nodeUniqueId").get
    }
}
