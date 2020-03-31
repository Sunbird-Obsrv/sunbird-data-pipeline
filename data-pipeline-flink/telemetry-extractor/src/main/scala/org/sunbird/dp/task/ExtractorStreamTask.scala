package org.sunbird.dp.task

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.functions.{ DeduplicationFunction, ExtractionFunction }
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.FlinkKafkaConnector

class ExtractorStreamTask(config: ExtractionConfig, kafkaConnector: FlinkKafkaConnector) extends BaseStreamTask(config) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
    implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
    /**
     * Enabling the check point, It backup the cheeck point for every X(Default = 1Min) interval of time.
     */
    env.enableCheckpointing(config.checkpointingInterval)

    /**
     * Invoke De-Duplication - Filter all duplicate batch events from the mobile app.
     * 1. Push all duplicate events to duplicate topic.
     * 2. Push all unique events to unique topic.
     */
    val deDupStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
      env.addSource(kafkaConnector.getObjectSource(config.kafkaInputTopic), "telemetry-ingest-events-consumer")
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
        
    deDupStream.getSideOutput(config.duplicateEventOutputTag).addSink(kafkaConnector.getObjectSink(config.kafkaDuplicateTopic)).name("kafka-telemetry-duplicate-events-producer")
    extractionStream.getSideOutput(config.rawEventsOutputTag).addSink(kafkaConnector.getRawSink(config.kafkaSuccessTopic)).name("kafka-telemetry-raw-events-producer")
    extractionStream.getSideOutput(config.logEventsOutputTag).addSink(kafkaConnector.getObjectSink(config.kafkaSuccessTopic)).name("kafka-telemetry-log-events-producer")
    extractionStream.getSideOutput(config.failedEventsOutputTag).addSink(kafkaConnector.getRawSink(config.kafkaFailedTopic)).name("kafka-telemetry-failed-events-producer")
    env.execute("Telemetry Extractor")
  }
}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object ExtractorStreamTask {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load().withFallback(ConfigFactory.systemEnvironment())
    val eConfig = new ExtractionConfig(config);
    val kafkaUtil = new FlinkKafkaConnector(eConfig);
    val task = new ExtractorStreamTask(eConfig, kafkaUtil)
    task.process()
  }
}
// $COVERAGE-ON$
