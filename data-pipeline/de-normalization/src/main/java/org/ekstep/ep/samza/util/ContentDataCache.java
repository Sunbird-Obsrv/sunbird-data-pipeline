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
            Map<String, Object> parsedData = null;
            Map contentMap = new HashMap();
            jedis.select(contentDBIndex);
            String contentNode = jedis.get(contentId);
            if (contentNode == null) {
                return null;
            } else {
                Type type = new TypeToken<Map<String, Object>>() {
                }.getType();
                parsedData = gson.fromJson(contentNode, type);
                parsedData.keySet().retainAll(fieldsList);
                for (Map.Entry<String, Object> entry : parsedData.entrySet()) {
                    contentMap.put(entry.getKey().toLowerCase(), entry.getValue());
                }
                return contentMap;
            }
        } catch (JedisException ex) {
            LOGGER.error("", "GetDataForContentId: Unable to get a resource from the redis connection pool ", ex);
            return null;
        }

    }
}
