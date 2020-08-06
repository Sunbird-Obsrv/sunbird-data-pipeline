package org.sunbird.dp.usercache.util

import java.util

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.{Clause, QueryBuilder}
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.DataCache
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfigV2

import scala.collection.mutable

object UserMetadataUpdater {

  val logger = LoggerFactory.getLogger("UserMetadataUpdater")

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
      logger.info(s"User details ( $userId ) are fetched from the db's and updating the redis now.")
      updatedCache
    } else {
      logger.info(s"Skipping the event update from databases since event Does not have user properties or user sigin in type is Anonymous ")
      userCacheData.asInstanceOf[mutable.Map[String,AnyRef]]
    }
  }

  /**
    * The output map contains following information
    * 1. User information from user table
    * 2. Root User's Org name for reports
    * 3. iscustodianuser: Booleon which confirms
    *     if the user is custodian user or state user for fetching report information further
    */
  def getUserDetails(config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil, metrics: Metrics, userId: String,
                     custodianRootOrgId: String): mutable.Map[String, AnyRef] = {
    val userDetails = extractUserMetaData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.userTable,
      QueryBuilder.eq("id", userId),
      metrics,
      cassandraConnect: CassandraUtil,
      config)
    )
    val rootOrgId: String = userDetails.getOrElse("rootorgid", "").asInstanceOf[String]
    val orgname: String = getRootOrgName(rootOrgId, cassandraConnect, metrics, config)
    userDetails.+=("iscustodianuser" -> isCustodianUser(rootOrgId, custodianRootOrgId).asInstanceOf[AnyRef],
      config.orgnameKey -> orgname)
  }

  def getRootOrgName(rootOrgId: String, cassandraConnect: CassandraUtil, metrics: Metrics, config: UserCacheUpdaterConfigV2): String = {
    var orgNameList: util.List[String] = new util.ArrayList[String]()
    var orgName: String = null;
    val orgNameQuery = QueryBuilder.select("orgname").from(config.keySpace, config.orgTable).where(QueryBuilder.eq("id", rootOrgId)).toString
    val rowSet = cassandraConnect.find(orgNameQuery)
    if (null != rowSet && !rowSet.isEmpty) {
      metrics.incCounter(config.dbReadSuccessCount)
      rowSet.forEach(row => orgNameList.add(row.getString(0)))
      orgName = String.join(",", orgNameList)
    }
    else {
      metrics.incCounter(config.dbReadMissCount)
    }
    orgName
  }

  def getReportInfo(userDetails: mutable.Map[String, AnyRef], config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil,
                    userId: String, metrics: Metrics): mutable.Map[String, AnyRef] = {
    if (userDetails.get("iscustodianuser").get == true) {
      val custodianInfo = getCustodianUserInfo(metrics, userId, config, cassandraConnect)
      //fetch location information from USER.locationids
      val locationIds = userDetails.get("locationids").getOrElse(new util.ArrayList()).asInstanceOf[util.List[String]]
      val locationInfoMap= getLocationInformation(locationIds, metrics, userId, config, cassandraConnect)
      custodianInfo.++(locationInfoMap)
    }
    else {
      val custUserChannel = userDetails.getOrElse("rootorgid", "").asInstanceOf[String]
      getStateUserInfo(userDetails, metrics, userId, config, cassandraConnect).+=(config.userChannelKey -> custUserChannel)
    }
  }

  def getCustUserChannel(origianlProvider: AnyRef, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil, metrics: Metrics): String = {
    var result: String = null
    val organisationQuery = QueryBuilder.select("id").from(config.keySpace, config.orgTable)
      .where(QueryBuilder.in("channel", origianlProvider))
      .and(QueryBuilder.eq("isrootorg", true)).allowFiltering().toString
    val organisationInfo = cassandraConnect.findOne(organisationQuery)
    if (null != organisationInfo) {
      metrics.incCounter(config.dbReadSuccessCount)
      result = organisationInfo.getString(0)
    } else {
      metrics.incCounter(config.dbReadMissCount)
    }
    result
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
    * @param rootOrgId
    * @param custRootOrgId
    * @return true/false
    */
  def isCustodianUser(rootOrgId: String, custRootOrgId: String): Boolean = {
    if (rootOrgId == custRootOrgId) true else false
  }

  def getCustodianUserInfo(metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil): mutable.Map[String, AnyRef] = {
    val custodianExtUserMap = extractCustodianUserExternalData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.userExternalIdTable,
      clause = QueryBuilder.eq("userid", userId),
      metrics,
      cassandraConnect, config
    ), config)

    //fetch custodian User's channel from organisation table
    val originalProvider = custodianExtUserMap.getOrElse("originalprovider", "")
    val custChannel = getCustUserChannel(originalProvider, config, cassandraConnect, metrics)
    custodianExtUserMap.+=(config.userChannelKey -> custChannel)
  }

  def extractCustodianUserExternalData(externalIdentity: util.List[Row], config: UserCacheUpdaterConfigV2): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    externalIdentity.forEach{row =>
      row.getString("idtype").toLowerCase() match {
        case config.declareExternalId => result.put(config.externalidKey, row.getString("externalid"))
        case config.declaredSchoolName => result.put(config.schoolNameKey, row.getString("externalid"))
        case config.declaredSchoolCode => result.put(config.schoolUdiseCodeKey, row.getString("externalid"))
        case _ => result
      }
      result.put(config.originalprovider, row.getString("originalprovider"))
    }
    result
  }

  def getStateUserInfo(userDetails: mutable.Map[String, AnyRef], metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil): mutable.Map[String, AnyRef] = {
    val userOrgId = getUserOrgId(metrics, userId, config, cassandraConnect)
    val orgInfoMap = getOrganisationInfo(userOrgId, cassandraConnect, config, metrics)

    // locationids from organisation table
    val locationIds = orgInfoMap.get("locationids").getOrElse(new util.ArrayList()).asInstanceOf[util.List[String]]
    val locationInfoMap= getLocationInformation(locationIds, metrics, userId, config, cassandraConnect)
    //externalid from usr_external_table
    val channel = userDetails.getOrElse("channel", "").asInstanceOf[String]
    val externalId: String = getExternalId(channel, userId, cassandraConnect, config)
    val externalMap = orgInfoMap.++(locationInfoMap).+=(config.externalidKey -> externalId.asInstanceOf[AnyRef])
    externalMap
  }

  def getOrganisationInfo(userOrgIds: util.List[String], cassandraConnect: CassandraUtil, config: UserCacheUpdaterConfigV2,
                          metrics: Metrics): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    val organisationQuery = QueryBuilder.select("id", "isrootorg", "orgcode", "orgname", "locationids").from(config.keySpace, config.orgTable)
      .where(QueryBuilder.in("id", userOrgIds))
      .and(QueryBuilder.eq("isrootorg", false)).allowFiltering().toString
    val organisationInfo = cassandraConnect.findOne(organisationQuery)
    if (null != organisationInfo) {
      metrics.incCounter(config.dbReadSuccessCount)
      val columnDefinitions = organisationInfo.getColumnDefinitions()
      val columnCount = columnDefinitions.size
      for (i <- 0 until columnCount) {
        result.put(columnDefinitions.getName(i), organisationInfo.getObject(i))
      }
      if (result.contains("orgname"))
        result.put(config.schoolNameKey, result.remove("orgname").getOrElse(""))
      if (result.contains("orgcode"))
        result.put(config.schoolUdiseCodeKey, result.remove("orgcode").getOrElse(""))
      result
    } else {
      metrics.incCounter(config.dbReadMissCount)
      mutable.Map[String, AnyRef]()
    }
  }

  def getLocationInformation(locationIds: util.List[String], metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2,
                             cassandraConnect: CassandraUtil): mutable.Map[String, AnyRef] = {
    extractLocationMetaData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.locationTable,
      clause = QueryBuilder.in("id", locationIds),
      metrics, cassandraConnect, config
    ), config)
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
    if (null != userOrgId && !userOrgId.isEmpty) {
      metrics.incCounter(config.dbReadSuccessCount)
      userOrgId.forEach(row => userOrgIdList.add(row.getString(0)))
    } else {
      metrics.incCounter(config.dbReadMissCount)
    }
    userOrgIdList
  }

  def getExternalId(channel: String, userid: String, cassandraConnect: CassandraUtil, config: UserCacheUpdaterConfigV2): String = {
    val externalIdQuery = QueryBuilder.select("externalid").from(config.keySpace, config.userExternalIdTable)
      .where(QueryBuilder.eq("idtype", channel))
      .and(QueryBuilder.eq("provider", channel))
      .and(QueryBuilder.eq("userid", userid)).toString
    val row = cassandraConnect.findOne(externalIdQuery)
    if (null != row)
      row.getString("externalid")
    else ""
  }

  def stringify(userData: mutable.Map[String, AnyRef]): mutable.Map[String, String] = {
    userData.map{f =>
      (f._1, if(!f._2.isInstanceOf[String]) {
        if(null != f._2) {
          new Gson().toJson(f._2)
        }
        else {
          ""
        }
      } else {
        f._2.asInstanceOf[String]
      }
      )}
  }
}
