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
import java.util

trait JobMetrics {

  def checkAndRegisterTimer[I, T](metrics: Metrics, context: KeyedProcessFunction[I, T, T]#Context, windowSize: Long) {
    Option(metrics) match {
      case None =>
        context.timerService().registerEventTimeTimer(context.timestamp + windowSize)
      case Some(current) =>
      // Do Nothing
    }
  }

  def getMetricsEvent(metrics: Map[String, Long], timestamp: Long, config: BaseJobConfig, partition: Integer): String = {

    val metricsEvent = new util.HashMap[String, AnyRef]();
    metricsEvent.put("system", "samza");
    metricsEvent.put("subsystem", "pipeline-metrics");
    metricsEvent.put("metricts", timestamp.asInstanceOf[AnyRef]);
    val dimsList = new util.ArrayList[util.Map[String, AnyRef]]();
    dimsList.add(Map("job-name" -> config.jobName.asInstanceOf[AnyRef]).asJava);
    dimsList.add(Map("partition" -> partition.asInstanceOf[AnyRef]).asJava);

    val metricsList = new util.ArrayList[util.Map[String, AnyRef]]();
    metrics.foreach(metric => {
      metricsList.add(Map("id" -> metric._1, "value" -> metric._2.asInstanceOf[AnyRef]).asJava);
    })

    metricsEvent.put("dimensions", dimsList);
    metricsEvent.put("metrics", metricsList);
    return new Gson().toJson(metricsEvent);
    (new Gson()).toJson(metricsEvent);
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

abstract class BaseProcessFunction[T](config: BaseJobConfig) extends KeyedProcessFunction[Integer, T, T] with JobMetrics {

  lazy val metricsState: ValueState[Metrics] = getRuntimeContext.getState(new ValueStateDescriptor[Metrics]("state", classOf[Metrics]))

  def processElement(event: T, context: KeyedProcessFunction[Integer, T, T]#Context, metrics: Metrics): Unit;

  def getMetricsList(): List[String];

  override def processElement(event: T, context: KeyedProcessFunction[Integer, T, T]#Context, out: Collector[T]): Unit = {

    checkAndRegisterTimer(metricsState.value(), context, config.metricsWindowSize);
    val metrics = if (metricsState.value() == null) JobMetrics.apply(getMetricsList()) else metricsState.value();
    processElement(event, context, metrics);
    metricsState.update(metrics);
  }

  override def onTimer(timestamp: Long, ctx: KeyedProcessFunction[Integer, T, T]#OnTimerContext, out: Collector[T]): Unit = {
    Option(metricsState.value) match {
      case None => // ignore
      case Some(metrics) => {
        ctx.output(config.metricOutputTag, getMetricsEvent(metrics.toMap(), timestamp, config, ctx.getCurrentKey))
        metricsState.clear
      }
    }
  }
}