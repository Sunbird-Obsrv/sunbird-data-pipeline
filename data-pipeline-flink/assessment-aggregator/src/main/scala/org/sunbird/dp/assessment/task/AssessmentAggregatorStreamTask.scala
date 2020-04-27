package org.sunbird.dp.assessment.task

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.assessment.domain.Event
import org.sunbird.dp.assessment.functions.AssessementAggregatorFunction
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil


class AssessmentAggregatorStreamTask(config: AssessmentAggregatorConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
      val source = kafkaConnector.kafkaEventSource[Event](config.kafkaInputTopic)

      val aggregatorStream : SingleOutputStreamOperator[Event] = env.addSource(source, "telemetry-assess")
        .rebalance()
        .process(new AssessementAggregatorFunction(config))
        .name("Assessemnt Aggreagator")
        .setParallelism(config.assessAggregatorParallelism)
      aggregatorStream.getSideOutput(config.failedEventsOutputTag).addSink(kafkaConnector.kafkaEventSink[Event](config.kafkaFailedTopic)).name("assess-failed-events")
    env.execute("AssessmentAggregator")


  }
}



// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object AssessmentAggregatorStreamTask { {
    val config = ConfigFactory.load("assessment-aggregator.conf").withFallback(ConfigFactory.systemEnvironment())
    val eConfig = new AssessmentAggregatorConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(eConfig)
    val task = new AssessmentAggregatorStreamTask(eConfig, kafkaUtil)
    task.process()
  }
}

// $COVERAGE-ON$
