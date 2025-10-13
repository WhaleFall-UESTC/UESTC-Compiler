package com.compiler.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiniLogger {
    private static final Logger logger = LoggerFactory.getLogger(MiniLogger.class);

    public static void logErrorAndExit(String message, Object ... args) {
        logger.error(message, args);
        System.exit(1);
    }

    public static void info(String message, Object ... args) {
        logger.info(message, args);
    }

    public static void error(String message, Object ... args) {
        logger.error(message, args);
    }

    public static void warn(String message, Object ... args) {
        logger.warn(message, args);
    }
}