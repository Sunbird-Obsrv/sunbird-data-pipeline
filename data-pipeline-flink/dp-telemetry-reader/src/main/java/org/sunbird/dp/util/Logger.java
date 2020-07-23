package org.sunbird.dp.util;

import org.slf4j.LoggerFactory;

import static java.text.MessageFormat.format;

public class Logger {

    private final org.slf4j.Logger logger;

    public Logger(Class<?> clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public void trace(String eventId, String s) {
        logger.trace(logPrependedWithMetadata(eventId, s));
    }

    public void trace(String eventId, String s, Object o) {
        logger.trace(logPrependedWithMetadata(eventId, s), o);
    }

    public void debug(String eventId, String s) {
        logger.debug(logPrependedWithMetadata(eventId, s));
    }

    public void debug(String eventId, String s, Object o) {
        logger.debug(logPrependedWithMetadata(eventId, s), o);
    }

    public void info(String eventId, String s) {
        logger.info(logPrependedWithMetadata(eventId, s));
    }

    public void info(String eventId, String s, Object o) {
        logger.info(logPrependedWithMetadata(eventId, s), o);
    }

    public void warn(String eventId, String s) {
        logger.warn(logPrependedWithMetadata(eventId, s));
    }

    public void warn(String eventId, String s, Object o) {
        logger.warn(logPrependedWithMetadata(eventId, s), o);
    }

    public void error(String eventId, String s) {
        logger.error(logPrependedWithMetadata(eventId, s));
    }

    public void error(String eventId, String s, Object o) {
        logger.error(logPrependedWithMetadata(eventId, s), o);
    }

    public void error(String eventId, String s, Throwable throwable) {
        logger.error(logPrependedWithMetadata(eventId, s), throwable);
        throwable.printStackTrace();
    }

    private String logPrependedWithMetadata(String eventId, String s) {
        return eventId == null ? s : format("{0} {1}", eventId, s);
    }
}
