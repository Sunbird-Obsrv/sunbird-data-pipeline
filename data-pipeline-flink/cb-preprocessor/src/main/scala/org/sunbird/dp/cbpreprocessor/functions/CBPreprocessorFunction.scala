package org.sunbird.dp.cbpreprocessor.functions

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.slf4j.LoggerFactory

import org.sunbird.dp.core.cache.{DedupEngine, RedisConnect}
import org.sunbird.dp.core.job.{BaseProcessFunction, Metrics}
import org.sunbird.dp.cbpreprocessor.domain.Event
import org.sunbird.dp.cbpreprocessor.task.CBPreprocessorConfig
import org.sunbird.dp.cbpreprocessor.util.CBEventsFlattener

class CBPreprocessorFunction(config: CBPreprocessorConfig,
                             @transient var cbEventsFlattener: CBEventsFlattener = null,
                             @transient var dedupEngine: DedupEngine = null
                            )(implicit val eventTypeInfo: TypeInformation[Event])
  extends BaseProcessFunction[Event, Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[CBPreprocessorFunction])

  override def metricsList(): List[String] = {
    List(
      config.cbAuditEventMetricCount,
      config.cbWorkOrderRowMetricCount,
      config.cbWorkOrderOfficerMetricCount,
      config.cbAuditFailedMetricCount
    ) ::: deduplicationMetrics
  }

  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    if (dedupEngine == null) {
      val redisConnect = new RedisConnect(config.redisHost, config.redisPort, config)
      dedupEngine = new DedupEngine(redisConnect, config.dedupStore, config.cacheExpirySeconds)
    }
    if (cbEventsFlattener == null) {
      cbEventsFlattener = new CBEventsFlattener()
    }
  }

  override def close(): Unit = {
    super.close()
     dedupEngine.closeConnectionPool()
  }

  override def processElement(event: Event,
                              context: ProcessFunction[Event, Event]#Context,
                              metrics: Metrics): Unit = {

    val isUnique =
      deDuplicate[Event, Event](event.cbUid, event, context, config.duplicateEventsOutputTag,
        flagName = config.DEDUP_FLAG_NAME)(dedupEngine, metrics)

    // TODO: remove, temp fix to null org
    // event.correctCbObjectOrg()

    if (isUnique) {

      // output to druid cb audit events topic, competency/role/activity/workorder state (Draft, Approved, Published)
      context.output(config.cbAuditEventsOutputTag, event)
      metrics.incCounter(metric = config.cbAuditEventMetricCount)

      // flatten work order events till officer data and output to druid work order officer topic
      val isWorkOrder = event.isWorkOrder
      if (isWorkOrder) {
        cbEventsFlattener.flattenedOfficerEvents(event).foreach(itemEvent => {
          context.output(config.cbWorkOrderOfficerOutputTag, itemEvent)
          metrics.incCounter(metric = config.cbWorkOrderOfficerMetricCount)
        })
      }

      val isPublishedWorkOrder = isWorkOrder && event.isPublishedWorkOrder
      if (isPublishedWorkOrder) {
        cbEventsFlattener.flattenedEvents(event).foreach {
          case (itemEvent, childType, hasRole) => {
            // here we can choose to route competencies and activities to different routes
            context.output(config.cbWorkOrderRowOutputTag, itemEvent)
            metrics.incCounter(metric = config.cbWorkOrderRowMetricCount)
          }
        }
      }

    }
  }
}
