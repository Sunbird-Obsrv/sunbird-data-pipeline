package org.sunbird.dp.usercache.util

import com.google.gson.Gson
import org.slf4j.LoggerFactory
import org.sunbird.dp.contentupdater.core.util.RestUtil
import org.sunbird.dp.core.cache.DataCache
import org.sunbird.dp.core.job.Metrics
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.UserCacheUpdaterConfigV2

import scala.collection.JavaConverters._
import scala.collection.mutable

case class UserReadResult(result: java.util.HashMap[String, Any], responseCode: String, params: Params)
case class Response(firstName: String, lastName: String, encEmail: String, encPhone: String, language: java.util.List[String], rootOrgId: String, profileUserType: java.util.HashMap[String, String],
                    userLocations: java.util.ArrayList[java.util.Map[String, AnyRef]], rootOrg: RootOrgInfo, userId: String, framework: java.util.LinkedHashMap[String, java.util.List[String]])
case class RootOrgInfo(orgName: String)
case class Params(msgid: String, err: String, status: String, errmsg: String)

object UserMetadataUpdater {

  private lazy val gson = new Gson()

  val logger = LoggerFactory.getLogger("UserMetadataUpdater")

  def execute(userId: String, event: Event, metrics: Metrics, config: UserCacheUpdaterConfigV2, dataCache: DataCache, restUtil: RestUtil): mutable.Map[String, AnyRef] = {

    val generalInfo = getGeneralInfo(userId, event, metrics, config, dataCache);
    val regdInfo = if (config.regdUserProducerPid.equals(event.producerPid())) {
      getRegisteredUserInfo(userId, event, metrics, config, dataCache, restUtil)
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

  @throws(classOf[Exception])
  def getRegisteredUserInfo(userId: String, event: Event, metrics: Metrics, config: UserCacheUpdaterConfigV2, dataCache: DataCache,
                            restUtil: RestUtil): mutable.Map[String, AnyRef] = {
    var userCacheData: mutable.Map[String, AnyRef] = mutable.Map[String, AnyRef]()

    //?fields=locations is appended in url to get userLocation in API response
    val userReadRes = gson.fromJson[UserReadResult](restUtil.get(String.format("%s%s",config.userReadApiUrl, userId + "?fields=" + config.userReadApiFields)), classOf[UserReadResult])
    if(event.isValid(userReadRes)) {
      // Inc API Read metrics
      metrics.incCounter(config.apiReadSuccessCount)

      val response = gson.fromJson[Response](gson.toJson(userReadRes.result.get("response")), classOf[Response])
      val framework = response.framework
      //flatten BGMS value
      /**
        * Assumption: Board and Framework-id is single valued
        */
      if (!framework.isEmpty) {
        val boardList = framework.getOrDefault("board", List().asJava)
        val board = if (!boardList.isEmpty) boardList.get(0) else ""
        val medium = framework.getOrDefault("medium", List().asJava)
        val grade = framework.getOrDefault("gradeLevel", List().asJava)
        val subject = framework.getOrDefault("subject", List().asJava)
        val frameworkIdList = framework.getOrDefault("id", List().asJava)
        val id = if (!frameworkIdList.isEmpty) frameworkIdList.get(0) else ""
        userCacheData.+=("board" -> board, "medium" -> medium, "grade" -> grade, "subject" -> subject, "framework" -> id)
      }

      //Location and School Information
      val locationInfo = response.userLocations
      if(null != locationInfo && !locationInfo.isEmpty) {
        locationInfo.forEach(location => {
          location.getOrDefault("type", "").asInstanceOf[String].toLowerCase match {
          case config.schoolKey => userCacheData.put(config.schoolNameKey, location.getOrDefault("name", "").asInstanceOf[String])
              userCacheData.put(config.schoolUdiseCodeKey, location.getOrDefault("code", "").asInstanceOf[String])
          case _ => userCacheData.put(location.getOrDefault("type", "").asInstanceOf[String], location.getOrDefault("name", "").asInstanceOf[String])
          }
        })
      }

      //Flatten User Type and subType
      val profileUserType = response.profileUserType
      if (null != profileUserType && !profileUserType.isEmpty) {
        userCacheData.+=(config.userTypeKey -> profileUserType.getOrDefault(config.`type`, ""),
          config.userSubtypeKey -> profileUserType.getOrDefault(config.subtype, ""))
      }

      //Personal information
      userCacheData.+=(config.firstName -> response.firstName, config.lastName -> response.lastName,
        config.language -> response.language,
        config.orgnameKey -> response.rootOrg.orgName,
        config.rootOrgId -> response.rootOrgId,
        config.phone -> response.encPhone,
        config.email -> response.encEmail,
        config.userId -> response.userId)

    } else if (config.userReadApiErrors.contains(userReadRes.responseCode.toUpperCase) && userReadRes.params.status.equalsIgnoreCase("USER_ACCOUNT_BLOCKED")) { //Skip the events for which response is 400 Bad request
      logger.info(s"User Read API has response as ${userReadRes.responseCode.toUpperCase} for user: ${userId}")
      metrics.incCounter(config.apiReadMissCount)
    } else {
      logger.info(s"User Read API does not have details for user: ${userId}")
      metrics.incCounter(config.apiReadMissCount)
      throw new Exception(s"User Read API does not have details for user: ${userId}")
    }
    userCacheData
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
