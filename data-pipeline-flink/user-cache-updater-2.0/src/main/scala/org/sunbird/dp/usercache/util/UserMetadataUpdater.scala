package org.sunbird.dp.usercache.util

import java.util

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.{ Clause, QueryBuilder }
import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.DataCache
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfigV2

import scala.collection.JavaConverters._
import scala.collection.mutable

object UserMetadataUpdater {

  val logger = LoggerFactory.getLogger("UserMetadataUpdater")

  def execute(userId: String, event: Event, metrics: Metrics, config: UserCacheUpdaterConfigV2, dataCache: DataCache,
              cassandraConnect: CassandraUtil, custodianRootOrgId: String): mutable.Map[String, AnyRef] = {

    val generalInfo = getGeneralInfo(userId, event, metrics, config, dataCache);
    val regdInfo = if (config.regdUserProducerPid.equals(event.producerPid())) {
      getRegisteredUserInfo(userId, event, metrics, config, dataCache, cassandraConnect, custodianRootOrgId)
    } else mutable.Map[String, String]()
    generalInfo.++:(regdInfo);
  }

  def getGeneralInfo(userId: String, event: Event, metrics: Metrics, config: UserCacheUpdaterConfigV2, dataCache: DataCache): mutable.Map[String, String] = {
    val userCacheData: mutable.Map[String, String] = mutable.Map[String, String]()
    Option(event.getContextDataId(cDataType = "SignupType")).map(signInType => {
      if (config.userSelfSignedInTypeList.contains(signInType)) {
        userCacheData.put(config.userSignInTypeKey, config.userSelfSignedKey)
      }
      if (config.userValidatedTypeList.contains(signInType)) {
        userCacheData.put(config.userSignInTypeKey, config.userValidatedKey)
      }
    }).orNull
    Option(event.getContextDataId(cDataType = "UserRole")).map(loginType => {
      userCacheData.put(config.userLoginTypeKey, loginType)
    })
    userCacheData;
  }

