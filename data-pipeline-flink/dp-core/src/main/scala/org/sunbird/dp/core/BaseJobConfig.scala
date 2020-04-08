package org.sunbird.dp.core

import java.util.Properties
import java.io.Serializable

import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag

class BaseJobConfig(val config: Config, val jobName: String) extends Serializable {

  implicit val metricTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  val kafkaBrokerServers: String = config.getString("kafka.broker-servers")
  val zookeeper: String = config.getString("kafka.zookeeper")
  val groupId: String = config.getString("kafka.groupId")
  val checkpointingInterval: Int = config.getInt("task.checkpointing.interval")
  val restartAttempts: Int = config.getInt("task.restart-strategy.attempts")
  val delayBetweenAttempts: Long = config.getLong("task.restart-strategy.delay")
  val metricsWindowSize: Int = config.getInt("task.metrics.window.size");
  val metricsTopic: String = config.getString("kafka.output.metrics.topic")
  val JOB_METRICS = "job_metrics"
  val metricOutputTag: OutputTag[String] = new OutputTag[String](JOB_METRICS)

  val parallelism: Int = config.getInt("task.parallelism")

  def kafkaConsumerProperties: Properties = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", kafkaBrokerServers)
    properties.setProperty("group.id", groupId)
    properties
  }

  def kafkaProducerProperties: Properties = {
    val properties = new Properties()
    properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerServers)
    properties.put(ProducerConfig.LINGER_MS_CONFIG, new Integer(10))
    properties.put(ProducerConfig.BATCH_SIZE_CONFIG, new Integer(16384 * 4))
    properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy")
    properties
  }

}
