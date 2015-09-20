package org.ekstep.ep.samza.system;

import org.ekstep.ep.samza.service.Fetchable;
import org.ekstep.ep.samza.service.TaxonomyService;
import org.junit.Before;
import org.junit.Test;
import org.apache.samza.storage.kv.KeyValueStore;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static org.mockito.Mockito.*;

public class TaxonomyCacheTest {

    private TaxonomyCache taxonomyCache;
    private KeyValueStore mockStore;
    private Clock mockClock;
    private Date mockDate;
    private Fetchable mockService;

    final String KEY = "KEY";
    final String VALUE = "VALUE";
    final Long STARTTIME = 30L;
    final Long TTL = 1000L;
    final Long DELAY = 1L;

    @Before
    public void setup(){
        mockStore = Mockito.mock(KeyValueStore.class);
        mockClock = Mockito.mock(Clock.class);
        mockDate = Mockito.mock(Date.class);
        mockService = Mockito.mock(TaxonomyService.class);
        Mockito.when(mockDate.getTime()).thenReturn(STARTTIME);
        Mockito.when(mockClock.getDate()).thenReturn(mockDate);
        taxonomyCache = new TaxonomyCache(mockStore,mockClock);
    }

    @Test
    public void ShouldReturnNullForUnsetKey() throws Exception{
        assertNull(taxonomyCache.get(KEY));
    }

    @Test
    public void ShouldPut() throws Exception{
        KeyValueStore storeSpy = Mockito.spy(KeyValueStore.class);
        taxonomyCache = new TaxonomyCache(storeSpy);
        taxonomyCache.put(KEY, VALUE);
        verify(storeSpy, times(1)).put(KEY, VALUE);
    }

    @Test
    public void ShouldClearCacheIfTTLIsExpired() throws Exception{
        KeyValueStore storeSpy = Mockito.spy(KeyValueStore.class);
        Mockito.when(storeSpy.get(KEY)).thenReturn(VALUE);
        taxonomyCache = new TaxonomyCache(storeSpy,mockClock);
        taxonomyCache.setTTL(TTL);
        taxonomyCache.put(KEY, VALUE);
        assertEquals(VALUE, taxonomyCache.get(KEY));
        Mockito.when(mockDate.getTime()).thenReturn(STARTTIME + TTL + DELAY);
        assertNull(taxonomyCache.get(KEY));
        verify(storeSpy, times(1)).delete(KEY);
    }

    @Test
    public void ShouldWarmCache(){
        HashMap<String,Object> map = new HashMap<String, Object>();
        map.put(KEY, VALUE);
        try{ Mockito.when(mockService.fetch()).thenReturn(map); } catch(java.io.IOException e){}
        taxonomyCache.setService(mockService);
        try{ taxonomyCache.warm(); } catch (java.io.IOException e){}
        verify(mockStore).put(eq(KEY), eq(VALUE));
    }
}
