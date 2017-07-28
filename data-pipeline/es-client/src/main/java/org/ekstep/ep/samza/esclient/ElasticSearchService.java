package org.ekstep.ep.samza.esclient;

import java.io.IOException;

public interface ElasticSearchService {

    ClientResponse index(String indexName, String indexType, String document, String id) throws IOException;
}
