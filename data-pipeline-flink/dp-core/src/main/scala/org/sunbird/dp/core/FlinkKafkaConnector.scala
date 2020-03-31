package org.sunbird.dp.core

import java.util
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.sunbird.dp.serde._
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer.Semantic
import org.ekstep.dp.core.BaseJobConfig

class FlinkKafkaConnector(config: BaseJobConfig) extends Serializable {
  
  def getObjectSource(kafkaTopic: String) : SourceFunction[util.Map[String, AnyRef]] = {
    new FlinkKafkaConsumer[util.Map[String, AnyRef]](kafkaTopic, new MapDeserializationSchema, config.kafkaConsumerProperties)
  }
  
  def getObjectSink(kafkaTopic: String): SinkFunction[util.Map[String, AnyRef]] = {
    new FlinkKafkaProducer[util.Map[String, AnyRef]](kafkaTopic, new MapSerializationSchema(kafkaTopic), config.kafkaConsumerProperties, Semantic.AT_LEAST_ONCE)
  }
  
  def getRawSink(kafkaTopic: String): SinkFunction[String] = {
    new FlinkKafkaProducer[String](kafkaTopic, new StringSerializationSchema(kafkaTopic), config.kafkaConsumerProperties, Semantic.AT_LEAST_ONCE)
  }
  
}