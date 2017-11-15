package org.ekstep.ep.samza.system;

import org.ekstep.ep.samza.search.domain.Item;
import org.ekstep.ep.samza.service.ItemService;
import org.ekstep.ep.samza.domain.Event;

import java.io.IOException;

public class ItemDeNormStrategy implements Strategy {

    private final ItemService itemService;

    public ItemDeNormStrategy(ItemService itemService) {
        this.itemService = itemService;
    }

    @Override
    public void execute(Event event) throws IOException {
//        Item item = itemService.getItem(event.id(), event.getObjectID());
//        if(item != null){
//            event.updateItem(item);
//        }
    }
}
