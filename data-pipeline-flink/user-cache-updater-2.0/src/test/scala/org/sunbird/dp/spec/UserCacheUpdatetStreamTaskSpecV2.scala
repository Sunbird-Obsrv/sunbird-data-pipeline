package org.sunbird.dp.spec

import java.io.IOException
import java.util
import com.google.gson.Gson
import com.typesafe.config.{Config, ConfigFactory}
import okhttp3.mockwebserver.{MockResponse, MockWebServer}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.sunbird.dp.core.cache.RedisConnect
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.fixture.EventFixture
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.task.{UserCacheUpdaterConfigV2, UserCacheUpdaterStreamTaskV2}
import org.sunbird.dp.{BaseMetricsReporter, BaseTestSpec}
import redis.clients.jedis.Jedis
import redis.embedded.RedisServer

class UserCacheUpdatetStreamTaskSpecV2 extends BaseTestSpec with BeforeAndAfterEach {

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setConfiguration(testConfiguration())
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  var redisServer: RedisServer = _
  val config: Config = ConfigFactory.load("test.conf")
  val userCacheConfig: UserCacheUpdaterConfigV2 = new UserCacheUpdaterConfigV2(config)
  val mockKafkaUtil: FlinkKafkaConnector = mock[FlinkKafkaConnector](Mockito.withSettings().serializable())
  val gson = new Gson()
  var jedis: Jedis = _
  var server = new MockWebServer()

  override protected def beforeAll(): Unit = {
    super.beforeAll()
    redisServer = new RedisServer(6340)
    redisServer.start()
    BaseMetricsReporter.gaugeMetrics.clear()
    val redisConnect = new RedisConnect(userCacheConfig.metaRedisHost, userCacheConfig.metaRedisPort, userCacheConfig)
    jedis = redisConnect.getConnection(userCacheConfig.userStore)
    flinkCluster.before()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
    server.close()
    flinkCluster.after()
  }

  override protected def beforeEach(): Unit = {
    server.close()
    server = new MockWebServer()
    super.beforeEach()
  }

