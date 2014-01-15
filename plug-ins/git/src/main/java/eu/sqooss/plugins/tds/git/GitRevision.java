/*
 * Copyright 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.plugins.tds.git;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;

import eu.sqooss.plugins.tds.scm.SCMProjectRevision;
import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;

/**
 * An implementation of the Revision interface for Git
 * 
 * @author Georgios Gousios <gousiosg@gmail.com>
 */
public class GitRevision extends SCMProjectRevision {

    private String id;
    private boolean isResolved = false;
    GitAccessor git = null;
    RevCommit commit = null;

    public GitRevision(RevCommit obj, GitAccessor git) {
        this.id = getRevCommitIDName(obj);
        this.date = getRevCommitAuthorIdent(obj).getWhen();
        this.author = getRevCommitAuthorIdent(obj).getName() + " <"
                + getRevCommitAuthorIdent(obj).getEmailAddress() + ">";
        this.message = getRevCommitFullMessage(obj);
        this.git = git;
        this.commit = obj;
        this.parents = new HashSet<String>();

        for (RevCommit s : getRevCommitParents(obj)) {
            parents.add(getRevCommitName(s));
        }
        isResolved = false;
    }

    public GitRevision(RevCommit obj, Map<String, PathChangeType> paths,
            List<CommitCopyEntry> copies) {
        this.id = getRevCommitIDName(obj);
        this.date = getRevCommitAuthorIdent(obj).getWhen();
        this.author = getRevCommitAuthorIdent(obj).getName() + " <"
                + getRevCommitAuthorIdent(obj).getEmailAddress() + ">";
        this.message = getRevCommitFullMessage(obj);
        this.changedPaths = paths;
        this.copyOps = copies;
        this.parents = new HashSet<String>();

        for (RevCommit s : getRevCommitParents(obj)) {
            parents.add(getRevCommitName(s));
        }
        isResolved = true;
    }

    protected String getRevCommitIDName(RevCommit obj) {
		return getRevCommitID(obj).name();
	}

    protected String getRevCommitName(RevCommit s) {
		return s.getName();
	}

    protected ObjectId getRevCommitID(RevCommit obj) {
		return obj.getId();
	}

    protected String getRevCommitFullMessage(RevCommit obj) {
		return obj.getFullMessage();
	}

	protected PersonIdent getRevCommitAuthorIdent(RevCommit obj) {
		return obj.getAuthorIdent();
	}

	protected RevCommit[] getRevCommitParents(RevCommit obj) {
		return obj.getParents();
	}

    public void resolve() {
	    if (isResolved == false) {
	        SCMProjectRevision r = git.getRevision(commit, true);
	        this.changedPaths = r.changedPaths;
	        this.copyOps = r.copyOps;
	        // We don't need these now that the commit is resolved.
	        // Let the GC grab them.
	        git = null;
	        commit = null;
	        isResolved = true;
	    }
	}

	public boolean isResolved() {
        resolve();
        return isResolved;
    }

    // Interface methods
	@Override
	public String getUniqueId() {
	    return id;
	}

	@Override
    public int compareTo(Revision other) {
        if (!(other instanceof GitRevision))
            throw new RuntimeException("Not of type: "
                    + this.getClass().getName());
        SCMProjectRevision othergit = (SCMProjectRevision) other;
        if (this.date.getTime() == othergit.date.getTime())
            return 0;

        if (this.date.getTime() > othergit.date.getTime())
            return 1;
        else
            return -1;
    }

	@Override
	public String toString() {
	    return getUniqueId() + " - " + date + " - " + author;
	}
}
