//package org.ekstep.dp
//
//import com.typesafe.config.ConfigFactory
//import org.apache.flink.api.common.typeinfo.TypeInformation
//import org.apache.flink.api.java.typeutils.TypeExtractor
//import org.apache.flink.runtime.testutils.MiniClusterResourceConfiguration
//import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator
//import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
//import org.apache.flink.test.util.MiniClusterWithClientResource
//import org.scalatest.FlatSpec
//import org.sunbird.dp.core.{BaseJobConfig, FlinkKafkaConnector}
//import org.sunbird.dp.functions.TestStreamFunc
//import org.sunbird.dp.util.FlinkUtil
//
//
//class BaseProcessFunTestSpec extends FlatSpec  {
//
//  val flinkCluster = new MiniClusterWithClientResource(new MiniClusterResourceConfiguration.Builder()
//    .setNumberSlotsPerTaskManager(1)
//    .setNumberTaskManagers(1)
//    .build)
//  val config = ConfigFactory.load("test.conf");
//  val bsConfig: BaseJobConfig = new BaseJobConfig(config, "base-job");
//  val kafkaConnector = new FlinkKafkaConnector(bsConfig)
//
//  "KafkaConnector" should "Able to Consume and sink message" in {
//    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(bsConfig);
//    implicit val eventTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])
//    val kafkaConsumer = kafkaConnector.kafkaStringSource("test")
//
//    val validationStream: SingleOutputStreamOperator[String] =
//      env.addSource(kafkaConsumer, "telemetry-raw-events-consumer")
//        .rebalance().keyBy(0)
//        .process(new TestStreamFunc(bsConfig)).name("TestFun")
//    //    kafkaConnector.kafkaStringSink("testtopic")
//    //    kafkaConnector.kafkaStringSource("testtopic").run()
//    //println("kafkaConnector.kafkaStringSource(\"testtopic\")" + kafkaConnector.kafkaStringSource("testtopic"))
//  }
//
//
//  //  val customBrokerConfig = Map("replica.fetch.max.bytes" -> "2000000",
//  //    "message.max.bytes" -> "2000000")
//  //
//  //  val customProducerConfig = Map("max.request.size" -> "2000000")
//  //  val customConsumerConfig = Map("max.partition.fetch.bytes" -> "2000000")
//
//  //  implicit val customKafkaConfig = EmbeddedKafkaConfig(
//  //    customBrokerProperties = customBrokerConfig,
//  //    customProducerProperties = customProducerConfig,
//  //    customConsumerProperties = customConsumerConfig)
//
//  //  withRunningKafkaOnFoundPort(customKafkaConfig) { implicit actualConfig =>
//  //
//  //  }
//
//
//}
