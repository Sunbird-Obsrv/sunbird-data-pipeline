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

public class ContentDataCache {

    private static Logger LOGGER = new Logger(ContentDataCache.class);

    private RedisConnect redisConnect;
    private Integer contentDBIndex;
    private List fieldsList;

    public ContentDataCache(Config config, RedisConnect redisConnect) {

        List defaultList = new ArrayList<String>();
        defaultList.add("name");
        defaultList.add("objectType");
        defaultList.add("contentType");
        defaultList.add("mediaType");
        defaultList.add("language");
        defaultList.add("medium");
        defaultList.add("mimeType");
        defaultList.add("framework");
        defaultList.add("board");
        defaultList.add("status");
        defaultList.add("pkgVersion");
        defaultList.add("lastSubmittedOn");
        defaultList.add("lastUpdatedOn");
        defaultList.add("lastPublishedOn");
        this.contentDBIndex = config.getInt("redis.contentDB.index", 2);
        this.fieldsList = config.getList("content.fields", defaultList);
        this.redisConnect = redisConnect;
    }

    public Map getDataForContentId(String contentId) {

        try (Jedis jedis = redisConnect.getConnection()) {
            Gson gson = new Gson();
            Map contentMap = new HashMap();
            jedis.select(contentDBIndex);
            Map<String, String> fields = jedis.hgetAll(contentId);
            if (fields.isEmpty()) {
                return null;
            } else {
                fields.keySet().retainAll(fieldsList);
                for (Map.Entry<String, String> entry : fields.entrySet())
                {
                    contentMap.put(entry.getKey().toLowerCase(), gson.fromJson(entry.getValue(), Object.class));
                }
                return contentMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForContentId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
