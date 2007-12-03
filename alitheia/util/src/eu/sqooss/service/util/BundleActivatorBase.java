/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
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

package eu.sqooss.service.util;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LogManager;

public abstract class BundleActivatorBase implements Logger, ServiceListener {
    private ServiceReference logManagerService = null;
    private BundleContext bc = null;
    private LogManager logManager = null;
    private Logger logger = null;
    private String loggerName = null;
    private static String loggerClass = LogManager.class.getName();
    
    protected void start(final BundleContext bc, String loggerName) {
        this.bc = bc;
        this.loggerName = loggerName;
        getLogger();
        addListener();
    }

    protected void stop() {
        removeListener();
        ungetLogger();
    }

    private void addListener() {
        if (logManagerService != null) {
            String loggerFilter = "(" + Constants.OBJECTCLASS
                + "=" + loggerClass +")";
            try {
                bc.addServiceListener(this, loggerFilter);
            } catch (InvalidSyntaxException e) {
                warning(e.getMessage());
            }
        }
    }

    private void removeListener() {
        bc.removeServiceListener(this);
    }

    private void getLogger() {
        if (logManagerService == null) {
            logManagerService = bc.getServiceReference(loggerClass);
        }
        if (logManagerService != null) {
            logManager = (LogManager)bc.getService(logManagerService);
            logger = logManager.createLogger(loggerName);
        }
    }

    private void ungetLogger() {
        if (logManagerService != null) {
            logManager.releaseLogger(logger.getName());
            logger = null;
            bc.ungetService(logManagerService);
            logManager = null;
            // The service *reference* stays around, since it's OK
            // to keep service references even when the bundle they
            // refer to is not available.
            // logManagerService = null;
        }
    }

    // Interface ServiceListener.
    public void serviceChanged(ServiceEvent event) {
        int eventType = event.getType();
        if ((ServiceEvent.REGISTERED == eventType) ||
            (ServiceEvent.MODIFIED == eventType)) {
            getLogger();
        } else if (ServiceEvent.UNREGISTERING == eventType) {
            ungetLogger();
        }
    }


    // Interface Logger. All calls forwarded to the actual logger object.
    public void debug(String m) {
        if (logger != null) {
            logger.debug(m);
        }
    }

    public void info(String m) {
        if (logger != null) {
            logger.info(m);
        }
    }

    public void warn(String m) {
        if (logger != null) {
            logger.warn(m);
        }
    }

    public void error(String m) {
        if (logger != null) {
            logger.error(m);
        }
    }

    public void config(String m) {
        if (logger != null) {
            logger.config(m);
        }
    }

    public void warning(String m) {
        if (logger != null) {
            logger.warning(m);
        }
    }

    public void severe(String m) {
        if (logger != null) {
            logger.severe(m);
        }
    }

    public String getName() {
        if (logger != null) {
            return logger.getName();
        }
        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

