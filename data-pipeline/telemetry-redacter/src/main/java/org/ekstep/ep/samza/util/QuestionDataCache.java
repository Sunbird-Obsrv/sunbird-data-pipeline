package org.ekstep.ep.samza.util;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

public class QuestionDataCache {

  private static Logger LOGGER = new Logger(QuestionDataCache.class);

  private RedisConnect redisPool;
  private Jedis redisConnection;
  @SuppressWarnings("serial")
  private Type mapType = new TypeToken<Map<String, Object>>() {
  }.getType();
  private Gson gson = new Gson();
  private int databaseIndex;

  public QuestionDataCache(Config config, RedisConnect redisConnect) {

    this.databaseIndex = config.getInt("redis.contentDB.index", 5);
    this.redisPool = redisConnect;
    this.redisConnection = this.redisPool.getConnection(databaseIndex);
  }

  private Map<String, Object> getDataFromCache(String key) {
    Map<String, Object> cacheData = new HashMap<>();
    String data = redisConnection.get(key);
    if (data != null && !data.isEmpty()) {
      cacheData = gson.fromJson(data, mapType);
    }
    return cacheData;
  }

  public Map<String, Object> getData(String key) {
    Map<String, Object> cacheDataMap;
    try {
      cacheDataMap = getDataFromCache(key);
    } catch (JedisException ex) {
      LOGGER.error("", "Exception when retrieving data from redis cache ", ex);
      this.redisConnection.close();
      this.redisConnection.close();
      this.redisConnection = redisPool.getConnection(databaseIndex);
      cacheDataMap = getDataFromCache(key);
    }
    return cacheDataMap;
  }
}
