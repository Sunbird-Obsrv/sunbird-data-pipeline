package org.ekstep.ep.samza.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.lang.reflect.Type;
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
            if (data != null && !data.isEmpty()) {
                list.add(data);
            }
        }
        return list;
    }

    public Map getDataForDialCode(String dialcode) {

        try (Jedis jedis = redisConnect.getConnection()) {
            Gson gson = new Gson();
            Map<String, Object> parsedData = null;
            Map dialcodeMap = new HashMap();
            jedis.select(dialcodeDB);

            String fields = jedis.get(dialcode);
            if (fields == null) {
                return null;
            } else {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                parsedData = gson.fromJson(fields, type);
                parsedData.keySet().retainAll(fieldsList);
                for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                    dialcodeMap.put(entry.getKey().toLowerCase().replace("_", ""), entry.getValue());
                }
                return dialcodeMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForDialCode: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
