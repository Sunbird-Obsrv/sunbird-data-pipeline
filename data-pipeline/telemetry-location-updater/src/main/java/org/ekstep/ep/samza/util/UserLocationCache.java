package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Location;
import com.datastax.driver.core.querybuilder.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class UserLocationCache {

    private static Logger LOGGER = new Logger(UserLocationCache.class);

    private String cassandra_db;
    private String cassandra_user_table;
    private String cassandra_location_table;
    private RedisConnect redisConnect;
    private CassandraConnect cassandraConnection;
    private int locationDbKeyExpiryTimeInSeconds;
    private Config config;

    public UserLocationCache(Config config, RedisConnect redisConnect, CassandraConnect cassandraConnect) {
        this.config = config;
        this.redisConnect = redisConnect;
        this.cassandra_db = config.get("middleware.cassandra.keyspace", "sunbird");
        this.cassandra_user_table = config.get("middleware.cassandra.user_table", "user");
        this.cassandra_location_table = config.get("middleware.cassandra.location_table", "location");
        this.cassandraConnection = cassandraConnect;
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
    }

    public Location getLocationByUser(String userId) {
        if (userId == null) return null;
        try(Jedis jedis = redisConnect.getConnection()) {
            jedis.select(config.getInt("redis.database.userLocationStore.id", 1));
            Map<String, String> locationMap = jedis.hgetAll(userId);
            if (locationMap.isEmpty()) {
                Location location = fetchUserLocation(userId);
                if(location != null) {
                    addToCache(userId, location);
                } else {
                    addToCache(userId, new Location("", "", "", "", "", ""));
                }
                return location;
            } else {
                return new Location(null, null, null, locationMap.get("state"), null, locationMap.get("district"));
            }
        } catch (JedisException ex) {
            LOGGER.error("", "getLocationByUser: Unable to get a resource from the redis connection pool. userId: " + userId, ex);
            return null;
        }
    }

    public Location fetchUserLocation(String userId) {
        List<Row> rows;
        Location location = new Location();
        List<String> locationIds = null;

        if (userId == null) return null;
        try {
            String locationQuery = QueryBuilder.select("locationids")
                    .from(cassandra_db, cassandra_user_table)
                    .where(QueryBuilder.eq("id", userId))
                    .toString();
            rows = cassandraConnection.execute(locationQuery);
            if (rows.size() > 0) {
                Row row = rows.get(0);
                locationIds = row.getList("locationids", String.class);
            }

            if (locationIds != null && !locationIds.isEmpty()) {
                String resolveLocation = QueryBuilder.select().all()
                        .from(cassandra_db, cassandra_location_table)
                        .where(QueryBuilder.in("id", locationIds))
                        .toString();
                rows = cassandraConnection.execute(resolveLocation);
                if (rows.size() > 0) {
                    rows.forEach(record -> {
                        String name = record.getString("name");
                        String type = record.getString("type");
                        if (type.toLowerCase().equals("state")) {
                            location.setState(name);
                        } else if (type.toLowerCase().equals("district")) {
                            location.setDistrict(name);
                        }
                    });
                }
                return location;
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error("", "fetchUserLocation: Unable to fetch user location from cassandra! userId: " + userId, ex);
        }
        return null;
    }

    private void addToCache(String userId, Location location) {
        try(Jedis jedis = redisConnect.getConnection()) {
            jedis.select(config.getInt("redis.database.userLocationStore.id", 1));
            // Key will be userId
            String key = userId;
            Map<String, String> values = new HashMap<>();
            values.put("state", Location.getValueOrDefault(location.getState(), ""));
            values.put("district", Location.getValueOrDefault(location.getDistrict(), ""));
            jedis.hmset(key, values);
            jedis.expire(key, locationDbKeyExpiryTimeInSeconds);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get connection from the redis connection pool. userId: " + userId, ex);
        }
    }
}

