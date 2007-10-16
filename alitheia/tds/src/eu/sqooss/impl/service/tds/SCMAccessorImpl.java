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

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.ProjectRevision;

public class SCMAccessorImpl implements SCMAccessor {
    private String url;
    private String projectName;
    public static Logger logger = null;

    public SCMAccessorImpl( String projectName, String url ) {
        this.url = url;
        this.projectName = projectName;
        if (logger != null) {
            logger.info("Created SCMAccessor for " + projectName);
        }
    }

    // Interface methods
    public void checkOut( String repoPath, ProjectRevision revision, String localPath ) {
    }

    public void checkOutFile( String repoPath, ProjectRevision revision, String localPath ) {
    }

    private static int revcount = 1;
    public CommitLog getCommitLog( ProjectRevision r1, ProjectRevision r2 ) {
        logger.info("getting log message for r." + revcount);
        return null;
    }

    public CommitLog getCommitLog( String repoPath, ProjectRevision r1, ProjectRevision r2 ) {
        return null;
    }

    public Diff getDiff( String repoPath, ProjectRevision r1, ProjectRevision r2 ) {
        logger.info("diff -r" +
            r1.getSVNRevision() + ":" + r2.getSVNRevision() + " " + repoPath);
        return null;
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

