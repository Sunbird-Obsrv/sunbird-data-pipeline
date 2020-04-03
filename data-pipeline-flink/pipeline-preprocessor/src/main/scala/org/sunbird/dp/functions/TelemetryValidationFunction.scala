package org.sunbird.dp.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.BaseDeduplication
import org.sunbird.dp.domain.Event
import org.sunbird.dp.task.PipelinePreprocessorConfig
import org.sunbird.dp.util.SchemaValidator

class TelemetryValidationFunction(config: PipelinePreprocessorConfig,
                                  @transient var schemaValidator: SchemaValidator = null,
                                  @transient var dedupEngine: DedupEngine = null)
                                 (implicit val eventTypeInfo: TypeInformation[Event])
  extends ProcessFunction[Event, Event] with BaseDeduplication {

  private[this] val logger = LoggerFactory.getLogger(classOf[TelemetryValidationFunction])


  override def open(parameters: Configuration): Unit = {
    if (dedupEngine == null) {
      val redisConnect = new RedisConnect(config)
      dedupEngine = new DedupEngine(redisConnect, config.dedupStore, config.cacheExpirySeconds)
    }
    if (schemaValidator == null) {
      schemaValidator = new SchemaValidator(config)
    }
  }

  override def close(): Unit = {
    super.close()
    dedupEngine.closeConnectionPool()
  }

  override def processElement(event: Event, context: ProcessFunction[Event, Event]#Context, out: Collector[Event]): Unit = {
    println("evet" + event)
    dataCorrection(event)
    if (!schemaValidator.schemaFileExists(event)) {
      logger.info(s"Schema not found, Skipping the: ${event.eid} from validation")
      event.markSkipped("pp_validation_processed") // Telemetry validation skipped
      deDup[Event](event.mid(), event, context, config.uniqueEventsOutputTag, config.duplicateEventsOutputTag, flagName = "pp_dedup_processed")(dedupEngine)
    } else {
      val validationReport = schemaValidator.validate(event)
      println("validationReport" + validationReport)
      if (validationReport.isSuccess) {
        event.updateDefaults(config)
        deDup[Event](event.mid(), event, context, config.uniqueEventsOutputTag, config.duplicateEventsOutputTag, flagName = "pp_dedup_processed")(dedupEngine)
      } else {
        val failedErrorMsg = schemaValidator.getInvalidFieldName(validationReport.toString)
        event.markValidationFailure(failedErrorMsg)
        context.output(config.validationFailedEventsOutputTag, event)
      }
    }
  }

  private def dataCorrection(event: Event): Event = {
    // Remove prefix from federated userIds
    val eventActorId = event.actorId
    if (eventActorId != null && !eventActorId.isEmpty && eventActorId.startsWith("f:"))
      event.updateActorId(eventActorId.substring(eventActorId.lastIndexOf(":") + 1))
    if (event.eid != null && event.eid.equalsIgnoreCase("SEARCH"))
      event.correctDialCodeKey()
    event
  }

}
