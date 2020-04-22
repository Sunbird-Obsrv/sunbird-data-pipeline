package org.sunbird.dp.deviceprofile.task

import java.util

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig

class DeviceProfileUpdaterConfig(override val config: Config) extends BaseJobConfig(config, jobName = "device-profile-updater") {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[util.Map[String, AnyRef]] = TypeExtractor.getForClass(classOf[util.Map[String, AnyRef]])

  val deviceDbStore: Int = config.getInt("redis.database.devicestore.id")

  // Kafka Topics Configuration
  val kafkaInputTopic: String = config.getString("kafka.input.topic")

  val deviceProfileParallelism: Int = config.getInt("task.deviceprofile.parallelism")

  // Metric List
  val deviceDbHitCount = "device-db-update-count"
  val cacheHitCount = "device-cache-update-count"
  val successCount = "success-event-count"
  val failedEventCount = "failed-event-count"


  //Postgress

  val postgresUser = config.getString("postgres.user")
  val postgresPassword = config.getString("postgres.password")
  val postgresTable = config.getString("postgres.table")
  val postgresDb = config.getString("postgres.database")
  val postgresHost = config.getString("postgres.host")
  val postgresPort = config.getInt("postgres.port")
  val postgresMaxConnections = config.getInt("postgres.maxConnections")


  //val uniqueEventOutputTag: OutputTag[util.Map[String, AnyRef]] = OutputTag[util.Map[String, AnyRef]](id = UNIQUE_EVENTS_OUTPUT_TAG)

}
