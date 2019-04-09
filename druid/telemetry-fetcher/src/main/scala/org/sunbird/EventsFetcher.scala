package org.sunbird

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

case class Query(bucket: Option[String] = None, prefix: Option[String] = None, startDate: Option[String] = None, endDate: Option[String] = None, brokerList: Option[String] = None, topic: Option[String] = None, datePattern: Option[String] = None, eventType: Option[String] = None, service: Option[String] = None)

object EventsFetcher {
  val conf = new SparkConf().setAppName("app")
  val master = conf.getOption("spark.master")
  implicit val sc: SparkContext = new SparkContext(conf)
  if (master.isEmpty) conf.setMaster("local[*]")

  def main(args: Array[String]): Unit = {
    val result = executeCommands(args)
    if (null != result) {
      result.service.get.toLowerCase match {
        case "azure" => azureFetcher(result)
        case "s3" => s3Fetcher(result)
        case _ => println("Invalid Service provider name!. Please choose either AZURE or S3 as service.")
      }
    } else {
      println("Invalid arguments!!!!!!!!!, Terminating the process now.")
      System.exit(0)
    }
  }

  /**
    * Which is used to validate and execute the arguments
    * `startDate` and `endDate` arguments are mandatory
    *
    */
  def executeCommands(args: Array[String]): Query = {
    val parser = new scopt.OptionParser[Query]("scopt") {
      opt[String]('s', "startDate") required() action { (x, c) =>
        c.copy(startDate = Some(x))
      }
      opt[String]('e', "endDate") required() action { (x, c) =>
        c.copy(endDate = Some(x))
      }
      opt[String]('c', "service") required() action { (x, c) =>
        c.copy(service = Some(x))
      }
      opt[String]('t', "eventType") required() action { (x, c) =>
        c.copy(eventType = Some(x))
      }
      opt[String]('b', "bucket") required() action { (x, c) =>
        c.copy(bucket = Some(x))
      }
      opt[String]('p', "prefix") required() action { (x, c) =>
        c.copy(prefix = Some(x))
      }
      opt[String]('i', "topic") required() action { (x, c) =>
        c.copy(topic = Some(x))
      }
    }
    parser.parse(args, Query()) map {
      config => config
    } getOrElse {
      null
    }
  }

  /**
    * 1. Which is used to fetch the data from the Amazon S3
    * 2. Once Data is retrieved then push the data into kafka topic
    */
  def s3Fetcher(config: Query): Unit = {
    sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", System.getenv("aws_storage_key"))
    sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", System.getenv("aws_storage_secret"))
    val s3folder = Query(Option(config.bucket.get), Option(config.prefix.get), Option(config.startDate.get), Option(config.endDate.get))
    val events = S3DataFetcher.getEvents(s3folder)
    val data = process(events)
    dispatch(data, config.topic.get)
  }

  /**
    * 1. Which is used to fetch the data from the Amazon S3
    * 2. Once Data is retrieved then push the data into kafka topic
    */
  def azureFetcher(config: Query) = {
    sc.hadoopConfiguration.set("fs.azure", "org.apache.hadoop.fs.azure.NativeAzureFileSystem")
    sc.hadoopConfiguration.set("fs.azure.account.key." + System.getenv("azure_storage_key") + ".blob.core.windows.net", System.getenv("azure_storage_secret"))
    val azureFolder = Query(Option(config.bucket.get), Option(config.prefix.get), Option(config.startDate.get), Option(config.endDate.get))
    val events = AzureDataFetcher.getEvents(azureFolder)
    val data = process(events)
    dispatch(data, config.topic.get)
  }

  /**
    *
    *
    */
  def process[T](events: Array[T])(implicit mf: Manifest[T], sc: SparkContext): RDD[T] = {
    sc.textFile(events.mkString(","), 10).map { line => {
      line.asInstanceOf[T]
    }
    }.filter { x => x != null }
  }

  /**
    * Which is used to push the RDD of data into kafka topic
    */
  def dispatch(data: RDD[String], topic: String): Unit = {
    val brokerListIP = System.getenv("brokerListIP") // BrokerList IP Must be Defined in the System environment variable
    println("Total " + data.count() + " Events are Pushing to " + topic + " kafka topic")
    KafkaProducer.dispatch(Map("brokerList" -> brokerListIP, "topic" -> topic), data)
  }
}