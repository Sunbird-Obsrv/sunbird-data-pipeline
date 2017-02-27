package org.ekstep.ep.samza.external;

import org.ekstep.ep.samza.Content;

import java.io.IOException;

public interface SearchService {
    Content search(String contentId) throws IOException;
}
