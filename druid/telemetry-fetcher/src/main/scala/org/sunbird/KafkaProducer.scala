package org.sunbird

import java.util.Properties
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.spark.rdd.RDD
import org.apache.kafka.clients.producer.Callback
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.clients.producer.RecordMetadata

object KafkaProducer {
  /**
    *
    * @param config Configurations related to kafka (brokerList, topic)
    * @param events RDD of Telemetry Events
    */
  def dispatch(config: Map[String, AnyRef], events: RDD[String]): Unit = {
    val brokerList = config.getOrElse("brokerList", "").asInstanceOf[String]
    val topic = config.getOrElse("topic", "").asInstanceOf[String]
    val max_black_ms_config: AnyRef = 3000L.asInstanceOf[AnyRef]
    val props = new Properties()
    props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, max_black_ms_config)
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    events.foreachPartition(part => {
      val producer = new KafkaProducer[String, String](props)
      part.foreach(record => {
        try {
          producer.send(new ProducerRecord[String, String](topic, record), new Callback {
            override def onCompletion(recordMetadata: RecordMetadata, e: Exception): Unit = {
              if (null != recordMetadata) {
                recordMetadata.checksum()
              } else {
                println("Record MetaData not found!")
              }
            }
          })
        } catch {
          case ex: Exception => {
            ex.printStackTrace()
          }
        }
      })
      producer.flush()
      producer.close()
    })

  }
}

