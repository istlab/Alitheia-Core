/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,  
 *                Athens, Greece.
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

import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.osgi.framework.BundleContext;

import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;

public class LogManagerImpl implements LogManager {
    // Our singleton manager
    public static LogManagerImpl logManager = null;

    // Our OSGi context; used to indicate initialization status.
    private BundleContext bc;

    // This map stores all of the valid and active loggers in the system.
    private Map<String,LoggerImpl> loggers = null;

    private CyclicLogger cyclicLogger = null;

    public LogManagerImpl() {}

    public Logger createLogger(String name) {
        LoggerImpl logger = loggers.get(name);

        if (logger == null) {
            org.apache.log4j.Logger.getRootLogger().info(
                "Creating logger <" + name + ">");
            logger = new LoggerImpl(name);
            loggers.put(name, logger);
        }
        
        logger.get();
        return logger;
    }

    public void releaseLogger(String name) {
        LoggerImpl logger;
        if (!loggers.containsKey(name)) {
            org.apache.log4j.Logger.getRootLogger().error("Release for bogus logger <" + name + ">");
            return;
        }

        logger = loggers.get(name);
        if (logger == null) {
            org.apache.log4j.Logger.getRootLogger().error("Release on unallocated logger <" + name + ">");
            return;
        }

        if (logger.unget() == 0) {
            org.apache.log4j.Logger.getRootLogger().info(
                    "Released last logger for <" + name + ">");

            loggers.put(name, null);
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

    @Override
	public void setInitParams(BundleContext bc, Logger l) {
		this.bc = bc;
	}

	@Override
	public void shutDown() {
	    
	}

	@Override
	public boolean startUp() {
	    loggers = new HashMap<String, LoggerImpl>();
		// The configuration is read automatically from the file log4j.properties
        // in the bundle .jar ; this is much like calling:
        //     PropertyConfigurator.configure("/log4j.properties");
        // The default configuration will suppress this info message:
        
        Enumeration<URL> props;
        Properties p = new Properties();
        try {
            props = getClass().getClassLoader().getResources("log4j.properties");
            p.load(props.nextElement().openStream());
        } catch (Exception e) {
            System.err.println("Logging initialisation failed, " +
                    "cannot find log4j.properties file:" + e);
        }
        PropertyConfigurator.configure(p);
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
        l.setThreshold(org.apache.log4j.Level.WARN);
        org.apache.log4j.Logger.getRootLogger().addAppender(l);
        cyclicLogger = l;

        logManager = this;
        return true;
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
