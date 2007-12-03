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

import org.osgi.framework.*;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.logging.LogManager;

public abstract class BundleActivatorBase implements Logger {
    private ServiceReference logManagerService = null;
    private LogManager logManager = null;
    private Logger logger = null;
    private String loggerName = null;
    
    protected void getLogger(final BundleContext bc) {
        logManagerService = bc.getServiceReference(LogManager.class.getName());
        if (logManagerService != null) {
            logManager = (LogManager)bc.getService(logManagerService);
            logger = logManager.createLogger(loggerName);
        }
    }

    protected void ungetLogger(final BundleContext bc) {
        if (logManagerService != null) {
            logManager.releaseLogger(logger.getName());
            logger = null;
            bc.ungetService(logManagerService);
            logManager = null;
            logManagerService = null;
        }
    }

    // Interface methods, all of them forwarded to the logger.
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

