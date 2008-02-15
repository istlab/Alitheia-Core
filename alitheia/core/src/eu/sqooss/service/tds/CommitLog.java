/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 by Adriaan de Groot <groot@kde.org>
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
 * messages in commit order.
 */
public interface CommitLog extends Iterable<CommitEntry> {
    /**
         * Retrieve the project revision information for the first entry in this
         * commit log. May return null if the log is empty.
         * 
         * @return starting revision or null if empty
         */
    ProjectRevision first();

    /**
         * Retrieve the project revision information for the last entry in this
         * commit log. This may be the same as first() for 1-entry logs. May
         * return null if the log is empty.
         * 
         * @return final revision or null if empty
         */
    ProjectRevision last();

    /**
         * Retrieve the message (commit message) for project revision
         * <code>r</code> in this log. If <code>r</code> is not valid in
         * some way, throw an exception. If <code>r</code> is not in the log
         * (the revision does not occur, for instance) return <code>null</code>.
         * For <code>ProjectRevisions</code> with no SVN revision attached
         * (date revisions) return the last revision that is not after the
         * indicated date, or <code>null</code> if there isn't one.
         * 
         * @param r
         *                Revision for which the message should be retrieved
         * @return message at revision <code>r</code>
         * @throws InvalidProjectRevisionException
         *                 if <code>r</code> is not within thea scope of this
         *                 log.
         */
    String message(ProjectRevision r) throws InvalidProjectRevisionException;

    /**
         * For debugging purposes, dump the log to stdout.
         */
    void dump();

    /**
         * @return the number of entries in the log
         */
    int size();
}

// vi: ai nosi sw=4 ts=4 expandtab

