package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;

import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.engine.DeDupEngine;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.DeDuplicationConfig;
import org.ekstep.ep.samza.task.DeDuplicationSink;
import org.ekstep.ep.samza.task.DeDuplicationSource;

import static java.text.MessageFormat.format;

public class DeDuplicationService {
	static Logger LOGGER = new Logger(DeDuplicationService.class);
	private final DeDupEngine deDupEngine;
	private final DeDuplicationConfig config;

	public DeDuplicationService(DeDupEngine deDupEngine, DeDuplicationConfig config) {
		this.deDupEngine = deDupEngine;
		this.config = config;
	}

	public void process(DeDuplicationSource source, DeDuplicationSink sink) {
		Event event = null;

		try {
			event = source.getEvent();
			sink.setMetricsOffset(source.getSystemStreamPartition(), source.getOffset());
			String checksum = event.getChecksum();
			sink.setMetricsOffset(source.getSystemStreamPartition(), source.getOffset());

			if (checksum == null) {
				LOGGER.info(event.id(), "EVENT WITHOUT CHECKSUM & MID, PASSING THROUGH : {}", event);
				event.markSkipped();
				event.updateDefaults(config);
				sink.toSuccessTopic(event);
				return;
			}

			if (!deDupEngine.isUniqueEvent(checksum)) {
				LOGGER.info(event.id(), "DUPLICATE EVENT, CHECKSUM: {}", checksum);
				event.markDuplicate();
				sink.toDuplicateTopic(event);
				return;
			}

			LOGGER.info(event.id(), "ADDING EVENT CHECKSUM TO STORE");

			deDupEngine.storeChecksum(checksum);
			event.updateDefaults(config);
			event.markSuccess();
			sink.toSuccessTopic(event);
		} catch (JsonSyntaxException e) {
			LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
			sink.toMalformedEventsTopic(source.getMessage());
		} catch (Exception e) {
			event.markFailure(e.getMessage(), config);
			LOGGER.error(null,
					format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
							event),
					e);
			sink.toErrorTopic(event);
		}
	}
}
