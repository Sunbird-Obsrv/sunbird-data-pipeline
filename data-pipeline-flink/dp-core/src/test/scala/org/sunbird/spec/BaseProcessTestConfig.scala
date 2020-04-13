package org.sunbird.spec

import java.util

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.BaseJobConfig

class BaseProcessTestConfig(override val config: Config) extends BaseJobConfig(config, "Test-job") {
  private val serialVersionUID = -2349318979085017498L
  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
  val eventOutPutTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]]("test-stream-tag")
  val kafkaInputTopic: String = config.getString("kafka.map.input.topic")
  val kafkaOutPutTopic: String = config.getString("kafka.map.output.topic")
  val kafkaMetricsOutPutTopic: String = config.getString("kafka.output.metrics.topic")

}