  def setupRestUtilData(): Unit = {

    val user1 = """{"id":".private.user.v1.read.123456","ver":"private","ts":"2021-03-09 11:33:42:061+0530","params":{"resmsgid":null,"msgid":"06ff91d6-043e-4714-b002-6f8b90a04723","err":null,"status":"success","errmsg":null},"responseCode":"OK","result":{"response":{"webPages":[],"maskedPhone":null,"subject":[],"channel":"root2","language":["English"],"updatedDate":null,"password":null,"managedBy":null,"flagsValue":2,"id":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","recoveryEmail":"","identifier":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","thumbnail":null,"updatedBy":null,"accesscode":null,"locationIds":["8db3345c-1bfc-4276-aef1-7ea0f3183211","d087424e-18cb-49b0-865c-98f265c73ed3","13087424e-18cb-49b0-865c-98f265c73ed3","43087424e-18cb-49b0-865c-98f265c73ed3"],"externalIds":[{"idType":"root2","provider":"root2","id":"123456"}],"registryId":null,"rootOrgId":"0127738024883077121","prevUsedEmail":"","firstName":"Utkarsha","tncAcceptedOn":null,"allTncAccepted":{},"phone":"","dob":null,"grade":[],"currentLoginTime":null,"userType":null,"status":1,"lastName":"Kapoor","tncLatestVersion":"v1","gender":null,"roles":["PUBLIC"],"prevUsedPhone":"","stateValidated":false,"isDeleted":false,"organisations":[{"organisationId":"0127738024883077121","updatedBy":null,"addedByName":null,"addedBy":null,"roles":["PUBLIC"],"approvedBy":null,"updatedDate":null,"userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","approvaldate":null,"isDeleted":false,"hashTagId":"0127738024883077121","isRejected":null,"id":"0132322953313320960","position":null,"isApproved":null,"orgjoindate":"2021-03-09 11:31:31:930+0530","orgLeftDate":null}],"profileUserType":{"type":"administrator","subType":"deo"},"profileUserTypes":[{"type":"administrator","subType":"hm"},{"type":"teacher"},{"type":"administrator","subType":"crp"},{"type":"other"},{"type":"parent"}],"userLocations":[{"code":"21","name":"Odisha","id":"8db3345c-1bfc-4276-aef1-7ea0f3183211","type":"state","parentId":null},{"code":"2112","name":"CUTTACK","id":"d087424e-18cb-49b0-865c-98f265c73ed3","type":"district","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"BLOCK1","id":"13087424e-18cb-49b0-865c-98f265c73ed3","type":"block","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"CLUSTER1","id":"43087424e-18cb-49b0-865c-98f265c73ed3","type":"cluster","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"name":"DPS, MATHURA","id":"63087424e-18cb-49b0-865c-98f265c73ed3","type":"school","code":"3183211"}],"countryCode":null,"tncLatestVersionUrl":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html","maskedEmail":"am******@yopmail.com","tempPassword":null,"email":"am******@yopmail.com","rootOrg":{"dateTime":null,"preferredLanguage":"English","keys":{},"approvedBy":null,"channel":"root2","description":"Root Org2","updatedDate":null,"addressId":null,"orgType":null,"provider":null,"locationId":null,"orgCode":null,"theme":null,"id":"0127738024883077121","communityId":null,"isApproved":null,"email":null,"slug":"root2","isSSOEnabled":null,"thumbnail":null,"orgName":"Root Org2","updatedBy":null,"locationIds":[],"externalId":null,"isRootOrg":true,"rootOrgId":"0127738024883077121","approvedDate":null,"imgUrl":null,"homeUrl":null,"orgTypeId":null,"isDefault":null,"createdDate":"2019-05-31 16:41:29:485+0530","createdBy":null,"parentOrgId":null,"hashTagId":"0127738024883077121","noOfMembers":null,"status":1},"phoneVerified":false,"profileSummary":null,"recoveryPhone":"","avatar":null,"userName":"creatortest_72yz","userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","userSubType":null,"promptTnC":true,"emailVerified":true,"lastLoginTime":null,"createdDate":"2021-03-09 11:31:23:189+0530","framework":{"board":["IGOT-Health"],"gradeLevel":["Volunteers"],"id":["igot_health"],"medium":["English"],"subject":["IRCS"]},"createdBy":null,"location":null,"tncAcceptedVersion":null}}}"""
    val clientError = """{"id":".private.user.v1.read.bcda3d80-0de0-4179-a702-9f89e6eeec56","ver":"private","ts":"2021-05-24 07:10:48:908+0000","params":{"resmsgid":null,"msgid":"f17cf782-6f67-4575-9152-21cf2daabe85","err":"USER_ACCOUNT_BLOCKED","status":"USER_ACCOUNT_BLOCKED","errmsg":"User account has been blocked ."},"responseCode":"CLIENT_ERROR","result":{}}"""
    val user2 = """{"id":".private.user.v1.read.95e4942d-cbe8-477d-aebd-ad8e6de4bfc81","ver":"private","ts":"2021-03-16 11:42:24:074+0000","params":{"resmsgid":null,"msgid":"e082153a-159c-4d18-a2d3-a5dc509f4d9f","err":"USER_NOT_FOUND","status":"USER_NOT_FOUND","errmsg":"user not found."},"responseCode":"RESOURCE_NOT_FOUND","result":{}}"""
    val user3 = """{"id":".private.user.v1.read.123456","ver":"private","ts":"2021-03-09 11:33:42:061+0530","params":{"resmsgid":null,"msgid":"06ff91d6-043e-4714-b002-6f8b90a04723","err":null,"status":"success","errmsg":null},"responseCode":"OK","result":{"response":{"webPages":[],"maskedPhone":null,"subject":[],"channel":"root2","language":[],"updatedDate":null,"password":null,"managedBy":null,"flagsValue":2,"id":"user-3","recoveryEmail":"","identifier":"user-3","thumbnail":null,"updatedBy":null,"accesscode":null,"locationIds":["8db3345c-1bfc-4276-aef1-7ea0f3183211","d087424e-18cb-49b0-865c-98f265c73ed3","13087424e-18cb-49b0-865c-98f265c73ed3","43087424e-18cb-49b0-865c-98f265c73ed3"],"externalIds":[{"idType":"root2","provider":"root2","id":"123456"}],"registryId":null,"rootOrgId":"01285019302823526477","prevUsedEmail":"","firstName":"Isha","lastName":"Wakankar","tncLatestVersion":"v1","gender":null,"roles":["PUBLIC"],"prevUsedPhone":"","stateValidated":false,"isDeleted":false,"organisations":[{"organisationId":"0127738024883077121","updatedBy":null,"addedByName":null,"addedBy":null,"roles":["PUBLIC"],"approvedBy":null,"updatedDate":null,"userId":"user-3","approvaldate":null,"isDeleted":false,"hashTagId":"0127738024883077121","isRejected":null,"id":"0132322953313320960","position":null,"isApproved":null,"orgjoindate":"2021-03-09 11:31:31:930+0530","orgLeftDate":null}],"profileUserType":{"type":"administrator","subType":"deo"},"profileUserTypes":[{"type":"administrator","subType":"deo"}],"userLocations":[{"code":"21","name":"Odisha","id":"8db3345c-1bfc-4276-aef1-7ea0f3183211","type":"state","parentId":null},{"code":"2112","name":"CUTTACK","id":"d087424e-18cb-49b0-865c-98f265c73ed3","type":"district","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"CLUSTER1","id":"43087424e-18cb-49b0-865c-98f265c73ed3","type":"cluster","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"name":"DPS, MATHURA","id":"63087424e-18cb-49b0-865c-98f265c73ed3","type":"school","code":"3183211"}],"countryCode":null,"tncLatestVersionUrl":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html","maskedEmail":"am******@yopmail.com","tempPassword":null,"email":"am******@yopmail.com","rootOrg":{"dateTime":null,"preferredLanguage":"English","keys":{},"approvedBy":null,"channel":"root2","description":"Root Org2","updatedDate":null,"addressId":null,"orgType":null,"provider":null,"locationId":null,"orgCode":null,"theme":null,"id":"0127738024883077121","communityId":null,"isApproved":null,"email":null,"slug":"root2","isSSOEnabled":null,"thumbnail":null,"orgName":"Root Org2","updatedBy":null,"locationIds":[],"externalId":null,"isRootOrg":true,"rootOrgId":"0127738024883077121","approvedDate":null,"imgUrl":null,"homeUrl":null,"orgTypeId":null,"isDefault":null,"createdDate":"2019-05-31 16:41:29:485+0530","createdBy":null,"parentOrgId":null,"hashTagId":"0127738024883077121","noOfMembers":null,"status":1},"phoneVerified":false,"profileSummary":null,"recoveryPhone":"","avatar":null,"userName":"creatortest_72yz","userId":"user-3","userSubType":null,"promptTnC":true,"emailVerified":true,"lastLoginTime":null,"createdDate":"2021-03-09 11:31:23:189+0530","framework":{},"createdBy":null,"location":null,"tncAcceptedVersion":null}}}"""
    val user4 = """{"id":".private.user.v1.read.123456","ver":"private","ts":"2021-03-09 11:33:42:061+0530","params":{"resmsgid":null,"msgid":"06ff91d6-043e-4714-b002-6f8b90a04723","err":null,"status":"success","errmsg":null},"responseCode":"OK","result":{"response":{"thumbnail":null,"encPhone": "LednrgEat6NcG8DX3ue89T7osrjR76AWYqtSYzAvg1jvx1a4lZn6KssNuPP4UeiGoVPQ24MeJbTing10uKJND1TrTfg+K3pGBxLTV+B2SKBxLdBdWzwFOkCsEv53x7bP4T6a+wzaAmCWueMEdPmZuRg==", "encEmail": "LednrgEat6NcG8DX3ue89T7osrjR76AWYqtSYzAvg1jvx1a4lZn6KssNuPP4UeiGoVPQ24MeJbTing10uKJND1TrTfg+K3pGBxLTV+B2SKBxLdBdWzwFOkCsEv53x7bP4T6a+wzaAmCWueMEdPmZuRg==","updatedBy":null,"accesscode":null,"locationIds":["8db3345c-1bfc-4276-aef1-7ea0f3183211","d087424e-18cb-49b0-865c-98f265c73ed3","13087424e-18cb-49b0-865c-98f265c73ed3","43087424e-18cb-49b0-865c-98f265c73ed3"],"externalIds":[{"idType":"root2","provider":"root2","id":"123456"}],"registryId":null,"rootOrgId":"0127738024883077121","prevUsedEmail":"","firstName":"Manju","tncAcceptedOn":null,"allTncAccepted":{},"phone":"","dob":null,"grade":[],"currentLoginTime":null,"userType":null,"status":1,"lastName":"Davanam","tncLatestVersion":"v1","gender":null,"roles":["PUBLIC"],"prevUsedPhone":"","stateValidated":false,"isDeleted":false,"organisations":[{"organisationId":"0127738024883077121","updatedBy":null,"addedByName":null,"addedBy":null,"roles":["PUBLIC"],"approvedBy":null,"updatedDate":null,"userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","approvaldate":null,"isDeleted":false,"hashTagId":"0127738024883077121","isRejected":null,"id":"0132322953313320960","position":null,"isApproved":null,"orgjoindate":"2021-03-09 11:31:31:930+0530","orgLeftDate":null}],"profileUserType":{"type":"teacher"},"profileUserTypes":[{"type":"teacher"}],"userLocations":[{"code":"21","name":"Odisha","id":"8db3345c-1bfc-4276-aef1-7ea0f3183211","type":"state","parentId":null},{"code":"2112","name":"CUTTACK","id":"d087424e-18cb-49b0-865c-98f265c73ed3","type":"district","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"BLOCK1","id":"13087424e-18cb-49b0-865c-98f265c73ed3","type":"block","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"CLUSTER1","id":"43087424e-18cb-49b0-865c-98f265c73ed3","type":"cluster","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"type":"location","name":""}],"countryCode":null,"tncLatestVersionUrl":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html","maskedEmail":"am******@yopmail.com","tempPassword":null,"email":"am******@yopmail.com","rootOrg":{"dateTime":null,"preferredLanguage":"English","keys":{},"approvedBy":null,"channel":"root2","description":"Root Org2","updatedDate":null,"addressId":null,"orgType":null,"provider":null,"locationId":null,"orgCode":null,"theme":null,"id":"0127738024883077121","communityId":null,"isApproved":null,"email":null,"slug":"root2","isSSOEnabled":null,"thumbnail":null,"orgName":"Root Org2","updatedBy":null,"locationIds":[],"externalId":null,"isRootOrg":true,"rootOrgId":"0127738024883077121","approvedDate":null,"imgUrl":null,"homeUrl":null,"orgTypeId":null,"isDefault":null,"createdDate":"2019-05-31 16:41:29:485+0530","createdBy":null,"parentOrgId":null,"hashTagId":"0127738024883077121","noOfMembers":null,"status":1},"phoneVerified":false,"profileSummary":null,"recoveryPhone":"","avatar":null,"userName":"creatortest_72yz","userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","userSubType":null,"promptTnC":true,"emailVerified":true,"lastLoginTime":null,"createdDate":"2021-03-09 11:31:23:189+0530","framework":{"board":[],"gradeLevel":["Volunteers"],"medium":["English"]},"createdBy":null,"location":null,"tncAcceptedVersion":null}}}"""
    val invalid_json = "{\"id\":\"sunbird.dialcode.read\",\"ver\":\"3.0\",\"ts\":\"2020-04-21T02:51:39ZZ\",\"params\":{\"resmsgid\":\"4544fce4-efee-4ee2-8816-fdb3f60ac492\",\"msgid\":null,\"err\":null,\"status\":\"successful\",\"errmsg\":null},\"responseCode\":\"OK\",\"result\":{\"status\":\"No dialcodeFound\"}}"
    val user10 = """{"id":".private.user.v1.read.123456","ver":"private","ts":"2021-03-09 11:33:42:061+0530","params":{"resmsgid":null,"msgid":"06ff91d6-043e-4714-b002-6f8b90a04723","err":null,"status":"success","errmsg":null},"responseCode":"OK","result":{"response":{"webPages":[],"maskedPhone":null,"subject":[],"channel":"root2","language":["English"],"updatedDate":null,"password":null,"managedBy":null,"flagsValue":2,"id":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","recoveryEmail":"","identifier":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","thumbnail":null,"updatedBy":null,"accesscode":null,"locationIds":["8db3345c-1bfc-4276-aef1-7ea0f3183211","d087424e-18cb-49b0-865c-98f265c73ed3","13087424e-18cb-49b0-865c-98f265c73ed3","43087424e-18cb-49b0-865c-98f265c73ed3"],"externalIds":[{"idType":"root2","provider":"root2","id":"123456"}],"registryId":null,"rootOrgId":"0127738024883077121","prevUsedEmail":"","firstName":"Utkarsha","tncAcceptedOn":null,"allTncAccepted":{},"phone":"","dob":null,"grade":[],"currentLoginTime":null,"userType":null,"status":1,"lastName":"Kapoor","tncLatestVersion":"v1","gender":null,"roles":["PUBLIC"],"prevUsedPhone":"","stateValidated":false,"isDeleted":false,"organisations":[{"organisationId":"0127738024883077121","updatedBy":null,"addedByName":null,"addedBy":null,"roles":["PUBLIC"],"approvedBy":null,"updatedDate":null,"userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","approvaldate":null,"isDeleted":false,"hashTagId":"0127738024883077121","isRejected":null,"id":"0132322953313320960","position":null,"isApproved":null,"orgjoindate":"2021-03-09 11:31:31:930+0530","orgLeftDate":null}],"profileUserType":{"type":"administrator-hm","subType":null},"profileUserTypes":[{"type":"administrator","subType":null}],"userLocations":[{"code":"21","name":"Odisha","id":"8db3345c-1bfc-4276-aef1-7ea0f3183211","type":"state","parentId":null},{"code":"2112","name":"CUTTACK","id":"d087424e-18cb-49b0-865c-98f265c73ed3","type":"district","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"BLOCK1]","id":"13087424e-18cb-49b0-865c-98f265c73ed3","type":"block","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"CLUSTER1","id":"43087424e-18cb-49b0-865c-98f265c73ed3","type":"cluster","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"name":"[RPMMAT M.S UDHADIH","id":"63087424e-18cb-49b0-865c-98f265c73ed3","type":"school","code":"3183211"}],"countryCode":null,"tncLatestVersionUrl":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html","maskedEmail":"am******@yopmail.com","tempPassword":null,"email":"am******@yopmail.com","rootOrg":{"dateTime":null,"preferredLanguage":"English","keys":{},"approvedBy":null,"channel":"root2","description":"Root Org2","updatedDate":null,"addressId":null,"orgType":null,"provider":null,"locationId":null,"orgCode":null,"theme":null,"id":"0127738024883077121","communityId":null,"isApproved":null,"email":null,"slug":"root2","isSSOEnabled":null,"thumbnail":null,"orgName":"Root Org2","updatedBy":null,"locationIds":[],"externalId":null,"isRootOrg":true,"rootOrgId":"0127738024883077121","approvedDate":null,"imgUrl":null,"homeUrl":null,"orgTypeId":null,"isDefault":null,"createdDate":"2019-05-31 16:41:29:485+0530","createdBy":null,"parentOrgId":null,"hashTagId":"0127738024883077121","noOfMembers":null,"status":1},"phoneVerified":false,"profileSummary":null,"recoveryPhone":"","avatar":null,"userName":"creatortest_72yz","userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","userSubType":null,"promptTnC":true,"emailVerified":true,"lastLoginTime":null,"createdDate":"2021-03-09 11:31:23:189+0530","framework":{"board":["IGOT-Health"],"gradeLevel":["Volunteers"],"id":["igot_health"],"medium":["English"],"subject":["IRCS"]},"createdBy":null,"location":null,"tncAcceptedVersion":null}}}"""
    try
      server.start(3000)
    catch {
      case e: IOException =>
        System.out.println("Exception" + e)
    }
    server.enqueue(new MockResponse().setBody(user1))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-1")
    server.enqueue(new MockResponse().setBody(user1))
//    server.enqueue(new MockResponse().setBody(clientError))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-9")
    server.enqueue(new MockResponse().setBody(user1))
//    server.enqueue(new MockResponse().setBody(user2))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-2")
    server.enqueue(new MockResponse().setBody(user3))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-3")
    server.enqueue(new MockResponse().setBody(user4))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-4")
    server.enqueue(new MockResponse().setBody(user1))
//    server.enqueue(new MockResponse().setBody(user2))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-5")
    server.enqueue(new MockResponse().setBody(user10))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-10")
//    server.enqueue(new MockResponse().setBody(invalid_json))
//    server.url("http://127.0.0.1:3000/api/dialcode/v3/read/X3J6W2")
//    server.enqueue(new MockResponse().setBody(invalid_json))
//    server.url("http://127.0.0.1:3000/api/dialcode/v3/read/X3J6W3")
    server.enqueue(new MockResponse().setHeader("Authorization", "auth_token"))

  }

