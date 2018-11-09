package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.domain.Location;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class LocationCache {

    String cassandra_db;
    String cassandra_table;
    String redis_host;
    Integer redis_port;
    Config config;
    Jedis jedis;
    CassandraConnect cc ;

    public LocationCache(Config config) {
        this.config = config;
        this.cassandra_db = config.get("cassandra.keyspace", "device_db");
        this.cassandra_table = config.get("cassandra.device_profile_table", "device_profile");
        this.redis_host = config.get("redis.host", "localhost");
        this.redis_port = config.getInt("redis.port", 6379);
        this.jedis = new Jedis(redis_host, redis_port);
        this.cc = new CassandraConnect(config);
    }

    public Location getLoc(String did) {

        Map<String, String> fields = jedis.hgetAll(did);
        List<Row> rows = null;
        if(fields.isEmpty()) {
            String query = "SELECT device_id, state, district FROM " + cassandra_db + "." + cassandra_table + " WHERE device_id="+"'"+did+"'";
            rows = cc.execute(query);
            if(rows.size() > 0) {
                Row r = rows.get(0);
                String state = r.getString("state");
                String district = r.getString("district");
                Location location = new Location();
                location.setDistrict(district);
                location.setState(state);
                putLocation(did, state, district);
                return location;
            }
            else return null;
        }
        else {
            Location location = new Location();
            location.setDistrict(fields.get("district"));
            location.setState(fields.get("state"));
            return location;
        }
    }

    public void putLocation(String did, String state, String district) {
        jedis.hset(did, "state", state);
        jedis.hset(did, "district", district);
    }
}
