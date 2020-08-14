package org.sunbird.dp.denorm.task

import com.typesafe.config.Config
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.job.BaseJobConfig
import org.sunbird.dp.denorm.domain.Event

import scala.collection.JavaConversions._

class DenormalizationConfig(override val config: Config, jobName: String) extends BaseJobConfig(config, jobName ) {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  implicit val anyTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  // Kafka Topics Configuration
  val telemetryInputTopic: String = config.getString("kafka.input.telemetry.topic")
  val summaryInputTopic: String = config.getString("kafka.input.summary.topic")
  val denormSuccessTopic: String = config.getString("kafka.output.success.topic")
  val failedTopic: String = config.getString("kafka.output.failed.topic")
  val summaryOutputEventsTopic: String = config.getString("kafka.output.summary.topic")

  override val kafkaConsumerParallelism: Int = config.getInt("task.consumer.parallelism")
  val denormParallelism: Int = config.getInt("task.denorm.parallelism")
  val denormSinkParallelism: Int = config.getInt("task.denorm.sink.parallelism")
  val summarySinkParallelism: Int = config.getInt("task.summary.sink.parallelism")

  val userStore: Int = config.getInt("redis-meta.database.userstore.id")
  val contentStore: Int = config.getInt("redis-meta.database.contentstore.id")
  val deviceStore: Int = config.getInt("redis-meta.database.devicestore.id")
  val dialcodeStore: Int = config.getInt("redis-meta.database.dialcodestore.id")

  val deviceFields = List("country_code", "country", "state_code", "state", "city", "district_custom", "state_code_custom",
    "state_custom", "user_declared_state", "user_declared_district", "devicespec", "firstaccess")
  val contentFields = List("name", "objectType", "contentType", "mediaType", "language", "medium", "mimeType", "createdBy",
    "createdFor", "framework", "board", "subject", "status", "pkgVersion", "lastSubmittedOn", "lastUpdatedOn", "lastPublishedOn", "channel")
  val userFields = List("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype")
  val dialcodeFields = List("identifier", "channel", "batchcode", "publisher", "generated_on", "published_on", "status")
  
  val ignorePeriodInMonths:Int = if(config.hasPath("telemetry.ignore.period.months")) config.getInt("telemetry.ignore.period.months") else 3
  val summaryFilterEvents: List[String] = if(config.hasPath("summary.filter.events")) config.getStringList("summary.filter.events").toList else List("ME_WORKFLOW_SUMMARY")
  
  val userSignInTypeDefault: String = if (config.hasPath("user.signin.type.default")) config.getString("user.signin.type.default") else "Anonymous"
  val userLoginInTypeDefault: String = if (config.hasPath("user.login.type.default")) config.getString("user.login.type.default") else "NA"

  val DENORM_EVENTS_PRODUCER = "telemetry-denorm-events-producer"

  val WITH_LOCATION_EVENTS = "with_location_events"
  val WITH_DEVICE_EVENTS = "with_device_events"
  val WITH_USER_EVENTS = "with_user_events"
  val WITH_CONTENT_EVENTS = "with_content_events"
  val WITH_DIALCODE_EVENTS = "with_dialcode_events"
  val DENORM_EVENTS = "denorm_events"

  val withLocationEventsTag: OutputTag[Event] = OutputTag[Event](WITH_LOCATION_EVENTS)
  val withDeviceEventsTag: OutputTag[Event] = OutputTag[Event](WITH_DEVICE_EVENTS)
  val withUserEventsTag: OutputTag[Event] = OutputTag[Event](WITH_USER_EVENTS)
  val withContentEventsTag: OutputTag[Event] = OutputTag[Event](WITH_CONTENT_EVENTS)
  val withDialCodeEventsTag: OutputTag[Event] = OutputTag[Event](WITH_DIALCODE_EVENTS)
  val denormEventsTag: OutputTag[Event] = OutputTag[Event](DENORM_EVENTS)

  // Device Denorm Metrics
  val deviceTotal = "device-total"
  val deviceCacheHit = "device-cache-hit"
  val deviceCacheMiss = "device-cache-miss"
  val eventsExpired = "events-expired"

  // User Denorm Metrics
  val userTotal = "user-total"
  val userCacheHit = "user-cache-hit"
  val userCacheMiss = "user-cache-miss"

  // Dialcode Denorm Metrics
  val dialcodeTotal = "dialcode-total"
  val dialcodeCacheHit = "dialcode-cache-hit"
  val dialcodeCacheMiss = "dialcode-cache-miss"

  // Content Denorm Metrics
  val contentTotal = "content-total"
  val contentCacheHit = "content-cache-hit"
  val contentCacheMiss = "content-cache-miss"

  // Location Denorm Metrics
  val locTotal = "loc-total"
  val locCacheHit = "loc-cache-hit"
  val locCacheMiss = "loc-cache-miss"

  // Consumers
  val denormalizationConsumer = "denormalization-consumer"

  // Functions
  val denormalizationFunction = "DenormalizationFunction"


  // Summary Denorm config
  val duplicateTopic: String = config.getString("kafka.output.duplicate.topic")

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  val uniqueSummaryEventsOutputTag: OutputTag[Event] = OutputTag[Event]("unique-summary-events")
  val duplicateEventsOutputTag: OutputTag[Event] = OutputTag[Event]("duplicate-events")

  val DEDUP_FLAG_NAME = "summary_denorm_duplicate"

  // Consumers
  val summaryDenormalizationConsumer = "summary-denorm-consumer"

  // Producers
  val summaryDenormEventsProducer = "summary-denorm-events-producer"
  val summaryDuplicateEventProducer = "summary-duplicate-events-sink"
  val summaryEventsProducer = "summary-events-producer"

  // Functions
  val summaryDedupFunction = "SummaryDeduplicationFunction"
  val summaryDenormalizationFunction = "SummaryDenormalizationFunction"

  val summaryDedupParallelism: Int = config.getInt("task.denorm.summary-dedup.parallelism")
  val summarydenormParallelism: Int = config.getInt("task.denorm.parallelism")
  val summaryDenormSinkParallelism: Int = config.getInt("task.summary.sink.parallelism")

  // Metrics
  val summaryEventsCount = "summary-events-count"

}
