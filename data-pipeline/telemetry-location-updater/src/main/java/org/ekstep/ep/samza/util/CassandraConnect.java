package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.List;
import org.apache.samza.config.Config;

public class CassandraConnect {
    String host;
    Integer port;
    Cluster cluster;
    Session session;

    public CassandraConnect(Config config) {
        this.host = config.get("cassandra.host", "127.0.0.1");
        this.port = config.getInt("cassandra.port", 9042);
        this.cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
        this.session = cluster.connect();
    }

    public List<Row> execute(String query) {
        ResultSet rs = session.execute(query);
        List<Row> rows = rs.all();
        return rows;
    }
}
