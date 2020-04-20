package org.sunbird.dp.core.job

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

import org.apache.flink.api.scala.metrics.ScalaGauge
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.ProcessFunction
import org.apache.flink.util.Collector

case class Metrics(metrics: ConcurrentHashMap[String, AtomicLong]) {
  def incCounter(metric: String): Unit = {
    metrics.get(metric).getAndIncrement()
  }

  def getAndReset(metric: String): Long = metrics.get(metric).getAndSet(0L)
  def get(metric: String): Long = metrics.get(metric).get()
  def reset(metric: String): Unit = metrics.get(metric).set(0L)
}

trait JobMetrics {
  def registerMetrics(metrics: List[String]): Metrics = {
    val metricMap = new ConcurrentHashMap[String, AtomicLong]()
    metrics.map { metric => metricMap.put(metric, new AtomicLong(0L)) }
    Metrics(metricMap)
  }
}

abstract class BaseProcessFunction[T, R](config: BaseJobConfig) extends ProcessFunction[T, R] with BaseDeduplication with JobMetrics {

  private val metrics: Metrics = registerMetrics(metricsList())

  override def open(parameters: Configuration): Unit = {
    metricsList().map { metric =>
      getRuntimeContext.getMetricGroup.addGroup(config.jobName)
        .gauge[Long, ScalaGauge[Long]](metric, ScalaGauge[Long]( () => metrics.getAndReset(metric) ))
    }
  }

  def processElement(event: T, context: ProcessFunction[T, R]#Context, metrics: Metrics): Unit
  def metricsList(): List[String]

  override def processElement(event: T, context: ProcessFunction[T, R]#Context, out: Collector[R]): Unit = {
    processElement(event, context, metrics)
  }
}
