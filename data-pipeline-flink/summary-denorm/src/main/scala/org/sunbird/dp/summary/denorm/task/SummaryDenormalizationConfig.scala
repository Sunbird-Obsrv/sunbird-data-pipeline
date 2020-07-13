package org.sunbird.dp.summary.denorm.task

import com.typesafe.config.Config
import org.apache.flink.streaming.api.scala.OutputTag
import org.sunbird.dp.core.denorm.config.DenormalizationConfig
import org.sunbird.dp.core.denorm.domain.Event

class SummaryDenormalizationConfig(override val config: Config)
  extends DenormalizationConfig(config, "SummaryDenormalizationJob") {

  val dedupStore: Int = config.getInt("redis.database.duplicationstore.id")
  val cacheExpirySeconds: Int = config.getInt("redis.database.key.expiry.seconds")

  val uniqueSummaryEventsOutputTag: OutputTag[Event] = OutputTag[Event]("unique-events")
  val duplicateEventsOutputTag: OutputTag[Event] = OutputTag[Event]("duplicate-events")

  val DEDUP_FLAG_NAME = "summary_denorm_duplicate"

  // Consumers
  override val denormalizationConsumer = "summary-denorm-consumer"

  // Producers
  override val DENORM_EVENTS_PRODUCER = "summary-denorm-events-producer"

  // Functions
  val summaryDedupFunction = "SummaryDeduplicationFunction"
  override val denormalizationFunction = "SummaryDenormalizationFunction"

  val summaryDedupParallelism: Int = config.getInt("task.denorm.summary-dedup.parallelism")
  override val denormParallelism: Int = config.getInt("task.denorm.parallelism")
  override val denormSinkParallelism: Int = config.getInt("task.denorm.sink.parallelism")

  // Metrics
  val summaryEventsCount = "summary-events-count"

}
