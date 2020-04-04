package org.sunbird.dp.core

import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import com.google.gson.Gson
import org.apache.flink.metrics.Counter
import org.apache.flink.metrics.SimpleCounter
import org.apache.flink.api.common.state.ValueState
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.util.Collector
import scala.collection.mutable.Map
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

case class Metrics(val counter: Map[String, Counter]) extends Serializable {

  def incCounter(metric: String) = counter.get(metric).get.inc()
  def toMap(): Map[String, Long] = Map(counter.mapValues(f => f.getCount).toSeq: _*)
}

object JobMetrics {
  def apply(metrics: List[String]): Metrics = {
    val counters = Map[String, Counter]()
    metrics.foreach(metric => counters.put(metric, new SimpleCounter))
    new Metrics(counters)
  }
}

abstract class BaseProcessFunction[T](config: BaseJobConfig) extends KeyedProcessFunction[String, T, T] with JobMetrics {

  lazy val metricsState: ValueState[Metrics] = getRuntimeContext.getState(new ValueStateDescriptor[Metrics]("state", classOf[Metrics]))

  def processElement(event: T, context: KeyedProcessFunction[String, T, T]#Context, metrics: Metrics): Unit;

  def getMetricsList(): List[String];

  override def processElement(event: T, context: KeyedProcessFunction[String, T, T]#Context, out: Collector[T]): Unit = {

    checkAndRegisterTimer(metricsState.value(), context, config.metricsWindowSize);
    val metrics = if (metricsState.value() == null) JobMetrics.apply(getMetricsList()) else metricsState.value();
    processElement(event, context, metrics);
    metricsState.update(metrics);
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[String, T, T]#OnTimerContext, out: Collector[T]): Unit = {
    Option(metricsState.value) match {
      case None => // ignore
      case Some(metrics) => {
        ctx.output(config.metricOutputTag, getMetricsEvent(metrics.toMap()))
        metricsState.clear
      }
    }
  }
}