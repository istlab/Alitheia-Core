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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNodeType;

public class SCMAccessorImpl extends NamedAccessorImpl implements SCMAccessor {
    private String url;
    private SVNRepository svnRepository = null;
    public static Logger logger = null;

    public SCMAccessorImpl( long id, String projectName, String url ) {
        super(id,projectName);
        this.url = url;
        if (logger != null) {
            logger.info("Created SCMAccessor for " + getName());
        }
    }

    /**
     * Connect to the repository named in the constructor (the URL
     * is stored in this.url); may set the repo to null on error.
     */
    private void connectToRepository()
        throws InvalidRepositoryException {
        try {
            svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIDecoded(url));
            // All access is assumed to be anonynmous, so no
            // authentication manager is used.
        } catch (SVNException e) {
            logger.warn("Could not create SVN repository connection for " + getName() +
                e.getMessage());
            svnRepository = null;
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }
    }

    /**
     * For a ProjectRevision which has only got a date associated
     * with it (typically from things like revisions stated
     * as a {YYYYMMDD} string) resolve the date to a SVN revision
     * number. Throws SVNException on errors in the underlying
     * library or InvalidProjectRevisionException if the
     * ProjectRevision can't be used for resolution.
     */
    private long resolveDatedProjectRevision( ProjectRevision r )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        if ( (r==null) || (!r.hasDate()) ) {
            throw new InvalidProjectRevisionException("Can only resolve a revision with a date",
                          ProjectRevision.Kind.FROM_DATE);
        }

        long revno = -1;
        try {
            revno = svnRepository.getDatedRevision(r.getDate());
        } catch (SVNException e) {
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }
        if (revno > 0) {
            r.setSVNRevision(revno);
        }
        return revno;
    }

    // Interface methods
    public long resolveProjectRevision( ProjectRevision r )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        if ( (r==null) || (!r.isValid()) ) {
            throw new InvalidProjectRevisionException("Can only resolve a valid revision",null);
        }

        if (r.hasSVNRevision()) {
            if (r.getSVNRevision() > getHeadRevision()) {
                throw new InvalidProjectRevisionException(
                        r.getSVNRevision() + " > HEAD", null);
            }
            return r.getSVNRevision();
        } else {
            return resolveDatedProjectRevision(r);
        }
    }

    public boolean isRevisionValid( ProjectRevision r )
        throws InvalidRepositoryException {
        if ( (r==null) || (!r.isValid()) ) {
            return false;
        }
        if (svnRepository == null) {
            connectToRepository();
        }
        if (!r.hasSVNRevision()) {
            // Must be a dated revision, those are always ok
            // TODO: Check that. What about dated revisions < r.0 ?
            return true;
        }
        long n = r.getSVNRevision();
        return ( (n>=1) && (n<=getHeadRevision()) );
    }

    public long getHeadRevision()
        throws InvalidRepositoryException {
        long endRevision = -1;
        if (svnRepository == null) {
            connectToRepository();
        }
        try {
            endRevision = svnRepository.getLatestRevision();
            logger.info("Latest revision of " + getName() + " is " + endRevision);
        } catch (SVNException e) {
            logger.warn("Could not get latest revision of " + getName() +
                e.getMessage());
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }

        return endRevision;
    }

    public void getCheckout(String repoPath, ProjectRevision revision,
        File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }
        CheckoutEditor.logger = logger;
        CheckoutBaton.logger = logger;

        logger.info("Checking out project " + getName() + " path <" +
            repoPath + "> in " + revision + " in <" +
            localPath + ">");

        long revno = resolveProjectRevision(revision);
        SVNNodeKind nodeKind;
        try {
            nodeKind = svnRepository.checkPath(repoPath, revno);
        } catch (SVNException e) {
            throw new FileNotFoundException(repoPath);
        }

        // Handle the various kinds of nodes that repoPath may refer to
        if ( (SVNNodeKind.NONE == nodeKind) ||
                (SVNNodeKind.UNKNOWN == nodeKind) ) {
            logger.info("Requested path " + repoPath + " does not exist.");
            throw new FileNotFoundException(repoPath);
        }
        if (SVNNodeKind.FILE == nodeKind) {
            getFile(repoPath, revision, new File(localPath, repoPath));
            return;
        }
        // It must be a directory now.
        if (SVNNodeKind.DIR != nodeKind) {
            logger.warn("Node " + repoPath + " has weird type.");
            throw new FileNotFoundException(repoPath);
        }

        ISVNReporterBaton baton = new CheckoutBaton(revno);
        ISVNEditor editor = new CheckoutEditor(revno,localPath);

        try {
            svnRepository.update(revno,repoPath,true,baton,editor);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }
    }

    public void updateCheckout(String repoPath, ProjectRevision src,
        ProjectRevision dst, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }
        CheckoutEditor.logger = logger;
        CheckoutBaton.logger = logger;

        logger.info("Updating project " + getName() + " path <" +
            repoPath + "> from " + src + " to " + dst + " in <" +
            localPath + ">");

        resolveProjectRevision(src);
        long revno = resolveProjectRevision(dst);
        SVNNodeKind nodeKind;
        try {
            nodeKind = svnRepository.checkPath(repoPath, revno);
        } catch (SVNException e) {
            throw new FileNotFoundException(repoPath);
        }

        // Handle the various kinds of nodes that repoPath may refer to
        if ( (SVNNodeKind.NONE == nodeKind) ||
                (SVNNodeKind.UNKNOWN == nodeKind) ) {
            logger.info("Requested path " + repoPath + " does not exist.");
            throw new FileNotFoundException(repoPath);
        }
        if (SVNNodeKind.FILE == nodeKind) {
            getFile(repoPath, dst, new File(localPath, repoPath));
            return;
        }
        // It must be a directory now.
        if (SVNNodeKind.DIR != nodeKind) {
            logger.warn("Node " + repoPath + " has weird type.");
            throw new FileNotFoundException(repoPath);
        }

        ISVNReporterBaton baton = new CheckoutBaton(src.getSVNRevision(),
            dst.getSVNRevision());
        ISVNEditor editor = new CheckoutEditor(dst.getSVNRevision(),localPath);

        try {
            svnRepository.update(revno,repoPath,true,baton,editor);
        } catch (SVNException e) {
            e.printStackTrace();
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }
    }

    public void getFile(String repoPath,
            ProjectRevision revision, OutputStream stream)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        // Connect to the repository if a connection has not yet been created
        if (svnRepository == null) {
            connectToRepository();
        }

        long revno = resolveProjectRevision(revision);
        try {
            SVNNodeKind nodeKind = svnRepository.checkPath(repoPath, revno);
            logger.info("Requesting path \"" + repoPath + "\", revision " + revno);
            logger.info("nodeKind=" + nodeKind.toString());
            /* NOTE: Seems like checkPath() sometimes returns a node kind in
             *       small letter (i.e. "dir" instead of "DIR"). Converting it
             *       to upper case solves the "problem", although the actual
             *       reason for such a behavior is not clear.
             */
            // TODO: aboves NOTE should not matter, actually it didn't worked :-/
            //nodeKind = SVNNodeKind.parseKind(
            //        nodeKind.toString().toUpperCase());

            if (SVNNodeKind.NONE == nodeKind) {
                logger.info(
                        "Requested path "
                        + repoPath
                        + " does not exist.");
                throw new FileNotFoundException(repoPath);
            }
            if (SVNNodeKind.DIR == nodeKind) {
                logger.info(
                        "Requested path "
                        + repoPath
                        + " is a directory.");
                throw new FileNotFoundException(repoPath + " (dir)");
            }
            if (SVNNodeKind.UNKNOWN == nodeKind) {
                logger.info(
                        "Requested path "
                        + repoPath
                        + " is of unknown type.");
                throw new FileNotFoundException(repoPath + " (unknown)");
            }

            svnRepository.getFile(
                repoPath, revno, null, stream);
            stream.close();
        } catch (SVNException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            logger.warn("Failed to close output stream on SVN request." + e);
            // Swallow this exception.
        }
    }

    public void getFile(String repoPath,
            ProjectRevision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        FileOutputStream stream = new FileOutputStream(localPath);
        getFile(repoPath, revision, stream);
        // Stream was closed by other getFile()
    }

    public CommitEntryImpl getCommitLog(String repoPath, ProjectRevision r1)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        CommitLogImpl l = getCommitLog(repoPath,r1,r1);
        Iterator<CommitEntry> i = l.iterator();
        CommitEntry e = i.next();
        return (CommitEntryImpl)e;
    }

    public CommitLogImpl getCommitLog(ProjectRevision r1, ProjectRevision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        return getCommitLog("",r1,r2);
    }

    public CommitLogImpl getCommitLog(String repoPath, ProjectRevision r1, ProjectRevision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        if (svnRepository == null) {
            connectToRepository();
        }

        if ((r1 == null) || (!r1.isValid())) {
            throw new InvalidProjectRevisionException("Invalid start revision",null);
        }

        // Map the project revisions to SVN revision numbers
        long revstart=-1, revend=-1;
        revstart = resolveProjectRevision(r1);
        logger.info("Start revision for log " + r1);

        if (r2 == null) {
            revend = revstart;
        } else {
            if (!r2.isValid()) {
                throw new InvalidProjectRevisionException("Invalid end revision",null);
            }
            revend = resolveProjectRevision(r2);
            logger.info("End revision for log " + r2);
        }

        ArrayList<SVNLogEntry> l = new ArrayList<SVNLogEntry>();
        CommitLogImpl result = new CommitLogImpl();
        
        String checkoutRoot;
        try {
            checkoutRoot = svnRepository.getRepositoryPath("");
            svnRepository.log(new String[]{repoPath},
                l,revstart, revend, true, true);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(getName(), url, e.getMessage());
        }

        
        Iterator<SVNLogEntry> i = l.iterator();
        while(i.hasNext()) {
            SVNLogEntry entry = i.next();
            result.getEntriesReference().add(new CommitEntryImpl(entry, checkoutRoot));   
        }
        
        return result;
    }

    public Diff getDiff( String repoPath, ProjectRevision r1, ProjectRevision r2 )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }

        if ((r1 == null) || (!r1.isValid())) {
            throw new InvalidProjectRevisionException("Invalid start revision",null);
        }

        // Map the project revisions to SVN revision numbers
        long revstart=-1, revend=-1;
        revstart = resolveProjectRevision(r1);
        logger.info("Start revision for diff " + r1);

        if (r2 == null) {
            revend = revstart + 1;
        } else {
            if (!r2.isValid()) {
                throw new InvalidProjectRevisionException("Invalid end revision",null);
            }
            revend = resolveProjectRevision(r2);
            logger.info("End revision for diff " + r2);
        }

        SVNNodeKind nodeKind;
        try {
            nodeKind = svnRepository.checkPath(repoPath, revstart);
        } catch (SVNException e) {
            throw new FileNotFoundException(repoPath);
        }

        // Handle the various kinds of nodes that repoPath may refer to
        if ( (SVNNodeKind.NONE == nodeKind) ||
                (SVNNodeKind.UNKNOWN == nodeKind) ) {
            logger.info("Requested path " + repoPath + " does not exist.");
            throw new FileNotFoundException(repoPath);
        }

        try {
            SVNDiffClient d = new SVNDiffClient(svnRepository.getAuthenticationManager(),null);
            File f = File.createTempFile("tds",".diff");
            SVNURL u = svnRepository.getLocation().appendPath(repoPath,true);
            d.doDiff(u,
                SVNRevision.create(revstart),
                SVNRevision.create(revstart),
                SVNRevision.create(revend),
                true,
                false,
                new FileOutputStream(f));
            // Store the diff
            DiffImpl theDiff = new DiffImpl(r1,r2,f);
            // Add status information
            d.doDiffStatus(u,SVNRevision.create(revstart),
                u,SVNRevision.create(revend),
                true,false,new DiffStatusHandler(theDiff));
            f.deleteOnExit();
            logger.info("Done diff of " + repoPath +
                " to " + f.getAbsolutePath());
            return theDiff;
        } catch (SVNException e) {
            logger.warn(e.getMessage());
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new FileNotFoundException("Could not create temporary file for diff.");
        }
    }

    public Diff getChange(String repoPath, ProjectRevision r)
        throws InvalidProjectRevisionException,
            InvalidRepositoryException,
            FileNotFoundException {
        return getDiff(repoPath, r.prev(), r);
    }

    public SCMNodeType getNodeType(String repoPath, ProjectRevision r)
            throws InvalidRepositoryException {
        try {
            SVNNodeKind k = svnRepository.checkPath(repoPath, r.getSVNRevision());
            if (k == SVNNodeKind.DIR)
                return SCMNodeType.DIR;
            
            if (k == SVNNodeKind.FILE)
                return SCMNodeType.FILE;
            
            return SCMNodeType.UNKNOWN;
            
        } catch (SVNException e) {
            logger.warn(e.getMessage());
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }
    }

	public String getSubProjectPath() throws InvalidRepositoryException {
        if (svnRepository == null) {
            connectToRepository();
        }
        
        try {
			return svnRepository.getRepositoryPath("");
		} catch (SVNException e) {
            logger.warn(e.getMessage());
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
		}
	}
}

// vi: ai nosi sw=4 ts=4 expandtab

