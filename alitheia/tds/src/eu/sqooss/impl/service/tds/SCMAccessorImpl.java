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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;

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
            logger.warning("Could not create SVN repository connection for " + getName() +
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
                throw new InvalidProjectRevisionException("Revision > HEAD", null);
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
            logger.warning("Could not get latest revision of " + getName() +
                e.getMessage());
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }

        return endRevision;
    }

    public void checkOut( String repoPath, ProjectRevision revision, String localPath )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }
        CheckoutEditor.logger = logger;
        CheckoutBaton.logger = logger;

        logger.info("Checking out project " + getName() + " path <" +
            repoPath + "> in " + revision + " to <" +
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
            checkOutFile(repoPath, revision,
                localPath + File.separator + new File(repoPath).getName());
            return;
        }
        // It must be a directory now.
        if (SVNNodeKind.DIR != nodeKind) {
            logger.warning("Node " + repoPath + " has weird type.");
            throw new FileNotFoundException(repoPath);
        }

        ISVNReporterBaton baton = new CheckoutBaton(revno,localPath);
        ISVNEditor editor = new CheckoutEditor(revno,localPath);

        try {
            svnRepository.update(revno,repoPath,true,baton,editor);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        }
    }

    public void checkOutFile( String repoPath,
        ProjectRevision revision, String localPath )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }

        long revno = resolveProjectRevision(revision);
        try {
            SVNNodeKind nodeKind = svnRepository.checkPath(repoPath, revno);
            if (SVNNodeKind.NONE == nodeKind) {
                logger.info("Requested path " + repoPath + " does not exist.");
                throw new FileNotFoundException(repoPath);
            }
            if (SVNNodeKind.DIR == nodeKind) {
                logger.info("Requested path " + repoPath + " is a directory.");
                throw new FileNotFoundException(repoPath + " (dir)");
            }

            FileOutputStream stream = new FileOutputStream(localPath);
            long retrieved_revision = svnRepository.getFile(
                repoPath, revno, null, stream);
            stream.close();
        } catch (SVNException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            logger.warning("Failed to close output stream on SVN request.");
            // Swallow this exception.
        }
    }

    public CommitLog getCommitLog( ProjectRevision r1, ProjectRevision r2 )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        return getCommitLog("",r1,r2);
    }

    public CommitLog getCommitLog( String repoPath, ProjectRevision r1, ProjectRevision r2 )
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

        CommitLogImpl l = new CommitLogImpl();
        try {
            Collection logEntries = svnRepository.log(new String[]{repoPath},
                l.getEntriesReference(),
                revstart, revend, true, true);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(getName(), url, e.getMessage());
        }

        return l;
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
            logger.warning(e.getMessage());
            throw new InvalidRepositoryException(getName(),url,e.getMessage());
        } catch (IOException e) {
            logger.warning(e.getMessage());
            throw new FileNotFoundException("Could not create temporary file for diff.");
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

