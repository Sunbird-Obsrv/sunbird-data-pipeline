package org.ekstep.ep.samza.task;

import com.google.gson.Gson;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.metrics.MetricsRegistry;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.ekstep.ep.samza.cache.CacheEntry;
import org.ekstep.ep.samza.fixture.EventFixture;
import org.ekstep.ep.samza.fixture.ItemFixture;
import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.search.service.SearchServiceClient;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class ItemDeNormalizationTaskTest {

    private static final String SUCCESS_TOPIC = "telemetry.item.de_normalized";
    private static final String FAILED_TOPIC = "telemetry.item.de_normalized.fail";
    private static final String CONTENT_CACHE_TTL = "60000";
    private MessageCollector collectorMock;
    private SearchServiceClient searchServiceMock;
    private TaskContext contextMock;
    private MetricsRegistry metricsRegistry;
    private Counter counter;
    private TaskCoordinator coordinatorMock;
    private IncomingMessageEnvelope envelopeMock;
    private Config configMock;
    private ItemDeNormalizationTask itemDeNormalizationTask;
    private KeyValueStore itemStoreMock;

    @Before
    public void setUp() {
        collectorMock = mock(MessageCollector.class);
        searchServiceMock = mock(SearchServiceClient.class);
        contextMock = Mockito.mock(TaskContext.class);
        metricsRegistry = Mockito.mock(MetricsRegistry.class);
        counter = Mockito.mock(Counter.class);
        coordinatorMock = mock(TaskCoordinator.class);
        envelopeMock = mock(IncomingMessageEnvelope.class);
        configMock = Mockito.mock(Config.class);
        itemStoreMock = mock(KeyValueStore.class);

        stub(configMock.get("output.success.topic.name", SUCCESS_TOPIC)).toReturn(SUCCESS_TOPIC);
        stub(configMock.get("output.failed.topic.name", FAILED_TOPIC)).toReturn(FAILED_TOPIC);
        stub(configMock.get("item_id.overridden.events", "")).toReturn("me_item_usage_summary.item_id.field,me.item_id.field,oe.item_id.field");
        stub(configMock.get("me_item_usage_summary.item_id.field", "")).toReturn("dimensions.item_id");
        stub(configMock.get("me.item_id.field", "")).toReturn("edata.eks.itemId");
        stub(configMock.get("oe.item_id.field", "")).toReturn("edata.eks.qid");
        stub(configMock.containsKey("me_item_usage_summary.item_id.field")).toReturn(true);
        stub(configMock.containsKey("me.item_id.field")).toReturn(true);
        stub(configMock.containsKey("oe.item_id.field")).toReturn(true);
        stub(configMock.get("item.store.ttl", "60000")).toReturn(CONTENT_CACHE_TTL);
        stub(metricsRegistry.newCounter(anyString(), anyString()))
                .toReturn(counter);
        stub(contextMock.getMetricsRegistry()).toReturn(metricsRegistry);

        itemDeNormalizationTask = new ItemDeNormalizationTask(configMock, contextMock, searchServiceMock, itemStoreMock);
    }

    @Test
    public void shouldSkipIfItemIdIsBlank() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.EventWithoutItemId());
        itemDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldProcessEventFromCacheIfPresentAndSkipServiceCall() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.MeItemSummaryEvent());

        CacheEntry ItemCache = new CacheEntry(ItemFixture.getItem(), new Date().getTime());
        String itemCacheJson = new Gson().toJson(ItemCache, CacheEntry.class);

        stub(itemStoreMock.get(ItemFixture.getItemID())).toReturn(itemCacheJson);

        itemDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(itemStoreMock, times(1)).get(ItemFixture.getItemID());
        verify(searchServiceMock, times(0)).search(ItemFixture.getItemID());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldCallSearchApiAndUpdateCacheIfEventIsNotPresentInCache() throws Exception {
        stub(envelopeMock.getMessage()).toReturn(EventFixture.MeItemSummaryEvent());
        when(itemStoreMock.get(ItemFixture.getItemID())).thenReturn(null, getItemCacheJson());

        stub(searchServiceMock.searchItem(ItemFixture.getItemID())).toReturn(ItemFixture.getItem());

        itemDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(itemStoreMock, times(2)).get(ItemFixture.getItemID());
        verify(searchServiceMock, times(1)).searchItem(ItemFixture.getItemID());
        verify(itemStoreMock, times(1)).put(anyString(), anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldCallSearchApiAndUpdateCacheIfCacheIsExpired() throws Exception {

        CacheEntry contentCache = new CacheEntry(ItemFixture.getItem(), new Date().getTime() - 100000);
        String contentCacheJson = new Gson().toJson(contentCache, CacheEntry.class);

        stub(itemStoreMock.get(ItemFixture.getItemID())).toReturn(contentCacheJson);
        stub(envelopeMock.getMessage()).toReturn(EventFixture.MeItemSummaryEvent());
        stub(searchServiceMock.searchItem(ItemFixture.getItemID())).toReturn(ItemFixture.getItem());

        itemDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(itemStoreMock, times(2)).get(ItemFixture.getItemID());
        verify(searchServiceMock, times(1)).searchItem(ItemFixture.getItemID());
        verify(itemStoreMock, times(1)).put(anyString(), anyString());
        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }

    @Test
    public void shouldProcessAllOeEventsAndUpdateItemData() throws Exception {
        when(itemStoreMock.get(ItemFixture.getItemID())).thenReturn(null, getItemCacheJson());
        stub(envelopeMock.getMessage()).toReturn(EventFixture.OeAssessEvent());

        verifyEventHasBeenProcessed();
    }

    @Test
    public void shouldProcessAllMeItemSummaryEventsAndUpdateItemData() throws Exception {
        when(itemStoreMock.get(ItemFixture.getItemID())).thenReturn(null, getItemCacheJson());
        stub(envelopeMock.getMessage()).toReturn(EventFixture.MeItemSummaryEvent());

        verifyEventHasBeenProcessed();
    }

    @Test
    public void shouldProcessAllMeItemUsageSummaryEventsAndUpdateItemData() throws Exception {
        when(itemStoreMock.get(ItemFixture.getItemID())).thenReturn(null, getItemCacheJson());
        stub(envelopeMock.getMessage()).toReturn(EventFixture.MeItemUsageSummaryEvent());

        verifyEventHasBeenProcessed();
    }

    
    private ArgumentMatcher<OutgoingMessageEnvelope> validateOutputTopic(final Object message, final String stream) {
        return new ArgumentMatcher<OutgoingMessageEnvelope>() {
            @Override
            public boolean matches(Object o) {
                OutgoingMessageEnvelope outgoingMessageEnvelope = (OutgoingMessageEnvelope) o;
                SystemStream systemStream = outgoingMessageEnvelope.getSystemStream();
                assertEquals("kafka", systemStream.getSystem());
                assertEquals(stream, systemStream.getStream());
                assertEquals(message, outgoingMessageEnvelope.getMessage());
                return true;
            }
        };
    }

    private String getItemCacheJson() {
        Item item = ItemFixture.getItem();
        CacheEntry itemCache = new CacheEntry(item, new Date().getTime());
        return new Gson().toJson(itemCache, CacheEntry.class);
    }

    private void verifyEventHasBeenProcessed() throws Exception {
        stub(searchServiceMock.searchItem(ItemFixture.getItemID())).toReturn(ItemFixture.getItem());

        CacheEntry expiredContent = new CacheEntry(ItemFixture.getItem(), new Date().getTime() - 100000);
        CacheEntry validContent = new CacheEntry(ItemFixture.getItem(), new Date().getTime() + 100000);

        when(itemStoreMock.get(ItemFixture.getItemID()))
                .thenReturn(
                        new Gson().toJson(expiredContent, CacheEntry.class),
                        new Gson().toJson(validContent, CacheEntry.class));

        itemDeNormalizationTask.process(envelopeMock, collectorMock, coordinatorMock);

        verify(searchServiceMock, times(1)).searchItem("domain_4502");
        Map<String, Object> processedMessage = (Map<String, Object>) envelopeMock.getMessage();

        assertTrue(processedMessage.containsKey("itemdata"));

        HashMap<String, Object> itemData = (HashMap<String, Object>) processedMessage.get("itemdata");
        assertEquals(itemData.get("name"), ItemFixture.getItemMap().get("name"));
        assertEquals(itemData.get("title"), ItemFixture.getItemMap().get("title"));
        assertEquals(itemData.get("template"), ItemFixture.getItemMap().get("template"));
        assertEquals(itemData.get("type"), ItemFixture.getItemMap().get("type"));
        assertEquals(itemData.get("owner"), ItemFixture.getItemMap().get("owner"));
        assertEquals(itemData.get("qlevel"), ItemFixture.getItemMap().get("qlevel"));
        assertEquals(itemData.get("language"), ItemFixture.getItemMap().get("language"));
        assertEquals(itemData.get("keywords"), ItemFixture.getItemMap().get("keywords"));
        assertEquals(itemData.get("concepts"), ItemFixture.getItemMap().get("concepts"));
        assertEquals(itemData.get("gradeLevel"), ItemFixture.getItemMap().get("gradeLevel"));

        verify(collectorMock).send(argThat(validateOutputTopic(envelopeMock.getMessage(), SUCCESS_TOPIC)));
    }
}