package org.ekstep.ep.samza;

import org.ekstep.ep.samza.cleaner.CleanerFactory;
import org.ekstep.ep.samza.logger.Logger;

public class ContentDeNormalizationService {
    static Logger LOGGER = new Logger(ContentDeNormalizationService.class);
    private final CleanerFactory cleanerFactory;
    private final ContentService contentService;

    public ContentDeNormalizationService(CleanerFactory cleanerFactory, ContentService contentService) {
        this.cleanerFactory = cleanerFactory;
        this.contentService = contentService;
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
            sink.toFailedTopic(event);
            sink.toSuccessTopic(event);
        }

    }
}
