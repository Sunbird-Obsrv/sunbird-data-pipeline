package org.sunbird.dp.task

import java.util

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.apache.flink.streaming.connectors.kafka.{FlinkKafkaConsumer, FlinkKafkaProducer}
import org.ekstep.dp.core.BaseJobConfig
import org.sunbird.dp.domain.Events
import org.sunbird.dp.serde._


abstract class BaseStreamTask(config: BaseJobConfig) extends Serializable {

  def kafkaStringSchemaConsumer(kafkaTopic: String): FlinkKafkaConsumer[String] = {
    new FlinkKafkaConsumer[String](kafkaTopic, new StringDeserializationSchema, config.kafkaConsumerProperties)
  }

  def kafkaStringSchemaProducer(kafkaTopic: String): FlinkKafkaProducer[String] = {
    new FlinkKafkaProducer[String](kafkaTopic, new StringSerializationSchema(kafkaTopic), config.kafkaConsumerProperties, Semantic.AT_LEAST_ONCE)
  }

  def kafkaEventSchemaConsumer[T <: Events](kafkaTopic: String)(implicit m: Manifest[T]): FlinkKafkaConsumer[T] = {
    new FlinkKafkaConsumer[T](kafkaTopic, new EventDeserializationSchema[T], config.kafkaConsumerProperties)
  }

  def kafkaEventSchemaProducer[T <: Events](kafkaTopic: String)(implicit m: Manifest[T]): FlinkKafkaProducer[T] = {
    new FlinkKafkaProducer[T](kafkaTopic,
      new EventSerializationSchema[T](kafkaTopic), config.kafkaProducerProperties, Semantic.AT_LEAST_ONCE)
  }

  def kafkaMapSchemaConsumer(kafkaTopic: String): FlinkKafkaConsumer[util.Map[String, AnyRef]] = {
    new FlinkKafkaConsumer[util.Map[String, AnyRef]](kafkaTopic, new MapDeserializationSchema, config.kafkaConsumerProperties)
  }

  def kafkaMapSchemaProducer(kafkaTopic: String): FlinkKafkaProducer[util.Map[String, AnyRef]] = {
    new FlinkKafkaProducer[util.Map[String, AnyRef]](kafkaTopic, new MapSerializationSchema(kafkaTopic), config.kafkaConsumerProperties, Semantic.AT_LEAST_ONCE)
  }

}


