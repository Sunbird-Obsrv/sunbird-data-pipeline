package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.EventsFlattenerConfig;
import org.ekstep.ep.samza.task.EventsFlattenerSink;
import org.ekstep.ep.samza.task.EventsFlattenerSource;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class EventsFlattenerService {

    private static Logger LOGGER = new Logger(EventsFlattenerService.class);
    private final EventsFlattenerConfig config;
    private static String IMPORT_KEY = "import";
    private static String DOWNLOAD_KEY = "download";
    private static String FLATTEN_EVENT_NAME = "SHARE_ITEM";

    /**
     * Constructor of the EventFlattenService
     *
     * @param config - Configurations of the EventsFlatten Samza job.
     */
    public EventsFlattenerService(EventsFlattenerConfig config) {
        this.config = config;
    }

    /**
     * Listening to "telemetry.share" topic, Assuming only share events should produce
     * From this "telemetry.share" topic.
     *
     * @param source - Producer - "telemetry.share" topic
     * @param sink   - Consumer - "telemetry.sink"
     */
    public void process(EventsFlattenerSource source, EventsFlattenerSink sink) {
        Event event = source.getEvent();
        // Flattening the "SHARE" Event to Multiple "SHARE_ITEM" Events
        this.toFlatten(event, getClonedEventObject(event), sink);
        // Adding Original "SHARE" Events to success topic
        sink.toSuccessTopic(event);
    }

    /**
     * Method to flatten the SHARE EVENT Object to multiple share event.
     * Events flattening constraints
     * <p>
     * ================= Constraints===============================
     * 1. If the Item list object as params.transfers = 0 then edata.type should be "download" else do not modify the edata.type value.
     * 2. If the Item list object as params.transfers > 0 then edata.type should be "import" else do not modify the edta.type value.
     * 3. If the Item list object as params.transfers = null then do not update the edata.type (Keep the orginal value ie., dir, type)
     * 4. If the share event has object then move the object data to rollup l1, share event item.id, item.typ and item.ver should be in object properties
     * ===============================================================
     *
     * @param orginalEvent - SHARE Event object.
     * @param sink         - Object to push the event to kafka sink
     */
    private void toFlatten(Event orginalEvent, Event clonedEvent, EventsFlattenerSink sink) {
        String objectId = null;
        String eDataType = clonedEvent.edataType();
        Gson gson = new Gson();
        Type type = new TypeToken<List<Map<String, String>>>() {
        }.getType();

        List<Map<String, Object>> items = clonedEvent.edataItems();
        if (clonedEvent.objectFieldsPresent()) {
            objectId = clonedEvent.objectID();
        }
        for (Map<String, Object> item : items) {
            clonedEvent.updateEventObjectKey(item.get("id").toString(), item.get("type").toString(), item.get("ver").toString(), objectId);
            Object itemParams = item.get("params");
            if (itemParams != null) {
                List<Map<String, String>> param = gson.fromJson(itemParams.toString(), type);
                for (Map<String, String> p : param) {
                    String transfers = p.get("transfers");
                    if (transfers != null || !transfers.isEmpty()) {
                        if (Double.parseDouble(transfers) == 0) {
                            eDataType = DOWNLOAD_KEY;
                        }
                        if (Double.parseDouble(transfers) > 0) {
                            eDataType = IMPORT_KEY;
                        }
                        clonedEvent.updatedEventEdata(eDataType, Double.parseDouble(p.get("size")));
                    } else {
                        clonedEvent.updatedEventEdata(orginalEvent.edataType(), null);
                    }
                }
            } else {
                clonedEvent.updatedEventEdata(orginalEvent.edataType(), null);
            }
            clonedEvent.renameEventIdTo(FLATTEN_EVENT_NAME);
            clonedEvent.removeItems();
            // Adding "SHARE_ITEM" Events to success topic
            sink.toSuccessTopic(clonedEvent);
        }
    }

    /**
     * Method to clone the telemetry event object data.
     *
     * @param event Event object
     * @return Duplicated Event object.
     */
    private Event getClonedEventObject(Event event) {
        String message = event.getJson();
        return new Event((Map<String, Object>) new Gson().fromJson(message, Map.class));
    }
}
