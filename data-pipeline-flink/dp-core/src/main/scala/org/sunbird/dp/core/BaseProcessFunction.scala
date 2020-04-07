package org.sunbird.dp.core

import java.util.concurrent.atomic.AtomicLong

import org.apache.flink.api.scala.metrics.ScalaGauge
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector

import scala.collection.mutable

case class CustomMetrics(metrics: scala.collection.immutable.Map[String, AtomicLong]) {
  def incrementMetric(metric: String): Long = metrics(metric).getAndIncrement()
  def getAndReset(metric: String): Long = metrics(metric).getAndSet(0L)
}

trait CustomJobMetrics {
  def registerMetrics(metrics: List[String]): CustomMetrics = {
    val metricMap = mutable.Map[String, AtomicLong]()
    metrics.map {
      metric => metricMap += metric -> new AtomicLong(0L)
    }
    CustomMetrics(metricMap.toMap)
  }
}

abstract class BaseProcessFunction[T, R](config: BaseJobConfig) extends ProcessFunction[T, R] with CustomJobMetrics {

  @transient private var metrics: CustomMetrics = _

  override def open(parameters: Configuration): Unit = {
    metrics = registerMetrics(metricsList())
    metricsList().map { metric =>
      getRuntimeContext.getMetricGroup.addGroup(config.jobName)
        .gauge[Long, ScalaGauge[Long]](metric, ScalaGauge[Long]( () => metrics.getAndReset(metric)))
    }
  }

  def processElement(event: T, context: ProcessFunction[T, R]#Context, metrics: CustomMetrics): Unit
  def metricsList(): List[String]

  override def processElement(event: T, context: ProcessFunction[T, R]#Context, out: Collector[R]): Unit = {
    processElement(event, context, metrics)
  }
}
