package org.sunbird.dp

import org.apache.flink.configuration.Configuration
import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}
import org.scalatestplus.mockito.MockitoSugar

class BaseTestSpec extends FlatSpec with Matchers with BeforeAndAfterAll with MockitoSugar {

  def testConfiguration(): Configuration = {
    val config = new Configuration()
    config.setString("metrics.reporter", "job_metrics_reporter")
    config.setString("metrics.reporter.job_metrics_reporter.class", classOf[BaseMetricsReporter].getName)
    config
  }

}
