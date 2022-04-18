package org.sunbird.dp.cbpreprocessor.task

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil
import org.sunbird.dp.cbpreprocessor.domain.Event
import org.sunbird.dp.cbpreprocessor.functions.CBPreprocessorFunction

import java.io.File

/**
 *
 */

class CBPreprocessorStreamTask(config: CBPreprocessorConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = 146697324640926024L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
    val kafkaConsumer = kafkaConnector.kafkaEventSource[Event](config.kafkaInputTopic)

    val eventStream: SingleOutputStreamOperator[Event] =
      env.addSource(kafkaConsumer, config.cbPreprocessorConsumer)
        .uid(config.cbPreprocessorConsumer).setParallelism(config.kafkaConsumerParallelism)
        .rebalance()
        .process(new CBPreprocessorFunction(config)).setParallelism(config.downstreamOperatorsParallelism)

    // all events to kafkaCbAuditRouteOutputTopic
    eventStream.getSideOutput(config.cbAuditEventsOutputTag)
      .addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaOutputCbAuditTopic))
      .name(config.cbAuditProducer).uid(config.cbAuditProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    // work order officer rows generated from work order events to kafkaOutputCbWorkOrderOfficerTopic
    eventStream.getSideOutput(config.cbWorkOrderOfficerOutputTag)
      .addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaOutputCbWorkOrderOfficerTopic))
      .name(config.cbWorkOrderOfficerProducer).uid(config.cbWorkOrderOfficerProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    // work order rows generated from published work order events to kafkaOutputCbWorkOrderRowTopic
    eventStream.getSideOutput(config.cbWorkOrderRowOutputTag)
      .addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaOutputCbWorkOrderRowTopic))
      .name(config.cbWorkOrderRowProducer).uid(config.cbWorkOrderRowProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    // send failed events to kafkaFailedTopic
    eventStream.getSideOutput(config.cbFailedOutputTag)
      .addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaFailedTopic))
      .name(config.cbFailedEventProducer).uid(config.cbFailedEventProducer)
      .setParallelism(config.downstreamOperatorsParallelism)

    eventStream.getSideOutput(config.duplicateEventsOutputTag)
      .addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaDuplicateTopic))
      .name(config.duplicateEventProducer).uid(config.duplicateEventProducer)
      .setParallelism(config.downstreamOperatorsParallelism)


    env.execute(config.jobName)
  }
}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object CBPreprocessorStreamTask {
  def main(args: Array[String]): Unit = {
    val configFilePath = Option(ParameterTool.fromArgs(args).get("config.file.path"))
    val config = configFilePath.map {
      path => ConfigFactory.parseFile(new File(path)).resolve()
    }.getOrElse(ConfigFactory.load("cb-preprocessor.conf").withFallback(ConfigFactory.systemEnvironment()))
    val cbPreprocessorConfig = new CBPreprocessorConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(cbPreprocessorConfig)
    val task = new CBPreprocessorStreamTask(cbPreprocessorConfig, kafkaUtil)
    task.process()
  }
}
// $COVERAGE-ON$
