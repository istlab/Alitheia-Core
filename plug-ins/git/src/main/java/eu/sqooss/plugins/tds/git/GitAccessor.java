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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Commit;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.AnnotatedLine;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNode;
import eu.sqooss.service.tds.SCMNodeType;

/**
 * An accessor for Git repositories. Encapsulates the functionality provided by
 * the JGit library. Known limitations:
 * 
 * <ul>
 * <li>JGit does not (yet?) support resolving commits by timestamp, we
 * approximate this by walking the log file around the desired timestamp</li>
 * <li>The accessor only supports on disk mirrors of repositories, connecting
 * to remote ones is not yet supported.</li>
 * </ul>
 * 
 * @author Georgios Gousios - <gousiosg@gmail.com>
 */
public class GitAccessor implements SCMAccessor {
    public static String ACCESSOR_NAME = "GitAccessor";
    private static List<URI> supportedSchemes;
    
    private URI uri;
    private String projectname;
    private Repository git = null;
    private Logger logger = null;
    
    static {
        supportedSchemes = new ArrayList<URI>();
        supportedSchemes.add(URI.create("git-file://www.sqo-oss.org"));
    }
    
	@Override
	public String getName() {
		return ACCESSOR_NAME;
	}

	@Override
	public List<URI> getSupportedURLSchemes() {
		return supportedSchemes;
	}

	@Override
	public void init(URI dataURL, String projectName) 
	throws AccessorException {

        doInit(dataURL, projectName);
	    this.logger = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_TDS);
        info("Created SCMAccessor for " + uri.toASCIIString());
	}
	
	/** {@inheritDoc} */
    public Revision newRevision(Date d) {
        if (d == null) {
            err("Cannot resolve commit with empty date");
            return null;
        }
        /*
         * Approximate revision resolution with a tree walk, as JGit does not
         * currently support revision resolution by timestamp. Given that Git stores
         * objects with millisecond accuracy, the following filter should just
         * return the revision we are looking for. 
         */
        RevWalk rw = new RevWalk(git);
        RevFilter exact = CommitTimeRevFilter.between(new Date(d.getTime() - 1), 
                new Date(d.getTime() + 1));
        rw.setRevFilter(exact);
        try {
            AnyObjectId headId = git.resolve(getHeadRevision().getUniqueId());
            RevCommit root = rw.parseCommit(headId);
            rw.markStart(root);
            RevCommit r = rw.next();
            Commit c = r.asCommit(rw);
            
            if (c == null) {
                err("Cannot resolve commit with timestamp: " + d);
                return null;
            }
            
            return revFromGitCommit(c);
        } catch (Exception e) {
           err("Cannot resolve commit with timestamp: " + d + ":" 
                   + e.getMessage());
        }
        return null;
    }
    
    /** {@inheritDoc} */
    public Revision newRevision(String uniqueId) {
        
        if (uniqueId == null || uniqueId.equals("")) {
            err("Cannot create new revision with null or empty revisionid");
            return null;
        }
        
        try {
            Commit obj = git.mapCommit(uniqueId);
            return revFromGitCommit(obj);
        } catch (IOException e) {
                err("Cannot resolve revision " + uniqueId + ":" + 
                        e.getMessage());
            return null;
        }
    }

    /** {@inheritDoc} */
    public Revision getHeadRevision()
        throws InvalidRepositoryException {
        AnyObjectId headId;
        try {
            RevWalk rw = new RevWalk(git);
            headId = git.resolve(Constants.HEAD);
            
            if (headId == null) {
                throw new InvalidRepositoryException(uri.toString(), 
                        "HEAD does not point to a known revision");
            }
            
            RevCommit root = rw.parseCommit(headId);
            Commit c = root.asCommit(rw);
            return revFromGitCommit(c);
        } catch (IOException e) {
            throw new InvalidRepositoryException("", e.getMessage());
        }
    }
    
    public Revision getFirstRevision() 
        throws InvalidRepositoryException {return null;}
    
    public Revision getNextRevision(Revision r)
        throws InvalidProjectRevisionException {return null;}
    
    public Revision getPreviousRevision(Revision r)
        throws InvalidProjectRevisionException {return null;}
    
    public boolean isValidRevision(Revision r) {return false;}
    
    public void getCheckout(String repoPath, Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}

    public void updateCheckout(String repoPath, Revision src,
        Revision dst, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}

    public void getFile(String repoPath, Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}

    public void getFile(String repoPath, Revision revision, OutputStream stream)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return;}
    
    public CommitLog getCommitLog(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        
        RevWalk rw = new RevWalk(git);
        RevFilter exact = CommitTimeRevFilter.between(r1.getDate(), r2.getDate());
        rw.setRevFilter(exact);
        rw.sort(RevSort.TOPO, true);
        rw.sort(RevSort.COMMIT_TIME_DESC, true);
        rw.sort(RevSort.REVERSE, true);
        
        Iterator<RevCommit> i = rw.iterator();
        
        while (i.hasNext()) {
            RevCommit c = i.next();
            
        }
        return null;
    }

    public Diff getDiff(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return null;}

    public Diff getChange(String repoPath, Revision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {return null;}

    public SCMNodeType getNodeType(String repoPath, Revision r)
        throws InvalidRepositoryException {return null;}

    public String getSubProjectPath() throws InvalidRepositoryException 
        {return null;}
    
    public List<SCMNode> listDirectory(SCMNode dir)
        throws InvalidRepositoryException,
        InvalidProjectRevisionException  {return null;}
    
    public SCMNode getNode(String path, Revision r) 
        throws  InvalidRepositoryException,
                InvalidProjectRevisionException {return null;}
    
    public PathChangeType getNodeChangeType(SCMNode s) 
        throws InvalidRepositoryException, 
               InvalidProjectRevisionException {return null;}
    
    public List<AnnotatedLine> getNodeAnnotations(SCMNode s) {return null;}
    
    /* Accessor internal methods*/
    
    /**Init a test repository when unit testing*/
    public void testInit(URI dataURL, String projectName) 
    throws AccessorException {
        doInit(dataURL, projectName);
    }
    
    /**
     * Actual repo initialization code, construct a repository instance per
     * tracked project.
     */
    private void doInit(URI dataURL, String projectName) 
    throws AccessorException {
        this.uri = dataURL;
        this.projectname = projectName;
        try {
            git = new Repository(toGitRepo(uri));
        } catch (IOException e) {
            throw new AccessorException(this.getClass(), 
                    "Cannot initialise accessor for URL " + uri.toASCIIString());
        }
    }

    /** Convert an Alitheia Core Git repository URL to an on-disk path*/
    private File toGitRepo(URI url) {
        File f = new File(url.getPath(), Constants.DOT_GIT);
        return f;
    }
    
    /**Convert a JGit revision to an Alitheia Core one*/
    private Revision revFromGitCommit(Commit obj) {
        Calendar c = GregorianCalendar.getInstance();
        c.setTimeInMillis(obj.getAuthor().getWhen().getTime());
        c.setTimeZone(obj.getAuthor().getTimeZone());
        Revision r = new GitRevision(obj.getCommitId().getName(), c.getTime());
        return r;
    }
    
    private void warn(String msg) {
        if (logger != null)
            logger.warn(msg);
    }
    
    private void err(String msg) {
        if (logger != null)
            logger.error(msg);
    }
    
    private void info(String msg) {
        if (logger != null)
            logger.info(msg);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
