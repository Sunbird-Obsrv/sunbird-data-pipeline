package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.search.domain.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Event {
    static Logger LOGGER = new Logger(Event.class);
    private Map<String, Object> itemTaxonomy;
    public Map<String, Object> map;
    private String itemId;

    public Event(Map<String, Object> map, Map<String, Object> itemTaxonomy) {
        this.itemTaxonomy = itemTaxonomy;
        this.map = map;
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public String id() {
        return getMap() != null && getMap().containsKey("metadata") &&
                (((Map<String, Object>) getMap().get("metadata")).containsKey("checksum"))
                ? (String) ((Map<String, Object>) getMap().get("metadata")).get("checksum")
                : null;
    }

    public String itemId() {

        if (itemTaxonomy.containsKey(getEid())) {
            ArrayList<String> fields = getRemovableFields(getEid());
            return getItemId(map, fields);
        }
        for (String event : itemTaxonomy.keySet()) {
            if (getEid().startsWith(event.toUpperCase())) {
                ArrayList<String> fields = getRemovableFields(event);
                return getItemId(map, fields);
            }
        }
        return null;
    }

    private ArrayList<String> getRemovableFields(String event) {
        ArrayList<String> fields = new ArrayList<String>();
        fields.addAll((Collection<? extends String>) itemTaxonomy.get(event));
        return fields;
    }

    //TODO: Make this method more readable when writing ItemDenormalizationJob
    private String getItemId(Map<String, Object> map, ArrayList<String> fields) {
        String key = fields.remove(0);
        if (key != null && map.containsKey(key)) {
            Object value = map.get(key);
            if (value instanceof String) {
                return (String) value;
            }
            return getItemId((Map<String, Object>) map.get(key), fields);
        }
        return null;
    }

    public void updateItem(Item item) {
        HashMap<String, Object> itemData = new HashMap<String, Object>();
        itemData.put("title", item.title());
        itemData.put("name", item.name());
        itemData.put("num_answers", item.num_answers());
        itemData.put("template", item.template());
        itemData.put("type", item.type());
        itemData.put("status", item.status());
        itemData.put("question", item.question());
        itemData.put("owner", item.owner());
        itemData.put("createdBy", item.createdBy());
        itemData.put("createdOn", item.createdOn());
        itemData.put("lastUpdatedBy", item.lastUpdatedBy());
        itemData.put("lastUpdatedOn", item.lastUpdatedOn());
        itemData.put("qlevel", item.qlevel());
        itemData.put("language", item.language());
        itemData.put("keywords", item.keywords());
        itemData.put("concepts", item.concepts());
        itemData.put("gradeLevel", item.gradeLevel());
        map.put("itemdata", itemData);

        updateMetadata(item);
    }

    private void updateMetadata(Item item) {
        Map<String, Object> metadata = (Map<String, Object>) map.get("metadata");
        if (metadata != null) {
            metadata.put("cachehit", item.getCacheHit());
            return;
        }
        metadata = new HashMap<String, Object>();
        metadata.put("cachehit", item.getCacheHit());
        map.put("metadata", metadata);

        LOGGER.info(id(), "METADATA CACHEHIT - ADDED " + metadata);
    }

    public String getEid() {
        return map != null && map.containsKey("eid") ? (String) map.get("eid") : null;
    }

}
