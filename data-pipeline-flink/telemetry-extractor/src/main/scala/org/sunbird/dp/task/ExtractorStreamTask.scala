package org.sunbird.dp.task

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.functions.{ DeduplicationFunction, ExtractionFunction }

class ExtractorStreamTask(config: ExtractionConfig) extends BaseStreamTask(config) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

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

    val extractionStream: SingleOutputStreamOperator[String] =
      deDupStream.getSideOutput(config.uniqueEventOutputTag)
        .process(new ExtractionFunction(config)).name("Extraction")
        .setParallelism(config.extractionParallelism)

    deDupStream.getSideOutput(config.duplicateEventOutputTag).addSink(kafkaMapSchemaProducer(config.kafkaDuplicateTopic)).name(s"kafka-telemetry-duplicate-events-producer")

    extractionStream.getSideOutput(config.rawEventsOutputTag).addSink(kafkaStringSchemaProducer(config.kafkaSuccessTopic)).name(s"kafka-telemetry-raw-events-producer")
    extractionStream.getSideOutput(config.logEventsOutputTag).addSink(kafkaMapSchemaProducer(config.kafkaSuccessTopic)).name(s"kafka-telemetry-log-events-producer")
    extractionStream.getSideOutput(config.failedEventsOutputTag).addSink(kafkaStringSchemaProducer(config.kafkaFailedTopic)).name(s"kafka-telemetry-failed-events-producer")

    env.execute("Telemetry Extractor")
  }
}

object ExtractorStreamTask {

  def apply(): ExtractorStreamTask = new ExtractorStreamTask(new ExtractionConfig)

  def main(args: Array[String]): Unit = {
    ExtractorStreamTask.apply().process()
  }
}
