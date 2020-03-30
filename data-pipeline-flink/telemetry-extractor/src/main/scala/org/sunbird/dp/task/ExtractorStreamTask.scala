package org.sunbird.dp.task

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.domain.Constants
import org.sunbird.dp.functions.{DeduplicationFunction, ExtractionFunction}


class ExtractorStreamTask(config: ExtractionConfig) extends BaseStreamTask(config) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

    /**
     * Enabling the check point, It backup the cheeck point for every X(Default = 1Min) interval of time.
     */
    env.enableCheckpointing(config.checkpointingInterval)

    val kafkaConsumer = kafkaMapSchemaConsumer(config.kafkaInputTopic)
    /**
     * Invoke De-Duplication - Filter all duplicate batch events from the mobile app.
     * 1. Push all duplicate events to duplicate topic.
     * 2. Push all unique events to unique topic.
     */

    val deDupStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
      env.addSource(kafkaConsumer, "telemetry-ingest-events-consumer")
        .process(new DeduplicationFunction(config))
        .setParallelism(config.deDupParallelism)

    /**
     * After - De-Duplication process.
     *  1. Extract the batch events.
     *  2. Generate Audit events (To know the number of events in the per batch)
     */

    val extractionStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
      deDupStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]](Constants.UNIQUE_EVENTS_OUTPUT_TAG))
        .process(new ExtractionFunction(config)).name("Extraction")
        .setParallelism(config.extractionParallelism)

    /**
     * Pushing all duplicate events to duplicate topic
     */
    deDupStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]](Constants.DUPLICATE_EVENTS_OUTPUT_TAG))
      .addSink(kafkaMapSchemaProducer(config.kafkaDuplicateTopic))
      .name("kafka-telemetry-duplicate-producer")

    /**
     * Pushing all extracted events to raw topic
     */
    extractionStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]](Constants.RAW_EVENTS_OUTPUT_TAG))
      .addSink(kafkaMapSchemaProducer(config.kafkaSuccessTopic))
      .name("kafka-telemetry-raw-events-producer")

    /**
     * Pushing the audit events(LOG Events/Audit Events) to raw topic
     */
    extractionStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]](Constants.LOG_EVENTS_OUTPUT_TAG))
      .addSink(kafkaMapSchemaProducer(config.kafkaSuccessTopic))
      .name("kafka-telemetry-log-events-producer")

    /**
     * Pushing all failed events to failed topic.
     * When the events size is exceeds the defined size in bytes then pushing all those
     * events to failed topic
     */

    extractionStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]](Constants.FAILED_EVENTS_OUTPUT_TAG))
      .addSink(kafkaMapSchemaProducer(config.kafkaFailedTopic))
      .name("kafka-telemetry-failed-events-producer")

    env.execute("Telemetry Extractor")
  }
}

object ExtractorStreamTask {

  def apply(): ExtractorStreamTask = new ExtractorStreamTask(new ExtractionConfig)

  def main(args: Array[String]): Unit = {
    ExtractorStreamTask.apply().process()
  }
}
