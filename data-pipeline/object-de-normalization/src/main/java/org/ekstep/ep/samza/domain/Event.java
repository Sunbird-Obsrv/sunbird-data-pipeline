package org.ekstep.ep.samza.domain;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.reader.NullableValue;
import org.ekstep.ep.samza.reader.Telemetry;
import org.ekstep.ep.samza.search.domain.Content;
import org.ekstep.ep.samza.search.domain.IObject;
import org.ekstep.ep.samza.search.domain.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Event {
	static Logger LOGGER = new Logger(Event.class);
	private final Telemetry telemetry;
	private Map<String, Object> contentTaxonomy;

	public Event(Map<String, Object> map, Map<String, Object> contentTaxonomy) {
		this.telemetry = new Telemetry(map);
		this.contentTaxonomy = contentTaxonomy;
	}

	public Map<String, Object> getMap() {
		return telemetry.getMap();
	}

	public String getDid() {
		NullableValue<String> did = telemetry.read("dimensions.did");
		return did.isNull() ? telemetry.<String>read("context.did").value() : did.value();
	}

	public String getObjectID() {
		if (objectFieldsPresent()) {
			return telemetry.<String>read("object.id").value();
		}
		for (String event : contentTaxonomy.keySet()) {
			if (getEid().startsWith(event.toUpperCase())) {
				ArrayList<String> fields = getRemovableFields(event);
				return getObjectID(telemetry.getMap(), fields);
			}
		}
		return null;
	}

	public boolean objectFieldsPresent() {
		String objectId = telemetry.<String>read("object.id").value();
		String objectType = telemetry.<String>read("object.type").value();
		return objectId != null && objectType != null && !objectId.isEmpty() && !objectType.isEmpty();
	}

	public String getObjectType() {
		NullableValue<String> checksum = telemetry.read("object.type");
		return checksum.value().toLowerCase();
	}

	private ArrayList<String> getRemovableFields(String event) {
		ArrayList<String> fields = new ArrayList<String>();
		fields.addAll((Collection<? extends String>) contentTaxonomy.get(event));
		return fields;
	}

	private String getObjectID(Map<String, Object> map, ArrayList<String> fields) {
		String key = fields.remove(0);
		if (key != null && map.containsKey(key)) {
			Object value = map.get(key);
			if (value instanceof String) {
				return (String) value;
			}
			return getObjectID((Map<String, Object>) map.get(key), fields);
		}
		return null;
	}

	public String id() {
		return telemetry.<String>read("metadata.checksum").value();
	}

	public void updateContent(Content content) {
		HashMap<String, Object> contentData = new HashMap<String, Object>();
		contentData.put("name", content.name());
		contentData.put("identifier", content.identifier());
		contentData.put("pkgVersion", content.pkgVersion());
		contentData.put("description", content.description());
		contentData.put("mediaType", content.mediaType());
		contentData.put("contentType", content.contentType());
		contentData.put("lastUpdatedOn", content.lastUpdatedOn());
		contentData.put("duration", content.duration());
		contentData.put("gradeLevel", content.gradeLevel());
		contentData.put("author", content.author());
		contentData.put("code", content.code());
		contentData.put("curriculum", content.curriculum());
		contentData.put("domain", content.domain());
		contentData.put("medium", content.medium());
		contentData.put("source", content.source());
		contentData.put("status", content.status());
		contentData.put("subject", content.subject());
		contentData.put("createdBy", content.createdBy());

		contentData.put("downloads", content.downloads());
		contentData.put("rating", content.rating());
		contentData.put("size", content.size());

		contentData.put("language", content.language());
		contentData.put("ageGroup", content.ageGroup());
		contentData.put("keywords", content.keywords());
		contentData.put("audience", content.audience());
		contentData.put("concepts", content.concepts());
		contentData.put("methods", content.methods());
		contentData.put("createdFor", content.createdFor());

		telemetry.add("contentdata", contentData);

		updateMetadata(content);
	}

	private void updateMetadata(IObject object) {
		Map<String, Object> metadata = (Map<String, Object>) telemetry.read("metadata").value();
		if (metadata != null) {
			metadata.put("cachehit", object.getCacheHit());
			return;
		}
		metadata = new HashMap<String, Object>();
		metadata.put("cachehit", object.getCacheHit());

		telemetry.add("metadata", metadata);

		LOGGER.info(id(), "METADATA CACHEHIT - ADDED " + metadata);
	}

	public String getEid() {
		return telemetry.<String>read("eid").value();
	}

	public void updateItem(Item item) {
		HashMap<String, Object> itemData = new HashMap<String, Object>();
		itemData.put("title", item.title());
		itemData.put("name", item.name());
		itemData.put("num_answers", item.num_answers());
		itemData.put("template", item.template());
		itemData.put("type", item.type());
		itemData.put("status", item.status());
		itemData.put("owner", item.owner());
		itemData.put("qlevel", item.qlevel());
		itemData.put("language", item.language());
		itemData.put("keywords", item.keywords());
		itemData.put("concepts", item.concepts());
		itemData.put("gradeLevel", item.gradeLevel());
		telemetry.add("itemdata", itemData);

		updateMetadata(item);
	}

	public void markFailed(String status, String errorMsg) {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.odn_processed", false);

		telemetry.addFieldIfAbsent("metadata", new HashMap<String, Object>());
		telemetry.add("metadata.odn_status", status);
		telemetry.add("metadata.odn_error", errorMsg);
	}

	public void markSkipped() {
		telemetry.addFieldIfAbsent("flags", new HashMap<String, Boolean>());
		telemetry.add("flags.odn_skipped", true);
	}

	public boolean canDeNormalize() {
		String objectType = getObjectType();
		return (objectType.equals("content") || objectType.equals("item") || objectType.equals("assessmentitem"));
	}

	public boolean isSummaryEvent() {
		NullableValue<String> eid = telemetry.read("eid");
		return (!eid.isNull() && eid.value().startsWith("ME_"));
	}
}
