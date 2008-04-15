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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.ProjectRevision;

public class DiffImpl implements Diff {
    private ProjectRevision revStart,revEnd;
    private File diffFile;
    private Map<String, PathChangeType> changedFiles;

    public DiffImpl(ProjectRevision start, ProjectRevision end, File path) {
        revStart = new ProjectRevision(start);
        if (end!=null) {
            revEnd = new ProjectRevision(end);
        } else {
            revEnd = new ProjectRevision(start.getSVNRevision()+1);
        }
        diffFile = path;
        changedFiles = new HashMap<String, PathChangeType>();
    }

    /**
     * Add a file to the set of changed files represented by
     * this Diff. Normally done by the DiffStatusHandler while
     * processing the diff from the server. 
     */
    public void addFile(String path) {
        changedFiles.put(path, PathChangeType.UNKNOWN);
    }
    
    /**
     * Add a file to the collection of changed files represented by
     * this Diff, along with the kind of change that has taken place
     * on each one. Normally done by the DiffStatusHandler while
     * processing the diff from the server.
     */
    public void addFile(String path, PathChangeType changeType) {
        changedFiles.put(path, changeType);
    }

    // Interface methods
    public ProjectRevision getSourceRevision() {
        return new ProjectRevision(revStart);
    }

    public ProjectRevision getTargetRevision() {
        return new ProjectRevision(revEnd);
    }

    public File getDiffFile() {
        return diffFile;
    }

    public Set<String> getChangedFiles() {
        return changedFiles.keySet();
    }
    
    public Map<String, PathChangeType> getChangedFilesStatus() {
        return changedFiles;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