  def setupRestUtilDataWithErrors(): Unit = {

    val user1 = """{"id":".private.user.v1.read.123456","ver":"private","ts":"2021-03-09 11:33:42:061+0530","params":{"resmsgid":null,"msgid":"06ff91d6-043e-4714-b002-6f8b90a04723","err":null,"status":"success","errmsg":null},"responseCode":"OK","result":{"response":{"webPages":[],"maskedPhone":null,"subject":[],"channel":"root2","language":["English"],"updatedDate":null,"password":null,"managedBy":null,"flagsValue":2,"id":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","recoveryEmail":"","identifier":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","thumbnail":null,"updatedBy":null,"accesscode":null,"locationIds":["8db3345c-1bfc-4276-aef1-7ea0f3183211","d087424e-18cb-49b0-865c-98f265c73ed3","13087424e-18cb-49b0-865c-98f265c73ed3","43087424e-18cb-49b0-865c-98f265c73ed3"],"externalIds":[{"idType":"root2","provider":"root2","id":"123456"}],"registryId":null,"rootOrgId":"0127738024883077121","prevUsedEmail":"","firstName":"Utkarsha","tncAcceptedOn":null,"allTncAccepted":{},"phone":"","dob":null,"grade":[],"currentLoginTime":null,"userType":null,"status":1,"lastName":"Kapoor","tncLatestVersion":"v1","gender":null,"roles":["PUBLIC"],"prevUsedPhone":"","stateValidated":false,"isDeleted":false,"organisations":[{"organisationId":"0127738024883077121","updatedBy":null,"addedByName":null,"addedBy":null,"roles":["PUBLIC"],"approvedBy":null,"updatedDate":null,"userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","approvaldate":null,"isDeleted":false,"hashTagId":"0127738024883077121","isRejected":null,"id":"0132322953313320960","position":null,"isApproved":null,"orgjoindate":"2021-03-09 11:31:31:930+0530","orgLeftDate":null}],"profileUserType":{"type":"administrator","subType":"deo"},"profileUserTypes":[{"type":"administrator","subType":"hm"},{"type":"teacher"},{"type":"administrator","subType":"crp"},{"type":"other"},{"type":"parent"}],"userLocations":[{"code":"21","name":"Odisha","id":"8db3345c-1bfc-4276-aef1-7ea0f3183211","type":"state","parentId":null},{"code":"2112","name":"CUTTACK","id":"d087424e-18cb-49b0-865c-98f265c73ed3","type":"district","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"BLOCK1","id":"13087424e-18cb-49b0-865c-98f265c73ed3","type":"block","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"CLUSTER1","id":"43087424e-18cb-49b0-865c-98f265c73ed3","type":"cluster","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"name":"DPS, MATHURA","id":"63087424e-18cb-49b0-865c-98f265c73ed3","type":"school","code":"3183211"}],"countryCode":null,"tncLatestVersionUrl":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html","maskedEmail":"am******@yopmail.com","tempPassword":null,"email":"am******@yopmail.com","rootOrg":{"dateTime":null,"preferredLanguage":"English","keys":{},"approvedBy":null,"channel":"root2","description":"Root Org2","updatedDate":null,"addressId":null,"orgType":null,"provider":null,"locationId":null,"orgCode":null,"theme":null,"id":"0127738024883077121","communityId":null,"isApproved":null,"email":null,"slug":"root2","isSSOEnabled":null,"thumbnail":null,"orgName":"Root Org2","updatedBy":null,"locationIds":[],"externalId":null,"isRootOrg":true,"rootOrgId":"0127738024883077121","approvedDate":null,"imgUrl":null,"homeUrl":null,"orgTypeId":null,"isDefault":null,"createdDate":"2019-05-31 16:41:29:485+0530","createdBy":null,"parentOrgId":null,"hashTagId":"0127738024883077121","noOfMembers":null,"status":1},"phoneVerified":false,"profileSummary":null,"recoveryPhone":"","avatar":null,"userName":"creatortest_72yz","userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","userSubType":null,"promptTnC":true,"emailVerified":true,"lastLoginTime":null,"createdDate":"2021-03-09 11:31:23:189+0530","framework":{"board":["IGOT-Health"],"gradeLevel":["Volunteers"],"id":["igot_health"],"medium":["English"],"subject":["IRCS"]},"createdBy":null,"location":null,"tncAcceptedVersion":null}}}"""
    val clientError = """{"id":".private.user.v1.read.bcda3d80-0de0-4179-a702-9f89e6eeec56","ver":"private","ts":"2022-03-17 08:33:32:188+0000","params":{"resmsgid":"4896831f-ef38-4731-bb0b-564a05e98576","msgid":"4896831f-ef38-4731-bb0b-564a05e98576","err":"UOS_USRRED0006","status":"FAILED","errmsg":"User account has been blocked ."},"responseCode":"CLIENT_ERROR","result":{}}"""
    val user2 = """{"id":".private.user.v1.read.95e4942d-cbe8-477d-aebd-ad8e6de4bfc81","ver":"private","ts":"2021-03-16 11:42:24:074+0000","params":{"resmsgid":null,"msgid":"e082153a-159c-4d18-a2d3-a5dc509f4d9f","err":"USER_NOT_FOUND","status":"USER_NOT_FOUND","errmsg":"user not found."},"responseCode":"RESOURCE_NOT_FOUND","result":{}}"""
    val user3 = """{"id":".private.user.v1.read.123456","ver":"private","ts":"2021-03-09 11:33:42:061+0530","params":{"resmsgid":null,"msgid":"06ff91d6-043e-4714-b002-6f8b90a04723","err":null,"status":"success","errmsg":null},"responseCode":"OK","result":{"response":{"webPages":[],"maskedPhone":null,"subject":[],"channel":"root2","language":[],"updatedDate":null,"password":null,"managedBy":null,"flagsValue":2,"id":"user-3","recoveryEmail":"","identifier":"user-3","thumbnail":null,"updatedBy":null,"accesscode":null,"locationIds":["8db3345c-1bfc-4276-aef1-7ea0f3183211","d087424e-18cb-49b0-865c-98f265c73ed3","13087424e-18cb-49b0-865c-98f265c73ed3","43087424e-18cb-49b0-865c-98f265c73ed3"],"externalIds":[{"idType":"root2","provider":"root2","id":"123456"}],"registryId":null,"rootOrgId":"01285019302823526477","prevUsedEmail":"","firstName":"Isha","lastName":"Wakankar","tncLatestVersion":"v1","gender":null,"roles":["PUBLIC"],"prevUsedPhone":"","stateValidated":false,"isDeleted":false,"organisations":[{"organisationId":"0127738024883077121","updatedBy":null,"addedByName":null,"addedBy":null,"roles":["PUBLIC"],"approvedBy":null,"updatedDate":null,"userId":"user-3","approvaldate":null,"isDeleted":false,"hashTagId":"0127738024883077121","isRejected":null,"id":"0132322953313320960","position":null,"isApproved":null,"orgjoindate":"2021-03-09 11:31:31:930+0530","orgLeftDate":null}],"profileUserTypes":[{"type":"administrator","subType":"deo"}],"profileUserType":{"type":"administrator","subType":"deo"},"userLocations":[{"code":"21","name":"Odisha","id":"8db3345c-1bfc-4276-aef1-7ea0f3183211","type":"state","parentId":null},{"code":"2112","name":"CUTTACK","id":"d087424e-18cb-49b0-865c-98f265c73ed3","type":"district","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"CLUSTER1","id":"43087424e-18cb-49b0-865c-98f265c73ed3","type":"cluster","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"name":"DPS, MATHURA","id":"63087424e-18cb-49b0-865c-98f265c73ed3","type":"school","code":"3183211"}],"countryCode":null,"tncLatestVersionUrl":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html","maskedEmail":"am******@yopmail.com","tempPassword":null,"email":"am******@yopmail.com","rootOrg":{"dateTime":null,"preferredLanguage":"English","keys":{},"approvedBy":null,"channel":"root2","description":"Root Org2","updatedDate":null,"addressId":null,"orgType":null,"provider":null,"locationId":null,"orgCode":null,"theme":null,"id":"0127738024883077121","communityId":null,"isApproved":null,"email":null,"slug":"root2","isSSOEnabled":null,"thumbnail":null,"orgName":"Root Org2","updatedBy":null,"locationIds":[],"externalId":null,"isRootOrg":true,"rootOrgId":"0127738024883077121","approvedDate":null,"imgUrl":null,"homeUrl":null,"orgTypeId":null,"isDefault":null,"createdDate":"2019-05-31 16:41:29:485+0530","createdBy":null,"parentOrgId":null,"hashTagId":"0127738024883077121","noOfMembers":null,"status":1},"phoneVerified":false,"profileSummary":null,"recoveryPhone":"","avatar":null,"userName":"creatortest_72yz","userId":"user-3","userSubType":null,"promptTnC":true,"emailVerified":true,"lastLoginTime":null,"createdDate":"2021-03-09 11:31:23:189+0530","framework":{},"createdBy":null,"location":null,"tncAcceptedVersion":null}}}"""
    val user4 = """{"id":".private.user.v1.read.123456","ver":"private","ts":"2021-03-09 11:33:42:061+0530","params":{"resmsgid":null,"msgid":"06ff91d6-043e-4714-b002-6f8b90a04723","err":null,"status":"success","errmsg":null},"responseCode":"OK","result":{"response":{"thumbnail":null,"encPhone": "LednrgEat6NcG8DX3ue89T7osrjR76AWYqtSYzAvg1jvx1a4lZn6KssNuPP4UeiGoVPQ24MeJbTing10uKJND1TrTfg+K3pGBxLTV+B2SKBxLdBdWzwFOkCsEv53x7bP4T6a+wzaAmCWueMEdPmZuRg==", "encEmail": "LednrgEat6NcG8DX3ue89T7osrjR76AWYqtSYzAvg1jvx1a4lZn6KssNuPP4UeiGoVPQ24MeJbTing10uKJND1TrTfg+K3pGBxLTV+B2SKBxLdBdWzwFOkCsEv53x7bP4T6a+wzaAmCWueMEdPmZuRg==","updatedBy":null,"accesscode":null,"locationIds":["8db3345c-1bfc-4276-aef1-7ea0f3183211","d087424e-18cb-49b0-865c-98f265c73ed3","13087424e-18cb-49b0-865c-98f265c73ed3","43087424e-18cb-49b0-865c-98f265c73ed3"],"externalIds":[{"idType":"root2","provider":"root2","id":"123456"}],"registryId":null,"rootOrgId":"0127738024883077121","prevUsedEmail":"","firstName":"Manju","tncAcceptedOn":null,"allTncAccepted":{},"phone":"","dob":null,"grade":[],"currentLoginTime":null,"userType":null,"status":1,"lastName":"Davanam","tncLatestVersion":"v1","gender":null,"roles":["PUBLIC"],"prevUsedPhone":"","stateValidated":false,"isDeleted":false,"organisations":[{"organisationId":"0127738024883077121","updatedBy":null,"addedByName":null,"addedBy":null,"roles":["PUBLIC"],"approvedBy":null,"updatedDate":null,"userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","approvaldate":null,"isDeleted":false,"hashTagId":"0127738024883077121","isRejected":null,"id":"0132322953313320960","position":null,"isApproved":null,"orgjoindate":"2021-03-09 11:31:31:930+0530","orgLeftDate":null}],"profileUserType":{"type":"teacher"},"profileUserTypes":[{"type":"teacher"}],"userLocations":[{"code":"21","name":"Odisha","id":"8db3345c-1bfc-4276-aef1-7ea0f3183211","type":"state","parentId":null},{"code":"2112","name":"CUTTACK","id":"d087424e-18cb-49b0-865c-98f265c73ed3","type":"district","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"BLOCK1","id":"13087424e-18cb-49b0-865c-98f265c73ed3","type":"block","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"code":"211","name":"CLUSTER1","id":"43087424e-18cb-49b0-865c-98f265c73ed3","type":"cluster","parentId":"8db3345c-1bfc-4276-aef1-7ea0f3183211"},{"type":"location","name":""}],"countryCode":null,"tncLatestVersionUrl":"https://dev-sunbird-temp.azureedge.net/portal/terms-and-conditions-v1.html","maskedEmail":"am******@yopmail.com","tempPassword":null,"email":"am******@yopmail.com","rootOrg":{"dateTime":null,"preferredLanguage":"English","keys":{},"approvedBy":null,"channel":"root2","description":"Root Org2","updatedDate":null,"addressId":null,"orgType":null,"provider":null,"locationId":null,"orgCode":null,"theme":null,"id":"0127738024883077121","communityId":null,"isApproved":null,"email":null,"slug":"root2","isSSOEnabled":null,"thumbnail":null,"orgName":"Root Org2","updatedBy":null,"locationIds":[],"externalId":null,"isRootOrg":true,"rootOrgId":"0127738024883077121","approvedDate":null,"imgUrl":null,"homeUrl":null,"orgTypeId":null,"isDefault":null,"createdDate":"2019-05-31 16:41:29:485+0530","createdBy":null,"parentOrgId":null,"hashTagId":"0127738024883077121","noOfMembers":null,"status":1},"phoneVerified":false,"profileSummary":null,"recoveryPhone":"","avatar":null,"userName":"creatortest_72yz","userId":"a962a4ff-b5b5-46ad-a9fa-f54edf1bcccb","userSubType":null,"promptTnC":true,"emailVerified":true,"lastLoginTime":null,"createdDate":"2021-03-09 11:31:23:189+0530","framework":{"board":[],"gradeLevel":["Volunteers"],"medium":["English"]},"createdBy":null,"location":null,"tncAcceptedVersion":null}}}"""
    val invalid_json = "{\"id\":\"sunbird.dialcode.read\",\"ver\":\"3.0\",\"ts\":\"2020-04-21T02:51:39ZZ\",\"params\":{\"resmsgid\":\"4544fce4-efee-4ee2-8816-fdb3f60ac492\",\"msgid\":null,\"err\":null,\"status\":\"successful\",\"errmsg\":null},\"responseCode\":\"OK\",\"result\":{\"status\":\"No dialcodeFound\"}}"
    try
      server.start(3000)
    catch {
      case e: IOException =>
        System.out.println("Exception" + e)
    }
    server.enqueue(new MockResponse().setBody(user1))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-1")
    server.enqueue(new MockResponse().setBody(clientError))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-9")
    server.enqueue(new MockResponse().setBody(user2))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-2")
    server.enqueue(new MockResponse().setBody(user3))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-3")
    server.enqueue(new MockResponse().setBody(user4))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-4")
    server.enqueue(new MockResponse().setBody(user2))
    server.url("http://127.0.0.1:3000/learner/private/user/v1/read/user-5")
    server.enqueue(new MockResponse().setHeader("Authorization", "auth_token"))

  }

