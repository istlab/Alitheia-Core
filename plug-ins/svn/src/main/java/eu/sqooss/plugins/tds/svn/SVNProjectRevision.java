/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 - 2010 - Organization for Free and Open Source Software,  
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

package eu.sqooss.plugins.tds.svn;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import eu.sqooss.service.tds.CommitCopyEntry;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;

/**
 * A Revision denotes a revision of a (any) project; revisions may be created
 * from dates or from SVN revision numbers. A specific Revision object contains @em
 * only dates or revisions; it is not of itself associated with a specific
 * project. If a SVNProjectRevision is created with a date it has no SVN
 * revision until it is applied to a specific project; at that point its SVN
 * revision @em may be set by querying the SVN repository. Similarly
 * SVNProjectRevisions created from a specific revision number have no date
 * until they hit a repository.
 * 
 * SVNProjectRevisions are passed to many functions of the raw accessor classes.
 * It may be invalid to pass certain kinds of revisions to some of those methods
 * (for instance, the email accessors only make sense if there is a date
 * attached to the revision). The InvalidProjectRevisionException is used to
 * indicate problems like that.
 */
public class SVNProjectRevision implements Revision {

    private long revision;
    private Date date;
    private String author;
    private String message;
    private Map<String, PathChangeType> changedPaths;
    private List<CommitCopyEntry> copyOps;
    private Set<String> parents;
    
    /**
     * Default constructor, creating an invalid revision.
     */
    private SVNProjectRevision() {
        revision = -1;
        date = null;
    }

    /**
     * Create a ProjectRevision from a raw SVN revision number.
     * There is no date associated with this until the project revision
     * is applied to a specific SVN repository.
     */
    public SVNProjectRevision(long revision) {
        this();
        this.revision = revision;
    }

    /**
     * Create a ProjectRevision from a date. No revision number
     * is associated with the date until the project revision
     * is applied to a specific SVN repository.
     */
    public SVNProjectRevision(Date date) {
        this();
        this.date = date;
    }

    /**
     * Create a revision from an SVNKit log entry object.
     */
    public SVNProjectRevision(SVNLogEntry l, String root) {
        author = l.getAuthor();
        message = l.getMessage();
        date = l.getDate();
        changedPaths = new LinkedHashMap<String, PathChangeType>();
        copyOps = new ArrayList<CommitCopyEntry>();
        revision = l.getRevision();
        
        Map<String, SVNLogEntryPath> paths = 
            (Map<String, SVNLogEntryPath>) l.getChangedPaths();
        
        for (Iterator i = paths.keySet().iterator(); i.hasNext();) {
            String path = (String) i.next();
            if (path.startsWith(root)) {
                changedPaths.put(
                        path, parseSVNLogEntryPath(
                                paths.get(path).getType()));
            }
            
            String copyPath = paths.get(path).getCopyPath();
            Long   copyRev = paths.get(path).getCopyRevision();
            
            if ((copyPath != null) && (copyRev != -1)) {
                copyOps.add(createCommitCopyEntry(path, copyPath, copyRev));
            }
        }
        parents = new HashSet<String>();
        if (l.getRevision() >= 1)
            parents.add(String.valueOf((l.getRevision() - 1)));
    }

	protected CommitCopyEntry createCommitCopyEntry(String path,
			String copyPath, Long copyRev) {
		return new CommitCopyEntry(copyPath, 
		        (Revision)(new SVNProjectRevision(copyRev)), path, 
		        this);
	}

    PathChangeType parseSVNLogEntryPath(char entryPathType) {
        if (entryPathType == SVNLogEntryPath.TYPE_ADDED) {
            return PathChangeType.ADDED;
        } else if (entryPathType == SVNLogEntryPath.TYPE_DELETED) {
            return PathChangeType.DELETED;
        } else if (entryPathType == SVNLogEntryPath.TYPE_MODIFIED) {
            return PathChangeType.MODIFIED;
        } else if (entryPathType == SVNLogEntryPath.TYPE_REPLACED) {
            return PathChangeType.REPLACED;
        } else {
            return PathChangeType.UNKNOWN;
        }
    }

    /**
     * Retrieve the SVN revision that most closely corresponds
     * with this project revision.
     */
    public long getSVNRevision() {
        return revision;
    }

    public boolean isResolved() {
        return (revision >= 0 
                && date != null
                && author != null
                && message != null);
    }
    
    //Interface methods
    /** {@inheritDoc}} */
    public Date getDate() {
        return date;
    }
    
    /** {@inheritDoc} */
    public String getUniqueId() {
        return String.valueOf(revision);
    }
    
    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Set<String> getChangedPaths() {
        return changedPaths.keySet();
    }

    @Override
    public Map<String, PathChangeType> getChangedPathsStatus() {
        return changedPaths;
    }

    @Override
    public List<CommitCopyEntry> getCopyOperations() {
        return copyOps;
    }  
    
    @Override
	public Set<String> getParentIds() {
	    return parents;
	}

	/** {@inheritDoc} */
    public int compareTo(Revision o) {
        if (!(o instanceof SVNProjectRevision))
            throw new RuntimeException("Revision not of type: " + this.getClass().getName());
        
        if (!((SVNProjectRevision)o).isResolved()) {
            throw new RuntimeException("Revision not resoved " 
                    + getUniqueId());
        }
        
        if (!isResolved()) {
            throw new RuntimeException("Revision not resoved "
                    + getUniqueId());
        }
        
        return (int) (revision - (((SVNProjectRevision)o).revision)); 
    }

    @Override
	public int compare(Revision o1, Revision o2) {
		return o1.compareTo(o2);
	}

	/** {@inheritDoc} */
	public String toString() {
	    if (!isResolved())
	        return null;
	    return "r" + revision + " - (" + getAuthor() + "): " + getMessage();
	}  
}

// vi: ai nosi sw=4 ts=4 expandtab

