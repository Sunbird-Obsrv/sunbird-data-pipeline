package org.ekstep.ep.samza.domain;

import com.fiftyonred.mock_jedis.MockJedis;
import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.ekstep.ep.samza.util.ContentDataCache;
import org.ekstep.ep.samza.util.DataCache;
import org.ekstep.ep.samza.util.RedisConnect;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.stub;

public class ContentDataUpdaterTest {

    private Config configMock;
    private RedisConnect redisConnectMock;
    private ContentDataUpdater contentDataUpdater;
    private ContentDataCache dataCacheMock;
    private Jedis jedisMock;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {

        jedisMock = new MockJedis("test");
        configMock = mock(Config.class);
        redisConnectMock = mock(RedisConnect.class);
        jedisMock.flushAll();
    }

    @Test
    public void shouldUpdateEventWithConvertedTimeFields() throws Exception {
        Map<String, Object> data = new HashMap();
        data.put("lastupdatedon", "2019-02-12T11:35:35.919+0000");
        data.put("lastsubmittedon", "2019-02-12T11:35:35.919+0000");
        data.put("lastpublishedon", "2019-02-12T11:35:35.919");
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        dataCacheMock = mock(ContentDataCache.class);
        contentDataUpdater = new ContentDataUpdater(dataCacheMock);
        stub(dataCacheMock.getData("test-content-1")).toReturn(data);

        Map objectMap = new HashMap(); objectMap.put("id", "test-content-1"); objectMap.put("ver", "1.0");
        objectMap.put("type", "Content");
        Map eventMap = new HashMap();
        eventMap.put("object", objectMap);
        Event input = new Event(eventMap);
        String output = contentDataUpdater.update(input, "test-content-1", true).toString();
        assertTrue(output.contains("lastsubmittedon=1549971335919"));
        assertTrue(output.contains("lastpublishedon=1549971335919"));
        assertTrue(output.contains("lastupdatedon=1549971335919"));
    }

    @Test
    public void shouldUpdateEventWithOutConvertion() throws Exception {
        Map<String, Object> data = new HashMap();
        data.put("lastupdatedon", 1549971335919L);
        data.put("lastsubmittedon", 1549971335919L);
        data.put("lastpublishedon", 1.549971335919E12);
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        dataCacheMock = mock(ContentDataCache.class);
        contentDataUpdater = new ContentDataUpdater(dataCacheMock);
        stub(dataCacheMock.getData("test-content-1")).toReturn(data);

        Map objectMap = new HashMap(); objectMap.put("id", "test-content-1"); objectMap.put("ver", "1.0");
        objectMap.put("type", "Content");
        Map eventMap = new HashMap();
        eventMap.put("object", objectMap);
        Event input = new Event(eventMap);
        String output = contentDataUpdater.update(input, "test-content-1", true).toString();
        assertTrue(output.contains("lastsubmittedon=1549971335919"));
        assertTrue(output.contains("lastpublishedon=1.549971335919E12"));
        assertTrue(output.contains("lastupdatedon=1549971335919"));
    }

    @Test
    public void shouldUpdateEventWith0ForIncorrectDateFormat() throws Exception {
        Map<String, Object> data = new HashMap();
        data.put("lastupdatedon", "2019-02-12");
        data.put("lastsubmittedon", "2019-02-12");
        data.put("lastpublishedon", "2019-02-12");
        stub(redisConnectMock.getConnection()).toReturn(jedisMock);
        dataCacheMock = mock(ContentDataCache.class);
        contentDataUpdater = new ContentDataUpdater(dataCacheMock);
        stub(dataCacheMock.getData("test-content-1")).toReturn(data);

        Map objectMap = new HashMap(); objectMap.put("id", "test-content-1"); objectMap.put("ver", "1.0");
        objectMap.put("type", "Content");
        Map eventMap = new HashMap();
        eventMap.put("object", objectMap);
        Event input = new Event(eventMap);
        String output = contentDataUpdater.update(input, "test-content-1", true).toString();
        assertTrue(output.contains("lastsubmittedon=0"));
        assertTrue(output.contains("lastpublishedon=0"));
        assertTrue(output.contains("lastupdatedon=0"));
    }


}
