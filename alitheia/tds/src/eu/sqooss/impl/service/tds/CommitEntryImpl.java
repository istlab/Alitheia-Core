/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007 by Adriaan de Groot <groot@kde.org>
 *   [[ Individual consortium members may list themselves here;
 *      third parties are to be listed here as well. You must
 *      include a real name and an email address. ]]
 *
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

import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.SVNLogEntry;

import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.ProjectRevision;

public class CommitEntryImpl implements CommitEntry {
    private ProjectRevision revision;
    private String author;
    private String message;
    private String[] changedPaths;

    public CommitEntryImpl(SVNLogEntry l) {
        revision = new ProjectRevision(l.getRevision());
        author = l.getAuthor();
        message = l.getMessage();

        String[] paths = new String[l.getChangedPaths().size()];
        int c = 0;
        for (Iterator i = l.getChangedPaths().keySet().iterator();
            i.hasNext(); ) {
            paths[c] = (String) i.next();
            ++c;
        }
        changedPaths = paths;
    }

    public ProjectRevision getRevision() {
        return revision;
    }

    public String getAuthor() {
        return author;
    }

    public String getMessage() {
        return message;
    }

    public String[] getChangedPaths() {
        return changedPaths;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

