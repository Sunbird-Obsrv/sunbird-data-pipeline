package org.sunbird.dp.contentupdater.domain

import java.util

import org.sunbird.dp.core.domain.Events

class Event(eventMap: util.Map[String, Any]) extends Events(eventMap) {

    private val jobName = "ContentCacheUpdater"
    import scala.collection.JavaConverters._
    def extractProperties(): Map[String, AnyRef] = {
        val properties = telemetry.read[util.Map[String, AnyRef]]("transactionData.properties")
        properties.map { propertySet =>
            propertySet.asScala.map {
                case(key, value) => key -> value.asInstanceOf[util.Map[String, AnyRef]].getOrDefault("nv", None)
            }.toMap
        }.getOrElse(Map[String, AnyRef]())
    }


    def getNodeUniqueId(): String = {
        telemetry.read[String]("nodeUniqueId").orNull
    }
}
