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

package eu.sqooss.service.logging;

/**
 * The <code>LogManager</code> creates and releases the loggers. When the
 * system starts up, there are no loggers in use. Use the LogManager to create
 * loggers for use. You may also release loggers when they are no longer needed.
 *
 * @note Releasing a logger does nothing useful in the current implementation.
 * @note There is only a limited selection of names allowed. See D5, tables 6
 *  and 7 for a list of which names are valid. Or look at LogManagerConstants,
 *  which is part of the implementation.
 */
public interface LogManager {
    /**
    * Creates a new logger if doesn't exist, otherwise returns a existent logger.
    * @param name the name of the logger
    * @return logger
    * @exception IllegalArgumentException - if the name is not valid logger name
    */
    public Logger createLogger(String name);

    /**
    * Releases the logger.
    * @param name
    * @exception NullPointerException - if the name is null
    */
    public void releaseLogger(String name);

    /**
     * Returns recent entries to the loggers.
     */
    public String[] getRecentEntries();
}

// vi: ai nosi sw=4 ts=4 expandtab
