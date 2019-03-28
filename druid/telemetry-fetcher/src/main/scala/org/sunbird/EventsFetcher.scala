package org.sunbird

import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

case class Query(bucket: Option[String] = None, prefix: Option[String] = None, startDate: Option[String] = None, endDate: Option[String] = None, delta: Option[Int] = None, brokerList: Option[String] = None, topic: Option[String] = None, windowType: Option[String] = None, windowDuration: Option[Int] = None, file: Option[String] = None, excludePrefix: Option[String] = None, datePattern: Option[String] = None, folder: Option[String] = None, creationDate: Option[String] = None)

case class fetch(`type`: String, query: Option[Query], queries: Option[Array[Query]]);

case class Config(service: String = null, bucket: Option[String] = None, fromDate: String = null, toDate: String = null, eventType: Option[String] = None, folderPath: Option[String] = None, env: Option[String] = None)


object EventsFetcher {
  val conf = new SparkConf().setAppName("app").setMaster("local[2]").set("spark.executor.memory", "1g")
  val master = conf.getOption("spark.master")
  implicit val sc: SparkContext = new SparkContext(conf)
  if (master.isEmpty) conf.setMaster("local[*]")
  val DEFAULT_EVENTS_FOLDER_PATH = "raw/"
  var topic = ""

  def main(args: Array[String]): Unit = {

    val parser = new scopt.OptionParser[Config]("scopt") {
      opt[String]('f', "fromDate") required() action { (x, c) =>
        c.copy(fromDate = x)
      }
      opt[String]('t', "toDate") required() action { (x, c) =>
        c.copy(toDate = x)
      }
      opt[String]('s', "service") optional() action { (x, c) =>
        c.copy(service = x)
      }
      opt[String]('t', "eventType") optional() action { (x, c) =>
        c.copy(eventType = Some(x))
      }
      opt[String]('b', "bucket") optional() action { (x, c) =>
        c.copy(bucket = Some(x))
      }
      opt[String]('e', "env") optional() action { (x, c) =>
        c.copy(env = Some(x))
      }
      opt[String]('p', "folderPath") optional() action { (x, c) =>
        c.copy(folderPath = Some(x))
      }
    }
    parser.parse(args, Config()) map { config =>
      println("config.service==" + config.service)
      println("config.eventType=" + config.eventType)
      println("config.fromDate=" + config.fromDate)
      println("config.toDate=" + config.toDate)
      println("config.env=" + config.env)
      println("config.location=" + config.bucket)
      println("config.foldersPath=" + config.folderPath)

      config.service match {
        case "azure" => azureFetcher(config)
        case "s3" => s3Fetcher(config)
        case _ => println("Invalid Service")
      }
    } getOrElse {
      println("Bad argument!!!!!!!!!, Terminating the process.")
      return
    }
  }

  def s3Fetcher(config: Config): Unit = {
    sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", System.getenv("aws_storage_key"))
    sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", System.getenv("aws_storage_secret"))
    val s3folder = Array(Query(Option(config.bucket.getOrElse("ekstep-prod-data-store")), Option(config.folderPath.getOrElse(DEFAULT_EVENTS_FOLDER_PATH)), Option(config.fromDate), Option(config.toDate)))
    val keys = S3DataFetcher.getObjectKeys(s3folder)
    val data = process(keys)
    dispatch(data, getTopic(config))
  }

  def azureFetcher(config: Config) = {
    sc.hadoopConfiguration.set("fs.azure", "org.apache.hadoop.fs.azure.NativeAzureFileSystem")
    sc.hadoopConfiguration.set("fs.azure.account.key." + System.getenv("azure_storage_key") + ".blob.core.windows.net", System.getenv("azure_storage_secret"))
    val azureFolder = Array(Query(Option(config.bucket.getOrElse("telemetry-data-store")), Option(config.folderPath.getOrElse(DEFAULT_EVENTS_FOLDER_PATH)), Option(config.fromDate), Option(config.toDate)))
    val keys = AzureDataFetcher.getObjectKeys(azureFolder)
    val data = process(keys)
    dispatch(data, getTopic(config))
  }

  def process[T](keys: Array[T])(implicit mf: Manifest[T], sc: SparkContext): RDD[T] = {
    if (null == keys || keys.length == 0) {
      return sc.parallelize(Seq[T](), 10);
    }
    val isString = mf.runtimeClass.getName.equals("java.lang.String");
    sc.textFile(keys.mkString(","), 10).map { line => {
      try {
        if (isString) {
          line.asInstanceOf[T]
        } else {
          line.asInstanceOf[T]
        }
      } catch {
        case ex: Exception =>
          null.asInstanceOf[T]
      }
    }
    }.filter { x => x != null };
  }

  def dispatch(data: RDD[String], topic:String): Unit = {
    val brokerListIP = System.getenv("brokerListIP")
    println("brokerListIp" + brokerListIP);
    val config = Map("brokerList" -> brokerListIP, "topic" -> topic)
    print("topic is", topic)
    println("data.count" + data.count())
    //KafkaProducer.dispatch(config, data)
  }

  def getTopic(config: Config): String = {
    val DEFAULT_EVENT_TYPE = "summary"
    val DEFAULT_ENV ="dev"
    if (config.eventType.get == DEFAULT_EVENT_TYPE) config.env.getOrElse(DEFAULT_ENV) + ".events.summary" else config.env.getOrElse(DEFAULT_ENV) + ".events.telemetry"
  }
}