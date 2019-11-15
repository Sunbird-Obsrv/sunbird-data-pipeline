package org.ekstep.ep.samza.task;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;

public class RedisConnectTest {

    private RedisConnect redisConnectMock;
    private Config configMock;
    private Jedis jedisMock = new MockJedis("test");
    private int storeId;
    Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        configMock = mock(Config.class);
        storeId = 2;

        redisConnectMock = new RedisConnect(configMock);
    }

    @Test
    public void shouldAddJsonToCache() throws Exception {
        redisConnectMock.addJsonToCache("85098674245678","{\"type\": \"User\", \"role\":\"student\"}", jedisMock);
        Gson gson = new Gson();
        String value = jedisMock.get("85098674245678");
        Map<String, Object> parsedData = gson.fromJson(value, type);

        assertEquals("User", parsedData.get("type"));
        assertEquals("student", parsedData.get("role"));
    }
}
