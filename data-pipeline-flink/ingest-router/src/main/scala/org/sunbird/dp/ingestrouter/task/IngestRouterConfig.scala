package org.sunbird.dp.ingestrouter.task

import java.util

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig

class IngestRouterConfig(override val config: Config) extends BaseJobConfig(config, "IngestRouterJob") {

  private val serialVersionUID = 2905979434303791379L

  implicit val bytesTypeInfo: TypeInformation[Array[Byte]] = TypeExtractor.getForClass(classOf[Array[Byte]])

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")
  val kafkaSuccessTopic: String = config.getString("kafka.output.success.topic")

  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
  val downstreamOperatorsParallelism: Int = config.getInt("task.downstream.operators.parallelism")


  val EVENTS_OUTPUT_TAG = "events"

  // Metric List
  val successEventCount = "success-event-count"

  val eventsOutputTag: OutputTag[Array[Byte]] = OutputTag[Array[Byte]](EVENTS_OUTPUT_TAG)

  // Consumers
  val telemetryProcessorConsumer = "ingest-router-consumer"

  // Producers
  val telemetryProcessorProducer = "ingest-router-sink"


}
