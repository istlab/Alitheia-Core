/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     * Redistributions in binary form must reproduce the above
 *       copyright notice, this list of conditions and the following
 *       disclaimer in the documentation and/or other materials provided
 *       with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package eu.sqooss.impl.service.logging;

import java.util.HashMap;

import org.apache.log4j.*;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LogManager;

public class LogManagerImpl implements LogManager {
    // Our singleton manager
    public static LogManagerImpl logManager = null;

    // Synchronization primitive.
    private Object lockObject = new Object();

    // Our OSGi context; used to indicate initialization status.
    private BundleContext bc;

    // This map stores all of the valid and active loggers in the system.
    private HashMap<String,LoggerImpl> validLoggers = null;

    private CyclicLogger cyclicLogger = null;

    public LogManagerImpl( BundleContext bc ) {
        // The configuration is read automatically from the file log4j.properties
        // in the bundle .jar ; this is much like calling:
        //     PropertyConfigurator.configure("/log4j.properties");
        // The default configuration will suppress this info message:
        org.apache.log4j.Logger.getRootLogger().info("Logging initialized.");
        CyclicLogger l = new CyclicLogger();
        String pattern = bc.getProperty("eu.sqooss.logbuffer.pattern");
        if (pattern != null) {
            org.apache.log4j.Logger.getRootLogger().info("Logging to buffer with pattern <" + pattern + ">");
            l.setLayout(new PatternLayout(pattern));
        } else {
            org.apache.log4j.Logger.getRootLogger().info("Logging to buffer with simple layout.");
            l.setLayout(new SimpleLayout());
        }
        org.apache.log4j.Logger.getRootLogger().addAppender(l);
        cyclicLogger = l;

        validLoggers = new HashMap<String,LoggerImpl>(16);
        // Push all the system logger names into the validLoggers map.
        String[] loggerNames = {
		Logger.NAME_SQOOSS,
                Logger.NAME_SQOOSS_SERVICE,
                Logger.NAME_SQOOSS_DATABASE,
                Logger.NAME_SQOOSS_SECURITY,
                Logger.NAME_SQOOSS_MESSAGING,
                Logger.NAME_SQOOSS_WEB_SERVICES,
                Logger.NAME_SQOOSS_SCHEDULING,
                Logger.NAME_SQOOSS_UPDATER,
                Logger.NAME_SQOOSS_WEBUI,
                Logger.NAME_SQOOSS_TDS,
                Logger.NAME_SQOOSS_FDS,
                null
        } ;
        for (String s : loggerNames) {
            if (s != null) {
                validLoggers.put(s,null);
            }
        }

        logManager = this;
    }

    public Logger createLogger(String name) {
        LoggerImpl logger = null;
        if (!LogManagerConstants.loggerValid(name)) {
            org.apache.log4j.Logger.getRootLogger().error("Request for logger <" + name + ">");
            throw new IllegalArgumentException("The logger name <" + name + "> is not valid!");
        }
        // From here, logger name is valid.

        org.apache.log4j.Logger.getRootLogger().info("Creating logger <" + name + ">");
        if (validLoggers.containsKey(name)) {
            // logger may still be null, if it's a pre-defined name.
            // Their keys are inserted into the map without allocating
            // a logger for them.
            logger = validLoggers.get(name);
        }
        if (logger == null) {
            // Must have been a pre-defined one or a new metric logger.
            logger = new LoggerImpl(name);
            validLoggers.put(name,logger);
        }
        logger.get();
        return logger;
    }

    public void releaseLogger(String name) {
        LoggerImpl logger;
        int takingsNumber;
        if (!validLoggers.containsKey(name)) {
            org.apache.log4j.Logger.getRootLogger().error("Release for bogus logger <" + name + ">");
            return;
        }

        logger = validLoggers.get(name);
        if (logger == null) {
            org.apache.log4j.Logger.getRootLogger().error("Release on unallocated logger <" + name + ">");
            return;
        }

        if (logger.unget() == 0) {
            org.apache.log4j.Logger.getRootLogger().info("Released last logger for <" + name + ">");
            if (LogManagerConstants.loggerIsPluginLogger(name)) {
                validLoggers.remove(name);
            } else {
                validLoggers.put(name,null);
            }
        }
    }

    public String[] getRecentEntries() {
        return cyclicLogger.getEntries();
    }

    public void setBundleContext(BundleContext bc) {
        this.bc = bc;
    }

    public BundleContext getBundleContext() {
        return this.bc;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
