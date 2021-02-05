package org.sunbird.dp.assessment.functions

import com.datastax.driver.core.querybuilder.{Insert, QueryBuilder}
import com.datastax.driver.core.{Row, UDTValue, UserType}
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow
import org.joda.time.{DateTime, DateTimeZone}
import org.slf4j.LoggerFactory
import org.sunbird.dp.assessment.domain._
import org.sunbird.dp.assessment.task.AssessmentAggregatorConfig
import org.sunbird.dp.core.cache.{DataCache, RedisConnect}
import org.sunbird.dp.core.job.{Metrics, WindowBaseProcessFunction}
import org.sunbird.dp.core.util.CassandraUtil

import java.lang.reflect.Type
import java.math.BigDecimal
import java.sql.Timestamp
import java.text.DecimalFormat
import java.util.UUID
import java.{lang, util}
import scala.collection.JavaConverters._
import scala.collection.mutable


// 1. Read the batch of List Events.
// remove the point2
// for same attempt merge the question data.
// 2. Remove the duplicate attempt Id &  grouping the userId & batchId & courseId & contentId&
// 3. Validate the all the list of events content on group
// 4. Compute the score & construct a batch query for every attempt
// 5. Update the batch query
// 6. Batch attempt query to get the status from the user_enrolement
// 7. Generate certificate issue event.

