package org.sunbird.dp.assessment.task

import java.io.File

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.assessment.domain.Event
import org.sunbird.dp.assessment.functions.{AssessmentAggregatorFunction, UserScoreAggregateFunction}
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil

/**
  * Assessment Aggregator task does the following pipeline processing in a sequence:
  *
  * 1. Parse the batch assess message into an event
  * 2. Get the last_attempted_on column from DB with respective batch_id,course_id,user_id,content_id and attempt_id
  * 3. if DB contains assessment
  *       3.1. Increment the db-hit count
  *       3.2. Compare the db event last_attempted_on with current batch event last_attempted_on
  *       3.3. if the batch_event.last_attempted_on > current_last_attemptedon
  *                3.3.1 Save the batch event to DB
  *                3.3.2 Increment batch-success-count
  *       3.4  else increment batch-skip-count
  * 4. else if db doesnt contain the batch assessment
  *       4.1 Save the batch event to DB
  *       4.2 Increment batch-success-count
  * 5. Save the batch event to DB:
  *       6.1 Loop through all the assessment events in batch event
  *       6.2 Remove the duplicate assess events in the bacth event with item id
  *       6.2 sum up all the score from edata and compute the total score
  *       6.3 sum up all the maxscore from item and compute the total max score
  *       6.7 Save the total_score,total_max_score, grand_total and the questionList to DB
  *       6.8 Increment db-update-count
  * 6. Common
  * 	  7.1 Retry once from connection issues from DB
  * 	  7.2 Stop the job from procee  ding further if there are any DB issues
  */
class AssessmentAggregatorStreamTask(config: AssessmentAggregatorConfig, kafkaConnector: FlinkKafkaConnector) {

    private val serialVersionUID = -7729362727131516112L

    def process(): Unit = {
        implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
        implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
        val source = kafkaConnector.kafkaEventSource[Event](config.kafkaInputTopic)

        val aggregatorStream =
            env.addSource(source, config.assessmentAggConsumer)
              .uid(config.assessmentAggConsumer).setParallelism(config.kafkaConsumerParallelism).rebalance()
              .process(new AssessmentAggregatorFunction(config))
              .name(config.assessmentAggregatorFunction).uid(config.assessmentAggregatorFunction)
              .setParallelism(config.assessAggregatorParallelism)

        aggregatorStream.getSideOutput(config.failedEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaFailedTopic))
          .name(config.assessFailedEventsSink).uid(config.assessFailedEventsSink)
          .setParallelism(config.downstreamOperatorsParallelism)
        aggregatorStream.getSideOutput(config.certIssueOutputTag).addSink(kafkaConnector.kafkaStringSink(config.kafkaCertIssueTopic))
          .name(config.certIssueEventSink).uid(config.certIssueEventSink)
          .setParallelism(config.downstreamOperatorsParallelism)
        aggregatorStream.getSideOutput(config.scoreAggregateTag).process(new UserScoreAggregateFunction(config))
          .name(config.userScoreAggregateFn).uid(config.userScoreAggregateFn).setParallelism(config.scoreAggregatorParallelism)
        env.execute(config.jobName)
    }
}


// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object AssessmentAggregatorStreamTask {
    def main(args: Array[String]): Unit = {
        val configFilePath = Option(ParameterTool.fromArgs(args).get("config.file.path"))
        val config = configFilePath.map {
            path => ConfigFactory.parseFile(new File(path)).resolve()
        }.getOrElse(ConfigFactory.load("assessment-aggregator.conf").withFallback(ConfigFactory.systemEnvironment()))
        val assessmentAggregatorConfig = new AssessmentAggregatorConfig(config)
        val kafkaUtil = new FlinkKafkaConnector(assessmentAggregatorConfig)
        val task = new AssessmentAggregatorStreamTask(assessmentAggregatorConfig, kafkaUtil)
        task.process()
    }
}

// $COVERAGE-ON$
