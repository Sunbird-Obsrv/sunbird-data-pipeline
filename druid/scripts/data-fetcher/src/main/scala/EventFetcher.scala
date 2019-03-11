package eventFetcher

import org.ekstep.analytics.framework._
import org.apache.spark.SparkContext
import org.ekstep.analytics.framework.dispatcher.KafkaDispatcher
import org.ekstep.analytics.framework.util.CommonUtil
import org.apache.spark.rdd.RDD

object EventsFetcher{

  def main(args: Array[String]): Unit = {
    implicit val sc: SparkContext = CommonUtil.getSparkContext(10, "fetcher");
    val arguments = args(0).split(" ")
    println("Arguments are:" + arguments(0),arguments(1),arguments(2),arguments(3))
    val data: RDD[String] =
      if(arguments(0) == "s3"){
        println("Fetching from S3")
        sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", System.getenv("aws_storage_key"));
        sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey",System.getenv("aws_storage_secret"));
        println(System.getenv("aws_storage_key"))
        println(System.getenv("aws_storage_secret"))
        val s3folder = Option(Array(Query(Option("ekstep-prod-data-store"), Option(arguments(3)), Option(arguments(1)), Option(arguments(2)))))
        val data = DataFetcher.fetchBatchData[String](Fetcher("s3", None, s3folder))
        println("S3 Data counts form: " + arguments(1) + "to" + arguments(2) +":" + data.count)
        data
      }else{
        println("azure")
        sc.hadoopConfiguration.set("fs.azure", "org.apache.hadoop.fs.azure.NativeAzureFileSystem")
        sc.hadoopConfiguration.set("fs.azure.account.key."+System.getenv("azure_storage_key")+".blob.core.windows.net", System.getenv("azure_storage_secret"))
        val azureFolder = Option(Array(Query(Option("telemetry-data-store"), Option(arguments(3)), Option(arguments(1)), Option(arguments(2)))))
        val data = DataFetcher.fetchBatchData[String](Fetcher("azure", None, azureFolder))
        println("Azure Data counts from: " + arguments(1) + "to" + arguments(2) +":" + data.count)
        data
      }
    println("Count is", data.count)
    val config = Map("brokerList" -> "localhost:9092", "topic" -> "telemetry_events")
    KafkaDispatcher.dispatch(config, data)
  }

}



