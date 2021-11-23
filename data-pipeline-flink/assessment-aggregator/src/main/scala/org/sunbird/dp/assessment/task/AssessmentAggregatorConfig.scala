package org.sunbird.dp.assessment.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.assessment.domain.Event
import org.sunbird.dp.core.job.BaseJobConfig

class AssessmentAggregatorConfig(override val config: Config) extends BaseJobConfig(config, jobName = "AssessmentAggregatorJob") {

  private val serialVersionUID = 2905979434303791379L

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])


  // Kafka Topics Configurationval kafkaInputTopic: String = config.getString("kafka.input.topic")
  // Parallelism configs
  val assessAggregatorParallelism: Int = config.getInt("task.assessaggregator.parallelism")
  val downstreamOperatorsParallelism: Int = config.getInt("task.downstream.parallelism")
  val scoreAggregatorParallelism: Int = config.getInt("task.scoreaggregator.parallelism")
  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")

  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaFailedTopic: String = config.getString("kafka.failed.topic")
  val kafkaCertIssueTopic: String = config.getString("kafka.output.certissue.topic")

  // Metric List
  val dbUpdateCount = "db-update-count"
  val dbReadCount = "db-read-count"
  val batchSuccessCount = "batch-success-event-count"
  val failedEventCount = "failed-event-count"
  val ignoredEventsCount = "ignored-event-count"
  val skippedEventCount = "skipped-event-count"
  val cacheHitCount = "cache-hit-count"
  val cacheHitMissCount = "cache-hit-miss-count"
  val certIssueEventsCount = "cert-issue-events-count"
  val dbScoreAggUpdateCount = "db-score-update-count"
  val dbScoreAggReadCount = "db-score-read-count"
  val apiHitSuccessCount = "api-hit-success-count"
  val apiHitFailedCount = "api-hit-failed-count"
  val recomputeAggEventCount = "recompute-agg-event-count"


  //Cassandra

  val dbTable: String = config.getString("lms-cassandra.table")
  val dbKeyspace: String = config.getString("lms-cassandra.keyspace")
  val dbHost: String = config.getString("lms-cassandra.host")
  val dbPort: Int = config.getInt("lms-cassandra.port")
  val dbudtType: String = config.getString("lms-cassandra.questionudttype")
  val enrolmentTable: String = config.getString("lms-cassandra.enrolmentstable")
  val activityTable: String = config.getString("lms-cassandra.activitytable")

  val FAILED_EVENTS_OUTPUT_TAG = "failed-events"

  val failedEventsOutputTag: OutputTag[Event] = OutputTag[Event]("assess-failed-events")
  val certIssueOutputTagName = "certificate-issue-events"
  val certIssueOutputTag: OutputTag[String] = OutputTag[String](certIssueOutputTagName)

  // Consumers
  val assessmentAggConsumer = "assessment-agg-consumer"

  // Functions
  val assessmentAggregatorFunction = "AssessmentAggregatorFunction"

  // Producers
  val assessFailedEventsSink = "assess-failed-events-sink"
  val certIssueEventSink = "certificate-issue-event-sink"
  val userScoreAggregateFn = "user-score-aggregator"

  // Cache
  val relationCacheNode:Int = config.getInt("redis.database.relationCache.id")
  val contentCacheNode:Int = config.getInt("redis.database.contentCache.id")
  
  //UserActivityAgg
  val scoreAggregateTag: OutputTag[Event] = OutputTag[Event]("score-aggregate-events")
  val activityType = "activity_type"
  val activityId = "activity_id"
  val contextId = "context_id"
  val activityUser = "user_id"
  val aggLastUpdated = "agg_last_updated"
  val aggDetails = "agg_details"
  val aggregates = "aggregates"

  val aggType = config.getString("user.activity.agg.type")

  val skipMissingRecords: Boolean = config.getBoolean("assessment.skip.missingRecords")
  val contentReadAPI: String = config.getString("content.read.api")

}
