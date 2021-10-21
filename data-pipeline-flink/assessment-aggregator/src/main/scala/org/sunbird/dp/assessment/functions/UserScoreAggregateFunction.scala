package org.sunbird.dp.assessment.functions

import java.lang.reflect.Type
import java.util
import java.util.Date

import com.datastax.driver.core.Row
import com.datastax.driver.core.querybuilder.QueryBuilder
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory
import org.sunbird.dp.assessment.domain.Event
import org.apache.flink.configuration.Configuration
import org.sunbird.dp.assessment.task.AssessmentAggregatorConfig
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.core.util.CassandraUtil

import scala.collection.JavaConverters._

case class AggDetails(attempt_id: String, last_attempted_on: Date, score: Int, content_id: String, max_score: Int, `type`: String)

case class UserActivityAgg(agg: Map[String, Int], aggDetails: List[String])

class UserScoreAggregateFunction(config: AssessmentAggregatorConfig,
                                 @transient var cassandraUtil: CassandraUtil = null
                                )(implicit val mapTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  val mapType: Type = new TypeToken[util.Map[String, AnyRef]]() {}.getType
  private[this] val logger = LoggerFactory.getLogger(classOf[UserScoreAggregateFunction])

  override def metricsList() = List(config.dbScoreAggUpdateCount, config.dbScoreAggReadCount,
    config.failedEventCount, config.batchSuccessCount,
    config.skippedEventCount)

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    cassandraUtil = new CassandraUtil(config.dbHost, config.dbPort)
  }

  override def close(): Unit = {
    super.close()
  }

  def getAggregateDetails(assessAggRows: util.List[Row]): List[String] = {
    if (null != assessAggRows && !assessAggRows.isEmpty) {
      assessAggRows.asScala.map { row =>
        val aggMap = AggDetails(row.getString("attempt_id"), row.getTimestamp("last_attempted_on"),
          row.getDouble("score").toInt, row.getString("content_id"), row.getDouble("total_max_score").toInt, config.aggType)
        new Gson().toJson(aggMap)
      }.toList
    } else List()
  }

  def getBestScore(event: Event): UserActivityAgg = {
    val query = QueryBuilder.select().column("content_id").column("attempt_id").column("last_attempted_on").column("total_max_score").max("total_score").as("score").from(config.dbKeyspace, config.dbTable)
      .where(QueryBuilder.eq("course_id", event.courseId)).and(QueryBuilder.eq("batch_id", event.batchId))
      .and(QueryBuilder.eq("user_id", event.userId)).groupBy("user_id", "course_id", "batch_id", "content_id")
    val rows: java.util.List[Row] = cassandraUtil.find(query.toString)
    val aggDetailsList = getAggregateDetails(rows)

    val aggMap = if (null != rows && !rows.isEmpty) {
      rows.asScala.toList.flatMap(row => {
        Map(s"score:${row.getString("content_id")}" -> row.getDouble("score").toInt,
          s"max_score:${row.getString("content_id")}" -> row.getDouble("total_max_score").toInt)
      }).toMap
    } else Map[String, Int]()
    UserActivityAgg(agg = aggMap, aggDetails = aggDetailsList)
  }

  def updateUserActivity(event: Event, score: UserActivityAgg): Unit = {
    val scoreLastUpdatedTime: Map[String, Long] = score.agg.map(m => m._1 -> System.currentTimeMillis())
    val updateQuery = QueryBuilder.update(config.dbKeyspace, config.activityTable)
      .`with`(QueryBuilder.putAll(config.agg, score.agg.asJava))
      .and(QueryBuilder.appendAll(config.aggDetails, score.aggDetails.asJava))
      .and(QueryBuilder.putAll(config.aggLastUpdated, scoreLastUpdatedTime.asJava))
      .where(QueryBuilder.eq(config.activityId, event.courseId))
      .and(QueryBuilder.eq(config.activityType, "Course"))
      .and(QueryBuilder.eq(config.contextId, "cb:" + event.batchId))
      .and(QueryBuilder.eq(config.activityUser, event.userId))
    cassandraUtil.upsert(updateQuery.toString)
    logger.info("Successfully updated scores in user activity  - batchid: "
      + event.batchId + " ,userid: " + event.userId + " ,couserid: "
      + event.courseId)
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {
    val score: UserActivityAgg = getBestScore(event)
    metrics.incCounter(config.dbScoreAggReadCount)
    if (!score.agg.isEmpty || score.aggDetails.nonEmpty) {
      updateUserActivity(event, score)
      metrics.incCounter(config.dbScoreAggUpdateCount)
    } else {
      logger.info("No scores to update for batchid: "
        + event.batchId + " ,userid: " + event.userId + " ,couserid: "
        + event.courseId)
    }
  }

}
