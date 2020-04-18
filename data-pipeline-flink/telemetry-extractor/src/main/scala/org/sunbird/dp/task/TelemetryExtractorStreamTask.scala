package org.sunbird.dp.task

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.functions.{DeduplicationFunction, ExtractionFunction}
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil

/**
 * Extraction stream task does the following pipeline processing in a sequence:
 * 
 * 1. Parse the message into a batch event
 * 2. Invoke the DedupFunction and check if the message is duplicate. The msgid is retrieved from the `params` attribute
 * 3. Duplicate messages are output to a `duplicate` topic and flags are set in the event that is duplicate from extractor. Increment the duplicate counter by 1
 * 4. Unique messages are then processed via the ExtractionFuntion
 * 5. The extractor unpacks the events and does the following in a sequence
 * 		5.1 Check if each event size is < configured limit. i.e. < 1 mb. If it is more than 1 mb push it to 
 * 				failed topic with appropriate flags. Increment the failed counter by 1
 * 		5.2 Extract the syncts and @timestamp from the batch event. If it is null, default to current timestamp
 * 		5.3 Stamp the syncts on @timestamp on all events
 * 		5.4 Generate a audit event for each batch with details of mid, sync_status, consumer_id, events_count, did and pdata fetched from the batch event
 * 		5.5 The events and audit event are then pushed to `raw` topic with appropriate flags. Increment the success counter by 1
 * 
 */
class TelemetryExtractorStreamTask(config: TelemetryExtractorConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    implicit val stringTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

    /**
     * Invoke De-Duplication - Filter all duplicate batch events from the mobile app.
     * 1. Push all duplicate events to duplicate topic.
     * 2. Push all unique events to unique topic.
     */
    val deDupStream =
      env.addSource(kafkaConnector.kafkaMapSource(config.kafkaInputTopic), "telemetry-ingest-events-consumer")
        .rebalance()
        .process(new DeduplicationFunction(config))
        .setParallelism(config.deDupParallelism)
    /**
     * After - De-Duplication process.
     *  1. Extract the batch events.
     *  2. Generate Audit events (To know the number of events in the per batch)
     */
    val extractionStream =
      deDupStream.getSideOutput(config.uniqueEventOutputTag)
        .process(new ExtractionFunction(config)).name("Extraction")
        .setParallelism(config.extractionParallelism)

    deDupStream.getSideOutput(config.duplicateEventOutputTag).addSink(kafkaConnector.kafkaMapSink(config.kafkaDuplicateTopic)).name("extractor-duplicate-events")
    extractionStream.getSideOutput(config.rawEventsOutputTag).addSink(kafkaConnector.kafkaMapSink(config.kafkaSuccessTopic)).name("extractor-raw-events")
    extractionStream.getSideOutput(config.logEventsOutputTag).addSink(kafkaConnector.kafkaMapSink(config.kafkaSuccessTopic)).name("extractor-audit-events")
    extractionStream.getSideOutput(config.failedEventsOutputTag).addSink(kafkaConnector.kafkaMapSink(config.kafkaFailedTopic)).name("extractor-failed-events")

    env.execute("Telemetry Extractor")

  }
}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object TelemetryExtractorStreamTask {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("telemetry-extractor.conf").withFallback(ConfigFactory.systemEnvironment())
    val eConfig = new TelemetryExtractorConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(eConfig)
    val task = new TelemetryExtractorStreamTask(eConfig, kafkaUtil)
    task.process()
  }
}
// $COVERAGE-ON$
