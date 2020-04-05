package org.sunbird.dp.task

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.functions.{DeduplicationFunction, TelemetryRouterFunction, TelemetryValidationFunction}
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.FlinkKafkaConnector

class PipelinePreprocessorStreamTask(config: PipelinePreprocessorConfig, kafkaConnector: FlinkKafkaConnector) {

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
        */

       val validtionStream: SingleOutputStreamOperator[Event] =
         env.addSource(kafkaConsumer, "telemetry-raw-events-consumer")
           .process(new TelemetryValidationFunction(config)).name("TelemetryValidator")
             .setParallelism(2)

       val duplicationStream: SingleOutputStreamOperator[Event] =
         validtionStream.getSideOutput(config.validEventsOutputTag)
           .process(new DeduplicationFunction(config)).name("Deduplication")
           .setParallelism(2)

      val routerStream: SingleOutputStreamOperator[Event] =
        duplicationStream.getSideOutput(config.uniqueEventsOutputTag)
            .process(new TelemetryRouterFunction(config)).name("Router")

      /**
        * Sink for invalid events, duplicate events, log events, audit events and telemetry events
        */
      validtionStream.getSideOutput(config.validationFailedEventsOutputTag).addSink(kafkaConnector.kafkaEventSink(config.kafkaFailedTopic)).name("kafka-telemetry-invalid-events-producer")
      duplicationStream.getSideOutput(config.duplicateEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaDuplicateTopic)).name("kafka-telemetry-duplicate-producer")
      routerStream.getSideOutput(config.primaryRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaPrimaryRouteTopic)).name("kafka-primary-route-producer")
      routerStream.getSideOutput(config.secondaryRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaSecondaryRouteTopic)).name("kafka-secondary-route-producer")
      routerStream.getSideOutput(config.auditRouteEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaAuditRouteTopic)).name("kafka-audit-route-producer")

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error("Error when processing stream: ", ex)
    }

    env.execute(config.jobName)
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
