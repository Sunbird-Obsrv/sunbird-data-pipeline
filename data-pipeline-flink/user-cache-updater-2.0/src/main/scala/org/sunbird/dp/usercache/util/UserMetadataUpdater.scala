package org.sunbird.dp.usercache.util

import java.util

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.{Clause, QueryBuilder}
import org.sunbird.dp.core.cache.DataCache
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfigV2

import scala.collection.mutable

object UserMetadataUpdater {

  /**
    * When Edata.State is Create/Created then insert the user data into redis in string formate
    */
  def createAction(userId: String, event: Event, metrics: Metrics, config: UserCacheUpdaterConfigV2): mutable.Map[String, AnyRef] = {
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

  def updateAction(userId: String, event: Event, metrics: Metrics, config: UserCacheUpdaterConfigV2, dataCache: DataCache,
                   cassandraConnect: CassandraUtil, custodianRootOrgId: String): mutable.Map[String, AnyRef] = {
    val userCacheData: mutable.Map[String, String] = dataCache.hgetAllWithRetry(userId)

    Option(event.getContextDataId(cDataType = "UserRole")).map(loginType => {
      userCacheData.put(config.userLoginTypeKey, loginType)
    })
    val userMetaDataList = event.userMetaData()
    if (!userMetaDataList.isEmpty() && userCacheData.contains(config.userSignInTypeKey) && ("Anonymous" != userCacheData.get(config.userSignInTypeKey))) {

      // Get the user details from the cassandra table
      val userDetails: mutable.Map[String, AnyRef] = getUserDetails(config, cassandraConnect, metrics, userId, custodianRootOrgId)
      val reportInfoMap: mutable.Map[String, AnyRef] = getReportInfo(userDetails, config, cassandraConnect, userId, metrics)
      val updatedCache = userCacheData.++(userDetails).++(reportInfoMap)

//      logger.info(s"User details ( $userId ) are fetched from the db's and updating the redis now.")
      updatedCache
    } else {
//      logger.info(s"Skipping the event update from databases since event Does not have user properties or user sigin in type is Anonymous ")
      userCacheData.asInstanceOf[mutable.Map[String,AnyRef]]
    }
  }

  def getUserDetails(config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil, metrics: Metrics, userId: String, custodianRootOrgId: String): mutable.Map[String, AnyRef] = {
    val userDetails = extractUserMetaData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.userTable,
      QueryBuilder.eq("id", userId),
      metrics,
      cassandraConnect: CassandraUtil,
      config)
    )
    userDetails.+=("iscustodianuser" -> isCustodianUser(userDetails, custodianRootOrgId).asInstanceOf[AnyRef])
  }

  def getReportInfo(userDetails: mutable.Map[String, AnyRef], config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil, userId: String, metrics: Metrics): mutable.Map[String, AnyRef] = {
    if (userDetails.get("iscustodianuser").get == true) getCustodianUserInfo(metrics, userId, config, cassandraConnect) else getStateUserInfo(userDetails, metrics, userId, config, cassandraConnect)
  }

  def readFromCassandra(keyspace: String, table: String, clause: Clause, metrics: Metrics, cassandraConnect: CassandraUtil, config: UserCacheUpdaterConfigV2): util.List[Row] = {
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
    val framework = result.get("framework").getOrElse(new util.LinkedHashMap()).asInstanceOf[util.LinkedHashMap[String, List[String]]]
    if(!framework.isEmpty)
     {
       val board = framework.getOrDefault("board", List()).asInstanceOf[util.ArrayList[String]]
       val medium = framework.getOrDefault("medium", List()).asInstanceOf[util.ArrayList[String]]
       val grade = framework.getOrDefault("gradeLevel", List()).asInstanceOf[util.ArrayList[String]]
       val subject = framework.getOrDefault("subject", List()).asInstanceOf[util.ArrayList[String]]
       val id = framework.getOrDefault("id", List()).asInstanceOf[util.ArrayList[String]]
       result.+=("board" -> board, "medium" -> medium, "grade" -> grade, "subject" -> subject, "framework" -> id)
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

  def getCustodianUserInfo(metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil): mutable.Map[String, AnyRef] = {
    val custodianUserMap = extractUserExternalData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.userExternalIdTable,
      clause = QueryBuilder.eq("userid", userId),
      metrics,
      cassandraConnect, config
    ), config)
    custodianUserMap
  }

  def extractUserExternalData(externalIdentity: util.List[Row], config: UserCacheUpdaterConfigV2): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    externalIdentity.forEach(row =>
      row.getString("idtype").toLowerCase() match {
        case config.declareExternalId => result.put(config.externalidKey, row.getString("externalid"))
        case config.declaredSchoolName => result.put(config.schoolNameKey, row.getString("externalid"))
        case config.declaredSchoolCode => result.put(config.schoolUdiseCodeKey, row.getString("externalid"))
        case config.declaredStateKey => result.put(config.stateKey, row.getString("externalid"))
        case config.declaredDistrictKey => result.put(config.districtKey, row.getString("externalid"))
        case _ => result
      })
    result
  }

  def getStateUserInfo(userDetails: mutable.Map[String, AnyRef], metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil): mutable.Map[String, AnyRef] = {
    val userOrgId = getUserOrgId(metrics, userId, config, cassandraConnect)
    val orgInfoMap = getOrganisationInfo(userOrgId, cassandraConnect, config)
    val locationInfoMap: mutable.Map[String, AnyRef] = orgInfoMap.++(getLocationInformation(orgInfoMap, metrics, userId, config, cassandraConnect))
    val externalId: String = getExternalId(userDetails, userId, cassandraConnect, config)
    val externalMap = locationInfoMap.+=(config.externalidKey -> externalId.asInstanceOf[AnyRef])
    externalMap
  }

  def getOrganisationInfo(userOrgIds: util.List[String], cassandraConnect: CassandraUtil, config: UserCacheUpdaterConfigV2): mutable.Map[String, AnyRef] = {
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
        result.put(config.schoolNameKey, result.remove("orgname").getOrElse(""))
      if (result.contains("orgcode"))
        result.put(config.schoolUdiseCodeKey, result.remove("orgcode").getOrElse(""))
    }
    result
  }

  def getLocationInformation(organisationInfo: mutable.Map[String, AnyRef], metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil): mutable.Map[String, AnyRef] = {

    val locationIds = organisationInfo.get("locationids").getOrElse(new util.ArrayList()).asInstanceOf[util.ArrayList[String]]
    val locationInfoMap:mutable.Map[String, AnyRef] = extractLocationMetaData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.locationTable,
      clause = QueryBuilder.in("id", locationIds),
      metrics, cassandraConnect, config
    ), config)
    locationInfoMap
  }

  def extractLocationMetaData(locationDetails: util.List[Row], config: UserCacheUpdaterConfigV2): mutable.Map[String, AnyRef] = {
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

  def getUserOrgId(metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil): util.List[String] = {
    val userOrgIdList:util.List[String] = new util.ArrayList()
    val userOrgIdQuery = QueryBuilder.select("organisationid").from(config.keySpace ,config.userOrgTable).where(QueryBuilder.eq("userid", userId)).toString
    val userOrgId = cassandraConnect.find(userOrgIdQuery)
    userOrgId.forEach(row => userOrgIdList.add(row.getString(0)))
    userOrgIdList
  }

  def getExternalId(userMap: mutable.Map[String, AnyRef], userid: String, cassandraConnect: CassandraUtil, config: UserCacheUpdaterConfigV2): String = {
    val userChannel = userMap.getOrElse("channel", "").asInstanceOf[String]
    val externalIdQuery = QueryBuilder.select("externalid").from(config.keySpace, config.userExternalIdTable)
      .where(QueryBuilder.eq("idtype", userChannel))
      .and(QueryBuilder.eq("provider", userChannel))
      .and(QueryBuilder.eq("userid", userid)).toString
    cassandraConnect.findOne(externalIdQuery).getString("externalid")
  }
}
