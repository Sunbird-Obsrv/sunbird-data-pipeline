package org.ekstep.ep.samza.task;

import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import com.datastax.driver.core.querybuilder.Clause;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.BaseCacheUpdaterService;
import org.ekstep.ep.samza.util.CassandraConnect;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        new BaseCacheUpdaterService(redisConnectMock);
        baseUpdater = new BaseCacheUpdaterService(redisConnectMock, cassandraConnectMock);

        stub(redisConnectMock.getConnection(anyInt())).toReturn(jedisMock);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
    }

    @Test
    public void shouldAddToCache() {
        baseUpdater.addToCache("4569876545678", "{\"role\":\"student\",\"type\":\"User\"}", storeId);
        String value = jedisMock.get("4569876545678");
        Map<String, Object> parsedData = gson.fromJson(value, type);

        assertEquals("User", parsedData.get("type"));
        assertEquals("student", parsedData.get("role"));

    }

    @Test
    public void shouldReadFromCache() {
        jedisMock.select(storeId);
        jedisMock.set("4569876545678", "{\"role\":\"teacher\",\"type\":\"Request\"}");
        String value = baseUpdater.readFromCache("4569876545678", storeId);
        Map<String, Object> parsedData = gson.fromJson(value, type);

        assertEquals("Request", parsedData.get("type"));
        assertEquals("teacher", parsedData.get("role"));
    }

    @Test(expected = DriverException.class)
    public void shouldHandleCassandraException() throws Exception {
        Clause userDataClause = QueryBuilder.eq("id", "id");

        when(cassandraConnectMock.find(anyString())).thenThrow(new DriverException("Cassandra Exception"));
        baseUpdater.readFromCassandra("sunbird", "user", userDataClause);
    }

    @Test(expected = DriverException.class)
    public void shouldHandleCassandraExceptionForLocationQuery() throws Exception {
        List<String> locationIds = new ArrayList<>();
        locationIds.add("1f56a8458d78df90");
        Clause locationDataClause = QueryBuilder.in("id", locationIds);

        when(cassandraConnectMock.find(anyString())).thenThrow(new DriverException("Cassandra Exception"));
        baseUpdater.readFromCassandra("sunbird", "location", locationDataClause);
    }

    @Test
    public void shouldHandleNullOrEmptyClause() {
        List<Row> emptyClauseValue = baseUpdater.readFromCassandra("sunbird", "user", QueryBuilder.eq("", ""));
        assertEquals(true, emptyClauseValue.isEmpty());
    }

    @Test
    public void shouldGetLocationDetailsFromDB() {
        List<String> locationIds = new ArrayList<>();
        locationIds.add("1f56a8458d78df90");
        ArrayList location = new ArrayList(3);
        location.add(0, "state-name");
        location.add(1, "district-name");
        Clause locationDataClause = QueryBuilder.in("id", locationIds);
        stub(baseUpdater.readFromCassandra("sunbird", "location", locationDataClause)).toReturn(location);

        List<Row> row = baseUpdater.readFromCassandra("sunbird", "location", locationDataClause);
        assertEquals("state-name", row.get(0));
        assertEquals("district-name", row.get(1));
    }

    @Test
    public void shouldUpdateToCassandra() {
        Map<String, String> map = new HashMap<>();
        map.put("device_id", "5975394fjh9543978987593");
        ArrayList location = new ArrayList(3);
        location.add(0, "device_id");
        baseUpdater.updateToCassandra("sunbird", "location", map);
        Clause locationDataClause = QueryBuilder.in("id", map);
        stub(baseUpdater.readFromCassandra("sunbird", "location", locationDataClause)).toReturn(location);
        List<Row> row = baseUpdater.readFromCassandra("sunbird", "location", locationDataClause);
        Assert.assertNotNull(row.get(0));
    }
}
