package org.sunbird.dp.core.serde

import java.nio.charset.StandardCharsets
import java.util

import com.google.gson.Gson
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.connectors.kafka.{KafkaDeserializationSchema, KafkaSerializationSchema}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.sunbird.dp.core.util.JSONUtil

import collection.JavaConverters._

class MapDeserializationSchema extends KafkaDeserializationSchema[util.Map[String, AnyRef]] {

  private val serialVersionUID = -3224825136576915426L

  override def isEndOfStream(nextElement: util.Map[String, AnyRef]): Boolean = false

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): util.Map[String, AnyRef] = {
    // val parsedString = new String(record.value(), StandardCharsets.UTF_8)
    // val recordMap = new Gson().fromJson(parsedString, new util.HashMap[String, AnyRef]().getClass).asScala
    val recordMap = JSONUtil.deserialize[util.HashMap[String, AnyRef]](record.value())
    // recordMap.asJava
    recordMap
  }

  override def getProducedType: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
}

class MapSerializationSchema(topic: String, key: Option[String] = None) extends KafkaSerializationSchema[util.Map[String, AnyRef]] {

  private val serialVersionUID = -4284080856874185929L

  override def serialize(element: util.Map[String, AnyRef], timestamp: java.lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
    val out = new Gson().toJson(element)
    key.map { kafkaKey =>
      new ProducerRecord[Array[Byte], Array[Byte]](topic, kafkaKey.getBytes(StandardCharsets.UTF_8), out.getBytes(StandardCharsets.UTF_8))
    }.getOrElse(new ProducerRecord[Array[Byte], Array[Byte]](topic, out.getBytes(StandardCharsets.UTF_8)))
  }
}
