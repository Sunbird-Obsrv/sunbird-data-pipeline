package org.ekstep.ep.samza.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.EventsFlattenConfig;
import org.ekstep.ep.samza.task.EventsFlattenSink;
import org.ekstep.ep.samza.task.EventsFlattenSource;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class EventsFlattenService {

    private static Logger LOGGER = new Logger(EventsFlattenService.class);
    private final EventsFlattenConfig config;
    private static String IMPORTED_KEY = "imported";
    private static String DOWNLOAD_KEY = "download";
    private static String FLATTEN_EVENT_NAME = "SHARE_ITEM";

    /**
     * Constructor of the EventFlattenService
     *
     * @param config - Configurations of the EventsFlatten Samza job.
     */
    public EventsFlattenService(EventsFlattenConfig config) {
        this.config = config;
    }

    /**
     * @param source
     * @param sink
     */
    public void process(EventsFlattenSource source, EventsFlattenSink sink) {
        Event event = source.getEvent();

        if (event.eid().equals(config.getFlattenEventName())) {
            try {
                this.toFlatten(event,getClonedEventObject(event), sink);
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
                LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
                sink.toMalformedTopic(source.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(null,
                        format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
                                event),
                        e);
                sink.toErrorTopic(event, e.getMessage());
            }
        } else {
            sink.toSuccessTopic(event);
        }
    }

    /**
     * Method to flatten the SHARE EVENT Object to multiple share event.
     *
     * @param orginalEvent - SHARE Event object.
     * @param sink         - Object to push the event to kafka sink
     */
    private void toFlatten(Event orginalEvent, Event clonedEvent, EventsFlattenSink sink) {
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
                            eDataType = IMPORTED_KEY;
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
            sink.toSuccessTopic(clonedEvent);
        }
        sink.toSuccessTopic(orginalEvent);
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
