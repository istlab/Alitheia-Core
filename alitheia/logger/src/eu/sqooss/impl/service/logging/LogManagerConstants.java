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

import eu.sqooss.service.logging.Logger;

/**
 * This class contains constants related to the logger naming scheme.
 * The naming scheme is defined in D5, table 1 and figures 6 and 7.
 *
 * When requesting loggers from the log manager, you must use one
 * of these pre-defined names @em or use a name that begins with
 * pluginLoggerPrefix; these are intended for metric plugins.
 */
public class LogManagerConstants {
    // Prefix for metric plugin loggers.
    public static final String PLUGIN_LOGGER_PREFIX = "sqooss.service.";

    /**
     * Check that the logger @p name is a valid logger name; only
     * the logger names defined in D5 are allowed.
     *
     * @return true iff the name is a valid name.
     */
    public static final boolean loggerValid(String name) {
        return loggerIsPluginLogger(name) ||
            loggerIsSystemLogger(name);
    }

    /**
     * @return true iff the name is a valid system logger name.
     */
    public static final boolean loggerIsSystemLogger(String name) {
        return name.equals(Logger.NAME_SQOOSS) || // Root logger
	    name.equals(Logger.NAME_SQOOSS_SERVICE) ||
            name.equals(Logger.NAME_SQOOSS_DATABASE) ||
            name.equals(Logger.NAME_SQOOSS_SECURITY) ||
            name.equals(Logger.NAME_SQOOSS_MESSAGING) ||
            name.equals(Logger.NAME_SQOOSS_WEB_SERVICES) ||
            name.equals(Logger.NAME_SQOOSS_SCHEDULING) ||
            name.equals(Logger.NAME_SQOOSS_UPDATER) ||
            name.equals(Logger.NAME_SQOOSS_WEBADMIN) ||
            name.equals(Logger.NAME_SQOOSS_TDS) ||
            name.equals(Logger.NAME_SQOOSS_FDS) ||
	    false; // No-op, but allows us to insert items above more easily
    }

    /**
     * @return true iff the name is valid as a plugin logger name.
     */
    public static final boolean loggerIsPluginLogger(String name) {
        return name.startsWith(PLUGIN_LOGGER_PREFIX);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

