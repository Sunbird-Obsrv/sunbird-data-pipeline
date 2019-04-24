package org.ekstep.analytics.jobs

import org.scalatest.{BeforeAndAfterAll, FlatSpec, Matchers}

class TestESRedisIndexer extends FlatSpec with Matchers with BeforeAndAfterAll {

    "main method" should  "run" in {
        println(s"======> Starting Redis indexing job <=======")
        //ESToRedisIndexer.main(Array())
    }
}
