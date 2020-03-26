package org.ekstep.dp.task

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.ekstep.dp.domain.Event
import org.ekstep.dp.functions.{DeduplicationFunction, ExtractionFunction}


class ExtractorStreamTask(config: DeduplicationConfig) extends BaseStreamTask(config) {

  private val serialVersionUID = 146697324640926024L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    //implicit val eventTypeSting: TypeInformation[AnyRef] = TypeExtractor.getForClass(classOf[AnyRef])
    implicit val eventTypeSting: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    env.enableCheckpointing(config.checkpointingInterval)

    try {
      val kafkaConsumer = createKafkaStreamConsumer(config.kafkaInputTopic)

      val deDupStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
        env.addSource(kafkaConsumer, "telemetry-raw-events-consumer")
          .process(new DeduplicationFunction(config))
          .setParallelism(2)

      deDupStream.getSideOutput(new OutputTag[Event]("duplicate-events"))
        .addSink(createObjectStreamProducer[Event](config.kafkaDuplicateTopic))
        .name("kafka-telemetry-duplicate-producer")

//      val extractedEventStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
//        deDupStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]]("unique-events"))
//          .process(new ExtractionFunction(config)).name("Extraction")
//          .setParallelism(2)

//      extractedEventStream.getSideOutput(new OutputTag[AnyRef]("raw-events"))
//        .addSink(createObjectStreamProducer[Map[String, AnyRef]](config.kafkaSuccessTopic))
//        .name("kafka-telemetry-raw-producer")


//      extractedEventStream.getSideOutput(new OutputTag[Map[String, AnyRef]]("failed-events"))
//        .addSink(createObjectStreamProducer[Map[String, AnyRef]](config.kafkaFailedTopic))
//        .name("kafka-telemetry-raw-producer")
      env.execute("Telemetry Extractor")
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[ExtractorStreamTask]

  override def equals(other: Any): Boolean = other match {
    case that: ExtractorStreamTask =>
      (that canEqual this) &&
        serialVersionUID == that.serialVersionUID
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(serialVersionUID)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object ExtractorStreamTask {
  val config = new DeduplicationConfig

  def apply(): ExtractorStreamTask = new ExtractorStreamTask(config)

  def main(args: Array[String]): Unit = {
    ExtractorStreamTask.apply().process()
  }
}
