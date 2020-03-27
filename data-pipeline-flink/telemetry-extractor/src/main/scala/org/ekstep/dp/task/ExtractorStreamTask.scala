package org.ekstep.dp.task

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.ekstep.dp.functions.{DeduplicationFunction, ExtractionFunction}


class ExtractorStreamTask(config: DeduplicationConfig) extends BaseStreamTask(config) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment
    implicit val eventTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    env.enableCheckpointing(config.checkpointingInterval)
    try {
      val kafkaConsumer = createKafkaStreamConsumer(config.kafkaInputTopic)

      val deDupStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
        env.addSource(kafkaConsumer, "telemetry-raw-events-consumer")
          .process(new DeduplicationFunction(config))
          .setParallelism(2)

      val extractionStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
        deDupStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]]("unique-events"))
          .process(new ExtractionFunction(config)).name("Extraction")
          .setParallelism(2)

      deDupStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]]("duplicate-events"))
        .addSink(createKafkaStreamProducer(config.kafkaDuplicateTopic))
        .name("kafka-telemetry-duplicate-producer")

      extractionStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]]("raw-events"))
        .addSink(createKafkaStreamProducer(config.kafkaSuccessTopic))
        .name("kafka-telemetry-invalid-events-producer")

      extractionStream.getSideOutput(new OutputTag[util.Map[String, AnyRef]]("failed-events"))
        .addSink(createKafkaStreamProducer(config.kafkaFailedTopic))
        .name("kafka-telemetry-invalid-events-producer")

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
