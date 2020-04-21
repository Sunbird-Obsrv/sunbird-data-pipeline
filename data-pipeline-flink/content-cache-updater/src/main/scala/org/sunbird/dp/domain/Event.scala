package org.sunbird.dp.domain

import java.util

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken

class Event(eventMap: util.Map[String, Any], partition: Integer) extends Events(eventMap) {


    private val jobName = "ContentCacheUpdater"

    def extractProperties(): Map[String, Object] = {

        val gson = new Gson()
        val transactionMap = eventMap.getOrDefault("transactionData", null).asInstanceOf[LinkedTreeMap[String, Object]]
        if (null != transactionMap) {
            val properties = transactionMap.getOrDefault("properties", null).asInstanceOf[LinkedTreeMap[String, Object]]
            val finalProperties = gson.fromJson(gson.toJson(properties),
                new TypeToken[util.HashMap[String, Object]]() {}.getType).asInstanceOf[util.HashMap[String, Object]]
            import scala.collection.JavaConverters._
            val myScalaMap = finalProperties.asScala.toMap
            myScalaMap.map(value => {
                val newValue = gson.fromJson(gson.toJson(value._2),
                    new TypeToken[util.HashMap[String, Object]]() {}.getType).asInstanceOf[util.HashMap[String, Object]]
                val finalValue = newValue.asScala
                (value._1, if (finalValue.contains("nv")) finalValue("nv") else null)

            })
        }
        else {
            Map.empty[String, Object]
        }
    }


    def getNodeUniqueId(): String = {
        telemetry.read[String]("nodeUniqueId").get
    }
}
