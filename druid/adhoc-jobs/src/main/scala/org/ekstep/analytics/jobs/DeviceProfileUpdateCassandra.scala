package org.ekstep.analytics.jobs
import org.apache.spark.sql.functions.{row_number}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.expressions.Window
import com.datastax.spark.connector._
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql._

object DeviceProfileUpdateCassandra {

private val config: Config = ConfigFactory.load

def main(args: Array[String]): Unit = {

val conf = new SparkConf()
            .setAppName("DeviceProfileUpdateCassandra")
            .setMaster("local[*]")
            .set("spark.cassandra.connection.host", config.getString("spark.cassandra.connection.host"))

val sc = new SparkContext(conf)
val spark = SparkSession.builder().master("local[*]").appName("DeviceProfileUpdateCassandra").getOrCreate()
import spark.implicits._ 

val deviceProfileDf = spark.read.format("org.apache.spark.sql.cassandra").options(Map( "table" -> config.getString("cassandra.deviceprofileOldTable")  , "keyspace" -> config.getString("cassandra.keyspace") )).load()

val windowUpdatedDate = Window.partitionBy($"device_id").orderBy($"updated_date".desc)

val deviceProfileTempDf = deviceProfileDf.filter(deviceProfileDf("state").isNotNull).withColumn("latest_updatedDate", row_number.over(windowUpdatedDate)).where($"latest_updatedDate" === 1).drop("latest_updatedDate","channel")

deviceProfileTempDf.write.format("org.apache.spark.sql.cassandra").options(Map( "table" -> config.getString("cassandra.deviceprofileNewTable"), "keyspace" -> config.getString("cassandra.keyspace") )).mode(SaveMode.Append).save()

}

}
