/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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


package eu.sqooss.service.logging;

/**
 * The <code>Logger</code> instances are used for logging. They are created
 * with createLogger method of the LogManager service. Loggers are given
 * log messages through a number of methods; the method names indicate
 * the severity of the message.
 *
 * The logging methods match those of the log4j Logger class. See the
 * documentation of log4j for more information.
 *
 * @section Using log levels
 *
 * The suggested use of the log levels -- in order to provide a useful
 * semantic basis for interpreting the logs of an Alitheia system when
 * troubleshooting -- is as follows:
 *
 *   - debug is used to log entering or exiting a method; small algorithmic
 *           steps within a method. It should probably be used only in the
 *           development phase.
 *   - info logs actions and largers algorithmic steps; log the sucessful
 *          completion of an action as info. The flow of info log statements
 *          should give an indication of what is happening in the application.
 *          With info messages, the high-level picture of what the system is doing
 *          should be clear.
 *   - warn is used to indicate that something recoverable is wrong. Typically
 *          used when configuration values are unusual or resources are not
 *          available. Warnings should indicate the nature of the problem
 *          and, if possible, how it may be resolved.
 *   - error indicates non-recoverable problems or program logic errors.
 *           Use error to log to the console that bad parameters have been
 *           passed in (perhaps before throwing an exception).
 *
 * The constants used here are the legal names for loggers; any others
 * will cause an exception to be thrown when used.
 */
public interface Logger {
    /**
     * Represents SQO-OSS system logger name.
     */
    public static final String NAME_SQOOSS              = "sqooss";

    /**
     * Represents service system logger name.
     */
    public static final String NAME_SQOOSS_SERVICE      = "sqooss.service";

    /**
     * Represents database connectivity logger name.
     */
    public static final String NAME_SQOOSS_DATABASE     = "sqooss.database";

    /**
     * Represents security logger name.
     */
    public static final String NAME_SQOOSS_SECURITY     = "sqooss.security";

    /**
     * Represents messaging logger name.
     */
    public static final String NAME_SQOOSS_MESSAGING    = "sqooss.messaging";

    /**
     * Represents web services logger name.
     */
    public static final String NAME_SQOOSS_WEB_SERVICES = "sqooss.webservice";

    /**
     * Represents scheduling logger name.
     */
    public static final String NAME_SQOOSS_SCHEDULING   = "sqooss.scheduler";

    /**
     * Represents updater logger name.
     */
    public static final String NAME_SQOOSS_UPDATER      = "sqooss.updater";

    /**
     * Represents web UI logger name.
     */
    public static final String NAME_SQOOSS_WEBADMIN     = "sqooss.webadmin";

    /**
     * Represents TDS logger name.
     */
    public static final String NAME_SQOOSS_TDS          = "sqooss.tds";

    /**
     * Represents FDS logger name.
     */
    public static final String NAME_SQOOSS_FDS          = "sqooss.fds";
    
    /**
     * Represents PluginAdmin logger name.
     */
    public static final String NAME_SQOOSS_PA           = "sqooss.pa";

    /**
     * Represents Metric logger name.
     */
    public static final String NAME_SQOOSS_METRIC       = "sqooss.metric";

    /**
     * Represents Metric logger name.
     */
    public static final String NAME_SQOOSS_TESTER       = "sqooss.tester";

    /**
    * Log a message with debug (lower than lowest) logging level.
    * The debug level is used for micro-steps in an algorithm, for
    * logging individual computations and for providing ongoing
    * status information. It really only makes sense in log files
    * when examining specific issues.
    *
    * @param message a log message
    * @note In the default configuration, debug messages will never
    *   be seen anywhere.
    */
    public void debug(String message);

    /**
    * Logs a message with a info (lowest) logging level. The info
    * level is used to give an idea of what is going on in the system
    * at a global level.
    *
    * @param message a log message
    */
    public void info(String message);

    /**
    * Logs a message with a warning logging level
    * @param message a log message
    */
    public void warn(String message);

    /**
    * Logs a message with a severe(highest) logging level
    * @param message a log message
    */
    public void error(String message);

    /**
    * Returns a name of the logger.
    * @return the logger's name
    */
    public String getName();
}

// vi: ai nosi sw=4 ts=4 expandtab

