package org.sunbird.dp.spec

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.databind.ObjectMapper
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.sunbird.dp.cbpreprocessor.domain.Event
import org.sunbird.dp.cbpreprocessor.util.CBEventsFlattener
import org.sunbird.dp.core.util.JSONUtil
import org.sunbird.dp.fixture.CBEventFixture

import java.util
import scala.collection.JavaConverters._

class CBEventsFlattenerTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll {

  val cbEventsFlattener= new CBEventsFlattener()
  val objectMapper = new ObjectMapper()

  override def beforeAll() {
    super.beforeAll()
    // important for including null values when serializing
    JSONUtil.mapper.setSerializationInclusion(Include.ALWAYS)
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    JSONUtil.mapper.setSerializationInclusion(Include.NON_NULL)
  }

  def getFlattenedWorkOrderOfficerJson(eventJson: String): String = {
    val cbEvent = new Event(JSONUtil.deserialize[util.LinkedHashMap[String, Any]](eventJson))
    val flattenedWorkOrderOfficerData = new util.ArrayList[util.Map[String, Any]]()
    cbEventsFlattener.flattenedOfficerEvents(cbEvent).foreach { event => flattenedWorkOrderOfficerData.add(event.cbData) }
    JSONUtil.serialize(flattenedWorkOrderOfficerData)
  }

  def getFlattenedWorkOrderDataJson(eventJson: String): String = {
    val cbEvent = new Event(JSONUtil.deserialize[util.LinkedHashMap[String, Any]](eventJson))
    val flattenedWorkOrderData = new util.ArrayList[util.Map[String, Any]]()
    cbEventsFlattener.flattenedEvents(cbEvent).foreach {
      case (event, childType, hasRole) => flattenedWorkOrderData.add(event.cbData)
    }
    JSONUtil.serialize(flattenedWorkOrderData)
  }

  "CBEventsFlattener" should "flatten a valid work order event" in {
    val resultJsonNode = objectMapper.readTree(getFlattenedWorkOrderDataJson(CBEventFixture.WO_EVENT))
    val expectedJsonNode = objectMapper.readTree(CBEventFixture.WO_EVENT_RESULT)
    resultJsonNode.equals(expectedJsonNode) should be(true)
  }

  "CBEventsFlattener" should "flatten a valid work order event to officer level" in {
    val resultJsonNode = objectMapper.readTree(getFlattenedWorkOrderOfficerJson(CBEventFixture.WO_EVENT))
    val expectedJsonNode = objectMapper.readTree(CBEventFixture.WO_EVENT_POSITIONS_RESULT)
    resultJsonNode.equals(expectedJsonNode) should be(true)
  }

  "CBEventsFlattener" should "handle missing keys and null values in a work order event" in {
    val resultJsonNodeNoUsers = objectMapper.readTree(getFlattenedWorkOrderDataJson(CBEventFixture.WO_EVENT_NO_USERS))
    val resultJsonNodeUsersNull = objectMapper.readTree(getFlattenedWorkOrderDataJson(CBEventFixture.WO_EVENT_USERS_NULL))
    val resultJsonNodeNoRCL = objectMapper.readTree(getFlattenedWorkOrderDataJson(CBEventFixture.WO_EVENT_NO_RCL))
    val resultJsonNodeNoActivity = objectMapper.readTree(getFlattenedWorkOrderDataJson(CBEventFixture.WO_EVENT_NO_ACTIVITY))
    val resultJsonNodeNoCompetency = objectMapper.readTree(getFlattenedWorkOrderDataJson(CBEventFixture.WO_EVENT_NO_COMPETENCY))

    val expectedJsonNodeNoUsers = objectMapper.readTree(CBEventFixture.WO_EVENT_NO_USERS_RESULT)
    val expectedJsonNodeUsersNull = objectMapper.readTree(CBEventFixture.WO_EVENT_USERS_NULL_RESULT)
    val expectedJsonNodeNoRCL = objectMapper.readTree(CBEventFixture.WO_EVENT_NO_RCL_RESULT)
    val expectedJsonNodeNoActivity = objectMapper.readTree(CBEventFixture.WO_EVENT_NO_ACTIVITY_RESULT)
    val expectedJsonNodeNoCompetency = objectMapper.readTree(CBEventFixture.WO_EVENT_NO_COMPETENCY_RESULT)

    resultJsonNodeNoUsers.equals(expectedJsonNodeNoUsers) should be(true)
    resultJsonNodeUsersNull.equals(expectedJsonNodeUsersNull) should be(true)
    resultJsonNodeNoRCL.equals(expectedJsonNodeNoRCL) should be(true)
    resultJsonNodeNoActivity.equals(expectedJsonNodeNoActivity) should be(true)
    resultJsonNodeNoCompetency.equals(expectedJsonNodeNoCompetency) should be(true)
  }

}
