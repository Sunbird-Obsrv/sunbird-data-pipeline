package org.ekstep.ep.samza.service;

import org.ekstep.ep.samza.cache.ContentService;
import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.logger.Logger;
import org.ekstep.ep.samza.search.domain.Content;
import org.ekstep.ep.samza.task.ContentDeNormalizationConfig;
import org.ekstep.ep.samza.task.ContentDeNormalizationSink;
import org.ekstep.ep.samza.task.ContentDeNormalizationSource;

public class ContentDeNormalizationService {
    static Logger LOGGER = new Logger(ContentDeNormalizationService.class);
    private final CleanerFactory cleanerFactory;
    private final ContentService contentService;
    private final ContentDeNormalizationConfig config;

    public ContentDeNormalizationService(CleanerFactory cleanerFactory, ContentService contentService, ContentDeNormalizationConfig config) {
        this.cleanerFactory = cleanerFactory;
        this.contentService = contentService;
        this.config = config;
    }

    public void process(ContentDeNormalizationSource source, ContentDeNormalizationSink sink) {
        Event event = source.getEvent();

        try {
            if (!cleanerFactory.shouldAllowEvent(event.getEid())) {
                LOGGER.info(event.id(), "EVENT IN SKIP LIST, PASSING EVENT THROUGH");
                sink.toSuccessTopic(event);
                return;
            }

            String contentId = event.getContentId();
            if (contentId == null) {
                LOGGER.info(event.id(), "NULL CONTENT ID, PASSING EVENT THROUGH");
                sink.toSuccessTopic(event);
                return;
            }

            LOGGER.info(event.id(), "FIND CONTENT");
            Content content = contentService.getContent(event.id(), contentId);
            if (content == null) {
                LOGGER.warn(event.id(), "CONTENT NOT FOUND FOR GIVEN ID");
                sink.toSuccessTopic(event);
                return;
            }
            LOGGER.info(event.id(), "CONTENT FOUND FOR GIVEN ID, DENORMALIZING");
            event.updateContent(content);
            sink.toSuccessTopic(event);
        } catch (Exception e) {
            LOGGER.error(event.id(), "EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO FAILED TOPIC", e);
            e.printStackTrace();
            sink.toSuccessTopic(event);
            sink.toFailedTopic(event);
        }
    }
}
