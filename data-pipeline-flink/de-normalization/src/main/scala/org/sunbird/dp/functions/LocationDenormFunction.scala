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

trait JobMetrics {

  def checkAndRegisterTimer[I, T](metrics: Metrics, context: KeyedProcessFunction[I, T, T]#Context, windowSize: Long) {
    Option(metrics) match {
      case None =>
        context.timerService().registerEventTimeTimer(context.timestamp + windowSize)
      case Some(current) =>
      // Do Nothing
    }
  }

  def getMetricsEvent(metrics: Map[String, Long]): String = {
    (new Gson()).toJson(metrics.asJava);
  }
}

trait BaseMetrics {}

case class Metrics(val counter: Map[String, Counter]) extends Serializable {

  def incCounter(metric: String) = counter.get(metric).get.inc()
  def toMap(): Map[String, Long] = Map(counter.mapValues(f => f.getCount).toSeq: _*)
}

object Metrics {
  def apply(metrics: List[String]): Metrics = {
    val counters = Map[String, Counter]()
    metrics.foreach(metric => counters.put(metric, new SimpleCounter))
    new Metrics(counters)
  }
}

class LocationDenormFunction(config: DenormalizationConfig)(implicit val mapTypeInfo: TypeInformation[Event]) extends KeyedProcessFunction[String, Event, Event] with JobMetrics {

  private[this] val logger = LoggerFactory.getLogger(classOf[LocationDenormFunction])
  lazy val state: ValueState[Metrics] = getRuntimeContext.getState(new ValueStateDescriptor[Metrics]("state", classOf[Metrics]))
  val locTotal = "loc-total";
  val locCacheHit = "loc-cache-hit";
  val locCacheMiss = "loc-cache-miss";

  override def open(parameters: Configuration): Unit = {

  }

  override def close(): Unit = {
    super.close()
  }

  override def processElement(event: Event, context: KeyedProcessFunction[String, Event, Event]#Context, out: Collector[Event]): Unit = {

    Console.println("context.timestamp", context.timestamp, context.getCurrentKey);
    Console.println("LocationDenormFunction:processElement", event.getTelemetry.read("derivedlocationdata"), event.getTelemetry.read("flags"));
    checkAndRegisterTimer(state.value(), context, config.metricsWindowSize);
    val metrics = if (state.value() == null) Metrics.apply(List(locTotal, locCacheHit, locCacheMiss)) else state.value();
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
    Console.println("metrics", metrics);
    state.update(metrics);
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, Event, Event]#OnTimerContext, out: Collector[Event]): Unit = {
    Option(state.value) match {
      case None => // ignore
      case Some(metrics) => {
        ctx.output(config.metricOutputTag, getMetricsEvent(metrics.toMap()))
        state.clear
      }
    }
  }

  private def nonEmpty(loc: Option[(String, String, String)]): Boolean = {
    loc.nonEmpty && loc.get._1 != null
  }
}
