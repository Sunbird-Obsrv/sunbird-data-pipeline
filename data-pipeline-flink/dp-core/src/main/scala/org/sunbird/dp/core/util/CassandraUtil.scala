package org.sunbird.dp.core.util

import java.util

import com.datastax.driver.core._
import com.datastax.driver.core.querybuilder.Insert

class CassandraUtil(host: String,port: Int) {


  val cluster = {
    Cluster.builder()
      .addContactPoint(host)
      .withPort(port)
      .withoutJMXReporting()
      .build()
  }
  var session = cluster.connect()

  def findOne(query: String): Row = {
    val rs: ResultSet = session.execute(query)
    rs.one
  }

  def find(query: String): util.List[Row] = {
    val rs: ResultSet = session.execute(query)
    rs.all
  }

  def upsert(query: String): Boolean = {
    val rs: ResultSet = session.execute(query)
    rs.wasApplied
  }

  def upsert(query: Insert): Boolean = {
    val rs = session.execute(query)
    rs.wasApplied
  }

  def getUDTType(keyspace: String, typeName: String): UserType = session.getCluster.getMetadata.getKeyspace(keyspace).getUserType(typeName)

  def reconnect(): Unit = {
    this.session.close()
    val cluster: Cluster = Cluster.builder.addContactPoint(host).withPort(port).build
    this.session = cluster.connect
  }

}