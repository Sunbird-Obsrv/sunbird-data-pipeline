package org.sunbird.dp.preprocessor.task

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil
import org.sunbird.dp.preprocessor.domain.Event
import org.sunbird.dp.preprocessor.functions.{ShareEventsFlattenerFunction, TelemetryRouterFunction, TelemetryValidationFunction}

/**
 * Telemetry Pipeline processor stream task does the following pipeline processing in a sequence:
 *  1. Pipeline processor should read the the message from raw topic
 *  2. Invoke the TelemetryValidation Function and Check the event is valid or not.
 *    2.1 Data Correction checks
 *      2.1.1 If the Channel(context.channel) is not present then update the event with the default channel(org.sunbird)
 *      2.1.2 If the syncTs and @TimeStamp is not present then update those values
 *      2.1.3 Remove prefix from federated userIds(actor id)
 *      2.1.4 For search events correct the dialcodes key (dialCodes to dialcode).
 *      2.1.5 If the object type is Dialcode/Qr then correct the dialcode values
 *    2.2 If the event is not valid then stamp the events with the flags, metdata and send to failed topic - Incr failed event count
 *    2.3 If the event json is invalid then send to malformed topic - incr malformed(error) count
 *    2.4 If the event is valid or schema not found, then stamp the events with the flags and de-dup the events immediatly - Incr valid count
 *    2.5 De-Duplication checks
 *      2.5.1 Other than portal and desktop events(should read from the config) the de-dup should be skiped and send to unique output tag. Mark those events with the de-dup skip flags - incr skip count
 *      2.5.2 De-dup should happen only for portal and desktop("prod.diksha.portal,prod.sunbird.desktop") should read form config.
 *      2.5.3 If the mid is not present in the redis then stamp the events with the flags, send to unique output tag and add mid to redis - Incr unique count
 *      2.5.4 If the redis if failed during the de-dup then mark the event with redis failure flag and send to unique output tag - incr redis error count
 *      2.5.5 If mid is present in the redis then stamp the events with the flags, metdata and send to duplicate topic - Incr duplicate event count
 *  3. Router function checks (should read from the unique tag)
 *    3.1 All events should pushed to sink topic - incr the primary router count
 *    3.2 Audit event should pushed to sink and audit topic. - incr the audit router success count
 *    3.3 Log event should pushed to only log topic( not sink) - Incr the log events router success count
 *    3.4 Share events should be send to next stream (ShareEventsFlattener)
 *  4.ShareEventsFlattener function checks
 *    4.1 If the SHARE Event Item list object has params.transfers = 0 then edata.type of SHARE_ITEM should be "download"
 *    4.2 If the SHARE Event Item list object has params.transfers = > 0 then edata.type of SHARE_ITEM should be "import"
 *    4.3 If the SHARE event has object then move the object data to rollup l1
 *    4.4 All flattened(SHARE_ITEM and SHARE) events should push to sink topic(denorm) with appropriate flags - incr SHARE and SHRE_ITEM success count
 *
 */

class PipelinePreprocessorStreamTask(config: PipelinePreprocessorConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = 146697324640926024L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    val kafkaConsumer = kafkaConnector.kafkaEventSource[Event](config.kafkaInputTopic)

    /**
     * Process functions
     * 1. TelemetryValidationFunction & DeduplicationFunction
     * 3. TelemetryRouterFunction
     * 4. Share Events Flattener
     */

    val validationStream: SingleOutputStreamOperator[Event] =
      env.addSource(kafkaConsumer, config.pipelinePreprocessorConsumer)
        .uid(config.pipelinePreprocessorConsumer).setParallelism(config.kafkaConsumerParallelism)
        .rebalance()
        .process(new TelemetryValidationFunction(config)).name(config.telemetryValidationFunction).uid(config.telemetryValidationFunction)
        .setParallelism(config.validationParallelism)

    val routerStream: SingleOutputStreamOperator[Event] =
      validationStream.getSideOutput(config.uniqueEventsOutputTag)
        .process(new TelemetryRouterFunction(config)).name(config.telemetryRouterFunction).uid(config.telemetryRouterFunction)
        .setParallelism(config.routerParallelism)

    val shareEventsFlattener: SingleOutputStreamOperator[Event] =
      routerStream.getSideOutput(config.shareRouteEventsOutputTag)
        .process(new ShareEventsFlattenerFunction(config)).name(config.shareEventsFlattenerFunction).uid(config.shareEventsFlattenerFunction)
        .setParallelism(config.shareEventsFlattnerParallelism)

    /**
     * Sink for invalid events, duplicate events, log events, audit events and telemetry events
     */
    validationStream.getSideOutput(config.validationFailedEventsOutputTag).addSink(kafkaConnector.kafkaEventSink(config.kafkaFailedTopic)).name(config.invalidEventProducer).uid(config.invalidEventProducer)
    validationStream.getSideOutput(config.duplicateEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaDuplicateTopic)).name(config.duplicateEventProducer).uid(config.duplicateEventProducer)

    /**
     * Routing LOG & ERROR Events to "event.log" & "events.error" topic respectively.
     */
    routerStream.getSideOutput(config.logEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaLogRouteTopic)).name(config.logRouterProducer).uid(config.logRouterProducer)
    routerStream.getSideOutput(config.errorEventOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaErrorRouteTopic)).name(config.errorRouterProducer).uid(config.errorRouterProducer)

    /**
     * Pushing "AUDIT" event into both sink and audit topic
     */
    routerStream.getSideOutput(config.auditRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaAuditRouteTopic)).name(config.auditRouterProducer).uid(config.auditRouterProducer)
    routerStream.getSideOutput(config.auditRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaPrimaryRouteTopic)).name(config.auditEventsPrimaryRouteProducer).uid(config.auditEventsPrimaryRouteProducer)

    /**
     * Pushing all the events to unique topic (next stream = denorm) , except LOG, ERROR, AUDIT Events,
     */
    routerStream.getSideOutput(config.primaryRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaPrimaryRouteTopic)).name(config.primaryRouterProducer).uid(config.primaryRouterProducer)

    /**
     * Pushing "SHARE and SHARE_ITEM" event into out put topic unique topic(next_streaming_process = denorm)
     */

    shareEventsFlattener.getSideOutput(config.primaryRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaPrimaryRouteTopic)).name(config.shareEventsPrimaryRouteProducer).uid(config.shareEventsPrimaryRouteProducer)
    shareEventsFlattener.getSideOutput(config.shareItemEventOutputTag).addSink(kafkaConnector.kafkaStringSink(config.kafkaPrimaryRouteTopic)).name(config.shareItemsPrimaryRouterProducer).uid(config.shareItemsPrimaryRouterProducer)

    env.execute(config.jobName)
  }
}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object PipelinePreprocessorStreamTask {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("pipeline-preprocessor.conf").withFallback(ConfigFactory.systemEnvironment())
    val eConfig = new PipelinePreprocessorConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(eConfig)
    val task = new PipelinePreprocessorStreamTask(eConfig, kafkaUtil)
    task.process()
  }
}

// $COVERAGE-ON$
