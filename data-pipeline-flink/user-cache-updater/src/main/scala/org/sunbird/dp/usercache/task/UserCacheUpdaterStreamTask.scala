package org.sunbird.dp.usercache.task

import com.typesafe.config.ConfigFactory
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment
import org.sunbird.dp.core.job.FlinkKafkaConnector
import org.sunbird.dp.core.util.FlinkUtil
import org.sunbird.dp.usercache.domain.Event
import org.sunbird.dp.usercache.functions.UserCacheUpdaterFunction

class UserCacheUpdaterStreamTask(config: UserCacheUpdaterConfig, kafkaConnector: FlinkKafkaConnector) {

  private val serialVersionUID = -7729362727131516112L

  def process(): Unit = {

    implicit val env: StreamExecutionEnvironment = FlinkUtil.getExecutionContext(config)
    implicit val eventTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])

    val source = kafkaConnector.kafkaEventSource[Event](config.inputTopic)
    env.addSource(source, config.userCacheConsumer).rebalance().process(new UserCacheUpdaterFunction(config)).setParallelism(config.userCacheParallelism)
    env.execute(config.jobName)
  }

}

// $COVERAGE-OFF$ Disabling scoverage as the below code can only be invoked within flink cluster
object UserCacheUpdaterStreamTask {

  def main(args: Array[String]): Unit = {
    val config = ConfigFactory.load("user-cache-updater.conf").withFallback(ConfigFactory.systemEnvironment())
    val eConfig = new UserCacheUpdaterConfig(config)
    val kafkaUtil = new FlinkKafkaConnector(eConfig)
    val task = new UserCacheUpdaterStreamTask(eConfig, kafkaUtil)
    task.process()
  }
}

// $COVERAGE-ON$
