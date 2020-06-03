package org.sunbird.dp.core.job

import java.util.Properties
import java.io.Serializable

import com.typesafe.config.Config
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.kafka.clients.consumer.ConsumerConfig

class BaseJobConfig(val config: Config, val jobName: String) extends Serializable {

  private val serialVersionUID = - 4515020556926788923L

  implicit val metricTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  val kafkaBrokerServers: String = config.getString("kafka.broker-servers")
  val zookeeper: String = config.getString("kafka.zookeeper")
  val groupId: String = config.getString("kafka.groupId")
  val restartAttempts: Int = config.getInt("task.restart-strategy.attempts")
  val delayBetweenAttempts: Long = config.getLong("task.restart-strategy.delay")
  val parallelism: Int = config.getInt("task.parallelism")
  val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
  // Only for Tests
  val kafkaAutoOffsetReset: Option[String] = if (config.hasPath("kafka.auto.offset.reset")) Option(config.getString("kafka.auto.offset.reset")) else None

  // Checkpointing config
  val checkpointingInterval: Int = config.getInt("task.checkpointing.interval")
  val enableDistributedCheckpointing: Option[Boolean] = if (config.hasPath("job")) Option(config.getBoolean("job.enable.distributed.checkpointing")) else None
  val checkpointingBaseUrl: Option[String] = if (config.hasPath("job")) Option(config.getString("job.statebackend.base.url")) else None


  def kafkaConsumerProperties: Properties = {
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", kafkaBrokerServers)
    properties.setProperty("group.id", groupId)
    properties.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed")
    kafkaAutoOffsetReset.map { properties.setProperty("auto.offset.reset", _) }
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
