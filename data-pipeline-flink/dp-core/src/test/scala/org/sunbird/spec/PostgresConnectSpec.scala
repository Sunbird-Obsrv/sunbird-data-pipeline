package org.sunbird.spec

import com.typesafe.config.{Config, ConfigFactory}
import org.junit.Assert.{assertEquals, assertNotNull}
import org.scalatest.Matchers
import org.scalatestplus.mockito.MockitoSugar
import org.sunbird.dp.core.util.{PostgresConnect, PostgresConnectionConfig}


class PostgresConnectSpec extends BaseSpec with Matchers with MockitoSugar {
  val config: Config = ConfigFactory.load("base-test.conf")
  "PostgresConnection" should "able to connect and query the postgres" in {

    val postgresConfig = PostgresConnectionConfig(
      user = config.getString("postgres.user"),
      password = config.getString("postgres.password"),
      database = "postgres",
      host = config.getString("postgres.host"),
      port = config.getInt("postgres.port"),
      maxConnections = config.getInt("postgres.maxConnections"),
      sslMode = config.getBoolean("postgres.sslMode")
    )
    val postgresConnect = new PostgresConnect(postgresConfig)
    val connection = postgresConnect.getConnection
    connection should not be (null)
    postgresConnect.execute("CREATE TABLE device_table(id text PRIMARY KEY, channel text);")
    postgresConnect.execute("INSERT INTO device_table(id,channel)  VALUES('12345','custchannel');")
    val st = connection.createStatement
    val rs = postgresConnect.executeQuery("SELECT * FROM device_table where id='12345';")
    while ( {
      rs.next
    }) {
      assertEquals("12345", rs.getString("id"))
      assertEquals("custchannel", rs.getString("channel"))
    }

    val resetConnection = postgresConnect.resetConnection
    assertNotNull(resetConnection)
    postgresConnect.closeConnection()
  }
  it should "Re Connect while executing query if the the postgres connection is off " in {
    val postgresConfig = PostgresConnectionConfig(
      user = config.getString("postgres.user"),
      password = config.getString("postgres.password"),
      database = "postgres",
      host = config.getString("postgres.host"),
      port = config.getInt("postgres.port"),
      maxConnections = config.getInt("postgres.maxConnections"),
      sslMode = config.getBoolean("postgres.sslMode")
    )
    val postgresConnect = new PostgresConnect(postgresConfig)
    val connection = postgresConnect.getConnection
    connection.close()
    postgresConnect.execute("CREATE TABLE device_table3(id text PRIMARY KEY, channel text);")
  }

}
