package org.ekstep.ep.samza.external;

import org.ekstep.ep.samza.domain.Content;

import java.io.IOException;

public interface SearchService {
    Content search(String contentId) throws IOException;
}
