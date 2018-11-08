package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.List;

public class CassandraConnect {
    String host = "127.0.0.1";
    Integer port = 9042;
    Cluster cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
    Session session = cluster.connect();

    public List<Row> execute(String query) {
        ResultSet rs = session.execute(query);
        List<Row> rows = rs.all();
        return rows;
    }
}
