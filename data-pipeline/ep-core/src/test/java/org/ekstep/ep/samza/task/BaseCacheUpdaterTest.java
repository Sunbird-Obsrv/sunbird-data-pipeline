package org.ekstep.ep.samza.task;

import org.ekstep.ep.samza.util.RedisConnect;
import org.ekstep.ep.samza.util.BaseCacheUpdater;
import org.apache.samza.config.Config;
import redis.clients.jedis.Jedis;
import com.fiftyonred.mock_jedis.MockJedis;
import java.lang.reflect.Type;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.stub;


public class BaseCacheUpdaterTest {

    private RedisConnect redisConnectMock;
    private BaseCacheUpdater baseCacheUpdater;
    private Jedis jedisMock = new MockJedis("test");
    private static int storeId = 5;
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        baseCacheUpdater = new BaseCacheUpdater(redisConnectMock);

        stub(redisConnectMock.getConnection(anyInt())).toReturn(jedisMock);
    }

    @Test
    public void shouldAddToCache() {
        baseCacheUpdater.addToCache("4569876545678","{\"role\":\"student\",\"type\":\"User\"}", storeId);
        String value = jedisMock.get("4569876545678");
        Map<String, Object> parsedData = gson.fromJson(value, type);

        assertEquals("User", parsedData.get("type"));
        assertEquals("student", parsedData.get("role"));

    }

    @Test
    public void shouldReadFromCache() {
        jedisMock.set("4569876545678","{\"role\":\"teacher\",\"type\":\"Request\"}");
        String value = baseCacheUpdater.readFromCache("4569876545678", storeId);
        Map<String, Object> parsedData = gson.fromJson(value, type);

        assertEquals("Request", parsedData.get("type"));
        assertEquals("teacher", parsedData.get("role"));
    }

}
