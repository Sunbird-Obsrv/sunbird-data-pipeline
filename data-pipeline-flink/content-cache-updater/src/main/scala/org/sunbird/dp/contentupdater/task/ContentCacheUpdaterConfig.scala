package org.sunbird.dp.contentupdater.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.contentupdater.domain.Event
import org.sunbird.dp.core.job.BaseJobConfig

class ContentCacheUpdaterConfig(override val config: Config) extends BaseJobConfig(config, "ContentCacheUpdaterJob") {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  implicit val anyTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  // Kafka Topics Configuration
  val inputTopic: String = config.getString("kafka.input.topic")

  val contentRedisHost: String = config.getString("redis-meta.content.host")
  val dialcodeRedisHost: String = config.getString("redis-meta.dialcode.host")

  val contentRedisPort: Int = config.getInt("redis-meta.content.port")
  val dialcodeRedisPort: Int = config.getInt("redis-meta.dialcode.port")

  val contentStore: Int = config.getInt("redis-meta.database.contentstore.id")
  val dialcodeStore: Int = config.getInt("redis-meta.database.dialcodestore.id")

  val contentListFields=List("gradeLevel","subject","medium","language", "keywords")
  val contentDateFields=List("lastStatusChangedOn","lastUpdatedOn","createdOn")
  val dialcodeFields = List( "identifier", "channel", "publisher", "batchCode","status","generatedOn","publishedOn","metadata")

  val dialCodeApiUrl: String = config.getString("dialcode.api.url")
  val dialCodeApiToken: String = config.getString("dialcode.api.token")
  

  val JOB_METRICS_PRODUCER = "telemetry-job-metrics-producer"
  
  val WITH_CONTENT_EVENTS = "with_content_events"
  val WITH_DIALCODE_EVENTS = "with_dialcode_events"
  val withDialCodeEventsTag: OutputTag[Event] = OutputTag[Event](WITH_DIALCODE_EVENTS)
  val withContentDailCodeEventsTag: OutputTag[Event] = OutputTag[Event](WITH_CONTENT_EVENTS)

  val skippedEventCount = "skipped-event-count"
  val contentCacheHit = "cache-hit-count"
  val dialCodeCacheHit = "dial-codes-cache-hit-count"
  val dialCodeApiHit = "dial-codes-api-hit-count"
  val dialCodeApiMissHit = "dial-codes-api-miss-count"
  val totaldialCodeCount = "total-dial-code-count"

  val contentDateFormat = "yyyy-MM-dd'T'HH:mm:ss"

  val dialCodeProperties = List("dialcodes","reservedDialCodes")

  // Consumers
  val contentCacheConsumer = "content-cache-consumer"

  // Functions
  val contentUpdaterFunction = "ContentUpdaterFunction"
  val dialcodeUpdaterFunction = "DialcodeUpdaterFunction"

}
