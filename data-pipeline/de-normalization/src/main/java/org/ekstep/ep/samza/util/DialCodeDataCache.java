package org.ekstep.ep.samza.util;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialCodeDataCache {

    private static Logger LOGGER = new Logger(DialCodeDataCache.class);

    private RedisConnect redisConnect;
    private Integer dialcodeDB;
    private List fieldsList;

    public DialCodeDataCache(Config config, RedisConnect redisConnect) {

        List defaultList = new ArrayList<String>();
        defaultList.add("identifier");
        defaultList.add("channel");
        defaultList.add("batchcode");
        defaultList.add("publisher");
        defaultList.add("generated_on");
        defaultList.add("published_on");
        defaultList.add("status");
        this.dialcodeDB = config.getInt("redis.dialcodeDB.index", 3);
        this.redisConnect = redisConnect;
        this.fieldsList = defaultList;
    }

    public List<Map> getDataForDialCodes(List<String> dialcodes) {
        List<Map> list = new ArrayList<>();
        for (String entry : dialcodes) {
            Map data = getDataForDialCode(entry);
            if(data != null && !data.isEmpty()) {
                list.add(data);
            }
        }
        return list;
    }

    public Map getDataForDialCode(String dialcode) {

        try (Jedis jedis = redisConnect.getConnection()) {
            Gson gson = new Gson();
            Map dialcodeMap = new HashMap();
            jedis.select(dialcodeDB);
            Map<String, String> fields = jedis.hgetAll(dialcode);
            if (fields.isEmpty()) {
                return null;
            } else {
                fields.keySet().retainAll(fieldsList);
                for (Map.Entry<String, String> entry : fields.entrySet())
                {
                    dialcodeMap.put(entry.getKey().toLowerCase().replace("_", ""), gson.fromJson(entry.getValue(), Object.class));
                }
                return dialcodeMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForDialCode: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
