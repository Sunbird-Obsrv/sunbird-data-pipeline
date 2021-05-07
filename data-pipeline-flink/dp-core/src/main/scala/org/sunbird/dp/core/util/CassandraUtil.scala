package org.sunbird.dp.core.util

import java.util

import com.datastax.driver.core._
import com.datastax.driver.core.exceptions.DriverException
import org.slf4j.LoggerFactory

class CassandraUtil(host: String, port: Int) {

  val logger = LoggerFactory.getLogger("CassandraUtil")
  val options : QueryOptions = new QueryOptions()
  val cluster = {
    Cluster.builder()
      .addContactPoint(host)
      .withPort(port)
      .withQueryOptions(options.setConsistencyLevel(ConsistencyLevel.QUORUM))
      .withoutJMXReporting()
      .build()
  }
  var session = cluster.connect()

  def findOne(query: String): Row = {
    val rs: ResultSet = session.execute(query)
    rs.one
  }

  def find(query: String): util.List[Row] = {
    try {
      val rs: ResultSet = session.execute(query)
      rs.all
    } catch {
      case ex: DriverException =>
        logger.info(s"Failed cassandra query is ${query}")
        ex.printStackTrace()
        throw ex
        // this.reconnect()
        // this.find(query)
    }
  }

  def upsert(query: String): Boolean = {
    val rs: ResultSet = session.execute(query)
    rs.wasApplied
  }

  def getUDTType(keyspace: String, typeName: String): UserType = session.getCluster.getMetadata.getKeyspace(keyspace).getUserType(typeName)

  def reconnect(): Unit = {
    this.session.close()
    val cluster: Cluster = Cluster.builder.addContactPoint(host).withPort(port).build
    this.session = cluster.connect
  }

  def close(): Unit = {
    this.session.close()
  }

}
