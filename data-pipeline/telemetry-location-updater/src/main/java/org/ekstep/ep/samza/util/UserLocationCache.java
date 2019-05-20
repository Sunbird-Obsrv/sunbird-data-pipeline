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
    private CassandraConnect cassandraConnection;
    private Jedis redisConnection;
    private int locationDbKeyExpiryTimeInSeconds;

    public UserLocationCache(Config config, Jedis redisConnection, CassandraConnect cassandraConnect) {
        this.redisConnection = redisConnection;
        this.cassandra_db = config.get("middleware.cassandra.keyspace", "sunbird");
        this.cassandra_user_table = config.get("middleware.cassandra.user_table", "user");
        this.cassandra_location_table = config.get("middleware.cassandra.location_table", "location");
        this.cassandraConnection = cassandraConnect;
        this.locationDbKeyExpiryTimeInSeconds = config.getInt("location.db.redis.key.expiry.seconds", 86400);
        this.redisConnection.select(config.getInt("redis.database.userLocationStore.id", 1));
    }

    public Location getLocationByUser(String userId) {
        if (userId == null) return null;
        try {
            Map<String, String> locationMap = redisConnection.hgetAll(userId);
            if (!locationMap.isEmpty()) {
                return new Location(null, null, null,
                        locationMap.get("state"), null, locationMap.get("district"));
            } else {
                return fetchFallbackUserLocation(userId);
            }
        } catch (JedisException ex) {
            LOGGER.error("", "getLocationByUser: Unable to get a resource from the redis connection pool. userId: " + userId, ex);
            return null;
        }
    }

    public Location fetchFallbackUserLocation(String userId) {
        if (userId == null) return null;
        try {
            List<String> locationIds = getUserOrgLocationIds(userId);
            if (locationIds != null && !locationIds.isEmpty()) {
                Location location = getUserLocation(locationIds);
                addToCache(userId, location);
                return location;
            } else {
                addToCache(userId, new Location("", "", "", "", "", ""));
                return null;
            }
        } catch (Exception ex) {
            LOGGER.error("", "fetchFallbackUserLocation: Unable to fetch user location " +
                    "from cassandra! userId: " + userId, ex);
        }
        return null;
    }

    private List<String> getUserOrgLocationIds(String userId) {
        List<String> locationIds = null;
        String locationQuery = QueryBuilder.select("locationids")
                .from(cassandra_db, cassandra_user_table)
                .where(QueryBuilder.eq("id", userId))
                .toString();
        List<Row> rows = cassandraConnection.execute(locationQuery);
        if (rows.size() > 0) {
            Row row = rows.get(0);
            locationIds = row.getList("locationids", String.class);
        }
        return locationIds;
    }

    private Location getUserLocation(List<String> locationIds) {
        String resolveLocation = QueryBuilder.select().all()
                .from(cassandra_db, cassandra_location_table)
                .where(QueryBuilder.in("id", locationIds))
                .toString();
        List<Row> rows = cassandraConnection.execute(resolveLocation);
        Location location = new Location();
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
    }

    private void addToCache(String userId, Location location) {
        try {
            Map<String, String> values = new HashMap<>();
            values.put("state", Location.getValueOrDefault(location.getState(), ""));
            values.put("district", Location.getValueOrDefault(location.getDistrict(), ""));
            redisConnection.hmset(userId, values);
            redisConnection.expire(userId, locationDbKeyExpiryTimeInSeconds);
        } catch (JedisException ex) {
            LOGGER.error("", "AddLocationToCache: Unable to get connection from the " +
                    "redis connection pool. userId: " + userId, ex);
        }
    }
}

