/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.service.tds;

/**
 * This interface represents a lowest-common-denominator approach to logs of
 * changes to a project. A commit log extends from some first revision to some
 * last revision (which may be the same revision, or may both be null if the log
 * is empty) and consists of messages. The log is iterable and will return the
 * messages in date order.
 * 
 * Even if its functionality is simple, this interface is provided in order to
 * allow implementations to configure the log retrieval strategy (on-request or
 * one-off) for themselves.
 */
public interface CommitLog extends Iterable<Revision> {
    /**
     * Retrieve the project revision information for the first entry in this
     * commit log. May return null if the log is empty.
     * 
     * @return starting revision or null if empty
     */
    Revision first();

    /**
     * Retrieve the project revision information for the last entry in this
     * commit log. This may be the same as first() for 1-entry logs. May return
     * null if the log is empty.
     * 
     * @return final revision or null if empty
     */
    Revision last();

    /**
     * Return the number of entries in the log
     * @return the number of entries in the log
     */
    int size();
}

// vi: ai nosi sw=4 ts=4 expandtab
