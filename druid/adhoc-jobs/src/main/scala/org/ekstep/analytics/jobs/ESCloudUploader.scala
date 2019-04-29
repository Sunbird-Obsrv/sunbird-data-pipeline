package org.ekstep.analytics.jobs

import java.io.File
import java.util.Date

import scala.reflect.io.Directory
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark._
import org.sunbird.cloud.storage.factory.{StorageConfig, StorageServiceFactory}

object ESCloudUploader {
    private val now: Date = new Date()
    private val config: Config = ConfigFactory.load
    case class Source(timestamp: Long, data: String)

    def main(args: Array[String]): Unit = {

        val index = config.getString("elasticsearch.query.index")
        val query = config.getString("elasticsearch.query.jsonString")
        val outputFilePath = config.getString("outputFilePath")

        println(s"[$index] query ===> $query")

        require(!index.isEmpty && !query.isEmpty, "require valid inputs! index name and query cannot be empty!")

        val conf = new SparkConf()
            .setAppName("SparkEStoJSON")
            .setMaster("local[*]")
            // Elasticsearch settings
            .set("es.nodes", config.getString("elasticsearch.host"))
            .set("es.port", config.getString("elasticsearch.port"))
            .set("es.scroll.size", config.getString("elasticsearch.scroll.size"))
            .set("es.query", query)

        val sparkSession = SparkSession.builder.config(conf).getOrCreate
        val sc = sparkSession.sparkContext

        println(s"deleting output folder if exist! : $outputFilePath")
        val directory = new Directory(new File(outputFilePath))
        if (directory.exists) directory.deleteRecursively()

        sc.esJsonRDD(index).map(data => s"""{ "timestamp": ${now.getTime}, "data": ${data._2} }""")
            .coalesce(1)
            .saveAsTextFile(outputFilePath)

        // backup the output file to cloud
        val storageService = StorageServiceFactory.getStorageService(StorageConfig(config.getString("cloudStorage.provider"), config.getString("cloudStorage.accountName"), config.getString("cloudStorage.accountKey")))
        storageService.upload(config.getString("cloudStorage.container"), outputFilePath + "/part-00000", config.getString("cloudStorage.objectKey"), isDirectory = Option(false))
        println("successfully backed up file to cloud!")
        System.exit(0)
    }
}
