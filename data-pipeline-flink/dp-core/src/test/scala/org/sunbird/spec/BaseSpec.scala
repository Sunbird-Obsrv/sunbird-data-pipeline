package org.sunbird.spec

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.scalatest.{BeforeAndAfterAll, FlatSpec}
import redis.embedded.RedisServer

class BaseSpec extends FlatSpec with BeforeAndAfterAll {
  var redisServer: RedisServer = _

  override def beforeAll() {
    super.beforeAll()
    redisServer = new RedisServer(6340)
    redisServer.start()
    EmbeddedPostgres.builder.setPort(5430).start() // Use the same port 5430 which is defined in the base-test.conf
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    redisServer.stop()
  }

}
