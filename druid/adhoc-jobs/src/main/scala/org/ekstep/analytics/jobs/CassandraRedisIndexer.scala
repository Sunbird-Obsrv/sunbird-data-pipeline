package org.ekstep.analytics.jobs

import com.typesafe.config.{Config, ConfigFactory}
import org.ekstep.analytics.util.JSONUtils
import com.datastax.spark.connector._
import com.redislabs.provider.redis._
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext


object CassandraRedisIndexer {

   private val config: Config = ConfigFactory.load

    def main(args: Array[String]): Unit = {

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

        val userData = sc.cassandraTable(userKeyspace, userTableName)
        val userMap = userData.map(f => f.toMap)
        val mappedData = userMap.map { obj =>
            (obj.getOrElse(redisKeyProperty, "").asInstanceOf[String] , JSONUtils.serialize(obj))
        }
        sc.toRedisKV(mappedData)
    }


}
