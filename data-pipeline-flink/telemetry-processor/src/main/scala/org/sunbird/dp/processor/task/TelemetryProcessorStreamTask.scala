package org.sunbird.dp.processor.task

import java.io.File
import java.util

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil

class TelemetryProcessorStreamTask(config: TelemetryProcessorConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val bytesTypeInfo: TypeInformation[Array[Byte]] = TypeExtractor.getForClass(classOf[Array[Byte]])

    val processStream =
      env.addSource(kafkaConnector.kafkaBytesSource(config.kafkaInputTopic), config.telemetryProcessorConsumer)
        .uid(config.telemetryProcessorConsumer).setParallelism(config.kafkaConsumerParallelism)
        .addSink(kafkaConnector.kafkaBytesSink(config.kafkaSuccessTopic))
        .name(config.telemetryProcessorProducer).uid(config.telemetryProcessorProducer)
        .setParallelism(config.downstreamOperatorsParallelism)

    env.execute(config.jobName)
  }
}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object TelemetryProcessorStreamTask {

  def main(args: Array[String]): Unit = {
    val configFilePath = Option(ParameterTool.fromArgs(args).get("config.file.path"))
    val config = configFilePath.map {
      path => ConfigFactory.parseFile(new File(path)).resolve()
    }.getOrElse(ConfigFactory.load("telemetry-processor.conf").withFallback(ConfigFactory.systemEnvironment()))
    val telemetryProcessorConfig = new TelemetryProcessorConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(telemetryProcessorConfig)
    val task = new TelemetryProcessorStreamTask(telemetryProcessorConfig, kafkaUtil)
    task.process()
  }
}

// $COVERAGE-ON$
