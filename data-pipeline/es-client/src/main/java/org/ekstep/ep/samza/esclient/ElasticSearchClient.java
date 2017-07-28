package org.ekstep.ep.samza.esclient;


import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.ekstep.ep.samza.logger.Logger;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ElasticSearchClient implements ElasticSearchService {
    static Logger LOGGER = new Logger(ElasticSearchClient.class);
    private final RestClient client;

    public ElasticSearchClient(String host, int port) {
        this.client = RestClient.builder(
                new HttpHost(host, port, "http")).build();
    }

    @Override
    public ClientResponse index(String indexName, String indexType, String document, String id) {
        String endPoint = getRequestEndpoint(indexName, indexType, id);
        HttpEntity entity = getDocumentEntity(document);

        try {

            Response indexResponse = client.performRequest("POST", endPoint, Collections.<String, String>emptyMap(), entity);
            LOGGER.info("indexResponse", indexResponse.toString());

            return new IndexResponse(getStatusCode(indexResponse), indexResponse.toString());

        } catch (ResponseException e) {
            LOGGER.info("ResponseException", e.getMessage());
            return new IndexResponse(getStatusCode(e.getResponse()), e.getMessage());
        } catch (IOException e) {
            LOGGER.info("IoException", e.getMessage());
            return new FailedResponse(e.getMessage());
        }
    }

    private String getRequestEndpoint(String indexName, String indexType, String documentId) {
        if (documentId != null) {
            return MessageFormat.format("/{0}/{1}/{2}", indexName, indexType, documentId);
        }
        return MessageFormat.format("/{0}/{1}", indexName, indexType);
    }

    private HttpEntity getDocumentEntity(String document) {
        return new NStringEntity(
                document, ContentType.APPLICATION_JSON);
    }

    private String getStatusCode(Response indexResponse){
        return indexResponse.getStatusLine() != null ?
                String.valueOf(indexResponse.getStatusLine().getStatusCode()) : null;

    }
}
