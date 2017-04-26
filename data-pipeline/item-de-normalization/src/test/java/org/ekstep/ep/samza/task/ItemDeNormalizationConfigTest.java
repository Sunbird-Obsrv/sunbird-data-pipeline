package org.ekstep.ep.samza.task;

import org.apache.samza.config.Config;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.stub;
import static org.mockito.MockitoAnnotations.initMocks;

public class ItemDeNormalizationConfigTest {
    @Mock
    private Config samzaConfig;

    private ItemDeNormalizationConfig config;
    private static final String CONTENT_CACHE_TTL = "60000";

    @Before
    public void setUp() {
        initMocks(this);
        stub(samzaConfig.get("item_id.overridden.events", "")).toReturn("me.item_id.field,me_item_usage_summary.item_id.field");
        stub(samzaConfig.get("me_item_usage_summary.item_id.field", "")).toReturn("dimensions.item_id");
        stub(samzaConfig.get("me.item_id.field", "")).toReturn("edata.eks.itemId");
        stub(samzaConfig.get("item.store.ttl", "60000")).toReturn(CONTENT_CACHE_TTL);

        stub(samzaConfig.containsKey("me_item_usage_summary.item_id.field")).toReturn(true);
        stub(samzaConfig.containsKey("me.item_id.field")).toReturn(true);

        config = new ItemDeNormalizationConfig(samzaConfig);
    }

    @Test
    public void shouldGetItemIdOfMeItemUsageSummary() throws Exception {
        Map<String, Object> expectedItemTaxonomy = new HashMap<String, Object>();
        expectedItemTaxonomy.put("ME_ITEM_USAGE_SUMMARY", asList("dimensions", "item_id"));
        expectedItemTaxonomy.put("ME", asList("edata", "eks", "itemId"));

        assertEquals(expectedItemTaxonomy, config.itemTaxonomy());
    }

}