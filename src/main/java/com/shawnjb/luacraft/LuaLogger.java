package com.shawnjb.luacraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.file.Paths;

public class LuaLogger {
    public static final Logger LOGGER = LogManager.getLogger("LuaCraft");

    public static void setupFileLogging() {
        try {
            LoggerContext ctx = LoggerContext.getContext(false);
            Configuration config = ctx.getConfiguration();

            String logFilePath = Paths.get("logs", "luacraft.log").toString();
            PatternLayout layout = PatternLayout.newBuilder()
                    .withPattern("[%d{HH:mm:ss}] [%t/%level]: %msg%n")
                    .build();

            @SuppressWarnings("deprecation")
            FileAppender fileAppender = FileAppender.newBuilder()
                    .withFileName(logFilePath)
                    .withAppend(true)
                    .withName("LuaCraftFileAppender")
                    .withBufferedIo(true)
                    .withLayout(layout)
                    .withConfiguration(config)
                    .build();

            fileAppender.start();

            LoggerConfig loggerConfig = config.getLoggerConfig(LOGGER.getName());
            if (!loggerConfig.getName().equals(LOGGER.getName())) {
                loggerConfig = new LoggerConfig(LOGGER.getName(), Level.INFO, false);
                config.addLogger(LOGGER.getName(), loggerConfig);
            }

            loggerConfig.addAppender(fileAppender, Level.INFO, null);
            loggerConfig.setLevel(Level.INFO);
            loggerConfig.setAdditive(false);

            ctx.updateLoggers();
        } catch (Exception e) {
            System.err.println("[LuaCraft] Failed to set up file logging: " + e.getMessage());
        }
    }
}
