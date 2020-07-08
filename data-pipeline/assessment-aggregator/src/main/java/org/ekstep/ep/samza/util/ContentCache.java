package org.ekstep.ep.samza.util;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.core.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ContentCache {

	private static Logger LOGGER = new Logger(ContentCache.class);

	private RedisConnect redisConnect;
	private int databaseIndex;
	private Jedis redisConnection;

	public ContentCache(Config config) {
		this.databaseIndex = config.getInt("redis.courseDB.index", 11);
		this.redisConnect = new RedisConnect(config);
		this.redisConnection = this.redisConnect.getConnection(databaseIndex);
	}

	public List<String> getData(String key) throws Exception {
		try {
			Set<String> set = redisConnection.smembers(key);
			List<String> list = new ArrayList<String>(set);
			return list;
		} catch (JedisException e) {
			LOGGER.error("ERR_GET_CACHE_DATA", "Unable to fetch data from redis cache for key : " + key, e);
			e.printStackTrace();
			throw e;
		}
	}

	public void saveData(String key, List<String> values, Integer ttl) {
		try {
			redisConnection.del(key);
			for (String val : values) {
				redisConnection.sadd(key, val);
			}
			if (ttl > 0) redisConnection.expire(key, ttl);
		} catch (JedisException e) {
			LOGGER.error("ERR_SAVE_CACHE_DATA", "Unable to save data into redis cache for key : " + key + " | values : " + values, e);
			e.printStackTrace();
			throw e;
		}
	}
}
