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

package eu.sqooss.impl.service.tds;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.tmatesoft.svn.core.SVNLogEntry;

import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.ProjectRevision;

public class CommitLogImpl implements CommitLog {
    private LinkedList<SVNLogEntry> entries;

    public CommitLogImpl() {
        entries = new LinkedList<SVNLogEntry>();
    }

    public Collection getEntriesReference() {
        return entries;
    }

    public void dump(SVNLogEntry l) {
        System.out.println("--------------------------------");
        System.out.println("r." + l.getRevision() +
            "  " + l.getAuthor() +
            "  " + l.getDate());
        System.out.println(l.getMessage());
    }

    public void dump() {
        for (Iterator i = entries.iterator(); i.hasNext(); ) {
            SVNLogEntry l = (SVNLogEntry) i.next();
            dump(l);
        }
    }

    // Interface methods
    public ProjectRevision first() {
        return null;
    }

    public ProjectRevision last() {
        return null;
    }

    public String message(ProjectRevision r) {
        return entries.get(0).getMessage();
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

