package org.sunbird.dp.ingestrouter.task

import java.io.File
import java.util

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil

class IngestRouterStreamTask(config: IngestRouterConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val bytesTypeInfo: TypeInformation[Array[Byte]] = TypeExtractor.getForClass(classOf[Array[Byte]])

    val ingestStream =
      env.addSource(kafkaConnector.kafkaBytesSource(config.kafkaIngestInputTopic), config.telemetryIngestProcessorConsumer)
        .uid(config.telemetryIngestProcessorConsumer).setParallelism(config.kafkaConsumerParallelism)
        .addSink(kafkaConnector.kafkaBytesSink(config.kafkaIngestSuccessTopic))
        .name(config.telemetryIngestProcessorProducer).uid(config.telemetryIngestProcessorProducer)
        .setParallelism(config.downstreamOperatorsParallelism)
    val rawStream =
      env.addSource(kafkaConnector.kafkaBytesSource(config.kafkaRawInputTopic), config.telemetryRawProcessorConsumer)
        .uid(config.telemetryRawProcessorConsumer).setParallelism(config.rawKafkaConsumerParallelism)
        .addSink(kafkaConnector.kafkaBytesSink(config.kafkaRawSuccessTopic))
        .name(config.telemetryRawProcessorProducer).uid(config.telemetryRawProcessorProducer)
        .setParallelism(config.rawDownstreamOperatorsParallelism)


    env.execute(config.jobName)
  }
}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object IngestRouterStreamTask {

  def main(args: Array[String]): Unit = {
    val configFilePath = Option(ParameterTool.fromArgs(args).get("config.file.path"))
    val config = configFilePath.map {
      path => ConfigFactory.parseFile(new File(path)).resolve()
    }.getOrElse(ConfigFactory.load("ingest-router.conf").withFallback(ConfigFactory.systemEnvironment()))
    val telemetryProcessorConfig = new IngestRouterConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(telemetryProcessorConfig)
    val task = new IngestRouterStreamTask(telemetryProcessorConfig, kafkaUtil)
    task.process()
  }
}

// $COVERAGE-ON$
