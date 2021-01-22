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
    val kafkaIngestInputTopic: String = config.getString("kafka.ingest.input.topic")
    val kafkaIngestSuccessTopic: String = config.getString("kafka.ingest.output.success.topic")

    val kafkaRawInputTopic: String = config.getString("kafka.raw.input.topic")
    val kafkaRawSuccessTopic: String = config.getString("kafka.raw.output.success.topic")


    override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
    val downstreamOperatorsParallelism: Int = config.getInt("task.downstream.operators.parallelism")
    val rawKafkaConsumerParallelism: Int = config.getInt("task.raw.consumer.parallelism")
    val rawDownstreamOperatorsParallelism: Int = config.getInt("task.raw.downstream.operators.parallelism")

    val EVENTS_OUTPUT_TAG = "events"

    // Metric List
    val ingestSuccessEventCount = "ingest-success-event-count"
    val rawSuccessEventCount = "raw-success-event-count"

    val eventsOutputTag: OutputTag[Array[Byte]] = OutputTag[Array[Byte]](EVENTS_OUTPUT_TAG)

    // Consumers
    val telemetryIngestProcessorConsumer = "ingest-router-consumer"
    val telemetryRawProcessorConsumer = "raw-router-consumer"

    // Producers
    val telemetryIngestProcessorProducer = "ingest-router-sink"
    val telemetryRawProcessorProducer = "raw-router-sink"



  }
