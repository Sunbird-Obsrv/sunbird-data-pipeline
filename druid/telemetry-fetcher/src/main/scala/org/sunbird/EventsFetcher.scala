package org.sunbird

import org.apache.spark.{SparkConf, SparkContext}

case class Query(bucket: Option[String] = None, prefix: Option[String] = None, startDate: Option[String] = None, endDate: Option[String] = None, delta: Option[Int] = None, brokerList: Option[String] = None, topic: Option[String] = None, windowType: Option[String] = None, windowDuration: Option[Int] = None, file: Option[String] = None, excludePrefix: Option[String] = None, datePattern: Option[String] = None, folder: Option[String] = None, creationDate: Option[String] = None)
case class fetch(`type`: String, query: Option[Query], queries: Option[Array[Query]]);
object EventsFetcher {

  val conf = new SparkConf().setAppName("app").setMaster("local[2]").set("spark.executor.memory", "1g")
  val master = conf.getOption("spark.master")
  implicit val sc: SparkContext = new SparkContext(conf)
  if (master.isEmpty) {
    conf.setMaster("local[*]")
  }

  def main(args: Array[String]): Unit = {
    println("Hello scala")
    azureFetcher();
  }



  def s3Fetcher(): Unit = {
    sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", System.getenv("aws_storage_key"))
    sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", System.getenv("aws_storage_secret"))
    val s3folder = Option(Array(Query(Option("ekstep-prod-data-store"), Option("raw/"), Option("2019-03-10"), Option("2019-03-10"))))
    val data = S3DataFetcher.fetchBatchData[String](fetch("s3", None, s3folder))
    println("Count of data is:"+ data.count())
  }

  def azureFetcher() = {
    sc.hadoopConfiguration.set("fs.azure", "org.apache.hadoop.fs.azure.NativeAzureFileSystem")
    sc.hadoopConfiguration.set("fs.azure.account.key." + System.getenv("azure_storage_key") + ".blob.core.windows.net", System.getenv("azure_storage_secret"))
    val azureFolder = Option(Array(Query(Option("telemetry-data-store"), Option("raw/"), Option("2019-01-12"), Option("2019-01-12"))))
    val data = S3DataFetcher.fetchBatchData[String](fetch("azure", None, azureFolder))
    println("Data is===" + data)
//    data.foreach(f=>println("fffff" + f))
    val config = Map("brokerList"->"localhost:9092", "topic" ->"telemetry-test")
    KafkaProducer.dispatch(config, data)

  }
}