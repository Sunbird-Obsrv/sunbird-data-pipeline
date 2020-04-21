package org.sunbird.dp.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.BaseJobConfig
import org.sunbird.dp.domain.Event

class ContentCacheUpdaterConfig(override val config: Config) extends BaseJobConfig(config, "content-cache-updater") {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  implicit val anyTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  // Kafka Topics Configuration
  val inputTopic: String = config.getString("kafka.input.topic")

  val contentStore: Int = config.getInt("redis.database.contentstore.id")
  val dialcodeStore: Int = config.getInt("redis.database.dialcodestore.id")

  val contentListFields=List("gradeLevel","subject","medium","language")
  val contentDateFields=List("lastStatusChangedOn","lastUpdatedOn","createdOn")
  val dialcodeFields = List( "identifier", "channel", "publisher", "batchCode","status","generatedOn","publishedOn","metadata")

  val dialCodeApiUrl = config.getString("dialcode.api.url")
  val dialCodeApiToken = config.getString("dialcode.api.token")
  

  val JOB_METRICS_PRODUCER = "telemetry-job-metrics-producer"
  
  val WITH_CONTENT_EVENTS = "with_content_events"
  val WITH_DIALCODE_EVENTS = "with_dialcode_events"
  val withDialCodeEventsTag: OutputTag[Event] = OutputTag[Event](WITH_DIALCODE_EVENTS)
  val withContentEventsTag: OutputTag[Event] = OutputTag[Event](WITH_CONTENT_EVENTS)

  val contentCacheHit = "cache-hit-count"
  val dialCodeCacheHit = "dial-codes-cache-hit-count"
  val dialCodeApiHit = "dial-codes-api-hit-count"
  val dialCodeApiMissHit = "dial-codes-api-miss-count"
}
