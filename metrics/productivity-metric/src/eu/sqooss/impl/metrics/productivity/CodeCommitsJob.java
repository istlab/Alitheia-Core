/* This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 * 
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
 * Copyright 2007-2008 Georgios Gousios <gousiosg@gmail.com>
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

package eu.sqooss.impl.metrics.productivity;

import java.util.HashMap;
import java.util.Iterator;

import eu.sqooss.service.abstractmetric.AbstractMetric;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.scheduler.Job;
import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.SCMAccessor;

/**
 * Code commit - A commit that affects at least 1 source code file
 */
public class CodeCommitsJob extends Job {

    HashMap<String, Integer> authorCommits = new HashMap<String, Integer>();
    ProjectVersion start, end;
    SCMAccessor svn;

    protected CodeCommitsJob(AbstractMetric owner, ProjectVersion a,
            ProjectVersion b) {
        start = a;
        end = b;
    }

    public void run() {
        /*
        log.debug(this.getClass().getName()
                + ":Calculating Code Commits per developer");

        CommitLog svnLog = null;

        if (tds.accessorExists(start.getProject().getId())
                && tds.accessorExists(end.getProject().getId())) {
            svn = (SCMAccessor) this.tds.getAccessor(1);
        } else {
            log.error("An accessor for projectid:" + start.getProject()
                    + " does not exist");
            return;
        }

        try {
            svnLog = svn.getCommitLog(new ProjectRevision(start.getVersion()),
                    new ProjectRevision(end.getVersion()));

        } catch (InvalidProjectRevisionException e) {
            log.error("No project with id " + start.getProject());
            return;
        } catch (InvalidRepositoryException e) {
            log.error("Invalid repository: Error was: " + e.getMessage());
            return;
        }

        log.debug("Got " + log + " log entries");

        // Get log entries
        Iterator<CommitEntry> i = svnLog.iterator();
        CommitEntry entry;

        while (i.hasNext()) {
            entry = i.next();
            // Iterate over changed paths
            for (String path : entry.getChangedPaths())
                // Get the entry type (binary or text)
                // System.err.println("Path " + path + " is a " +
                // FileTypeMatcher.getFileType(path) + " file");

                if (evaluate(path, entry))
                    // Author marked as having committed code,
                    // now break to next commit
                    break;
        }

        System.err.println("Code Commits per developer");
        Iterator<String> k = authorCommits.keySet().iterator();
        int commits = 0;
        while (k.hasNext()) {
            String author = k.next();
            commits += authorCommits.get(author).intValue();
            log.debug(authorCommits.get(author).intValue() + " " + author
                    + "\n");
        }

        log.debug("Total code commits:" + commits);
        */
    }

    public int priority() {
        return 0;
    }

    protected boolean evaluate(String path, CommitEntry entry) {

        if (FileTypeMatcher.getFileType(path) == FileTypeMatcher.FileType.SRC) {
            if (authorCommits.containsKey(entry.getAuthor()))
                authorCommits.put(entry.getAuthor(), authorCommits.get(entry
                        .getAuthor()) + 1);
            else
                authorCommits.put(entry.getAuthor(), new Integer(1));

            return true;
        }
        return false;
    }
}

//vi: ai nosi sw=4 ts=4 expandtab
