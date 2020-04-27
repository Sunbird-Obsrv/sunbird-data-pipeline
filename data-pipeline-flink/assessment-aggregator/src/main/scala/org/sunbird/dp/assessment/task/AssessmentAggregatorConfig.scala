package org.sunbird.dp.assessment.task

import java.util

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.assessment.domain.Event
import org.sunbird.dp.core.job.BaseJobConfig

class AssessmentAggregatorConfig(override val config: Config) extends BaseJobConfig(config, jobName = "device-profile-updater") {

  private val serialVersionUID = 2905979434303791379L

  implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])



  // Kafka Topics Configurationval kafkaInputTopic: String = config.getString("kafka.input.topic")

  val assessAggregatorParallelism: Int = config.getInt("task.assessaggregator.parallelism")
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaFailedTopic: String = config.getString("kafka.failed.topic")

  // Metric List
  val dbHitCount = "db-update-count"
  val batchSuccessCount = "batch-success-event-count"
  val failedEventCount = "failed-event-count"
  val skippedEventCount = "skipped-event-count"


  //Cassandra

  val dbTable = config.getString("cassandra.table")
  val dbKeyspace = config.getString("cassandra.keyspace")
  val dbHost = config.getString("cassandra.host")
  val dbPort = config.getInt("cassandra.port")
  val dbudtType = config.getString("cassandra.questionudttype")

  val FAILED_EVENTS_OUTPUT_TAG = "failed-events"

  val failedEventsOutputTag: OutputTag[Event] = OutputTag[Event]("assess-failed-events")


}
