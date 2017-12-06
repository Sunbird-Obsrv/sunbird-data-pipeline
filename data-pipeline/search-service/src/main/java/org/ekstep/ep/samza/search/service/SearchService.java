package org.ekstep.ep.samza.search.service;


import org.ekstep.ep.samza.search.domain.Content;
import org.ekstep.ep.samza.search.domain.Item;

import java.io.IOException;

public interface SearchService {
    Content searchContent(String contentId) throws IOException;
    Item searchItem(String itemId) throws IOException;
}
