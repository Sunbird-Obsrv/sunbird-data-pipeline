package org.sunbird.dp.usercache.functions

import java.util
import java.util.Collections

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.{Clause, QueryBuilder}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.CassandraUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfig

import scala.collection.JavaConverters._
import scala.collection.mutable

class UserCacheUpdaterFunction(config: UserCacheUpdaterConfig)(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[UserCacheUpdaterFunction])
  private var dataCache: DataCache = _
  private var cassandraConnect: CassandraUtil = _

  override def metricsList(): List[String] = {
    List(config.dbReadSuccessCount, config.dbReadMissCount, config.userCacheHit, config.skipCount, config.successCount, config.totalEventsCount)
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config), config.userStore, config.userFields)
    dataCache.init()
    cassandraConnect = new CassandraUtil(config.cassandraHost, config.cassandraPort)
  }

  override def close(): Unit = {
    super.close()
    dataCache.close()
  }

  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, metrics: Metrics): Unit = {
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

  /**
   * When edata.state is update/updated then update the user metadta
   * information by reading from the cassandra
   */

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

      val custodianRootOrgId: String = getCustodianRootOrgId(metrics)
      val isCustodianMap: mutable.Map[String, AnyRef] = getIsCustodianUser(userDetails, custodianRootOrgId)
      val reportInfoMap: mutable.Map[String, AnyRef] = if (isCustodianMap.get("iscustodianuser").get == true) getCustodianUserInfo(isCustodianMap, metrics, userId) else getStateUserInfo(isCustodianMap, metrics, userId)
      logger.info(s"User details ( $userId ) are fetched from the db's and updating the redis now.")
      reportInfoMap
    } else {
      logger.info(s"Skipping the event update from databases since event Does not have user properties or user sigin in type is Anonymous ")
      userCacheData.asInstanceOf[mutable.Map[String,AnyRef]]
    }
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

  /**
    * It Will return the rootOrgId to filter out custodian users and state users
    * @param metrics
    * @return custodianRootOrgId
    */
  def getCustodianRootOrgId(metrics: Metrics): String = {
    val custRootOrgId = readFromCassandra(
      keyspace = config.keySpace,
      table = config.systemSettingsTable,
      QueryBuilder.eq("id", "custodianRootOrgId"),
      metrics)
    custRootOrgId.get(0).getString(2)
  }

  /**
    *
    * @param userDetails, custRootOrgId
    * @return Map having isCustodianUser as true/false
    */
  def getIsCustodianUser(userDetails: mutable.Map[String, AnyRef], custRootOrgId: String): mutable.Map[String, AnyRef] ={
    val rootOrgId = userDetails.getOrElse("rootorgid", "")
    val isCustodianUserMap: mutable.Map[String, AnyRef] =
      if (rootOrgId == custRootOrgId) userDetails.+=("iscustodianuser" -> true.asInstanceOf[AnyRef])
      else userDetails.+=("iscustodianuser" -> false.asInstanceOf[AnyRef])
    isCustodianUserMap
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
    })
    result
  }

  def getStateUserInfo(userDetails: mutable.Map[String, AnyRef], metrics: Metrics, userId: String): mutable.Map[String, AnyRef] = {
    val userOrgId = getUserOrgId(metrics, userId)
    val orgInfoMap = getOrganisationInfo(userOrgId)
    val locationInfoMap: mutable.Map[String, AnyRef] = orgInfoMap.++(getLocationInformation(orgInfoMap, metrics, userId))
    val externalId: String = getExternalIdMap(userDetails, userId)
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

    val columnDefinitions = organisationInfo.getColumnDefinitions()
    val columnCount = columnDefinitions.size
    for (i <- 0 until columnCount) {
      result.put(columnDefinitions.getName(i), organisationInfo.getObject(i))
    }
    val filteredMap =
      if(result.contains("orgname"))
        result.put("schoolname", result.remove("orgname").getOrElse(""))
    if(result.contains("orgcode"))
        result.put("schooludisecode", result.remove("orgcode").getOrElse(""))
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

  def getUserOrgId(metrics: Metrics, userId: String): util.List[String] = {
    val userOrgIdList:util.List[String] = new util.ArrayList()
    val userOrgIdQuery = QueryBuilder.select("organisationid").from(config.keySpace ,config.userOrgTable).where(QueryBuilder.eq("userid", userId)).toString
    val userOrgId = cassandraConnect.find(userOrgIdQuery)
    userOrgId.forEach(row => userOrgIdList.add(row.getString(0)))
    userOrgIdList
  }

  def getExternalIdMap(userMap: mutable.Map[String, AnyRef], userid: String): String = {
    val userChannel = userMap.getOrElse("channel", "").asInstanceOf[String]
    val externalIdQuery = QueryBuilder.select("externalid").from(config.keySpace, config.userExternalIdTable)
      .where(QueryBuilder.eq("idtype", userChannel))
      .and(QueryBuilder.eq("provider", userChannel))
      .and(QueryBuilder.eq("userid", userid)).toString
    cassandraConnect.findOne(externalIdQuery).getString("externalId")
  }

}
