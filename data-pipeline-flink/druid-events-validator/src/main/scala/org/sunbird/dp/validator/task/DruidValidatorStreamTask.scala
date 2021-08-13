package org.sunbird.dp.validator.task

import java.io.File

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil
import org.sunbird.dp.validator.domain.Event
import org.sunbird.dp.validator.functions.DruidValidatorFunction

/**
 * Druid Validator stream task does the following pipeline processing in a sequence:
 *
 * 1. Parse the message into an event
 * 2. Invoke the DruidValidatorFunction and check if the metadata (denrom data) is valid.
 * 3. Invalid messages are output to a `failed` topic and flags are set in the event that the message is invalid from druid validator. Increment the failed counter by 1
 * 4. Valid messages are then processed via the RouterFunction
 * 5. The RouterFunction does the following
 * 		5.1 If it is LOG event, skip dedup step and push it to
 * `log router` topic. Increment the log-events counter by 1
 * 		5.2 If it is ERROR event, skip dedup step and push it to
 * `error router` topic. Increment the error-events counter by 1
 * 		5.3 If it is ME_WORKFLOW_SUMMARY, check if the event is duplicate via the DeDuplicationFunction.
 * 	        - Duplicate events are pushed to `duplicate` topic and flags are set in the event that is duplicate from router
 * 	    	- Unique events are pushed to `summary router` topic. Increment the summary-events counter by 1
 * 		5.4 If event is of other type (i.e., other than LOG, ERROR and ME_* events), check if the event is duplicate via the DeDuplicationFunction.
 * 	        - Duplicate events are pushed to `duplicate` topic and flags are set in the event that is duplicate from router
 *       	- Unique events are pushed to `telemetry router` topic. Increment the telemetry-events counter by 1
 *
 */

class DruidValidatorStreamTask(config: DruidValidatorConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = 146697324640926024L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

    /**
     * Perform validation
     */
    val validationDataStream =
      env.addSource(kafkaConnector.kafkaEventSource[Event](config.kafkaInputTopic), config.druidValidatorConsumer)
      .uid(config.druidValidatorConsumer).setParallelism(config.kafkaConsumerParallelism)
      .rebalance()
      .process(new DruidValidatorFunction(config)).name(config.druidValidatorFunction).uid(config.druidValidatorFunction)
      .setParallelism(config.downstreamOperatorsParallelism)

    validationDataStream.getSideOutput(config.telemetryRouterOutputTag)

    /**
     * Separate sinks for valid telemetry events, valid summary events, valid error events, valid log events and invalid events
     */
    validationDataStream.getSideOutput(config.telemetryRouterOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaTelemetryRouteTopic))
      .name(config.telemetryEventsProducer).uid(config.telemetryEventsProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    validationDataStream.getSideOutput(config.summaryRouterOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaSummaryRouteTopic))
      .name(config.summaryEventsProducer).uid(config.summaryEventsProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    validationDataStream.getSideOutput(config.duplicateEventOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaDuplicateTopic))
      .name(config.druidDuplicateEventsProducer).uid(config.druidDuplicateEventsProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    validationDataStream.getSideOutput(config.invalidEventOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaFailedTopic))
      .name(config.druidInvalidEventsProducer).uid(config.druidInvalidEventsProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    env.execute(config.jobName)

  }

}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object DruidValidatorStreamTask {

  def main(args: Array[String]): Unit = {
    val configFilePath = Option(ParameterTool.fromArgs(args).get("config.file.path"))
    val config = configFilePath.map {
      path => ConfigFactory.parseFile(new File(path)).resolve()
    }.getOrElse(ConfigFactory.load("druid-events-validator.conf").withFallback(ConfigFactory.systemEnvironment()))
    val druidValidatorConfig = new DruidValidatorConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(druidValidatorConfig)
    val task = new DruidValidatorStreamTask(druidValidatorConfig, kafkaUtil)
    task.process()
  }
}

// $COVERAGE-ON$