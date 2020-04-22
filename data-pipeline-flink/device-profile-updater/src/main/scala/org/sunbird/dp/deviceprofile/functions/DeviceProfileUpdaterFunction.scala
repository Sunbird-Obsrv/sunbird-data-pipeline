package org.sunbird.dp.deviceprofile.functions

import java.lang.reflect.Type
import java.util

import com.google.gson.reflect.TypeToken
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.{PostgresConnect, PostgresConnectionConfig}
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterConfig

class DeviceProfileUpdaterFunction(config: DeviceProfileUpdaterConfig,
                                   @transient var dataCache: DataCache = null,
                                   @transient var postgresConnect: PostgresConnect = null
                                  )(implicit val stringTypeInfo: TypeInformation[String])
  extends BaseProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]](config) {

  val mapType: Type = new TypeToken[util.Map[String, AnyRef]]() {}.getType

  override def metricsList(): List[String] = {
    List(config.deviceDbHitCount, config.cacheHitCount, config.failedEventCount, config.failedEventCount)
  }


  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    if (dataCache == null) {
      val redisConnect = new RedisConnect(config)
      dataCache = new DataCache(config, redisConnect, config.deviceDbStore, null)
      dataCache.init()
    }
    if (postgresConnect == null) {
      postgresConnect = new PostgresConnect(PostgresConnectionConfig(
        user = config.postgresUser,
        password = config.postgresPassword,
        database = config.postgresDb,
        host = config.postgresHost,
        port = config.postgresPort,
        maxConnections = config.postgresMaxConnections
      ))
    }
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
   // postgresConnect.closeConnection()
  }

  /**
   * Method to write the device profile events into redis and postgres
   *
   * @param event - Device profile events
   * @param context
   */
  override def processElement(event: util.Map[String, AnyRef],
                              context: ProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]]#Context,
                              metrics: Metrics): Unit = {

    println("Events are " + event)


  }

}

