package org.sunbird.dp.usercache.util

import java.util
import java.util.Collections

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.{Clause, QueryBuilder}
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.{UserCacheUpdaterConfigV2}

import scala.collection.JavaConverters.mapAsJavaMap
import scala.collection.mutable

class UserMetadataUpdater (config: UserCacheUpdaterConfigV2) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserMetadataUpdater])
  private val dataCache: DataCache = new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config), config.userStore, config.userFields)
  private val cassandraConnect: CassandraUtil = new CassandraUtil(config.cassandraHost, config.cassandraPort)
  private val custodianRootOrgId: String = getCustodianRootOrgId()
  dataCache.init()

  def userUpdater(event: Event, metrics: Metrics): Unit = {
    metrics.incCounter(config.totalEventsCount)
    Option(event.getId).map(id => {
      Option(event.getState).map(name => {
        val userData: mutable.Map[String, AnyRef] = name.toUpperCase match {
          case "CREATE" | "CREATED" => createAction(id, event, metrics)
          case "UPDATE" | "UPDATED" => updateAction(id, event, metrics)
          case _ => {
            logger.info(s"Invalid event state name either it should be(Create/Created/Update/Updated) but found $name for ${event.mid()}")
            metrics.incCounter(config.skipCount)
            mutable.Map[String, AnyRef]()
          }
        }
        if (!userData.isEmpty) {
          val redisData = userData.filter{f => (null != f._2)}
          val data = redisData.map(f => (f._1, f._2.toString))
          val filteredData = mapAsJavaMap(data)
          filteredData.values().removeAll(Collections.singleton({}))
          dataCache.hmSet(id, filteredData)
          metrics.incCounter(config.successCount)
          metrics.incCounter(config.userCacheHit)
        } else {
          metrics.incCounter(config.skipCount)
        }
      }).getOrElse(metrics.incCounter(config.skipCount))
    }).getOrElse(metrics.incCounter(config.skipCount))
  }

  /**
    * When Edata.State is Create/Created then insert the user data into redis in string formate
    */
  def createAction(userId: String, event: Event, metrics: Metrics): mutable.Map[String, AnyRef] = {
    val userData: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    Option(event.getContextDataId(cDataType = "SignupType")).map(signInType => {
      if (config.userSelfSignedInTypeList.contains(signInType)) {
        userData.put(config.userSignInTypeKey, config.userSelfSignedKey)
      }
      if (config.userValidatedTypeList.contains(signInType)) {
        userData.put(config.userSignInTypeKey, config.userValidatedKey)
      }
    }).orNull
    userData
  }

  def updateAction(userId: String, event: Event, metrics: Metrics): mutable.Map[String, AnyRef] = {
    val userCacheData: mutable.Map[String, String] = dataCache.hgetAllWithRetry(userId)

    Option(event.getContextDataId(cDataType = "UserRole")).map(loginType => {
      userCacheData.put(config.userLoginTypeKey, loginType)
    })
    val userMetaDataList = event.userMetaData()
    if (!userMetaDataList.isEmpty() && userCacheData.contains(config.userSignInTypeKey) && ("Anonymous" != userCacheData.get(config.userSignInTypeKey))) {

      // Get the user details from the cassandra table

      val userDetails: mutable.Map[String, AnyRef] = userCacheData.++(extractUserMetaData(readFromCassandra(
        keyspace = config.keySpace,
        table = config.userTable,
        QueryBuilder.eq("id", userId),
        metrics)
      ))
      val updatedMap: mutable.Map[String, AnyRef] = userDetails.+=("iscustodianuser" -> isCustodianUser(userDetails, custodianRootOrgId).asInstanceOf[AnyRef])
      val reportInfoMap: mutable.Map[String, AnyRef] = if (updatedMap.get("iscustodianuser").get == true) getCustodianUserInfo(updatedMap, metrics, userId) else getStateUserInfo(updatedMap, metrics, userId)

      logger.info(s"User details ( $userId ) are fetched from the db's and updating the redis now.")
      reportInfoMap
    } else {
      logger.info(s"Skipping the event update from databases since event Does not have user properties or user sigin in type is Anonymous ")
      userCacheData.asInstanceOf[mutable.Map[String,AnyRef]]
    }
  }

  def getCustodianRootOrgId(): String = {
    val custRootOrgIdQuery = QueryBuilder.select("value").from(config.keySpace, config.systemSettingsTable)
      .where(QueryBuilder.eq("id", "custodianRootOrgId")).and(QueryBuilder.eq("field", "custodianRootOrgId")).toString
    val custRootOrgId = cassandraConnect.findOne(custRootOrgIdQuery)
    custRootOrgId.getString("value")
  }

  def readFromCassandra(keyspace: String, table: String, clause: Clause, metrics: Metrics): util.List[Row] = {
    var rowSet: util.List[Row] = null
    val query = QueryBuilder.select.all.from(keyspace, table).where(clause).toString
    rowSet = cassandraConnect.find(query)
    if (null != rowSet && !rowSet.isEmpty) {
      metrics.incCounter(config.dbReadSuccessCount)
      rowSet
    } else {
      metrics.incCounter(config.dbReadMissCount)
      new util.ArrayList[Row]()
    }

  }

  def extractUserMetaData(userDetails: util.List[Row]): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    if (null != userDetails && !userDetails.isEmpty) {
      val row: Row = userDetails.get(0)
      val columnDefinitions = row.getColumnDefinitions()
      val columnCount = columnDefinitions.size
      for (i <- 0 until columnCount) {
        result.put(columnDefinitions.getName(i), row.getObject(i))
      }
    }
    result
  }

  /**
    *
    * @param userDetails, custRootOrgId
    * @return Map having isCustodianUser as true/false
    */
  def isCustodianUser(userDetails: mutable.Map[String, AnyRef], custRootOrgId: String): Boolean = {
    val rootOrgId = userDetails.getOrElse("rootorgid", "")
    if (rootOrgId == custRootOrgId) true else false
  }

  def getCustodianUserInfo(userDetails: mutable.Map[String, AnyRef], metrics: Metrics, userId: String): mutable.Map[String, AnyRef] = {
    val custodianUserMap = userDetails.++(extractUserExternalData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.userExternalIdTable,
      clause = QueryBuilder.eq("userid", userId),
      metrics
    )))
    custodianUserMap
  }

  def extractUserExternalData(externalIdentity: util.List[Row]): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    externalIdentity.forEach(row =>
      row.getString("idtype").toLowerCase() match {
        case config.declareExternalId => result.put("externalid", row.getString("externalid"))
        case config.declaredSchoolName => result.put("schoolname", row.getString("externalid"))
        case config.declaredSchoolCode => result.put("schooludisecode", row.getString("externalid"))
        case config.declaredState => result.put("state", row.getString("externalid"))
        case config.declaredDistrict => result.put("district", row.getString("externalid"))
        case _ => result
      })
    result
  }

  def getStateUserInfo(userDetails: mutable.Map[String, AnyRef], metrics: Metrics, userId: String): mutable.Map[String, AnyRef] = {
    val userOrgId = getUserOrgId(metrics, userId)
    val orgInfoMap = getOrganisationInfo(userOrgId)
    val locationInfoMap: mutable.Map[String, AnyRef] = orgInfoMap.++(getLocationInformation(orgInfoMap, metrics, userId))
    val externalId: String = getExternalId(userDetails, userId)
    val externalMap = locationInfoMap.+=("externalid" -> externalId.asInstanceOf[AnyRef])
    val finalMap = userDetails.++(externalMap)
    finalMap
  }

  def getOrganisationInfo(userOrgIds: util.List[String]): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()

    val organisationQuery = QueryBuilder.select("id", "isrootorg", "orgcode", "orgname", "locationids").from(config.keySpace, config.orgTable)
      .where(QueryBuilder.in("id", userOrgIds))
      .and(QueryBuilder.eq("isrootorg", false)).allowFiltering().toString
    val organisationInfo = cassandraConnect.findOne(organisationQuery)
    if (null != organisationInfo) {
      val columnDefinitions = organisationInfo.getColumnDefinitions()
      val columnCount = columnDefinitions.size
      for (i <- 0 until columnCount) {
        result.put(columnDefinitions.getName(i), organisationInfo.getObject(i))
      }
      if (result.contains("orgname"))
        result.put("schoolname", result.remove("orgname").getOrElse(""))
      if (result.contains("orgcode"))
        result.put("schooludisecode", result.remove("orgcode").getOrElse(""))
    }
    result
  }

  def getLocationInformation(organisationInfo: mutable.Map[String, AnyRef], metrics: Metrics, userId: String): mutable.Map[String, AnyRef] = {

    val locationIds = organisationInfo.get("locationids").getOrElse(new util.ArrayList()).asInstanceOf[util.ArrayList[String]]
    val locationInfoMap:mutable.Map[String, AnyRef] = extractLocationMetaData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.locationTable,
      clause = QueryBuilder.in("id", locationIds),
      metrics
    ))
    locationInfoMap
  }

  def extractLocationMetaData(locationDetails: util.List[Row]): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    locationDetails.forEach((record: Row) => {
      record.getString("type").toLowerCase match {
        case config.stateKey => result.put(config.stateKey, record.getString("name"))
        case config.districtKey => result.put(config.districtKey, record.getString("name"))
        case config.blockKey => result.put(config.blockKey, record.getString("name"))
      }
    })
    result
  }

  def getUserOrgId(metrics: Metrics, userId: String): util.List[String] = {
    val userOrgIdList:util.List[String] = new util.ArrayList()
    val userOrgIdQuery = QueryBuilder.select("organisationid").from(config.keySpace ,config.userOrgTable).where(QueryBuilder.eq("userid", userId)).toString
    val userOrgId = cassandraConnect.find(userOrgIdQuery)
    userOrgId.forEach(row => userOrgIdList.add(row.getString(0)))
    userOrgIdList
  }

  def getExternalId(userMap: mutable.Map[String, AnyRef], userid: String): String = {
    val userChannel = userMap.getOrElse("channel", "").asInstanceOf[String]
    val externalIdQuery = QueryBuilder.select("externalid").from(config.keySpace, config.userExternalIdTable)
      .where(QueryBuilder.eq("idtype", userChannel))
      .and(QueryBuilder.eq("provider", userChannel))
      .and(QueryBuilder.eq("userid", userid)).toString
    cassandraConnect.findOne(externalIdQuery).getString("externalid")
  }

  def closeCache(): Unit = {
    dataCache.close()
  }
}