class AssessmentAggWindowFun(config: AssessmentAggregatorConfig,
                             @transient var cassandraUtil: CassandraUtil = null
                            )(implicit val mapTypeInfo: TypeInformation[Event])
  extends WindowBaseProcessFunction[Event, Event, Int](config) {

  val mapType: Type = new TypeToken[util.Map[String, AnyRef]]() {}.getType
  private[this] val logger = LoggerFactory.getLogger(classOf[AssessmentAggWindowFun])
  private var dataCache: DataCache = _
  var questionType: UserType = _
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
  }

  override def close(): Unit = {
    super.close()
  }

  override def process(key: Int, context: ProcessWindowFunction[Event, Event, Int, GlobalWindow]#Context, events: lang.Iterable[Event], metrics: Metrics): Unit = {
    try {
      val computedAggRes: List[AssessmentAgg] = events.asScala.toList
        .filter(event => isValidContent(event.courseId, event.contentId)(metrics))
        .groupBy(event => (event.userId, event.batchId, event.courseId, event.contentId, event.attemptId))
        .values.map(value => AssessmentAgg(value.head.userId, value.head.courseId, value.head.batchId, value.head.contentId, value.head.attemptId, computeScore(value.head)(metrics)))
        .toList
      val queryList: List[Insert] = computedAggRes.map(res => constructQuery(res))
      insertData(batchWriteSize = config.thresholdBatchWriteSize, queryList)(metrics)
      computedAggRes.foreach(res => {
        context.output(config.certIssueOutputTag, createIssueCertEvent(res))
        metrics.incCounter(config.certIssueEventsCount)
      })
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.info("Assessment Failed with exception :", ex.getMessage)
        metrics.incCounter(config.failedEventCount)
    }
  }

  def computeScore(event: Event)(metrics: Metrics): Aggregate = {
    var totalScore = 0.0
    var totalMaxScore = 0.0
    val assessEvents = event.assessEvents.asScala
    val sortAndFilteredEvents = assessEvents.map(event => {
      AssessEvent(event.get("ets").asInstanceOf[Long], new Gson().fromJson(new Gson().toJson(event.get("edata")), classOf[QuestionData]))
    }).sortWith(_.ets > _.ets).groupBy(_.edata.item.id).map(_._2.head)
    val result: List[UDTValue] = sortAndFilteredEvents.map(event => {
      totalScore = totalScore + event.edata.score
      totalMaxScore = totalMaxScore + event.edata.item.maxscore
      getQuestion(event.edata, event.ets.longValue())
    }).toList
    val grandTotal = String.format("%s/%s", df.format(totalScore), df.format(totalMaxScore))
    Aggregate(totalScore,
      totalMaxScore,
      grandTotal,
      result,
      Some(getCreatedOnColValue(event)(metrics)),
      Some(event.assessmentEts))
  }

  def getCreatedOnColValue(event: Event)(metrics: Metrics): Long = {
    println("Reading Eventss")
    val assessmentData = readAssessment(event)
    metrics.incCounter(config.dbReadCount)
    if (assessmentData != null && event.assessmentEts > assessmentData.getTimestamp("last_attempted_on").getTime) {
      assessmentData.getTimestamp("created_on").getTime
    } else {
      new DateTime().getMillis
    }
  }

  def constructQuery(assessment: AssessmentAgg): Insert = {
    QueryBuilder.insertInto(config.dbKeyspace, config.dbTable)
      .value("course_id", assessment.courseId)
      .value("batch_id", assessment.batchId)
      .value("user_id", assessment.userId)
      .value("content_id", assessment.contentId)
      .value("attempt_id", assessment.attemptId)
      .value("updated_on", new DateTime(DateTimeZone.UTC).getMillis)
      .value("created_on", assessment.aggregate.createdOn.get)
      .value("last_attempted_on", assessment.aggregate.lastAttemptedOn.get)
      .value("total_score", assessment.aggregate.totalScore)
      .value("total_max_score", assessment.aggregate.totalMaxScore)
      .value("question", assessment.aggregate.questionsList.asJava)
      .value("grand_total", assessment.aggregate.grandTotal)
  }

  def insertData(batchWriteSize: Int, queriesList: List[Insert])(implicit metrics: Metrics): Unit = {
    val groupedQueries = queriesList.grouped(batchWriteSize).toList
    groupedQueries.foreach(queries => {
      val cqlBatch = QueryBuilder.batch()
      queries.map(query => cqlBatch.add(query))
      val result = cassandraUtil.upsert(cqlBatch.toString)
      if (result) {
        metrics.incCounter(config.dbUpdateCount)
      } else {
        metrics.incCounter(config.dbUpdateFailed)
        val msg = "Database update has failed: " + cqlBatch.toString
        logger.error(msg)
        throw new Exception(msg)
      }
    })
  }

  def readAssessment(event: Event): Row = {
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

  def getQuestion(questionData: QuestionData, assessTs: Long): UDTValue = {

    questionType.newValue().setString("id", questionData.item.id).setDouble("max_score", questionData.item.maxscore)
      .setDouble("score", questionData.score)
      .setString("type", questionData.item.`type`)
      .setString("title", questionData.item.title)
      .setList("resvalues", getListValues(questionData.resvalues).asJava).setList("params", getListValues(questionData.item.params).asJava)
      .setString("description", questionData.item.desc)
      .setDecimal("duration", BigDecimal.valueOf(questionData.duration)).setTimestamp("assess_ts", new Timestamp(assessTs))
  }

  def getListValues(values: util.List[util.HashMap[String, Any]]): mutable.Buffer[util.Map[String, Any]] = {
    values.asScala.map { res =>
      res.asScala.map {
        case (key, value) => key -> (if (null != value && !value.isInstanceOf[String]) new Gson().toJson(value) else value)
      }.toMap.asJava
    }
  }

  def createIssueCertEvent(assessmentAgg: AssessmentAgg): String = {
    val ets = System.currentTimeMillis
    val mid = s"""LP.${ets}.${UUID.randomUUID}"""
    s"""{"eid": "BE_JOB_REQUEST",
       |"ets": ${ets},
       |"mid": "${mid}",
       |"actor": {"id": "Course Certificate Generator","type": "System"},
       |"context": {"pdata": {"ver": "1.0","id": "org.sunbird.platform"}},
       |"object": {"id": "${assessmentAgg.batchId}_${assessmentAgg.courseId}","type": "CourseCertificateGeneration"},
       |"edata": {"userIds": ["${assessmentAgg.userId}"],"action": "issue-certificate","iteration": 1, "trigger": "auto-issue","batchId": "${assessmentAgg.batchId}","reIssue": false,"courseId": "${assessmentAgg.courseId}"}}"""
      .stripMargin.replaceAll("\n", "")
  }
}

