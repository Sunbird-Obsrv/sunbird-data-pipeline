package org.ekstep.ep.samza.task;

import com.datastax.driver.core.exceptions.DriverException;
import org.ekstep.ep.samza.core.BaseCacheUpdaterService;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class BaseUpdaterTest {

    private RedisConnect redisConnectMock;
    private CassandraConnect cassandraConnectMock;
    private BaseCacheUpdaterService baseUpdater;
    private Jedis jedisMock = new MockJedis("test");
    private static int storeId = 5;
    Gson gson = new Gson();
    Type type = new TypeToken<Map<String, Object>>() {
    }.getType();

    @Before
    public void setUp() {
        redisConnectMock = mock(RedisConnect.class);
        cassandraConnectMock = mock(CassandraConnect.class);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        baseUpdater = new BaseCacheUpdaterService(redisConnectMock, cassandraConnectMock);

        stub(redisConnectMock.getConnection(anyInt())).toReturn(jedisMock);
    }

    @Test
    public void shouldAddToCache() {
        baseUpdater.addToCache("4569876545678","{\"role\":\"student\",\"type\":\"User\"}", storeId);
        String value = jedisMock.get("4569876545678");
        Map<String, Object> parsedData = gson.fromJson(value, type);

        assertEquals("User", parsedData.get("type"));
        assertEquals("student", parsedData.get("role"));

    }

    @Test
    public void shouldReadFromCache() {
        jedisMock.set("4569876545678","{\"role\":\"teacher\",\"type\":\"Request\"}");
        String value = baseUpdater.readFromCache("4569876545678", storeId);
        Map<String, Object> parsedData = gson.fromJson(value, type);

//        assertEquals("Request", parsedData.get("type"));
//        assertEquals("teacher", parsedData.get("role"));
    }

        @Test(expected = DriverException.class)
    public void shouldHandleCassandraException() throws Exception {

        when(cassandraConnectMock.find(anyString())).thenThrow(new DriverException("Cassandra Exception"));
        baseUpdater.readFromCassandra("sunbird","user","id","id");
    }

}
