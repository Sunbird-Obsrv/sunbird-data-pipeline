package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContentDataCache {

    private static Logger LOGGER = new Logger(ContentDataCache.class);

    private RedisConnect redisConnect;
    private Integer contentDBIndex;
    private List fieldsList;

    public ContentDataCache(Config config, RedisConnect redisConnect) {

        List defaultList = new ArrayList<String>();
        defaultList.add("name");
        defaultList.add("objectType");
        this.contentDBIndex = config.getInt("redis.contentDB.index", 2);
        this.fieldsList = config.getList("content.fields", defaultList);
        this.redisConnect = redisConnect;
    }

    public Map getDataForContentId(String contentId) {

        try (Jedis jedis = redisConnect.getConnection()) {
            jedis.select(contentDBIndex);
            Map fields = jedis.hgetAll(contentId);
            if (fields.isEmpty()) {
                return null;
            } else {
                fields.keySet().retainAll(fieldsList);
                return fields;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForContentId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
