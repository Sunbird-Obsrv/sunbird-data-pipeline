package org.ekstep.analytics.jobs

import java.text.SimpleDateFormat

import com.datastax.spark.connector._
import com.redislabs.provider.redis._
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.lang.StringUtils
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.rdd.RDD
import org.ekstep.analytics.util.JSONUtils


object CassandraRedisIndexer {

  private val config: Config = ConfigFactory.load

  def main(args: Array[String]): Unit = {

    val isForAllUsers = args(0) // true/false
    val specificUserId = args(1) // userid
    val fromSpecificDate = args(2) // date in YYYY-MM-DD format

    val conf = new SparkConf()
      .setAppName("CassandraToRedisIndexer")
      .setMaster("local[*]")
      // Cassandra settings
      .set("spark.cassandra.connection.host", config.getString("spark.cassandra.connection.host"))
      // redis settings
      .set("spark.redis.host", config.getString("redis.host"))
      .set("spark.redis.port", config.getString("redis.port"))
      .set("spark.redis.db", config.getString("redis.user.database.index"))
      .set("spark.redis.max.pipeline.size", config.getString("redis.max.pipeline.size"))

    val sc = new SparkContext(conf)

    val userKeyspace = config.getString("cassandra.user.keyspace")
    val userTableName = config.getString("cassandra.user.table")
    val redisKeyProperty: String = config.getString("redis.user.index.source.key") // userid

    def seqOfAnyToSeqString(param: Seq[AnyRef]): Seq[(String, String)]
    = {
      param.map {
        case (x, y) => (x.toString, if (!y.isInstanceOf[String]) JSONUtils.serialize(y.asInstanceOf[AnyRef]) else y.asInstanceOf[String])
      }
    }

    val dtf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSZ")
    val dtf2 = new SimpleDateFormat("yyyy-MM-dd")

    def getFilteredUserRecords(usersData: RDD[Map[String, Any]]): RDD[Map[String, Any]] = {
      if (StringUtils.equalsIgnoreCase(isForAllUsers, "true")) {
        usersData
      } else if (null != specificUserId) {
        usersData.filter(user => StringUtils.equalsIgnoreCase(user.getOrElse(redisKeyProperty, "").asInstanceOf[String], specificUserId))
      } else if (null != fromSpecificDate) {
        println(s"Fetching all the user records from this specific date:$fromSpecificDate ")
        usersData.filter(user => {
          dtf1.parse(user.getOrElse("updateddate", null).asInstanceOf[String]).after(dtf2.parse(fromSpecificDate))
        })
      } else {
        println("Data is not fetching from the table since input is invalid")
        null
      }
    }

    val usersData = getFilteredUserRecords(sc.cassandraTable(userKeyspace, userTableName).map(f => f.toMap))
    val mappedData = usersData.map { obj =>
      (obj.getOrElse(redisKeyProperty, "").asInstanceOf[String], obj)
    }
    mappedData.collect().foreach(record => {
      val filteredData = record._2.toSeq.filterNot { case (_, y) => (y.isInstanceOf[String] && y.asInstanceOf[String].forall(_.isWhitespace)) || y == null }
      val userRdd = sc.parallelize(seqOfAnyToSeqString(filteredData))
      sc.toRedisHASH(userRdd, record._1)
    })
  }
}
