package org.ekstep.ep.samza.util;

import com.datastax.driver.core.*;

import java.util.List;
import org.apache.samza.config.Config;

public class CassandraConnect {
    private Session session;
    private Config config;
    private List<String> clusterHosts;
    private int port;

    public CassandraConnect(Config config) {
        this.config = config;
        String host = config.get("cassandra.host", "127.0.0.1");
        Integer port = config.getInt("cassandra.port", 9042);
        Cluster cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
        this.session = cluster.connect();
    }

    public CassandraConnect(String host, Integer port) {
        Cluster cluster = Cluster.builder().addContactPoints(host).withPort(port).build();
        this.session = cluster.connect();
    }

    public CassandraConnect(List<String> hosts, Integer port) {
        this.clusterHosts = hosts;
        this.port = port;
        Cluster.Builder builder = Cluster.builder();
        for (String host : hosts) {
            builder.addContactPoint(host);
        }
        // TODO: We need to check if the consistency level can be downgraded to ONE
        Cluster cluster = builder.withPort(port).withQueryOptions(new QueryOptions()
                .setConsistencyLevel(ConsistencyLevel.QUORUM)).build();
        this.session = cluster.connect();
    }

    public Row findOne(String query) {
        ResultSet rs = session.execute(query);
        return rs.one();
    }

    public List<Row> find(String query) {
        ResultSet rs = session.execute(query);
        return rs.all();
    }

    public void reconnect() {
        this.session.close();
        String host = config.get("cassandra.host", "127.0.0.1");
        Integer port = config.getInt("cassandra.port", 9042);
        Cluster cluster = Cluster.builder().addContactPoint(host).withPort(port).build();
        this.session = cluster.connect();
    }

    public void reconnectCluster() {
        this.session.close();
        Cluster.Builder builder = Cluster.builder();
        for (String host : clusterHosts) {
            builder.addContactPoint(host);
        }
        Cluster cluster = builder.withPort(port).withQueryOptions(new QueryOptions()
                .setConsistencyLevel(ConsistencyLevel.QUORUM)).build();
        this.session = cluster.connect();
    }
}
