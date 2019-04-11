package org.sunbird

import com.typesafe.config._
import org.apache.http.client.methods.{HttpPost, HttpRequestBase}
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}

import scala.io.Source
import scala.util.parsing.json.JSON

case class Query(bucket: Option[String] = None, prefix: Option[String] = None, startDate: Option[String] = None,
                 endDate: Option[String] = None, brokerList: Option[String] = None, topic: Option[String] = None,
                 datePattern: Option[String] = None, eventType: Option[String] = None,
                 service: Option[String] = None, verification: Option[Boolean] = None, dataSource: Option[String] = None)

case class response(TotalCount: Long)

object EventsFetcher {
  val conf = new SparkConf().setAppName("app")
  val master = conf.getOption("spark.master")
  implicit val sc: SparkContext = new SparkContext(conf)
  if (master.isEmpty) conf.setMaster("local[*]")
  val applicationConfig: Config = ConfigFactory.load("application")

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
      opt[String]('i', "topic") optional() action { (x, c) =>
        c.copy(topic = Some(x))
      }
      opt[Boolean]('v', "verification") optional() action { (x, c) =>
        c.copy(verification = Some(x))
      }

      opt[String]('d', "dataSource") optional() action { (x, c) =>
        c.copy(dataSource = Some(x))
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
    sc.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", applicationConfig.getString("aws_storage_key"))
    sc.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", applicationConfig.getString("aws_storage_secret"))
    val endDate = if (config.verification.get) config.startDate.get else config.endDate.get
    val startDate = config.startDate.get
    val s3folder = Query(Option(config.bucket.get), Option(config.prefix.get), Option(startDate), Option(endDate))
    val filesPaths = S3DataFetcher.getEvents(s3folder)
    val data = process(filesPaths)
    if (config.verification.get) {
      verifyData(data, config)
    } else {
      dispatch(data, config.topic.get)
    }
  }

  /**
    * 1. Which is used to fetch the data from the Amazon S3
    * 2. Once Data is retrieved then push the data into kafka topic
    */
  def azureFetcher(config: Query) = {
    sc.hadoopConfiguration.set("fs.azure", "org.apache.hadoop.fs.azure.NativeAzureFileSystem")
    sc.hadoopConfiguration.set("fs.azure.account.key." + applicationConfig.getString("azure_storage_key") + ".blob.core.windows.net", applicationConfig.getString("azure_storage_secret"))
    val endDate = if (config.verification.get) config.startDate.get else config.endDate.get
    val startDate = config.startDate.get
    val azureFolder = Query(Option(config.bucket.get), Option(config.prefix.get), Option(startDate), Option(endDate))
    val filesPaths = AzureDataFetcher.getEvents(azureFolder)
    val data = process(filesPaths)
    if (config.verification.get) {
      verifyData(data, config)
    } else {
      dispatch(data, config.topic.get)
    }
  }


  def process[T](filesPaths: Array[T])(implicit mf: Manifest[T], sc: SparkContext): RDD[T] = {
    sc.textFile(filesPaths.mkString(","), applicationConfig.getInt("min_partition")).map { line => {
      line.asInstanceOf[T]
    }
    }
  }

  /**
    * Which is used to push the RDD of data into kafka topic
    */
  def dispatch(data: RDD[String], topic: String): Unit = {
    val brokerURL = applicationConfig.getString("broker_list_ip") + applicationConfig.getString("broker_list_port") // BrokerList IP Must be Defined in the System environment variable
    println("Total " + data.count() + " Events are Pushing to " + topic + " kafka topic")
    KafkaProducer.dispatch(Map("brokerList" -> brokerURL, "topic" -> topic), data)
  }

  def verifyData(cloudData: RDD[String], config: Query): Unit = {
    import java.time.LocalDateTime
    val cloudDataCount = cloudData.count()
    val druidDataCount = getDataCountFromDruid(config.startDate.get, config.endDate.get, config.dataSource.get, config.eventType.get)
    val headerRecord = applicationConfig.getString("headers_field_labels").split(",")
    val values = Array(LocalDateTime.now().toString, cloudDataCount.toString, druidDataCount.toString, config.startDate.get, config.endDate.get, config.service.get, config.dataSource.get)
    writeToCsv(headerRecord, values, applicationConfig.getString("file_path"), new java.io.File(applicationConfig.getString("file_path")).exists)
    // Implement Trigger Notification service
  }

  def getDataCountFromDruid(fromSyncTs: String, toSyncTs: String, dataSource: String, eventType: String): Any = {
    var request = ""
    if (eventType.toLowerCase == "telemetry") {
      request = "{\n  \"query\":\"SELECT COUNT(*) AS TotalCount FROM \\\"" + dataSource + "\\\" where \\\"@timestamp\\\" BETWEEN " + "'" + fromSyncTs + " 00:00:00' and " + "'" + toSyncTs + " 00:00:00' \",\n  \"resultFormat\" : \"object\"\n}"
    } else {
      request = "{\n  \"query\":\"SELECT COUNT(*) AS TotalCount FROM \\\"" + dataSource + "\\\" where \\\"__time\\\" BETWEEN  TIMESTAMP " + "'" + fromSyncTs + " 00:00:00' and TIMESTAMP " + "'" + toSyncTs + " 00:00:00' \",\n  \"resultFormat\" : \"object\"\n}"
    }
    val apiURL = applicationConfig.getString("druid_host") + applicationConfig.getString("druid_port") + applicationConfig.getString("druid_base_path")
    val result = post(apiURL, request)
    val parsedData = JSON.parseFull(result)
    parsedData match {
      case Some(response: List[Map[String, String]]) => response.flatMap(e => e.get("TotalCount")).lift(0).get
      case _ => println("Unknown object")
    }
  }


  def post(apiURL: String, body: String, requestHeaders: Option[Map[String, String]] = None) = {
    val request = new HttpPost(apiURL)
    request.addHeader("Content-Type", "application/json")
    requestHeaders.getOrElse(Map()).foreach {
      case (headerName, headerValue) => request.addHeader(headerName, headerValue)
    }
    request.setEntity(new StringEntity(body))
    try {
      invoke(request)
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        null
    }
  }


  private def invoke(request: HttpRequestBase) = {

    val httpClient = HttpClients.createDefault();
    try {
      val httpResponse = httpClient.execute(request)
      val entity = httpResponse.getEntity()
      val inputStream = entity.getContent()
      val content = Source.fromInputStream(inputStream, "UTF-8").getLines.mkString
      inputStream.close()
      content
    } finally {
      httpClient.close()
    }
  }

  private def writeToCsv(headerRecord: Array[String], values: Array[String], fileName: String, isFilePresent: Boolean): Unit = {
    import com.opencsv.CSVWriter
    try {
      import java.io.FileWriter
      val writer = new FileWriter(fileName, true)
      val csvWriter = new CSVWriter(writer, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.DEFAULT_ESCAPE_CHARACTER, CSVWriter.DEFAULT_LINE_END)
      try {
        if (!isFilePresent) {
          csvWriter.writeNext(headerRecord)
        }
        csvWriter.writeNext(values)
      } finally {
        if (writer != null) writer.close()
        if (csvWriter != null) csvWriter.close()
      }
    }
  }

}