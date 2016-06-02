package org.ekstep.ep.samza.logger;

import org.slf4j.LoggerFactory;

import static java.text.MessageFormat.format;

public class Logger {

    private final org.slf4j.Logger logger;

    public Logger(Class clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public void trace(String eventId, String s) {
        logger.trace(format("{0} {1}", eventId, s));
    }

    public void trace(String eventId, String s, Object o) {
        logger.trace(format("{0} {1}", eventId, s), o);
    }

    public void debug(String eventId, String s) {
        logger.debug(format("{0} {1}", eventId, s));
    }

    public void debug(String eventId, String s, Object o) {
        logger.debug(format("{0} {1}", eventId, s), o);
    }

    public void info(String eventId, String s) {
        logger.info(format("{0} {1}", eventId, s));
    }

    public void info(String eventId, String s, Object o) {
        logger.info(format("{0} {1}", eventId, s), o);
    }

    public void warn(String eventId, String s) {
        logger.warn(format("{0} {1}", eventId, s));
    }

    public void warn(String eventId, String s, Object o) {
        logger.warn(format("{0} {1}", eventId, s), o);
    }

    public void error(String eventId, String s) {
        logger.error(format("{0} {1}", eventId, s));
    }

    public void error(String eventId, String s, Object o) {
        logger.error(format("{0} {1}", eventId, s), o);
    }

    public void error(String eventId, String s, Throwable throwable) {
        logger.error(format("{0} {1}", eventId, s), throwable);
    }
}
