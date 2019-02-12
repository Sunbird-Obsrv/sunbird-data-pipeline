package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.domain.EventUpdaterFactory;
import org.ekstep.ep.samza.domain.IEventUpdater;
import org.ekstep.ep.samza.task.DeNormalizationConfig;
import org.ekstep.ep.samza.task.DeNormalizationSink;
import org.ekstep.ep.samza.task.DeNormalizationSource;
import org.ekstep.ep.samza.util.*;

import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

public class DeNormalizationService {

    static Logger LOGGER = new Logger(DeNormalizationService.class);
    private final DeNormalizationConfig config;
    private final EventUpdaterFactory eventUpdaterFactory;

    public DeNormalizationService(DeNormalizationConfig config, EventUpdaterFactory eventUpdaterFactory) {
        this.config = config;
        this.eventUpdaterFactory = eventUpdaterFactory;
    }

    public void process(DeNormalizationSource source, DeNormalizationSink sink) {
        Event event = null;
        try {
            event = source.getEvent();
            // add content details to the event
            event = eventUpdaterFactory.getInstance("content-data-updater")
                    .update(event, event.getKey("content").get(0));
            // add user details to the event
            event = eventUpdaterFactory.getInstance("user-data-updater")
                    .update(event, event.getKey("user").get(0));
            // add device details to the event
            event = eventUpdaterFactory.getInstance("device-data-updater")
                    .update(event);
            // add dialcode details to the event
            event = eventUpdaterFactory.getInstance("dialcode-data-updater")
                    .update(event, event.getKey("dialcode"));;

            sink.toSuccessTopic(event);
        } catch(JsonSyntaxException e){
            LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
            sink.toMalformedTopic(source.getMessage());
        } catch (Exception e) {
            LOGGER.error(null,
                    format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
                            event),
                    e);
            sink.toErrorTopic(event, e.getMessage());
        }
    }

}
