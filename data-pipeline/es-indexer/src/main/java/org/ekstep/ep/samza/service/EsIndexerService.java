package org.ekstep.ep.samza.service;

import com.google.common.base.Optional;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.esclient.ClientResponse;
import org.ekstep.ep.samza.esclient.ElasticSearchService;
import org.ekstep.ep.samza.task.EsIndexerConfig;
import org.ekstep.ep.samza.task.EsIndexerSink;
import org.ekstep.ep.samza.task.EsIndexerSource;

public class EsIndexerService {

	static Logger LOGGER = new Logger(EsIndexerService.class);
	private final ElasticSearchService elasticSearchService;
	private final EsIndexerConfig config;

	public EsIndexerService(ElasticSearchService elasticSearchService, EsIndexerConfig config) {
		this.elasticSearchService = elasticSearchService;
		this.config = config;
	}

	public void process(EsIndexerSource source, EsIndexerSink sink) {

		Event event = source.getEvent();
		String indexName = event.indexName(config, source.getStreamName());
		try {

			if (null == indexName) {
				LOGGER.info("INDEX DETAILS ARE MISSING! SKIPPING", event.id());
				event.markSkipped();
				sink.toFailedTopic(event);
				return;
			}

			ClientResponse response = elasticSearchService.index(indexName, event.indexType(), event.getJson(),
					Optional.fromNullable(event.id()).or(""));

			if (success(response)) {
				LOGGER.info("ES INDEXER SUCCESS", Optional.fromNullable(event.id()).or("Eid Missing Event"));
				sink.markSuccess();
			} else {
				LOGGER.error("ES INDEXER FAILED : RESPONSE", response.toString());
				LOGGER.error("ES INDEXER FAILED : EVENT", event.toString());
				event.markFailed(response.getStatus(), response.getMessage());
				sink.toFailedTopic(event);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error("ES INDEXER EXCEPTION : MESSAGE", e.getMessage());
			LOGGER.error("ES INDEXER EXCEPTION : EVENT", event.toString());
			event.markFailed("Error", e.getMessage());
			sink.toErrorTopic(event);
		}
		sink.setMetricsOffset(source.getSystemStreamPartition(),source.getOffset());
	}

	private boolean success(ClientResponse response) {
		return (response.getStatus().equals("200") || response.getStatus().equals("201"));
	}
}
