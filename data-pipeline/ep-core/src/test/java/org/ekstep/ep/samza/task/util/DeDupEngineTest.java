package org.ekstep.ep.samza.task.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.samza.config.Config;
import org.ekstep.ep.samza.util.DeDupEngine;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import redis.clients.jedis.Jedis;
import redis.embedded.RedisServer;

public class DeDupEngineTest {
	
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
    public void shouldVerifyDedupEngine() {
		
		DeDupEngine engine = new DeDupEngine(redisConnect, 1, 100);
		
		engine.storeChecksum("1234");
		
		assertTrue(engine.isUniqueEvent("2345"));
		assertTrue(!engine.isUniqueEvent("1234"));
		
		Jedis jedis = engine.getRedisConnection();
		assertNotNull(jedis);
		assertEquals(Long.valueOf(1), jedis.getDB());
		
		redisServer.stop();
		try {
			engine.storeChecksum("1234");
		} catch(Exception ex) {
			assertNotNull(ex);
		}
		
		try {
			assertTrue(engine.isUniqueEvent("2345"));
		} catch(Exception ex) {
			assertNotNull(ex);
		} finally {
			redisServer.start();
			engine.storeChecksum("1234");
			assertTrue(engine.isUniqueEvent("2345"));
			assertTrue(!engine.isUniqueEvent("1234"));
		}
		
	}

}
