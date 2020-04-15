package org.sunbird.spec

import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import redis.embedded.RedisServer

class BaseSpec extends FlatSpec with BeforeAndAfterAll{
  var redisServer: RedisServer = _

  override def beforeAll() {
    super.beforeAll()
    //redisServer = new RedisServer(6341)
    //redisServer.start()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    //redisServer.stop()
  }

}
