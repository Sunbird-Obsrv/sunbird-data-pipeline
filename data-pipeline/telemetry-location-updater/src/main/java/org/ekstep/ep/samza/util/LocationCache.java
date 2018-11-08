package org.ekstep.ep.samza.util;

import com.datastax.driver.core.Row;
import org.ekstep.ep.samza.domain.Location;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

public class LocationCache {
    Jedis jedis = new Jedis();
    CassandraConnect cc = new CassandraConnect();

    public Location getLoc(String did) {

        Map<String, String> fields = jedis.hgetAll(did);
        List<Row> rows = null;
        if(fields.isEmpty()) {
            rows = cc.execute("select device_id, state, district from local_device_db.device_profile where device_id="+"'"+did+"'");
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
