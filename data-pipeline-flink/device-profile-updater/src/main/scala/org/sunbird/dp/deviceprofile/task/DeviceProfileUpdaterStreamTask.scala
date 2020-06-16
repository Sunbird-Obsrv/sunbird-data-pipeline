package org.sunbird.dp.deviceprofile.task

import java.io.File
import java.util

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil
import org.sunbird.dp.deviceprofile.functions.DeviceProfileUpdaterFunction


class DeviceProfileUpdaterStreamTask(config: DeviceProfileUpdaterConfig, kafkaConnector: FlinkKafkaConnector) {

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
    env.addSource(kafkaConnector.kafkaMapSource(config.kafkaInputTopic), config.deviceProfileConsumer)
      .uid(config.deviceProfileConsumer).rebalance()
      .process(new DeviceProfileUpdaterFunction(config))
      .name(config.deviceProfileUpdaterFunction).uid(config.deviceProfileUpdaterFunction)
      .setParallelism(config.deviceProfileParallelism)
    env.execute("Device profile updater")

  }
}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object DeviceProfileUpdaterStreamTask {

  def main(args: Array[String]): Unit = {
    val configFilePath = Option(ParameterTool.fromArgs(args).get("config.file.path"))
    val config = configFilePath.map {
      path => ConfigFactory.parseFile(new File(path)).resolve()
    }.getOrElse(ConfigFactory.load("device-profile-updater.conf").withFallback(ConfigFactory.systemEnvironment()))
    val deviceProfileUpdaterConfig = new DeviceProfileUpdaterConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(deviceProfileUpdaterConfig)
    val task = new DeviceProfileUpdaterStreamTask(deviceProfileUpdaterConfig, kafkaUtil)
    task.process()
  }
}

// $COVERAGE-ON$
