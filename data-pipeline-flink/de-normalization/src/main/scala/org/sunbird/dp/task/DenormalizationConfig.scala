package org.sunbird.dp.task

import java.util
import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor
import org.sunbird.dp.core.BaseJobConfig
import org.apache.flink.streaming.api.scala.OutputTag
import com.typesafe.config.Config
import org.sunbird.dp.domain.Event
import collection.JavaConversions._

class DenormalizationConfig(override val config: Config) extends BaseJobConfig(config) {

  private val serialVersionUID = 2905979434303791379L

  implicit val mapTypeInfo: TypeInformation[Event] = TypeExtractor.getForClass(classOf[Event])
  implicit val anyTypeInfo: TypeInformation[String] = TypeExtractor.getForClass(classOf[String])

  // Kafka Topics Configuration
  val uniqueInputTopic: String = config.getString("kafka.unique.input.topic")
  val derivedInputTopic: String = config.getString("kafka.derived.input.topic")
  val denormSuccessTopic: String = config.getString("kafka.output.success.topic")
  val failedTopic: String = config.getString("kafka.output.failed.topic")
  val metricsTopic: String = config.getString("kafka.output.metrics.topic")

  val userStore: Int = config.getInt("redis.database.userstore.id")
  val contentStore: Int = config.getInt("redis.database.contentstore.id")
  val deviceStore: Int = config.getInt("redis.database.devicestore.id")
  val dialcodeStore: Int = config.getInt("redis.database.dialcodestore.id")
  val metricsWindowSize: Int = 300;

  val deviceFields = List("country_code", "country", "state_code", "state", "city", "district_custom", "state_code_custom",
    "state_custom", "user_declared_state", "user_declared_district", "devicespec", "firstaccess")
  val contentFields = List("name", "objectType", "contentType", "mediaType", "language", "medium", "mimeType", "createdBy",
    "createdFor", "framework", "board", "subject", "status", "pkgVersion", "lastSubmittedOn", "lastUpdatedOn", "lastPublishedOn")
  val userFields = List("usertype", "grade", "language", "subject", "state", "district", "usersignintype", "userlogintype")
  val dialcodeFields = List("identifier", "channel", "batchcode", "publisher", "generated_on", "published_on", "status")
  
  val ignorePeriodInMonths:Int = if(config.hasPath("telemetry.ignore.period.months")) config.getInt("telemetry.ignore.period.months") else 3;
  val summaryFilterEvents: List[String] = if(config.hasPath("summary.filter.events")) config.getStringList("summary.filter.events").toList else List("ME_WORKFLOW_SUMMARY");
  
  val userSignInTypeDefault = if(config.hasPath("user.signin.type.default")) config.getString("user.signin.type.default") else "Anonymous";
  val userLoginInTypeDefault = if(config.hasPath("user.login.type.default")) config.getString("user.login.type.default") else "NA";

  val deviceDenormParallelism: Int = config.getInt("task.device.denorm.parallelism")
  val userDenormParallelism: Int = config.getInt("task.user.denorm.parallelism")
  val contentDenormParallelism: Int = config.getInt("task.content.denorm.parallelism")
  val locDenormParallelism: Int = config.getInt("task.loc.denorm.parallelism")
  val dialcodeDenormParallelism: Int = config.getInt("task.dialcode.denorm.parallelism")

  val WITH_LOCATION_EVENTS = "with_location_events"
  val WITH_DEVICE_EVENTS = "with_device_events"
  val WITH_USER_EVENTS = "with_user_events"
  val WITH_CONTENT_EVENTS = "with_content_events"
  val WITH_DIALCODE_EVENTS = "with_dialcode_events"
  val DENORM_EVENTS = "denorm_events"
  val JOB_METRICS = "job_metrics"

  val withLocationEventsTag: OutputTag[Event] = OutputTag[Event](WITH_LOCATION_EVENTS)
  val withDeviceEventsTag: OutputTag[Event] = OutputTag[Event](WITH_DEVICE_EVENTS)
  val withUserEventsTag: OutputTag[Event] = OutputTag[Event](WITH_USER_EVENTS)
  val withContentEventsTag: OutputTag[Event] = OutputTag[Event](WITH_CONTENT_EVENTS)
  val withDialCodeEventsTag: OutputTag[Event] = OutputTag[Event](WITH_DIALCODE_EVENTS)
  val denormEventsTag: OutputTag[Event] = OutputTag[Event](DENORM_EVENTS)
  val metricOutputTag: OutputTag[String] = OutputTag[String](JOB_METRICS)

  val jobName = "de-normalization"

}
