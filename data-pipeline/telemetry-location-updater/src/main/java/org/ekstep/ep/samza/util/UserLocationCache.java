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
    private Config config;
    private RedisConnect redisConnect;
    private CassandraConnect cassandraConnection;
    private int locationDbKeyExpiryTimeInSeconds;

    public UserLocationCache(Config config, RedisConnect redisConnect, CassandraConnect cassandraConnect) {
        this.redisConnect = redisConnect;
        this.config = config;
        this.cassandra_db = config.get("middleware.cassandra.keyspace", "sunbird");
        this.cassandra_user_table = config.get("middleware.cassandra.user_table", "user");
        this.cassandra_location_table = config.get("middleware.cassandra.location_table", "location");
        this.cassandraConnection = cassandraConnect;
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
    }

    public Location getLocationByUser(String userId) {
        if (userId == null) return null;

        try (Jedis jedis = redisConnect.getConnection()) {
            Map<String, String> locationMap = jedis.hgetAll(userId);
            if (locationMap.isEmpty()) {
                return fetchUserLocation(userId);
            } else {
                return new Location(null, null, null, locationMap.get("state"), null, locationMap.get("district"));
            }
        } catch (JedisException ex) {
            LOGGER.error("", "getLocationByUser: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }
    }

    public Location fetchUserLocation(String userId) {
        List<Row> rows;
        Location location = new Location();
        List<String> locationIds = null;

        if (userId == null) return null;
        try {
            String selectLocationIds = QueryBuilder.select("locationids")
                    .from(cassandra_db, cassandra_user_table)
                    .where(QueryBuilder.eq("id", userId))
                    .getQueryString();
            rows = cassandraConnection.execute(selectLocationIds);
            if (rows.size() > 0) {
                Row row = rows.get(0);
                locationIds = row.getList("locationids", String.class);
            }

            if (locationIds != null && !locationIds.isEmpty()) {
                String selectLocations = QueryBuilder.select().all()
                        .from(cassandra_db, cassandra_location_table)
                        .where(QueryBuilder.in("id", locationIds)).getQueryString();
                rows = cassandraConnection.execute(selectLocations);
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
                addToCache(userId, location);
                return location;
            } else {
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error("", "fetchUserLocation: Unable to fetch user location from cassandra!", ex);
        }
        return null;
    }

    private void addToCache(String userId, Location location) {
        try (Jedis jedis = redisConnect.getConnection()) {
            if(location.isStateDistrictResolved()) {
                // Key will be userId
                String key = userId;
                Map<String, String> values = new HashMap<>();
                values.put("state", location.getState());
                values.put("district", location.getCity());
                jedis.hmset(key, values);
                jedis.expire(key, locationDbKeyExpiryTimeInSeconds);
            }
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get a resource from the redis connection pool ", ex);
        }
    }
}

