package org.ekstep.ep.samza.system;

import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.search.domain.Content;
import org.ekstep.ep.samza.service.ContentService;

import java.io.IOException;
import java.util.HashMap;

public class GenericDeNormStrategy implements Strategy {
    private final ContentService contentService;

    public GenericDeNormStrategy(ContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    public void execute(Event event) throws IOException {
        Content content = contentService.getContent(event.id(), event.getObjectID());
        if(content != null ){
            event.updateContent(content);
        }
    }
}