  def getRegisteredUserInfo(userId: String, event: Event, metrics: Metrics, config: UserCacheUpdaterConfigV2, dataCache: DataCache,
                            cassandraConnect: CassandraUtil, custodianRootOrgId: String): mutable.Map[String, AnyRef] = {
    val userDetails: mutable.Map[String, AnyRef] = getUserDetails(config, cassandraConnect, metrics, userId, custodianRootOrgId)
    val reportInfoMap: mutable.Map[String, AnyRef] =
      if (!userDetails.isEmpty)
        getReportInfo(userDetails, config, cassandraConnect, userId, metrics)
      else {
        logger.info(s"User DB does not have details for user: ${userId}")
        metrics.incCounter(config.skipCount)
        mutable.Map[String, AnyRef]()
      }
    userDetails.++(reportInfoMap)
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
    var userDetails: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    userDetails = extractUserMetaData(readFromCassandra(
      keyspace = config.keySpace,
      table = config.userTable,
      QueryBuilder.eq("id", userId),
      metrics,
      cassandraConnect: CassandraUtil,
      config))

    if (!userDetails.isEmpty && null != userDetails) {
      val rootOrgId: String = userDetails.getOrElse("rootorgid", "").asInstanceOf[String]
      val orgname = if(null != rootOrgId) getRootOrgName(rootOrgId, cassandraConnect, metrics, config) else ""
      userDetails.+=(
        "iscustodianuser" -> isCustodianUser(rootOrgId, custodianRootOrgId).asInstanceOf[AnyRef],
        config.orgnameKey -> orgname)
    } else userDetails
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
    } else {
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
      val locationInfoMap = getLocationInformation(locationIds, metrics, userId, config, cassandraConnect)
      custodianInfo.++(locationInfoMap)
    } else {
      val userChannel = userDetails.getOrElse("rootorgid", "").asInstanceOf[String]
      getStateUserInfo(userDetails, metrics, userId, config, cassandraConnect, userChannel).+=(config.userChannelKey -> userChannel)
    }
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
    val framework = result.get("framework").getOrElse(new util.LinkedHashMap()).asInstanceOf[util.LinkedHashMap[String, java.util.List[String]]]
    if (!framework.isEmpty) {
      val board = framework.getOrDefault("board", List().asJava)
      val medium = framework.getOrDefault("medium", List().asJava)
      val grade = framework.getOrDefault("gradeLevel", List().asJava)
      val subject = framework.getOrDefault("subject", List().asJava)
      val id = framework.getOrDefault("id", List().asJava)
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
    var userExternalInfo: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()

    val userDeclarationList = readUserDeclaredInfo(userId, config, cassandraConnect, metrics)
    if (!userDeclarationList.isEmpty) {
      val userDeclarationInfo = userDeclarationList.stream().filter(f => f.getString("persona").equalsIgnoreCase(config.personaType)).findFirst().orElse(null)
      if (null != userDeclarationInfo) {
        val orgId = userDeclarationInfo.getString("orgid")
        val validOrgId = validateOrgId(orgId, config, cassandraConnect, metrics)

        if (validOrgId) {
          val columnDefinitions = userDeclarationInfo.getColumnDefinitions()
          val columnCount = columnDefinitions.size
          for (i <- 0 until columnCount) {
            userExternalInfo.put(columnDefinitions.getName(i), userDeclarationInfo.getObject(i))
          }
          val userInfo = userExternalInfo.get("userinfo").getOrElse(new util.LinkedHashMap()).asInstanceOf[util.HashMap[String, String]]
          if (!userInfo.isEmpty) {
            userExternalInfo.+=(
              config.externalidKey -> userInfo.getOrDefault(config.declareExternalId, ""),
              config.schoolUdiseCodeKey -> userInfo.getOrDefault(config.declaredSchoolCode, ""),
              config.schoolNameKey -> userInfo.getOrDefault(config.declaredSchoolName, ""),
              config.userChannelKey -> orgId)
          }
        }
      }
    }
    userExternalInfo
  }

  def readUserDeclaredInfo(userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil, metrics: Metrics): util.List[Row] = {
    var rowSet: util.List[Row] = null
    val userDeclarationQuery = QueryBuilder.select().all().from(config.keySpace, config.userDeclarationTable)
      .where(QueryBuilder.eq("userid", userId)).toString

    rowSet = cassandraConnect.find(userDeclarationQuery)
    if (null != rowSet && !rowSet.isEmpty) {
      metrics.incCounter(config.dbReadSuccessCount)
      rowSet
    } else {
      metrics.incCounter(config.dbReadMissCount)
      new util.ArrayList[Row]()
    }
  }

  def validateOrgId(orgId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil, metrics: Metrics): Boolean = {
    val organisationQuery = QueryBuilder.select("id", "isrootorg").from(config.keySpace, config.orgTable)
      .where(QueryBuilder.in("id", orgId)).toString
    val organisationList = cassandraConnect.find(organisationQuery).asScala.filter(p => p.getBool("isrootorg"))
    val organisationInfo = if(!organisationList.isEmpty) organisationList.head else null;
    if (null != organisationInfo) {
      metrics.incCounter(config.dbReadSuccessCount)
      true
    } else {
      metrics.incCounter(config.dbReadMissCount)
      false
    }
  }

  def getStateUserInfo(userDetails: mutable.Map[String, AnyRef], metrics: Metrics, userId: String,
                       config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil, rootOrgId: String): mutable.Map[String, AnyRef] = {
    val userOrgId = getUserOrgId(metrics, userId, config, cassandraConnect)
    val orgInfoMap = getOrganisationInfo(userOrgId, cassandraConnect, config, metrics)

    // locationids from organisation table
    val locationIds = orgInfoMap.get("locationids").getOrElse(new util.ArrayList()).asInstanceOf[util.List[String]]
    val locationInfoMap = getLocationInformation(locationIds, metrics, userId, config, cassandraConnect)
    //externalid from usr_external_table
    var externalId: String = if (null != rootOrgId) {
      getExternalId(rootOrgId, userId, cassandraConnect, config)
    } else ""
    val externalMap = orgInfoMap.++(locationInfoMap).+=(config.externalidKey -> externalId.asInstanceOf[AnyRef])
    externalMap
  }

