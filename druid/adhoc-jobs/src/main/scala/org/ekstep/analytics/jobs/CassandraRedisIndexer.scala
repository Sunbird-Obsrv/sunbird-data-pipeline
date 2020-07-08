package org.ekstep.analytics.jobs

import com.typesafe.config.{Config, ConfigFactory}
import org.ekstep.analytics.util.JSONUtils
import com.datastax.spark.connector._
import com.google.gson.Gson
import com.redislabs.provider.redis._
import org.apache.commons.lang.{StringEscapeUtils, StringUtils}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD


object CassandraRedisIndexer {

  private val config: Config = ConfigFactory.load

  def main(args: Array[String]): Unit = {

    val isForAllUsers = "true"
    val specificUserId = null
    val specificDate = null
    val insertionType = "hashmap"
    println("arg0" + args(0))
    println("arg1" + args(1))
    println("arg2" + args(2))
    println("arg3" + args(3))

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
    val redisKeyProperty: String = config.getString("redis.user.index.source.key")
    //val insertionType: String = config.getString("insertion.type")

    def seqOfAnyToSeqString(param: Seq[AnyRef]): Seq[(String, String)]
    = {
      param.map {
        case (x, y) => (x.toString, if(!y.isInstanceOf[String]) JSONUtils.serialize(y.asInstanceOf[AnyRef]) else  y.asInstanceOf[String])
      }
    }

    def getFilteredUserRecords(usersData: RDD[Map[String, AnyRef]]): RDD[Map[String, AnyRef]] = {
      if (StringUtils.equalsIgnoreCase(isForAllUsers, "true")) {
        usersData
      } else if (null != specificUserId) {
        usersData.filter(user => StringUtils.equalsIgnoreCase(user.getOrElse(redisKeyProperty, "").asInstanceOf[String], specificUserId))
      } else {
        println("Data is not fetching from the table since input is invalid")
        null
      }
    }

    val usersData = getFilteredUserRecords(sc.cassandraTable(userKeyspace, userTableName)).map(f => f.toMap.asInstanceOf[Map[String, AnyRef]])
    println("usersData" + usersData.count())

    if (StringUtils.equalsIgnoreCase(insertionType, "hashmap")) {
      val mappedData = usersData.map { obj =>
        (obj.getOrElse(redisKeyProperty, "").asInstanceOf[String], obj)
      }
      mappedData.collect().foreach(record => {val filteredData = record._2.toSeq.filterNot { case (_, y) => (y.isInstanceOf[String] && y.asInstanceOf[String].forall(_.isWhitespace)) || y == null}
        val userRdd = sc.parallelize(seqOfAnyToSeqString(filteredData))
        sc.toRedisHASH(userRdd, record._1)
      })
    } else if(StringUtils.equalsIgnoreCase(insertionType, "json")) {
      val mappedData = usersData.map { obj =>
        (obj.getOrElse(redisKeyProperty, "").asInstanceOf[String], JSONUtils.serialize(obj))
      }
      sc.toRedisKV(mappedData)
    }
  }
}
