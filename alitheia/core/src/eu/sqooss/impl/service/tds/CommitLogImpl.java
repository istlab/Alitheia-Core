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

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import org.tmatesoft.svn.core.SVNLogEntry;

import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.InvalidProjectRevisionException;

public class CommitLogImpl implements CommitLog {
    private LinkedList<CommitEntry> entries;

    public CommitLogImpl() {
        entries = new LinkedList<CommitEntry>();
    }

    public Collection<CommitEntry> getEntriesReference() {
        return entries;
    }

    private void dump(CommitEntry l) {
        System.out.println("--------------------------------");
        System.out.println("r." + l.getRevision() + "  " + l.getAuthor() + "  "
                + l.getDate());
        System.out.println(l.getMessage());
    }

    public void dump() {
        for (Iterator<CommitEntry> i = entries.iterator(); i.hasNext();) {
            CommitEntry l = i.next();
            dump(l);
        }
    }

    // Interface methods
    public ProjectRevision first() {
        if (entries.size() < 1) {
            return null;
        }
        ProjectRevision r = new ProjectRevision(entries.get(0).getRevision());
        r.setDate(entries.get(0).getDate());
        return r;
    }

    public ProjectRevision last() {
        if (entries.size() < 1) {
            return null;
        }
        ProjectRevision r = new ProjectRevision(entries.getLast().getRevision());
        r.setDate(entries.getLast().getDate());
        return r;
    }

    public String message(ProjectRevision r)
            throws InvalidProjectRevisionException {
        if ((r == null) || (!r.isValid())) {
            throw new InvalidProjectRevisionException(
                    "Need a valid revision to query log", null);
        }

        if (r.hasSVNRevision()) {
            long revno = r.getSVNRevision();
            for (Iterator<CommitEntry> i = entries.iterator(); i.hasNext();) {
                CommitEntry l = i.next();
                if (l.getRevision().getSVNRevision() == revno) {
                    return l.getMessage();
                }
            }
        } else {
            Date d = r.getDate();
            CommitEntry l = null, prev = null;
            for (Iterator<CommitEntry> i = entries.iterator(); i.hasNext();) {
                prev = l;
                l = i.next();
                if (l.getDate().after(d)) {
                    if (prev != null) {
                        return prev.getMessage();
                    } else {
                        // This is the case when the first entry already
                        // comes after the requested date.
                        return null;
                    }
                }
            }
            // We've gotten to the end without finding a revision that
            // comes after the requested date, so obviously the last
            // entry is the latest entry before the requested date.
            return prev.getMessage();
        }
        return null;
    }

    public int size() {
        return entries.size();
    }

    public Iterator<CommitEntry> iterator() {
        return entries.iterator();
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

