package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.task.ItemDeNormalizationConfig;
import org.ekstep.ep.samza.task.ItemDeNormalizationSink;
import org.ekstep.ep.samza.task.ItemDeNormalizationSource;

public class ItemDeNormalizationService {
    static Logger LOGGER = new Logger(ItemDeNormalizationService.class);
    private final ItemService itemService;
    private final ItemDeNormalizationConfig config;

    public ItemDeNormalizationService(ItemService itemService, ItemDeNormalizationConfig config) {
        this.itemService = itemService;
        this.config = config;
    }

    public void process(ItemDeNormalizationSource source, ItemDeNormalizationSink sink) {
        Event event = source.getEvent();

        try {
            String itemId = event.itemId();
            if (itemId == null) {
                LOGGER.info(event.id(), "NULL ITEM ID, PASSING EVENT THROUGH");
                sink.toSuccessTopic(event);
                return;
            }

            LOGGER.info(event.id(), "FIND ITEM");
            Item item = itemService.getItem(event.id(), itemId);
            if (item == null) {
                LOGGER.warn(event.id(), "ITEM NOT FOUND FOR GIVEN ID");
                sink.toSuccessTopic(event);
                return;
            }
            LOGGER.info(event.id(), "ITEM FOUND FOR GIVEN ID, DENORMALIZING");
            event.updateItem(item);
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC", e);
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
        }

    }
}
