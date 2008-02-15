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

package eu.sqooss.impl.service.tds;

import java.util.Date;
import java.util.List;
import java.util.LinkedList;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.BTSAccessor;
import eu.sqooss.service.tds.Bug;

class BTSAccessorImpl extends NamedAccessorImpl implements BTSAccessor {
    class BugNumberListImpl
        extends LinkedList<Integer>
        implements BugNumberList {
        private static final long serialVersionUID = 1;
    }

    public static Logger logger;

    public BTSAccessorImpl( long id, String projectName ) {
        super(id,projectName);
    }

    // Interface methds
    public BugNumberList getBugs() {
        return null;
    }

    public BugNumberList getBugs( int severity ) {
        return null;
    }

    public BugNumberList getBugs( Date d1, Date d2 ) {
        return null;
    }

    public BugNumberList getBugs( Date d1, Date d2, int severity ) {
        return null;
    }

    public Bug getBug( int n ) {
        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

