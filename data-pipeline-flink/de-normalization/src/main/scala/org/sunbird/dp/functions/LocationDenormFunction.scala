package org.sunbird.dp.functions

import java.util

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector
import org.slf4j.LoggerFactory
import org.sunbird.dp.cache.{ DedupEngine, RedisConnect }
import org.sunbird.dp.task.DenormalizationConfig
import org.sunbird.dp.core.BaseDeduplication
import org.sunbird.dp.domain.Event
import scala.collection.mutable.Map
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.state.ValueStateDescriptor
import com.google.gson.Gson
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.metrics.Counter
import org.apache.flink.metrics.SimpleCounter
import collection.JavaConverters._
import org.sunbird.dp.core.BaseJobConfig
import org.sunbird.dp.core.BaseProcessFunction
import org.sunbird.dp.core.Metrics

class LocationDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event]) extends BaseProcessFunction[Event](config) {

  private[this] val logger = LoggerFactory.getLogger(classOf[LocationDenormFunction])
  
  val locTotal = "loc-total";
  val locCacheHit = "loc-cache-hit";
  val locCacheMiss = "loc-cache-miss";
  
  override def getMetricsList(): List[String] = {
    List(locTotal, locCacheHit, locCacheMiss)
  }

  override def open(parameters: Configuration): Unit = {

  }

  override def close(): Unit = {
    super.close()
  }

  override def processElement(event: Event, context: KeyedProcessFunction[String, Event, Event]#Context, metrics: Metrics): Unit = {

    metrics.incCounter(locTotal)
    val userProfileLocation = event.getUserProfileLocation();
    val userDeclaredLocation = event.getUserDeclaredLocation();
    val ipLocation = event.getIpLocation();

    val declaredLocation = if (nonEmpty(userProfileLocation)) userProfileLocation else if (nonEmpty(userDeclaredLocation)) userDeclaredLocation else ipLocation;

    if (nonEmpty(declaredLocation)) {
      event.addDerivedLocation(declaredLocation.get)
      metrics.incCounter(locCacheHit)
    } else {
      metrics.incCounter(locCacheMiss)
    }

    context.output(config.withLocationEventsTag, event);
  }

  private def nonEmpty(loc: Option[(String, String, String)]): Boolean = {
    loc.nonEmpty && loc.get._1 != null
  }
}
