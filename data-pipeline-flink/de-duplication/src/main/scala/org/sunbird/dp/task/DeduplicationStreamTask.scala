package org.sunbird.dp.task

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.domain.Event
import org.sunbird.dp.functions.DeduplicationFunction
import com.typesafe.config.ConfigFactory
import org.sunbird.dp.core.FlinkKafkaConnector


class DeduplicationStreamTask(config: DeduplicationConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = 146697324640926024L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    env.enableCheckpointing(config.checkpointingInterval)

    try {
      val kafkaConsumer = kafkaConnector.kafkaEventSource[Event](config.kafkaInputTopic)

      val dataStream: SingleOutputStreamOperator[Event] =
        env.addSource(kafkaConsumer, "kafka-telemetry-valid-consumer")
          .process(new DeduplicationFunction(config)).setParallelism(1)

      /**
        * Separate sinks for duplicate events and unique events
        */
      dataStream.getSideOutput(config.uniqueEventsOutputTag)
        .addSink(kafkaConnector.kafkaEventSink(config.kafkaSuccessTopic))
        .name("kafka-telemetry-unique-producer")

      dataStream.getSideOutput(config.uniqueEventsOutputTag)
        .addSink(kafkaConnector.kafkaEventSink(config.kafkaDuplicateTopic))
        .name("kafka-telemetry-duplicate-producer")

      env.execute(config.jobName)

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }

}

object DeduplicationStreamTask {
  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("de-duplication.conf").withFallback(ConfigFactory.systemEnvironment())
    val dedupConfig = new DeduplicationConfig(config)
    val dedupTask: DeduplicationStreamTask = new DeduplicationStreamTask(dedupConfig, new FlinkKafkaConnector(dedupConfig))
    dedupTask.process()
  }
}
