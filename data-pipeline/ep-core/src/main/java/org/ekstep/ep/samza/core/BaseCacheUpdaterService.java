package org.ekstep.ep.samza.core;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;
import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.util.CassandraConnect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseCacheUpdaterService {

    private RedisConnect redisConnect;
    private CassandraConnect cassandraConnect;
    private Jedis connection;

    public BaseCacheUpdaterService(RedisConnect redisConnect) {
        this.redisConnect = redisConnect;
        connection = redisConnect.getConnection();
    }

    public BaseCacheUpdaterService(CassandraConnect cassandraConnect){
        this.cassandraConnect = cassandraConnect;
    }

    public BaseCacheUpdaterService(RedisConnect redisConnect, CassandraConnect cassandraConnect) {
        this.redisConnect = redisConnect;
        connection = redisConnect.getConnection();
        this.cassandraConnect = cassandraConnect;
    }

    public void addToCache(String key, String value, int storeId) {
        try {
            connection.select(storeId);
            if (key != null && !key.isEmpty() && null != value && !value.isEmpty()) {
                connection.set(key, value);
            }
        } catch(JedisException ex) {
            connection = redisConnect.getConnection(storeId);
            if (null != value)
                connection.set(key, value);
        }
    }

    public String readFromCache(String key, int storeId) {
        try {
            connection.select(storeId);
            return connection.get(key);
        }
        catch (JedisException ex) {
            connection = redisConnect.getConnection(storeId);
            return connection.get(key);
        }
    }

    public void updateToCassandra(String keyspace, String table, Map<String, String> data) {
        Insert query = QueryBuilder.insertInto(keyspace, table)
                .values(new ArrayList<>(data.keySet()), new ArrayList<>(data.values()));
        try {
            cassandraConnect.upsert(query);
        } catch(DriverException ex) {
            cassandraConnect.reconnectCluster();
            cassandraConnect.upsert(query);
        }
    }

    public List<Row> readFromCassandra(String keyspace, String table, Clause clause) {
        List<Row> rowSet = null;
        String query = QueryBuilder.select().all()
                .from(keyspace, table)
                .where(clause)
                .toString();
        try {
            rowSet = cassandraConnect.find(query);
        } catch(DriverException ex) {
            cassandraConnect.reconnectCluster();
            rowSet = cassandraConnect.find(query);
        }
        return rowSet;
    }
}
