import org.apache.spark.sql.functions.{row_number, max, broadcast}
import org.apache.spark.sql.expressions.Window
import org.apache.spark.sql.functions.unix_timestamp
import com.datastax.spark.connector._
import org.apache.spark.sql.SparkSession

object DeviceProfileUpdateCassandra {

def main(args: Array[String]): Unit = {

val conf = new SparkConf()
            .setAppName("DeviceProfileUpdateCassandra")
            .setMaster("local[*]")
            // Cassandra settings
            .set("spark.cassandra.connection.host", config.getString("spark.cassandra.connection.host"))

val sc = new SparkContext(conf)
val spark = SparkSession.builder().master("local[*]").appName("DeviceProfileUpdateCassandra").getOrCreate()

val deviceProfileDf = spark.read.format("org.apache.spark.sql.cassandra").options(Map( "table" -> "device_profile", "keyspace" -> "testdb" )).load()

val windowUpdatedDate = Window.partitionBy($"device_id").orderBy($"updated_date".desc)

val deviceIDPartition = Window.partitionBy($"device_id")

val deviceTotalTimeSpent = deviceProfileDf.withColumn("total_tssum", sum('total_ts) over deviceIDPartition).withColumn("total_launchessum", sum('total_launches) over deviceIDPartition).withColumn("avg_tsavg", avg('avg_ts) over deviceIDPartition)

val deviceAccessMinMax = device_total_values.groupBy(device_total_values("device_id")).agg(min(device_total_values("first_access")) alias "first_access", max(device_total_values("last_access")) alias "last_access")

val deviceUpdatedDate = deviceTotalTimeSpent.withColumn("rn", row_number.over(windowUpdatedDate)).where($"rn" === 1).drop("rn","last_access","first_access","total_launches","total_ts","avg_ts").withColumnRenamed("total_tssum","total_ts").withColumnRenamed("total_launchessum","total_launches").withColumnRenamed("avg_tsavg","avg_ts")

val finalDf = deviceUpdatedDate.join(deviceAccessMinMax, deviceUpdatedDate("device_id") === deviceAccessMinMax("device_id"))

finalDf.write.format("org.apache.spark.sql.cassandra").options(Map( "table" -> "device_profile1", "keyspace" -> "testdb")).save()

}

}
