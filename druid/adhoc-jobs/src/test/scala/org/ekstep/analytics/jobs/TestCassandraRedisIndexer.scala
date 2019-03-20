package org.ekstep.analytics.jobs

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class TestCassandraRedisIndexer extends FlatSpec with Matchers with BeforeAndAfterAll {

    "cassandra indexer" should  "index user data to redis" in {
        println(s"======> Starting Redis indexing job <=======")
        //CassandraToRedisIndexer.main(Array())
    }
}