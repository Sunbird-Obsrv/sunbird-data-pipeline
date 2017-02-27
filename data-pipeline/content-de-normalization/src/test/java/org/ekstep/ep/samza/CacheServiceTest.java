package org.ekstep.ep.samza;

import org.apache.samza.storage.kv.KeyValueStore;
import org.ekstep.ep.samza.external.SearchServiceClient;
import org.ekstep.ep.samza.fixture.ContentFixture;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.google.gson.Gson;

public class CacheServiceTest {

    private SearchServiceClient searchServiceMock;
    private long cacheTTL;
    private KeyValueStore contentStoreMock;
    private ContentCache contentCache;

    @Before
    public void setUp(){
        searchServiceMock = mock(SearchServiceClient.class);
        contentStoreMock = mock(KeyValueStore.class);
        contentCache = mock(ContentCache.class);
        cacheTTL = 6000;
    }

    @Test
    public void shouldProcessEventsFromCacheIfPresent() throws IOException {

        stub(contentStoreMock.get(getContentID())).toReturn(getContentCacheJson());

        CacheService cacheService = new CacheService(searchServiceMock, contentStoreMock, cacheTTL);
        Content content = cacheService.deNormalizeContent("someid", getContentID());

        assertEquals(content.name(), ContentFixture.getContentMap().get("name"));
        assertEquals(content.description(), ContentFixture.getContentMap().get("description"));
        assertEquals(content.ageGroup(), ContentFixture.getContentMap().get("ageGroup"));
        assertEquals(content.mediaType(), ContentFixture.getContentMap().get("mediaType"));
        assertEquals(content.contentType(), ContentFixture.getContentMap().get("contentType"));
        assertEquals(content.language(), ContentFixture.getContentMap().get("language"));
        assertEquals(content.owner(), ContentFixture.getContentMap().get("owner"));
        assertEquals(content.lastUpdatedOn(), ContentFixture.getContentMap().get("lastUpdatedOn"));
    }

    @Test
    public void shouldCallSearchServiceIfContentIsNotPresentInCache() throws IOException {
        stub(contentStoreMock.get(getContentID())).toReturn(null);
        stub(searchServiceMock.search(getContentID())).toReturn(ContentFixture.getContent());

        CacheService cacheService = new CacheService(searchServiceMock, contentStoreMock, cacheTTL);
        cacheService.deNormalizeContent("someid", getContentID());

        verify(contentStoreMock, times(1)).get(getContentID());
        verify(contentStoreMock, times(1)).put(anyString(), anyString());
        verify(searchServiceMock, times(1)).search(getContentID());
    }

    @Test
    public void shouldCallSearchServiceAndUpdateCacheIfCacheIsExpired() throws IOException {

        ContentCache contentCache = new ContentCache(ContentFixture.getContent(), new Date().getTime() - 10000);
        String contentCacheJson = new Gson().toJson(contentCache, ContentCache.class);

        stub(contentStoreMock.get(getContentID())).toReturn(contentCacheJson);
        stub(searchServiceMock.search(getContentID())).toReturn(ContentFixture.getContent());

        CacheService cacheService = new CacheService(searchServiceMock, contentStoreMock, cacheTTL);
        Content content = cacheService.deNormalizeContent("someid", getContentID());

        verify(contentStoreMock, times(1)).get(getContentID());
        verify(contentStoreMock, times(1)).put(anyString(), anyString());
        verify(searchServiceMock, times(1)).search(getContentID());

        assertEquals(content.name(), ContentFixture.getContentMap().get("name"));
        assertEquals(content.description(), ContentFixture.getContentMap().get("description"));
    }

    @Test
    public void shouldNotCallSearchServiceIfCacheIsNotExpired() throws IOException {

        ContentCache contentCache = new ContentCache(ContentFixture.getContent(), new Date().getTime() + 10000);
        String contentCacheJson = new Gson().toJson(contentCache, ContentCache.class);

        stub(contentStoreMock.get(getContentID())).toReturn(contentCacheJson);
        stub(searchServiceMock.search(getContentID())).toReturn(ContentFixture.getContent());
        CacheService cacheService = new CacheService(searchServiceMock, contentStoreMock, cacheTTL);
        Content content = cacheService.deNormalizeContent("someid", getContentID());

        verify(contentStoreMock, times(0)).put(anyString(), anyString());
        verify(searchServiceMock, times(0)).search(getContentID());

        assertEquals(content.name(), ContentFixture.getContentMap().get("name"));
        assertEquals(content.description(), ContentFixture.getContentMap().get("description"));
    }

    private String getContentID() {
        return ContentFixture.getContentID();
    }

    private String getContentCacheJson() {
        Content content = ContentFixture.getContent();
        ContentCache contentCache = new ContentCache(content, new Date().getTime());
        return new Gson().toJson(contentCache,ContentCache.class);
    }
}