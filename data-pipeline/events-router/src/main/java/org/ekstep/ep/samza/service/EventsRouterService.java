package org.ekstep.ep.samza.service;

import com.google.gson.JsonSyntaxException;
import org.ekstep.ep.samza.core.Logger;
import org.ekstep.ep.samza.domain.Event;
import org.ekstep.ep.samza.task.EventsRouterConfig;
import org.ekstep.ep.samza.task.EventsRouterSink;
import org.ekstep.ep.samza.task.EventsRouterSource;
import org.ekstep.ep.samza.util.DeDupEngine;
import redis.clients.jedis.exceptions.JedisException;

import java.text.SimpleDateFormat;
import java.util.Date;

import static java.text.MessageFormat.format;

public class EventsRouterService {

	private static Logger LOGGER = new Logger(EventsRouterService.class);
	private final DeDupEngine deDupEngine;
	private final EventsRouterConfig config;

	public EventsRouterService(DeDupEngine deDupEngine, EventsRouterConfig config) {

		this.config = config;
		this.deDupEngine = deDupEngine;
	}

	public void process(EventsRouterSource source, EventsRouterSink sink) {
		Event event = null;
		try {
			event = source.getEvent();
			if (config.isDedupEnabled() && isDupCheckRequired(event.eid())) {
				String checksum = event.getChecksum();
				if (!deDupEngine.isUniqueEvent(checksum)) {
					LOGGER.info(event.id(), "DUPLICATE EVENT, CHECKSUM: {}", checksum);
					event.markDuplicate();
					sink.toDuplicateTopic(event);
					return;
				}
				LOGGER.info(event.id(), "ADDING EVENT CHECKSUM TO STORE");
				deDupEngine.storeChecksum(checksum);
			} else {
				LOGGER.info(event.id(), "SKIPPING THE DEDUP CHECK");
			}
			String eid = event.eid();
			if(event.mid().contains("TRACE")){
				SimpleDateFormat simple = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
				String timeStamp  = simple.format(new Date());
				event.updateTs(timeStamp);
			}
			String summaryRouteEventPrefix = this.config.getSummaryRouteEvents();
			if (eid.startsWith(summaryRouteEventPrefix)) {
				sink.toSummaryEventsTopic(event);
			} else if (eid.startsWith("ME_")) {
				sink.incrementSkippedCount(event);
			} else {
				sink.toTelemetryEventsTopic(event);
			}
		} catch (JedisException e) {
			e.printStackTrace();
			LOGGER.error(null, "Exception when retrieving data from redis: ", e);
			deDupEngine.getRedisConnection().close();
			throw e;
		} catch (JsonSyntaxException e) {
			e.printStackTrace();
			LOGGER.error(null, "INVALID EVENT: " + source.getMessage());
			sink.toMalformedTopic(source.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(null,
					format("EXCEPTION. PASSING EVENT THROUGH AND ADDING IT TO EXCEPTION TOPIC. EVENT: {0}, EXCEPTION:",
							event),
					e);
			sink.toErrorTopic(event, e.getMessage());
		}
	}

	private boolean isDupCheckRequired(String eid) {
		return (config.excludedEids().isEmpty() || (null != eid && !(config.excludedEids().contains(eid))));
	}
}
