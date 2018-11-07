package org.ekstep.ep.samza.util;

import org.ekstep.ep.samza.domain.Location;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class Cache {
    Jedis jedis = new Jedis();

    public Location getLoc(String did) {

        Map<String, String> fields = jedis.hgetAll(did);
        String state = fields.get("state");
        String district = fields.get("district");
        Location location = new Location();
        location.setDistrict(district);
        location.setState(state);
        return location;
    }

    public void putLocation(String did, String state, String district) {
        jedis.hset(did, "state", state);
        jedis.hset(did, "district", district);
    }
}
