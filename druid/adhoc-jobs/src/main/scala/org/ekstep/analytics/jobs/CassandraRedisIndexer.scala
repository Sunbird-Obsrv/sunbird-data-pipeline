package org.ekstep.analytics.jobs

import com.typesafe.config.{Config, ConfigFactory}
import org.ekstep.analytics.util.JSONUtils
import com.datastax.spark.connector._
import com.redislabs.provider.redis._
import org.apache.commons.lang.{StringEscapeUtils, StringUtils}
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD


object CassandraRedisIndexer {

  private val config: Config = ConfigFactory.load

  def main(args: Array[String]): Unit = {

    val isForAllUsers = args(0)
    val specificUserId = args(1)
    val specificDate = args(2)
    val insertionType = args(4)

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

    def seqOfAnyToSeqString(param: Seq[Any]): Seq[(String, String)]
    = {
      param.map {
        case (x, y) => (x.toString, y.toString)
      }
    }

    def getFilteredUserRecords(usersData: RDD[Map[String, Any]]): RDD[Map[String, Any]] = {
      if (StringUtils.equalsIgnoreCase(isForAllUsers, "true")) {
        usersData
      } else if (null != specificUserId) {
        usersData.filter(user => StringUtils.equalsIgnoreCase(user.getOrElse("id", "").asInstanceOf[String], specificUserId))
      } else {
        null
      }
    }

    val usersData = getFilteredUserRecords(sc.cassandraTable(userKeyspace, userTableName).map(f => f.toMap))

    if (StringUtils.equalsIgnoreCase(insertionType, "hashmap")) {
      val mappedData = usersData.map { obj =>
        (obj.getOrElse(redisKeyProperty, "").asInstanceOf[String], obj)
      }
      mappedData.collect().foreach(record => {
        val filteredData = record._2.toSeq.filterNot { case (_, y) => (
          y.isInstanceOf[String] &&
            y.asInstanceOf[String].forall(_.isWhitespace)
          ) || y == null
        }
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
