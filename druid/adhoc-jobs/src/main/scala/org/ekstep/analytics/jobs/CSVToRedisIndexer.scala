package org.ekstep.analytics.jobs

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.{SparkConf, SparkContext}
import com.redislabs.provider.redis._
import org.apache.spark.sql.SparkSession
import org.ekstep.analytics.util.JSONUtils

object CSVToRedisIndexer {

    private val config: Config = ConfigFactory.load

    def main(args: Array[String]): Unit = {

        val filePath = "wasbs://dev-data-store@sunbirddevtelemetry.blob.core.windows.net/dialcode-data/dial_code.csv"
        val conf = new SparkConf()
          .setAppName("SparkCSVtoRedisIndexer")
          .setMaster("local[*]")

          // redis settings
          .set("spark.redis.host", config.getString("redis.host"))
          .set("spark.redis.port", config.getString("redis.port"))
          .set("spark.redis.db", config.getString("redis.dialcode.database.index"))
          .set("spark.redis.max.pipeline.size", config.getString("redis.max.pipeline.size"))

        val sc = new SparkContext(conf)
        val spark = SparkSession.builder.config(conf).getOrCreate()

        spark.sparkContext.hadoopConfiguration.set("fs.azure", "org.apache.hadoop.fs.azure.NativeAzureFileSystem")
        spark.sparkContext.hadoopConfiguration.set("fs.azure.account.key." + config.getString("cloudStorage.accountName") + ".blob.core.windows.net", config.getString("cloudStorage.accountKey"))

        val data = spark.read.option("header", "true").option("inferSchema", "true").csv(filePath).toJSON.rdd.map(f => f)
        val finalData = data.map(f => (getKey(f), f))
        finalData.foreach(f => println(f))

        spark.sparkContext.toRedisKV(
            finalData
        )
    }

    def getKey(str: String) ={
        val dataMap = JSONUtils.deserialize[Map[String, AnyRef]](str)
        val key = dataMap.get("identifier")
        key.mkString
    }
}
