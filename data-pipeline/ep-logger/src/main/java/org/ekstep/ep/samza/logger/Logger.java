package org.ekstep.ep.samza.logger;

import org.slf4j.LoggerFactory;

public class Logger {

    private final org.slf4j.Logger logger;

    public Logger(Class clazz) {
        logger = LoggerFactory.getLogger(clazz);
    }

    public void trace(String s) {
        logger.trace(s);
    }

    public void trace(String s, Object o) {
        logger.trace(s, o);
    }

    public void debug(String s) {
        logger.debug(s);
    }

    public void debug(String s, Object o) {
        logger.debug(s, o);
    }

    public void info(String s) {
        logger.info(s);
    }

    public void info(String s, Object o) {
        logger.info(s, o);
    }

    public void warn(String s) {
        logger.warn(s);
    }

    public void warn(String s, Object o) {
        logger.warn(s, o);
    }

    public void error(String s) {
        logger.error(s);
    }

    public void error(String s, Object o) {
        logger.error(s, o);
    }

    public void error(String s, Throwable throwable) {
        logger.error(s, throwable);
    }
}
