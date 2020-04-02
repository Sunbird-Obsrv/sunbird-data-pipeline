package org.sunbird.dp.task

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.slf4j.LoggerFactory
import org.sunbird.dp.core.FlinkKafkaConnector
import org.sunbird.dp.domain.Event
import org.sunbird.dp.functions.{TelemetryRouterFunction, TelemetryValidationFunction}

class PipelinePreprocessorStreamTask(config: PipelinePreprocessorConfig, kafkaConnector: FlinkKafkaConnector) {

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

  private val serialVersionUID = 146697324640926024L
  private val logger = LoggerFactory.getLogger(classOf[PipelinePreprocessorStreamTask])

  def process(): Unit = {

    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    env.enableCheckpointing(config.checkpointingInterval)

    try {
      val kafkaConsumer = kafkaConnector.kafkaEventSource[Event](config.kafkaInputTopic)

      /**
       * Process functions
       * 1. TelemetryValidationFunction
       * 2. DeduplicationFunction
       * 3. TelemetryRouterFunction
       * 4. Share Events Flattener
       */

      val validationStream: SingleOutputStreamOperator[Event] =
        env.addSource(kafkaConsumer, "telemetry-raw-events-consumer")
          .process(new TelemetryValidationFunction(config)).name("TelemetryValidator")
          .setParallelism(2)

      val routerStream: SingleOutputStreamOperator[Event] =
        validationStream.getSideOutput(config.uniqueEventsOutputTag)
          .process(new TelemetryRouterFunction(config)).name("Router")

//      val flattenerStream: SingleOutputStreamOperator[Event] =
//        routerStream.getSideOutput(config.primaryRouteEventsOutputTag)
//            .process(new ShareEventsFlattener(config)).name("Share Events Flattener")

      /**
       * Sink for invalid events, duplicate events, log events, audit events and telemetry events
       */
      validationStream.getSideOutput(config.validationFailedEventsOutputTag).addSink(kafkaConnector.kafkaEventSink(config.kafkaFailedTopic)).name("kafka-telemetry-invalid-events-producer")
      validationStream.getSideOutput(config.duplicateEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaDuplicateTopic)).name("kafka-telemetry-duplicate-producer")
      routerStream.getSideOutput(config.primaryRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaPrimaryRouteTopic)).name("kafka-primary-route-producer")
      routerStream.getSideOutput(config.secondaryRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaSecondaryRouteTopic)).name("kafka-secondary-route-producer")
      routerStream.getSideOutput(config.auditRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaAuditRouteTopic)).name("kafka-audit-route-producer")

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error("Error when processing stream: ", ex)
    }

    env.execute("PipelinePreprocessorStreamJob")
  }

}

object PipelinePreprocessorStreamTask {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load().withFallback(ConfigFactory.systemEnvironment())
    val preProcessorConfig = new PipelinePreprocessorConfig(config)
    val streamtask: PipelinePreprocessorStreamTask = new PipelinePreprocessorStreamTask(preProcessorConfig, new FlinkKafkaConnector(preProcessorConfig))
    streamtask.process()
  }
}
