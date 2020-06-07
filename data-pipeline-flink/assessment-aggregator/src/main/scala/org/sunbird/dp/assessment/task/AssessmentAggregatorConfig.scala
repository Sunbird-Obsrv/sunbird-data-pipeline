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

    val assessAggregatorParallelism: Int = config.getInt("task.assessaggregator.parallelism")
    val kafkaInputTopic: String = config.getString("kafka.input.topic")
    val kafkaFailedTopic: String = config.getString("kafka.failed.topic")

    // Metric List
    val dbUpdateCount = "db-update-count"
    val dbReadCount = "db-read-count"
    val batchSuccessCount = "batch-success-event-count"
    val failedEventCount = "failed-event-count"
    val skippedEventCount = "skipped-event-count"


    //Cassandra

    val dbTable: String = config.getString("lms-cassandra.table")
    val dbKeyspace: String = config.getString("lms-cassandra.keyspace")
    val dbHost: String = config.getString("lms-cassandra.host")
    val dbPort: Int = config.getInt("lms-cassandra.port")
    val dbudtType: String = config.getString("lms-cassandra.questionudttype")

    val FAILED_EVENTS_OUTPUT_TAG = "failed-events"

    val failedEventsOutputTag: OutputTag[Event] = OutputTag[Event]("assess-failed-events")

    // Consumers
    val assessmentAggConsumer = "assessment-agg-consumer"

    // Functions
    val assessmentAggregatorFunction = "AssessmentAggregatorFunction"

    // Producers
    val assessFailedEventsSink = "assess-failed-events-sink"

}
