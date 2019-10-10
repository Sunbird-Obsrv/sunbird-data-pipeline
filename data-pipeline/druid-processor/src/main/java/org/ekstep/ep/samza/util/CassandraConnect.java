package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import org.apache.samza.config.Config;

import java.util.List;

public class CassandraConnect {
    private Session session;

    public CassandraConnect(Config config) {
        String host = config.get("cassandra.host", "127.0.0.1");
        Integer port = config.getInt("cassandra.port", 9042);
        Cluster cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
        this.session = cluster.connect();
    }

    public CassandraConnect(String host, Integer port) {
        Cluster cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
        this.session = cluster.connect();
    }

    public List<Row> execute(String query) {
        ResultSet rs = session.execute(query);
        return rs.all();
    }
}