  def getOrganisationInfo(userOrgIds: util.List[String], cassandraConnect: CassandraUtil, config: UserCacheUpdaterConfigV2,
                          metrics: Metrics): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    val organisationQuery = QueryBuilder.select("id", "isrootorg", "externalid", "orgname", "locationids").from(config.keySpace, config.orgTable)
      .where(QueryBuilder.in("id", userOrgIds)).toString
    val organisationList = cassandraConnect.find(organisationQuery).asScala.filter(p => !p.getBool("isrootorg"))
    val organisationInfo = if(!organisationList.isEmpty) organisationList.head else null;
    if (null != organisationInfo) {
      metrics.incCounter(config.dbReadSuccessCount)
      val columnDefinitions = organisationInfo.getColumnDefinitions()
      val columnCount = columnDefinitions.size
      for (i <- 0 until columnCount) {
        result.put(columnDefinitions.getName(i), organisationInfo.getObject(i))
      }
      if (result.contains("orgname"))
        result.put(config.schoolNameKey, result.remove("orgname").getOrElse(""))
      if (result.contains("externalid"))
        result.put(config.schoolUdiseCodeKey, result.remove("externalid").getOrElse(""))
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
      metrics, cassandraConnect, config), config)
  }

  def extractLocationMetaData(locationDetails: util.List[Row], config: UserCacheUpdaterConfigV2): mutable.Map[String, AnyRef] = {
    val result: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()
    locationDetails.forEach((record: Row) => {
      record.getString("type").toLowerCase match {
        case config.stateKey    => result.put(config.stateKey, record.getString("name"))
        case config.districtKey => result.put(config.districtKey, record.getString("name"))
        case config.blockKey    => result.put(config.blockKey, record.getString("name"))
      }
    })
    result
  }

  def getUserOrgId(metrics: Metrics, userId: String, config: UserCacheUpdaterConfigV2, cassandraConnect: CassandraUtil): util.List[String] = {
    val userOrgIdList: util.List[String] = new util.ArrayList()
    val userOrgIdQuery = QueryBuilder.select("organisationid").from(config.keySpace, config.userOrgTable).where(QueryBuilder.eq("userid", userId)).toString
    val userOrgId = cassandraConnect.find(userOrgIdQuery)
    if (null != userOrgId && !userOrgId.isEmpty) {
      metrics.incCounter(config.dbReadSuccessCount)
      userOrgId.forEach(row => userOrgIdList.add(row.getString(0)))
    } else {
      metrics.incCounter(config.dbReadMissCount)
    }
    userOrgIdList
  }

  def getExternalId(rootOrgId: String, userid: String, cassandraConnect: CassandraUtil, config: UserCacheUpdaterConfigV2): String = {
    val externalIdQuery = QueryBuilder.select("externalid").from(config.keySpace, config.userExternalIdTable)
      .where(QueryBuilder.eq("idtype", rootOrgId))
      .and(QueryBuilder.eq("provider", rootOrgId))
      .and(QueryBuilder.eq("userid", userid)).toString
    val row = cassandraConnect.findOne(externalIdQuery)
    if (null != row)
      row.getString("externalid")
    else ""
  }

  def stringify(userData: mutable.Map[String, AnyRef]): mutable.Map[String, String] = {
    userData.map { f =>
      (f._1, if (!f._2.isInstanceOf[String]) {
        if (null != f._2) {
          new Gson().toJson(f._2)
        } else {
          ""
        }
      } else {
        f._2.asInstanceOf[String]
      })
    }
  }
}
