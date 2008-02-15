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

import java.util.Date;
import java.util.List;

import eu.sqooss.service.tds.Bug;

public interface BTSAccessor extends NamedAccessor {
    public interface BugNumberList extends List<Integer> { }

    /**
     * Retrieves the bug numbers in the database. This accessor
     * is attached to a specific project which has a specific BTS;
     * the list of bugs is valid only within that BTS.
     */
    BugNumberList getBugs();

    /**
     * Retrieves the bug numbers in the database, filtered for
     * the severity @p severity . Returns @em only the bugs with
     * exactly this severity. Bug severities are defined by the
     * bug tracker in use.
     */
    BugNumberList getBugs( int severity );

    /**
     * Retrieve bugs that are reported between the dates @p d1 and @p d2.
     */
    BugNumberList getBugs( Date d1, Date d2 );

    /**
     * Retrieve bugs reported between @p d1 and @p d2 with a
     * severity exactly @p severity .
     */
    BugNumberList getBugs( Date d1, Date d2, int severity );

    /**
     * Retrieve the bug information for one specific bug.
     */
    Bug getBug( int n );
}

// vi: ai nosi sw=4 ts=4 expandtab

