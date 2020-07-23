package org.ekstep.ep.samza.task.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class RedisConnectTest {
	
	private RedisServer redisServer;
	private Config configMock;
	private RedisConnect redisConnect;
	
	@Before
    public void setUp() throws IOException {
		redisServer = new RedisServer(6379);
		redisServer.start();
		configMock = Mockito.mock(Config.class);
		when(configMock.get("redis.host", "localhost")).thenReturn("localhost");
		when(configMock.getInt("redis.port", 6379)).thenReturn(6379);
		when(configMock.getInt("redis.connection.max", 2)).thenReturn(2);
		when(configMock.getInt("redis.connection.idle.max", 2)).thenReturn(2);
		when(configMock.getInt("redis.connection.idle.min", 1)).thenReturn(1);
		when(configMock.getInt("redis.connection.minEvictableIdleTimeSeconds", 120)).thenReturn(120);
		when(configMock.getInt("redis.connection.timeBetweenEvictionRunsSeconds", 300)).thenReturn(300);
		redisConnect = new RedisConnect(configMock);
	}
	
	@After
    public void tearDown() {
		redisServer.stop();
	}
	
	@Test
    public void shouldVerifyRedisConnect() {
		Jedis jedis = redisConnect.getConnection(0);
		assertEquals(Long.valueOf(0), jedis.getDB());
		assertNotNull(jedis);
		jedis.close();
		
		jedis = redisConnect.getConnection(1, 0);
		assertNotNull(jedis);
		assertEquals(Long.valueOf(1), jedis.getDB());
		jedis.set("key", "value");
		assertEquals("value", jedis.get("key"));
		jedis.close();
		
	}

}
