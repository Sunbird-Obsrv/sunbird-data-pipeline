package org.ekstep.dp.task

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.ekstep.dp.domain.Event
import org.ekstep.dp.functions.DeduplicationFunction


class DeduplicationStreamTask(config: DeduplicationConfig) extends BaseStreamTask(config) {

  private val serialVersionUID = 146697324640926024L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    env.enableCheckpointing(config.checkpointingInterval)

    try {
      val kafkaConsumer = createObjectStreamConsumer[Event](config.kafkaInputTopic)

      val dataStream: SingleOutputStreamOperator[Event] =
        env.addSource(kafkaConsumer, "kafka-telemetry-valid-consumer")
          .process(new DeduplicationFunction(config)).setParallelism(2)

      /**
        * Separate sinks for duplicate events and unique events
        */
      dataStream.getSideOutput(new OutputTag[Event]("unique-event"))
        .addSink(createObjectStreamProducer(config.kafkaSuccessTopic))
        .name("kafka-telemetry-unique-producer")

      dataStream.getSideOutput(new OutputTag[Event]("duplicate-event"))
        .addSink(createObjectStreamProducer(config.kafkaDuplicateTopic))
        .name("kafka-telemetry-duplicate-producer")

      env.execute("DeduplicationFlinkJob")

    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }

}

object DeduplicationStreamTask {
  val config = new DeduplicationConfig
  def apply(): DeduplicationStreamTask = new DeduplicationStreamTask(config)
  def main(args: Array[String]): Unit = {
    DeduplicationStreamTask.apply().process()
  }
}
