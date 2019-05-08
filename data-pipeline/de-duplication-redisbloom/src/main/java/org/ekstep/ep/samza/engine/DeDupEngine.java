package org.ekstep.ep.samza.engine;

import io.rebloom.client.Client;
import org.ekstep.ep.samza.util.RedisConnect;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Date;

public class DeDupEngine {


    private Client redisBloomClient;


    public DeDupEngine(Client redisBloomClient) {
        this.redisBloomClient = redisBloomClient;
    }

    public boolean isUniqueEvent(String checksum, int store) throws JedisException {
        return !redisBloomClient.exists("Dedup", checksum);
    }

    public void storeChecksum(String checksum, int store, int expirySeconds) throws JedisException {
        redisBloomClient.add("Dedup", checksum);
    }
}
