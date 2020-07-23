package org.sunbird.dp.deviceprofile.functions

import java.lang.reflect.Type
import java.sql.{PreparedStatement, SQLException, Timestamp}
import java.util
import java.util.Collections

import com.google.gson.reflect.TypeToken
import com.google.gson.{Gson, JsonObject, JsonSyntaxException}
import org.apache.commons.lang3.StringUtils
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.postgresql.util.PGobject
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.{PostgresConnect, PostgresConnectionConfig}
import org.sunbird.dp.deviceprofile.domain.DeviceProfile
import org.sunbird.dp.deviceprofile.task.DeviceProfileUpdaterConfig

import scala.collection.JavaConverters._
import scala.collection.mutable


class DeviceProfileUpdaterFunction(config: DeviceProfileUpdaterConfig,
                                   @transient var dataCache: DataCache = null,
                                   @transient var postgresConnect: PostgresConnect = null
                                  )(implicit val stringTypeInfo: TypeInformation[String])
  extends BaseProcessFunction[util.Map[String, AnyRef], util.Map[String, AnyRef]](config) {

  val mapType: Type = new TypeToken[util.Map[String, AnyRef]]() {}.getType
  private[this] val logger = LoggerFactory.getLogger(classOf[DeviceProfileUpdaterFunction])

  override def metricsList(): List[String] = {
    List(config.deviceDbHitCount, config.cacheHitCount, config.failedEventCount, config.successCount)
  }


  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    if (dataCache == null) {
      val redisConnect = new RedisConnect(config.metaRedisHost, config.metaRedisPort, config)
      dataCache = new DataCache(config, redisConnect, config.deviceDbStore, config.fields)
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


    val deviceId = event.get(config.deviceId).asInstanceOf[String]
    if (null != deviceId && !deviceId.isEmpty) {
      event.values.removeAll(Collections.singleton(""))
      event.values.removeAll(Collections.singleton("{}"))
      val deviceProfile = new DeviceProfile().fromMap(event.asInstanceOf[util.Map[String, String]], config)
      val cacheData = addDeviceDataToCache(deviceId, deviceProfile)
      addDeviceDataToDB(deviceId, updatedEvent(cacheData, event.asInstanceOf[util.Map[String, String]]))
      metrics.incCounter(config.successCount)
      metrics.incCounter(config.deviceDbHitCount)
      metrics.incCounter(config.cacheHitCount)
    } else {
      metrics.incCounter(config.failedEventCount)
    }
  }

  def addDeviceDataToDB(deviceId: String, deviceData: util.Map[String, String]): Unit = {
    val firstAccess: Long = if (deviceData.get("first_access").isInstanceOf[String]) deviceData.get("first_access").toLong else deviceData.get("first_access").asInstanceOf[Number].longValue()
    val lastUpdatedDate: Long = deviceData.get(config.apiLastUpdatedOn).asInstanceOf[Number].longValue()
    val parsedKeys = new util.ArrayList[String](util.Arrays.asList("first_access", config.apiLastUpdatedOn))
    deviceData.keySet.removeAll(parsedKeys)
    val columns = String.join(",", deviceData.keySet())
    val values = StringUtils.repeat("?,", deviceData.values.size - 1);
    /**
     * Inserting data into postgres
     */
    val postgresQuery = String.format("INSERT INTO %s (api_last_updated_on,updated_date,%s) VALUES(?,?,%s?) ON CONFLICT(device_id) DO UPDATE SET (api_last_updated_on,updated_date,%s)=(?,?,%s?);", config.postgresTable, columns, values, columns, values)

    val preparedStatement = postgresConnect.getConnection.prepareStatement(postgresQuery)
    preparedStatement.setTimestamp(1, new Timestamp(lastUpdatedDate)) // Adding api_last_updated_on as timestamp to index 1 of preparestatement

    preparedStatement.setTimestamp(deviceData.values.size + 3, new Timestamp(lastUpdatedDate)) // Adding api_last_updated_on as timestamp to 3rd index after the map size(for on conflict value)

    preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis)) // Adding updated_date as timestamp to index 2 of preparestatement

    preparedStatement.setTimestamp(deviceData.values.size + 4, new Timestamp(System.currentTimeMillis)) // Adding updated_date as timestamp to 4th index after the map size(for on conflict value)

    setPrepareStatement(preparedStatement, 2, deviceData.asInstanceOf[util.Map[String, AnyRef]]) // Adding map values to preparestatement from index after the api_last_updated_on and updated_on

    setPrepareStatement(preparedStatement, deviceData.values().size() + 4, deviceData.asInstanceOf[util.Map[String, AnyRef]])

    preparedStatement.executeUpdate
    preparedStatement.close()
    // Update first_access column if the first_access is null
    val updateFirstAccessQuery = String.format("UPDATE %s SET first_access = '%s' WHERE device_id = '%s' AND first_access IS NULL", config.postgresTable, new Timestamp(firstAccess).toString, deviceId)
    postgresConnect.execute(updateFirstAccessQuery)

    if (null != deviceData.get(config.userDeclaredState)) {
      // Updating user_declared_on column
      val updateUserDeclaredOnQuery = String.format("UPDATE %s SET user_declared_on = '%s' WHERE device_id = '%s' AND user_declared_on IS NULL", config.postgresTable, new Timestamp(lastUpdatedDate).toString, deviceId)
      postgresConnect.execute(updateUserDeclaredOnQuery)
    }
    logger.info(s"Device ID: $deviceId Updated the postgres device profile table")
  }

  private def addDeviceDataToCache(deviceId: String, deviceProfile: DeviceProfile): util.Map[String, String] = {
    val deviceMap = deviceProfile.toMap(config)
    val updatedDeviceMap = removeEmptyFields(deviceMap)
    if (updatedDeviceMap.get(config.userDeclaredState) == null) updatedDeviceMap.remove(config.userDeclaredOn)
    if (dataCache.isExists(deviceId)) {
      val redisData = dataCache.hgetAllWithRetry(deviceId)
      val firstAccess = redisData.get(config.firstAccess)
      val userDeclaredOn = redisData.get(config.userDeclaredOn)
      if ("0" != firstAccess.getOrElse("0")) updatedDeviceMap.remove(config.firstAccess)
      dataCache.hmSet(deviceId, updatedDeviceMap.asInstanceOf[util.Map[String, String]])
      logger.info(s"Device ID: $deviceId is present in the redis and required fields into redis")
      updatedDeviceMap
    }
    else {
      dataCache.hmSet(deviceId, updatedDeviceMap.asInstanceOf[util.Map[String, String]])
      logger.info(s"Device ID: $deviceId does not exists in the redis and all fields into redis")
      updatedDeviceMap
    }
  }

  @throws[SQLException]
  private def setPrepareStatement(preparedStatement: PreparedStatement, index: Int, deviceData: util.Map[String, AnyRef]): Unit = {
    val gson = new Gson()
    var count = index
    deviceData.values().forEach(value => {
      count += 1
      val jsonObject = new PGobject
      try {
        jsonObject.setType("json")
        jsonObject.setValue(gson.fromJson(value.toString, classOf[JsonObject]).toString)
        preparedStatement.setObject(count, jsonObject)
      } catch {
        case ex@(_: JsonSyntaxException | _: ClassCastException) =>
          preparedStatement.setString(count, value.toString)
      }
    })
  }

  def removeEmptyFields(deviceMap: util.Map[String, String]): util.Map[String, String] = {
    deviceMap.values.removeAll(Collections.singleton(""))
    deviceMap.values.removeAll(Collections.singleton("{}"))
    deviceMap
  }

  /**
   * Updating the redis data into postgres
   * UseCase:- If the firstAccess is present in only redis but not in the postgres.
   * Then it should updated the postgres with redis data.
   */
  def updatedEvent(redisData: util.Map[String, String], deviceProfileEvent: util.Map[String, String]): util.Map[String, String] = {
    if(redisData.containsKey(config.firstAccess)) deviceProfileEvent.put("first_access", redisData.get(config.firstAccess))
    deviceProfileEvent
  }
}

