package eventFetcher

import org.ekstep.analytics.framework._
import org.apache.spark.SparkContext
import org.ekstep.analytics.framework.dispatcher.KafkaDispatcher
import org.ekstep.analytics.framework.util.CommonUtil
import org.apache.spark.rdd.RDD

object EventsFetcher {

  def main(args: Array[String]): Unit = {
    implicit val sc: SparkContext = CommonUtil.getSparkContext(10, "fetcher");
    val arguments = args(0).split(" ")
    val env = System.getenv("env")
    val S3_SUMMARY_EVENTS_PATH = "wfs/"
    val S3_RAW_TELEMETRY_EVENTS_PATH = "raw/"
    val AZURE_SUMMMARY_EVENTS_PATH = "derived/wfs/"
    val AZURE_RAW_TELEMETRY_EVENTS_PATH = "raw/"
    var topic = ""
    var path = ""
    topic = if (arguments(3) == "summary") env + ".events.summary" else env + ".events.telemetry"
    println("Arguments are:" + arguments(0), arguments(1), arguments(2), arguments(3))
    val data: RDD[String] =
      if (arguments(0) == "s3") {
        path = if (arguments(3) == "summary") S3_SUMMARY_EVENTS_PATH else S3_RAW_TELEMETRY_EVENTS_PATH
        println("Fetching from S3", path)
        sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", System.getenv("aws_storage_key"));
        sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", System.getenv("aws_storage_secret"));
        println(System.getenv("aws_storage_key"))
        println(System.getenv("aws_storage_secret"))
        val s3folder = Option(Array(Query(Option("ekstep-prod-data-store"), Option(path), Option(arguments(1)), Option(arguments(2)))))
        val data = DataFetcher.fetchBatchData[String](Fetcher("s3", None, s3folder))
        println("S3 Data counts form: " + arguments(1) + "to" + arguments(2) + ":" + data.count)
        data
      } else {

        path = if (arguments(3) == "summary") AZURE_SUMMMARY_EVENTS_PATH else AZURE_RAW_TELEMETRY_EVENTS_PATH
        println("azure", path)
        sc.hadoopConfiguration.set("fs.azure", "org.apache.hadoop.fs.azure.NativeAzureFileSystem")
        sc.hadoopConfiguration.set("fs.azure.account.key." + System.getenv("azure_storage_key") + ".blob.core.windows.net", System.getenv("azure_storage_secret"))
        val azureFolder = Option(Array(Query(Option("telemetry-data-store"), Option(path), Option(arguments(1)), Option(arguments(2)))))
        println("auzreFolder", azureFolder)
        val data = DataFetcher.fetchBatchData[String](Fetcher("azure", None, azureFolder))
        println("Azure Data counts from: " + arguments(1) + "to" + arguments(2) + ":" + data.count)
        data
      }

    val config = Map("brokerList" -> "localhost:9092", "topic" -> topic)
    KafkaDispatcher.dispatch(config, data)
  }

}