  "UserCacheUpdater" should "be able to add user to cache with all information" in {
    setupRestUtilData()
    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new InputSource)

    val task = new UserCacheUpdaterStreamTaskV2(userCacheConfig, mockKafkaUtil)
    task.process()

    /**
      * Metrics Assertions
      */
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.userCacheHit}").getValue() should be(7)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.totalEventsCount}").getValue() should be(9)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.skipCount}").getValue() should be(3)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.successCount}").getValue() should be(7)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.apiReadSuccessCount}").getValue() should be(7)
    BaseMetricsReporter.gaugeMetrics(s"${userCacheConfig.jobName}.${userCacheConfig.apiReadMissCount}").getValue() should be(0)

    // select index: 12
    jedis.select(userCacheConfig.userStore)

    //user information: user-1
    var userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-1")
    userInfo.get("firstname") should be ("Utkarsha")
    userInfo.get("lastname") should be ("Kapoor")
    userInfo.get("state") should be ("Odisha")
    userInfo.get("district") should be ("CUTTACK")
    userInfo.get("block") should be ("BLOCK1")
    userInfo.get("cluster") should be ("CLUSTER1")
    userInfo.get("schooludisecode") should be ("3183211")
    userInfo.get("schoolname") should be ("DPS, MATHURA")
    userInfo.get("usertype") should be ("administrator,teacher,other,parent")
    userInfo.get("usersubtype") should be ("hm,crp")
    userInfo.get("board") should be ("IGOT-Health")
    userInfo.get("rootorgid") should be ("0127738024883077121")
    userInfo.get("orgname") should be ("Root Org2")
    userInfo.get("subject") should be ("""["IRCS"]""")
    userInfo.get("language") should be ("""["English"]""")
    userInfo.get("grade") should be ("""["Volunteers"]""")
    userInfo.get("framework") should be ("igot_health")
    userInfo.get("medium") should be ("""["English"]""")
    userInfo.get("profileusertypes") should be ("""\[{"subType":"hm","type":"administrator"},{"type":"teacher"},{"subType":"crp","type":"administrator"},{"type":"other"},{"type":"parent"}\]""")

    //user information: user-2
    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-2")
    userInfo.get("usersignintype") should be ("Self-Signed-In")
    userInfo.keySet() should not contain theSameElementsAs(Seq("firstname", "lastname", "schoolname", "schooludisecode", "schoolname"))

    /***
      * user information: user-3
      * Framework and block information is stored as empty string
      */
    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-3")
    userInfo.get("firstname") should be ("Isha")
    userInfo.get("lastname") should be ("Wakankar")
    userInfo.get("state") should be ("Odisha")
    userInfo.get("district") should be ("CUTTACK")
    userInfo.get("block") should be (null)
    userInfo.get("cluster") should be ("CLUSTER1")
    userInfo.get("schooludisecode") should be ("3183211")
    userInfo.get("schoolname") should be ("DPS, MATHURA")
    userInfo.get("usertype") should be ("administrator")
    userInfo.get("usersubtype") should be ("deo")
    userInfo.get("rootorgid") should be ("01285019302823526477")
    userInfo.get("orgname") should be ("Root Org2")
    userInfo.get("board") should be (null)
    userInfo.get("subject") should be (null)
    userInfo.get("language") should be ("""[]""")
    userInfo.get("grade") should be (null)
    userInfo.get("framework") should be (null)
    userInfo.get("medium") should be (null)
    userInfo.get("profileusertypes") should be ("""\[{"subType":"deo","type":"administrator"}\]""")

    /***
      * userinformation: user-4
      * school information not present and subject not present in framework
      */
    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-4")
    userInfo.get("firstname") should be ("Manju")
    userInfo.get("lastname") should be ("Davanam")
    userInfo.get("state") should be ("Odisha")
    userInfo.get("district") should be ("CUTTACK")
    userInfo.get("block") should be ("BLOCK1")
    userInfo.get("cluster") should be ("CLUSTER1")
    userInfo.get("schooludisecode") should be (null)
    userInfo.get("schoolname") should be (null)
    userInfo.get("usertype") should be ("teacher")
    userInfo.get("usersubtype") should be ("")
    userInfo.get("board") should be ("")
    userInfo.get("rootorgid") should be ("0127738024883077121")
    userInfo.get("orgname") should be ("Root Org2")
    userInfo.get("subject") should be ("""[]""")
    userInfo.get("language") should be ("")
    userInfo.get("grade") should be ("""["Volunteers"]""")
    userInfo.get("framework") should be ("")
    userInfo.get("medium") should be ("""["English"]""")
    userInfo.get("phone") should be ("LednrgEat6NcG8DX3ue89T7osrjR76AWYqtSYzAvg1jvx1a4lZn6KssNuPP4UeiGoVPQ24MeJbTing10uKJND1TrTfg+K3pGBxLTV+B2SKBxLdBdWzwFOkCsEv53x7bP4T6a+wzaAmCWueMEdPmZuRg==")
    userInfo.get("email") should be ("LednrgEat6NcG8DX3ue89T7osrjR76AWYqtSYzAvg1jvx1a4lZn6KssNuPP4UeiGoVPQ24MeJbTing10uKJND1TrTfg+K3pGBxLTV+B2SKBxLdBdWzwFOkCsEv53x7bP4T6a+wzaAmCWueMEdPmZuRg==")
    userInfo.get("profileusertypes") should be ("""\[{"type":"teacher"}\]""")

    /**
      * UserId = user-5
      * EData state is "Created"
      * User SignupType is "sso"
      * It should able to insert The Map(usersignintype, Validated)
      *
      */
    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-5")
    userInfo.get("usersignintype")

    //user information: user-10
    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-10")
    userInfo.get("firstname") should be ("Utkarsha")
    userInfo.get("lastname") should be ("Kapoor")
    userInfo.get("state") should be ("Odisha")
    userInfo.get("district") should be ("CUTTACK")
    userInfo.get("block") should be ("""BLOCK1\]""")
    userInfo.get("cluster") should be ("CLUSTER1")
    userInfo.get("schooludisecode") should be ("3183211")
    userInfo.get("schoolname") should be ("""\[RPMMAT M.S UDHADIH""")
    userInfo.get("usertype") should be ("administrator")
    userInfo.get("usersubtype") should be ("")
    userInfo.get("board") should be ("IGOT-Health")
    userInfo.get("rootorgid") should be ("0127738024883077121")
    userInfo.get("orgname") should be ("Root Org2")
    userInfo.get("subject") should be ("""["IRCS"]""")
    userInfo.get("language") should be ("""["English"]""")
    userInfo.get("grade") should be ("""["Volunteers"]""")
    userInfo.get("framework") should be ("igot_health")
    userInfo.get("medium") should be ("""["English"]""")
    userInfo.get("profileusertypes") should be ("""\[{"type":"administrator"}\]""")
  }

  "UserCacheUpdater" should "be able to add and update user record with different producer ids" in {
    setupRestUtilData()
    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new LeanerInputSource)

    val task = new UserCacheUpdaterStreamTaskV2(userCacheConfig, mockKafkaUtil)
    task.process()

    // select index: 12
    jedis.select(userCacheConfig.userStore)

    //user information: user-1
    var userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-1")
    userInfo.get("firstname") should be ("Utkarsha")
    userInfo.get("lastname") should be ("Kapoor")
    userInfo.get("state") should be ("Odisha")
    userInfo.get("district") should be ("CUTTACK")
    userInfo.get("block") should be ("BLOCK1")
    userInfo.get("cluster") should be ("CLUSTER1")
    userInfo.getOrDefault(userCacheConfig.userLoginTypeKey, null) should be (null)

    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new AppInputSource)
    task.process()

    userInfo = jedis.hgetAll(userCacheConfig.userStoreKeyPrefix +  "user-1")
    userInfo.get("firstname") should be ("Utkarsha")
    userInfo.get("lastname") should be ("Kapoor")
    userInfo.get("state") should be ("Odisha")
    userInfo.get("district") should be ("CUTTACK")
    userInfo.get("block") should be ("BLOCK1")
    userInfo.get("cluster") should be ("CLUSTER1")
    userInfo.getOrDefault(userCacheConfig.userLoginTypeKey, null) should be ("teacher")
  }

  "UserCacheUpdater" should "throw exception" in intercept[Exception] {
    setupRestUtilDataWithErrors
    when(mockKafkaUtil.kafkaEventSource[Event](userCacheConfig.inputTopic)).thenReturn(new InputSource)

    val task = new UserCacheUpdaterStreamTaskV2(userCacheConfig, mockKafkaUtil)
    task.process()

  }
}

abstract class InputSourceBase[T] extends SourceFunction[T] {
  override def cancel() = {}
}

class InputSource extends InputSourceBase[Event]{

  override def run(ctx: SourceContext[Event]) {
    val gson = new Gson()
    EventFixture.telemetrEvents.foreach(f => {
      val eventMap = gson.fromJson(f, new util.HashMap[String, Any]().getClass)
      val event = new Event(eventMap)
      event.kafkaKey()
      ctx.collect(event)
    })
  }
}

class LeanerInputSource extends InputSourceBase[Event]{
  override def run(ctx: SourceContext[Event]): Unit = {
    val gson = new Gson()
    val eventMap = gson.fromJson(EventFixture.telemetryEventWithLearnerPid, new util.HashMap[String, Any]().getClass)
    val event = new Event(eventMap)
    event.kafkaKey()
    ctx.collect(event)
  }
}

class AppInputSource extends InputSourceBase[Event]{
  override def run(ctx: SourceContext[Event]): Unit = {
    val gson = new Gson()
    val eventMap = gson.fromJson(EventFixture.telemetryEventWithAppPid, new util.HashMap[String, Any]().getClass)
    val event = new Event(eventMap)
    event.kafkaKey()
    ctx.collect(event)
  }
}

