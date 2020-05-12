package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.DeDuplicationConfig;
import org.ekstep.ep.samza.task.DeDuplicationSink;
import org.ekstep.ep.samza.task.DeDuplicationSource;
import org.ekstep.ep.samza.util.DeDupEngine;

import redis.clients.jedis.exceptions.JedisException;

public class DeDuplicationService {
	private static Logger LOGGER = new Logger(DeDuplicationService.class);
	private final DeDupEngine deDupEngine;
	private final DeDuplicationConfig config;


	public DeDuplicationService(DeDupEngine deDupEngine, DeDuplicationConfig config) {
		this.deDupEngine = deDupEngine;
		this.config = config;
	}

	public void process(DeDuplicationSource source, DeDuplicationSink sink) throws Exception {
		Event event = null;

		try {
			event = source.getEvent();
			String checksum = event.getChecksum();
			if (checksum == null) {
				LOGGER.info(event.id(), "EVENT WITHOUT CHECKSUM & MID, PASSING THROUGH : {}", event);
				event.markSkipped();
				sink.toSuccessTopic(event);
				return;
			}

			if (isDupCheckRequired(event)) {
				if (!deDupEngine.isUniqueEvent(checksum)) {
					LOGGER.info(event.id(), "DUPLICATE EVENT, CHECKSUM: {}", checksum);
					event.markDuplicate();
					sink.toDuplicateTopic(event);
					return;
				}

				LOGGER.info(event.id(), "ADDING EVENT CHECKSUM TO STORE");

				deDupEngine.storeChecksum(checksum);
			}
			event.markSuccess();
			sink.toSuccessTopic(event);

		} catch (JedisException e) {
			LOGGER.error(null, "Exception when retrieving data from redis:  ", e);
			event.markRedisFailure();
			sink.toSuccessTopicIfRedisException(event);
			throw e;
		} catch (JsonSyntaxException e) {
			LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
			sink.toMalformedEventsTopic(source.getMessage());
		}
	}

	public boolean isDupCheckRequired(Event event) {
		return (config.inclusiveProducerIds().isEmpty() || (null != event.producerId() && config.inclusiveProducerIds().contains(event.producerId())));
	}
}

