package org.sunbird.dp.task

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.slf4j.LoggerFactory
import org.sunbird.dp.domain.Event
import org.sunbird.dp.functions.{DeduplicationFunction, TelemetryRouterFunction, TelemetryValidationFunction}

class PipelinePreprocessorStreamTask(config: PipelinePreprocessorConfig) extends BaseStreamTask(config) {

  private val serialVersionUID = 146697324640926024L
  private val logger = LoggerFactory.getLogger(classOf[PipelinePreprocessorStreamTask])

  def process(): Unit = {

    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    env.enableCheckpointing(config.checkpointingInterval)

    try {
      val kafkaConsumer = createObjectStreamConsumer[Event](config.kafkaInputTopic)

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
         validtionStream.getSideOutput(new OutputTag[Event]("valid-events"))
           .process(new DeduplicationFunction(config)).name("Deduplication")
           .setParallelism(2)

      val routerStream: SingleOutputStreamOperator[Event] =
        duplicationStream.getSideOutput(new OutputTag[Event]("unique-events"))
            .process(new TelemetryRouterFunction(config)).name("Router")

      /**
        * Sink for invalid events, duplicate events, log events, audit events and telemetry events
        */

      validtionStream.getSideOutput(new OutputTag[Event]("validation-falied-events"))
        .addSink(createObjectStreamProducer(config.kafkaFailedTopic))
        .name("kafka-telemetry-invalid-events-producer")

      duplicationStream.getSideOutput(new OutputTag[Event]("duplicate-events"))
        .addSink(createObjectStreamProducer[Event](config.kafkaDuplicateTopic))
        .name("kafka-telemetry-duplicate-producer")

      routerStream.getSideOutput(new OutputTag[Event]("primary-route-events"))
          .addSink(createObjectStreamProducer[Event](config.kafkaPrimaryRouteTopic))
        .name("kafka-primary-route-producer")

      routerStream.getSideOutput(new OutputTag[Event]("secondary-route-events"))
        .addSink(createObjectStreamProducer[Event](config.kafkaSecondaryRouteTopic))
        .name("kafka-secondary-route-producer")

      routerStream.getSideOutput(new OutputTag[Event]("audit-route-events"))
        .addSink(createObjectStreamProducer[Event](config.kafkaAuditRouteTopic))
        .name("kafka-audit-route-producer")

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        logger.error("Error when processing stream: ", ex)
    }

    env.execute("PipelinePreprocessorStreamJob")
  }

}

object PipelinePreprocessorStreamTask {
  val config = new PipelinePreprocessorConfig
  def apply(): PipelinePreprocessorStreamTask = new PipelinePreprocessorStreamTask(config)
  def main(args: Array[String]): Unit = {
    PipelinePreprocessorStreamTask.apply().process()
  }
}
