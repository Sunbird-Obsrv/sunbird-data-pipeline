package org.sunbird.dp.assessment.functions

import java.lang.reflect.Type
import java.math.BigDecimal
import java.sql.Timestamp
import java.text.DecimalFormat
import java.util
import java.util.UUID
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.datastax.driver.core.{Row, UDTValue, UserType}
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.LoggerFactory
import org.sunbird.dp.assessment.domain.Event
import org.sunbird.dp.assessment.task.AssessmentAggregatorConfig
import org.sunbird.dp.contentupdater.core.util.RestUtil
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.{CassandraUtil, JSONUtil}

import scala.collection.JavaConverters._
import scala.collection.mutable

case class Question(id: String, maxscore: Double, params: util.List[util.HashMap[String, Any]], title: String, `type`: String, desc: String)

case class QuestionData(resvalues: util.List[util.HashMap[String, Any]], duration: Double, score: Double, item: Question)

case class AssessEvent(ets: Long, edata: QuestionData)


case class Aggregate(totalScore: Double, totalMaxScore: Double, grandTotal: String, questionsList: List[UDTValue])


class AssessmentAggregatorFunction(config: AssessmentAggregatorConfig,
                                   @transient var cassandraUtil: CassandraUtil = null
                                  )(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  val mapType: Type = new TypeToken[util.Map[String, AnyRef]]() {}.getType
  private[this] val logger = LoggerFactory.getLogger(classOf[AssessmentAggregatorFunction])
  private var dataCache: DataCache = _
  var questionType: UserType = _
  private var restUtil: RestUtil = _
  private val df = new DecimalFormat("0.0#")

  override def metricsList() = List(config.dbUpdateCount, config.dbReadCount,
    config.failedEventCount, config.batchSuccessCount,
    config.skippedEventCount, config.cacheHitCount, config.cacheHitMissCount, config.certIssueEventsCount)


  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    dataCache = new DataCache(config, new RedisConnect(config.metaRedisHost, config.metaRedisPort, config), config.relationCacheNode, List())
    dataCache.init()
    cassandraUtil = new CassandraUtil(config.dbHost, config.dbPort)
    questionType = cassandraUtil.getUDTType(config.dbKeyspace, config.dbudtType)
    restUtil = new RestUtil()
  }

  override def close(): Unit = {
    super.close()
  }

  def getListValues(values: util.List[util.HashMap[String, Any]]): mutable.Buffer[util.Map[String, Any]] = {
    values.asScala.map { res =>
      res.asScala.map {
        case (key, value) => key -> (if (null != value && !value.isInstanceOf[String]) new Gson().toJson(value) else value)
      }.toMap.asJava
    }
  }

  /**
   * Method to write the assess event to cassandra table
   *
   * @param event   - Assess Batch Events
   * @param context - Process Context
   */
  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {
    try {
      // Validating the contentId
      if (isValidContent(event.courseId, event.contentId)(metrics)) {
        var totalScore = 0.0
        var totalMaxScore = 0.0
        val assessment = getAssessment(event)
        val assessEvents = event.assessEvents.asScala
        val sortAndFilteredEvents = assessEvents.map(event => {
          AssessEvent(event.get("ets").asInstanceOf[Long], new Gson().fromJson(new Gson().toJson(event.get("edata")), classOf[QuestionData]))
        }).sortWith(_.ets > _.ets).groupBy(_.edata.item.id).map(_._2.head)

        val result = if (config.forceFilterQuestions) {
          logger.info("Force Filter of Question Is Enabled - " + config.forceFilterQuestions)
          val totalQuestions = getTotalQuestionsCount(event.contentId)
          sortAndFilteredEvents.take(totalQuestions).foreach(event => { // Reading Only Top TotalQuestionCount Values From the sortAndFilteredEvents Object
            totalScore = totalScore + event.edata.score
            totalMaxScore = totalMaxScore + event.edata.item.maxscore
          })
          sortAndFilteredEvents.map(event => {getQuestion(event.edata, event.ets.longValue())})
        } else {
          logger.info("Force Filter of Question Is Disabled - " + config.forceFilterQuestions)
          sortAndFilteredEvents.map(event => {
            totalScore = totalScore + event.edata.score
            totalMaxScore = totalMaxScore + event.edata.item.maxscore
            getQuestion(event.edata, event.ets.longValue())
          }).toList
        }
        val grandTotal = String.format("%s/%s", df.format(totalScore), df.format(totalMaxScore))

        if (null == assessment) {
          saveAssessment(event, Aggregate(totalScore, totalMaxScore, grandTotal, result.toList), new DateTime().getMillis)
          metrics.incCounter(config.dbUpdateCount)
          metrics.incCounter(config.batchSuccessCount)
          context.output(config.scoreAggregateTag, event)
          createIssueCertEvent(event, context, metrics)
        }
        else {
          metrics.incCounter(config.dbReadCount)
          if (event.assessmentEts > assessment.getTimestamp("last_attempted_on").getTime) {
            saveAssessment(event, Aggregate(totalScore, totalMaxScore, grandTotal, result.toList),
              assessment.getTimestamp("created_on").getTime)
            metrics.incCounter(config.dbUpdateCount)
            metrics.incCounter(config.batchSuccessCount)
            context.output(config.scoreAggregateTag, event)
            createIssueCertEvent(event, context, metrics)
          }
          else {
            metrics.incCounter(config.skippedEventCount)
          }
        }
      } else {
        context.output(config.failedEventsOutputTag, event)
        metrics.incCounter(config.failedEventCount)
      }
    } catch {
      case ex: Exception =>
        logger.info("Assessment Failed with exception :", ex)
        event.markFailed(ex.getMessage)
        context.output(config.failedEventsOutputTag, event)
        metrics.incCounter(config.failedEventCount)
    }
  }

  def getAssessment(event: Event): Row = {

    val query = QueryBuilder.select("last_attempted_on", "created_on")
      .from(config.dbKeyspace, config.dbTable).
      where(QueryBuilder.eq("attempt_id", event.attemptId))
      .and(QueryBuilder.eq("batch_id", event.batchId))
      .and(QueryBuilder.eq("user_id", event.userId))
      .and(QueryBuilder.eq("content_id", event.contentId))
      .and(QueryBuilder.eq("course_id", event.courseId)).toString

    cassandraUtil.findOne(query)

  }

  def isValidContent(courseId: String, contentId: String)(metrics: Metrics): Boolean = {
    val leafNodes = dataCache.getKeyMembers(key = s"$courseId:$courseId:leafnodes")
    if (!leafNodes.isEmpty) {
      metrics.incCounter(config.cacheHitCount)
      leafNodes.contains(contentId)
    } else {
      metrics.incCounter(config.cacheHitMissCount)
      true
    }
  }


  def saveAssessment(batchEvent: Event, aggregate: Aggregate, createdOn: Long): Unit = {
    val query = QueryBuilder.insertInto(config.dbKeyspace, config.dbTable)
      .value("course_id", batchEvent.courseId).value("batch_id", batchEvent.batchId).value("user_id", batchEvent.userId)
      .value("content_id", batchEvent.contentId).value("attempt_id", batchEvent.attemptId)
      .value("updated_on", new DateTime(DateTimeZone.UTC).getMillis).value("created_on", createdOn)
      .value("last_attempted_on", batchEvent.assessmentEts).value("total_score", aggregate.totalScore)
      .value("total_max_score", aggregate.totalMaxScore)
      .value("question", aggregate.questionsList.asJava).value("grand_total", aggregate.grandTotal).toString

    cassandraUtil.upsert(query)
    logger.info("Successfully Aggregated the batch event - batchid: "
      + batchEvent.batchId + " ,userid: " + batchEvent.userId + " ,couserid: "
      + batchEvent.courseId + " ,contentid: " + batchEvent.contentId + "attempid" + batchEvent.attemptId)
  }

  def getQuestion(questionData: QuestionData, assessTs: Long): UDTValue = {

    questionType.newValue().setString("id", questionData.item.id).setDouble("max_score", questionData.item.maxscore)
      .setDouble("score", questionData.score)
      .setString("type", questionData.item.`type`)
      .setString("title", questionData.item.title)
      .setList("resvalues", getListValues(questionData.resvalues).asJava).setList("params", getListValues(questionData.item.params).asJava)
      .setString("description", questionData.item.desc)
      .setDecimal("duration", BigDecimal.valueOf(questionData.duration)).setTimestamp("assess_ts", new Timestamp(assessTs))
  }

  /**
   * Generation of Certificate Issue event for the enrolment completed users to validate and generate certificate.
   * @param batchEvent
   * @param context
   * @param metrics
   */
  def createIssueCertEvent(batchEvent: Event, context: ProcessFunction[Event, Event]#Context,
  metrics: Metrics): Unit = {
    val ets = System.currentTimeMillis
    val mid = s"""LP.${ets}.${UUID.randomUUID}"""
    val event =
      s"""{"eid": "BE_JOB_REQUEST",
         |"ets": ${ets},
         |"mid": "${mid}",
         |"actor": {"id": "Course Certificate Generator","type": "System"},
         |"context": {"pdata": {"ver": "1.0","id": "org.sunbird.platform"}},
         |"object": {"id": "${batchEvent.batchId}_${batchEvent.courseId}","type": "CourseCertificateGeneration"},
         |"edata": {"userIds": ["${batchEvent.userId}"],"action": "issue-certificate","iteration": 1, "trigger": "auto-issue","batchId": "${batchEvent.batchId}","reIssue": false,"courseId": "${batchEvent.courseId}"}}"""
        .stripMargin.replaceAll("\n", "")
    context.output(config.certIssueOutputTag, event)
    metrics.incCounter(config.certIssueEventsCount)
  }


  def getTotalQuestionsCount(contentId: String): Int = {
    val contentReadResp = JSONUtil.deserialize[util.HashMap[String, AnyRef]](restUtil.get(config.contentReadAPI.concat(contentId)))
    if (contentReadResp.get("responseCode").asInstanceOf[String].toUpperCase.equalsIgnoreCase("OK")) {
      val result = contentReadResp.getOrDefault("result", new util.HashMap()).asInstanceOf[util.Map[String, AnyRef]]
      val content = result.getOrDefault("content", new util.HashMap()).asInstanceOf[util.Map[String, Any]]
      val totalQuestions = content.getOrDefault("totalQuestions", 0).asInstanceOf[Int]
      logger.info(s"Fetched the totalQuestion Value from the Content Read API - ContentId:$contentId, TotalQuestionCount:$totalQuestions")
      totalQuestions
    } else {
      println("else part")
      logger.info(s"API Failed to Fetch the TotalQuestion Count - ContentId:$contentId, ResponseCode - ${contentReadResp.get("responseCode")} ")
      0
    }
  }

}

