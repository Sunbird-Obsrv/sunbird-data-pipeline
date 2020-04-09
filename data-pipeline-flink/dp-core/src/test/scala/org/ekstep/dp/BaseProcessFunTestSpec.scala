package org.ekstep.dp

import java.util

import com.typesafe.config.ConfigFactory
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala.OutputTag
import org.apache.flink.test.util.MiniClusterWithClientResource
import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import org.sunbird.dp.core.{BaseJobConfig, FlinkKafkaConnector}
import org.sunbird.dp.functions.TestStreamFunc
import org.sunbird.dp.util.FlinkUtil
//import org.apache.kafka.common.serialization.{Serde, Serdes}
//import org.apache.kafka.streams.StreamsBuilder
//import org.apache.kafka.streams.kstream.{Consumed, KStream, Produced}
import org.scalatest.Matchers


class BaseProcessFunTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with EmbeddedKafka {


  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
    .setNumberSlotsPerTaskManager(1)
    .setNumberTaskManagers(1)
    .build)

  override def beforeAll(): Unit = {
    try {
      super.beforeAll()
      //EmbeddedKafka.start()
      println("===Is Running ===" + EmbeddedKafka.isRunning)
      flinkCluster.before()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }

  override def afterAll(): Unit = {
    try {
      super.afterAll()
      //EmbeddedKafka.stop()
      println("===Is Running ===" + EmbeddedKafka.isRunning)
      flinkCluster.after()
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }

  }

  val config = ConfigFactory.load("test.conf");
  val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
  val kafkaConnector = new FlinkKafkaConnector(bsConfig)

  "TestFlinkProcess" should "able to process the event" in {
    println("===Is Running ===" + EmbeddedKafka.isRunning)

    val userDefinedConfig = EmbeddedKafkaConfig(kafkaPort = 7000, zooKeeperPort = 7001)
    withRunningKafkaOnFoundPort(userDefinedConfig) { implicit actualConfig =>
      println("Kafka is starting.." + EmbeddedKafka.isRunning)
    }
    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(bsConfig)
    implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])
    val kafkaMapConsumer = kafkaConnector.kafkaMapSource("k8s.telemetry.unique.flink")
    lazy val testMapStreamTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]]("test-stream-tag")
    val mapStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
      env.addSource(kafkaMapConsumer, "telemetry-raw-events-consumer")
        .rebalance().keyBy(key => key.get("partition").asInstanceOf[Integer])
        .process(new TestStreamFunc(bsConfig)).name("TestFun")
    mapStream.getSideOutput(testMapStreamTag).addSink(kafkaConnector.kafkaMapSink("sunbirddev.telemetry.sink")).name("kafka-telemetry-failed-events-producer")
  }
}
