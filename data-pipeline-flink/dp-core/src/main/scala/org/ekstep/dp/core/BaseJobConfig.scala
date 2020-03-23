package org.ekstep.dp.core

import java.util.Properties
import java.io.Serializable

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.ProducerConfig

trait BaseJobConfig extends Serializable {

  val config: Config = ConfigFactory.load()
  val kafkaBrokerServers: String = config.getString("kafka.broker-servers")
  val zookeeper: String = config.getString("kafka.zookeeper")
  val groupId: String = config.getString("kafka.groupId")
  val checkpointingInterval: Int = config.getInt("task.checkpointing.interval")

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
