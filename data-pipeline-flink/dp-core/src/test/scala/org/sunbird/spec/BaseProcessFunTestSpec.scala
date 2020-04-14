package org.sunbird.spec

import java.util

import com.typesafe.config.ConfigFactory
import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.apache.flink.streaming.api.scala._
import org.scalatest.Matchers
import org.sunbird.dp.core.FlinkKafkaConnector
import org.sunbird.dp.util.FlinkUtil

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class SimpleFlinkKafkaTest extends BaseSpec with Matchers with EmbeddedKafka {

  "run the flink job with embedded kafka to process the events " should "work" in {
    val EVENT_WITH_MESSAGE_ID: String =
      """
        |{"id":"sunbird.telemetry","ver":"3.0","ets":1529500243591,"params":{"msgid":"3fc11963-04e7-4251-83de-18e0dbb5a684","requesterId":"","did":"a3e487025d29f5b2cd599a8817ac16b8f3776a63","key":""},"events":[{"eid":"LOG","ets":1529499971358,"ver":"3.0","mid":"LOG:5f3c177f90bd5833deade577cc28cbb6","actor":{"id":"159e93d1-da0c-4231-be94-e75b0c226d7c","type":"user"},"context":{"channel":"b00bc992ef25f1a9a8d63291e20efc8d","pdata":{"id":"local.sunbird.portal","ver":"0.0.1"},"env":"content-service","sid":"PCNHgbKZvh6Yis8F7BxiaJ1EGw0N3L9B","did":"cab2a0b55c79d12c8f0575d6397e5678","cdata":[],"rollup":{"l1":"ORG_001","l2":"0123673542904299520","l3":"0123673689120112640","l4":"b00bc992ef25f1a9a8d63291e20efc8d"}},"object":{},"tags":["b00bc992ef25f1a9a8d63291e20efc8d"],"edata":{"type":"api_access","level":"INFO","message":"","params":[{"url":"/content/composite/v1/search"},{"protocol":"https"},{"method":"POST"},{}]}}],"mid":"56c0c430-748b-11e8-ae77-cd19397ca6b0","syncts":1529500243955}
        |""".stripMargin

    val SHARE_EVENT: String =
      """
        |{"ver":"3.0","eid":"SHARE","ets":1577278681178,"actor":{"type":"User","id":"7c3ea1bb-4da1-48d0-9cc0-c4f150554149"},"context":{"channel":"505c7c48ac6dc1edc9b08f21db5a571d","pdata":{"id":"prod.sunbird.desktop","pid":"sunbird.app","ver":"2.3.162"},"env":"app","sid":"82e41d87-e33f-4269-aeae-d56394985599","did":"1b17c32bad61eb9e33df281eecc727590d739b2b"},"edata":{"dir":"In","type":"File","items":[{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_312785709424099328114191","type":"CONTENT","ver":"1","params":[{"transfers":0,"size":21084308}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31277435209002188818711","type":"CONTENT","ver":"18","params":[{"transfers":12,"size":"123"}]},{"origin":{"id":"1b17c32bad61eb9e33df281eecc727590d739b2b","type":"Device"},"id":"do_31278794857559654411554","type":"TextBook","ver":"1"}]},"object":{"id":"do_312528116260749312248818","type":"TextBook","version":"10","rollup":{}},"mid":"02ba33e5-15fe-4ec5-b32.1084308E760-3d03429fae84","syncts":1577278682630,"@timestamp":"2019-12-25T12:58:02.630Z","type":"events"}
        |""".stripMargin

    try {
      val config = ConfigFactory.load("test.conf");
      val bsConfig = new BaseProcessTestConfig(config)

      val kafkaConnector = new FlinkKafkaConnector(bsConfig)

      implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(bsConfig)
      val kafkaConfig = EmbeddedKafkaConfig(kafkaPort = 9092, zooKeeperPort = 2182)

      val mapStream: SingleOutputStreamOperator[util.Map[String, AnyRef]] =
        env.addSource(kafkaConnector.kafkaMapSource(bsConfig.kafkaMapInputTopic), "telemetry-raw-events-consumer")
          .rebalance()
          .process(new TestMapStreamFunc(bsConfig)).name("TestFun")

      val eventStream = env.addSource(kafkaConnector.kafkaEventSource[Event](bsConfig.kafkaEventInputTopic), "kafka-telemetry-denorm-consumer")
        .rebalance()
        .process[Event](new TestEventStreamFunc(bsConfig)).name("DruidValidator")

      mapStream.getSideOutput(bsConfig.mapOutPutTag)
        .addSink(kafkaConnector.kafkaMapSink(bsConfig.kafkaMapOutPutTopic))
        .name("kafka-telemetry-failed-events-producer")

      eventStream.getSideOutput(bsConfig.eventOutPutTag)
        .addSink(kafkaConnector.kafkaEventSink[Event](bsConfig.kafkaEventOutPutTopic))
        .name("Event-Producer")

      withRunningKafkaOnFoundPort(kafkaConfig) { implicit actualConfig =>
        createCustomTopic(bsConfig.kafkaMapInputTopic)
        createCustomTopic(bsConfig.kafkaMapOutPutTopic)
        createCustomTopic(bsConfig.kafkaEventInputTopic)
        createCustomTopic(bsConfig.kafkaEventOutPutTopic)
        createCustomTopic(bsConfig.kafkaMetricsOutPutTopic)
        publishStringMessageToKafka(bsConfig.kafkaMapInputTopic, EVENT_WITH_MESSAGE_ID)
        publishStringMessageToKafka(bsConfig.kafkaEventInputTopic, SHARE_EVENT)
        Future {
          env.execute("Test FlinkProcess Job")
        }
        println("Output from Map process func" + consumeFirstStringMessageFrom(bsConfig.kafkaMapOutPutTopic))
        println("Output from Event process func" + consumeFirstStringMessageFrom(bsConfig.kafkaEventOutPutTopic))

      }
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
    }
  }

}
