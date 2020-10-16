package org.sunbird.dp.core.serde

import java.nio.charset.StandardCharsets

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.connectors.kafka.{KafkaDeserializationSchema, KafkaSerializationSchema}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.producer.ProducerRecord

class ByteDeserializationSchema extends KafkaDeserializationSchema[Array[Byte]] {

    override def isEndOfStream(nextElement: Array[Byte]): Boolean = false

    override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): Array[Byte] = {
        record.value()
    }

    override def getProducedType: TypeInformation[Array[Byte]] = TypeExtractor.getForClass(classOf[Array[Byte]])
}

class ByteSerializationSchema(topic: String, key: Option[String] = None) extends KafkaSerializationSchema[Array[Byte]] {

    private val serialVersionUID = -4284080856874185929L

    override def serialize(element: Array[Byte], timestamp: java.lang.Long): ProducerRecord[Array[Byte], Array[Byte]] = {
        key.map { kafkaKey =>
            new ProducerRecord[Array[Byte], Array[Byte]](topic, kafkaKey.getBytes(StandardCharsets.UTF_8), element)
        }.getOrElse(new ProducerRecord[Array[Byte], Array[Byte]](topic, element))
    }
}