package org.ekstep.analytics.jobs

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.sunbird.cloud.storage.factory.{StorageConfig, StorageServiceFactory}
import com.redislabs.provider.redis._
import org.ekstep.analytics.util.JSONUtils

object CSVRedisIndexer {
  private val config: Config = ConfigFactory.load
  case class Source(timestamp: Long, data: String)
  def main(args: Array[String]): Unit = {

    val inputFilePath: String = config.getString("inputFilePath")
    println(s"inputFilePath: $inputFilePath")
    val filename: String = config.getString("cloudStorage.objectKey").split("/")(1)
    val storageService = StorageServiceFactory.getStorageService(StorageConfig(config.getString("cloudStorage.provider"), config.getString("cloudStorage.accountName"), config.getString("cloudStorage.accountKey")))
    storageService.download(config.getString("cloudStorage.container"), config.getString("cloudStorage.objectKey"), inputFilePath, isDirectory = Option(false))
    println("downloaded the backup file!")

    val conf = new SparkConf()
      .setAppName("SparkJSONRedisIndexer")
      .setMaster("local[*]")
      // redis settings
      .set("spark.redis.host", config.getString("redis.host"))
      .set("spark.redis.port", config.getString("redis.port"))
      .set("spark.redis.db", config.getString("redis.database.index"))
      .set("spark.redis.max.pipeline.size", config.getString("redis.max.pipeline.size"))

    val sparkSession = SparkSession.builder.config(conf).getOrCreate
    val sc = sparkSession.sparkContext

    val inputFile = sparkSession.read.format("csv").option("header", "true").load(inputFilePath + filename )
    val key = config.getString("redis.key.field")
    val kv = inputFile.rdd.map(data => (data.getAs[String](key), JSONUtils.serialize(data.getValuesMap(data.schema.fieldNames))))

    sc.toRedisKV(kv)
  }
}
