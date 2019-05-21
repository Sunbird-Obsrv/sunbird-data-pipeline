package org.ekstep.analytics.jobs
import org.apache.spark.sql.functions.{row_number, max, broadcast}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.unix_timestamp
import com.datastax.spark.connector._
import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.sql.functions._
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

val deviceIDPartition = Window.partitionBy($"device_id")

val deviceTotalTimeSpent = deviceProfileDf.withColumn("total_ts_sum", sum('total_ts) over deviceIDPartition).withColumn("total_launches_sum", sum('total_launches) over deviceIDPartition).withColumn("avg_ts_avg", avg('avg_ts) over deviceIDPartition)

val deviceAccessMinMax = deviceProfileDf.groupBy(deviceProfileDf("device_id")).agg(min(deviceProfileDf("first_access")) alias "first_access", max(deviceProfileDf("last_access")) alias "last_access")

val deviceUpdatedDate = deviceTotalTimeSpent.withColumn("rn", row_number.over(windowUpdatedDate)).where($"rn" === 1).drop("rn","last_access","first_access","total_launches","total_ts","avg_ts").withColumnRenamed("total_ts_sum","total_ts").withColumnRenamed("total_launches_sum","total_launches").withColumnRenamed("avg_ts_avg","avg_ts").withColumnRenamed("device_id","device_id_rn")

val finalDf = deviceAccessMinMax.join(deviceUpdatedDate, deviceUpdatedDate("device_id_rn") === deviceAccessMinMax("device_id")).drop("device_id_rn")

finalDf.write.format("org.apache.spark.sql.cassandra").options(Map( "table" -> config.getString("cassandra.deviceprofileNewTable"), "keyspace" -> config.getString("cassandra.keyspace") )).mode(SaveMode.Append).save()

}

}
