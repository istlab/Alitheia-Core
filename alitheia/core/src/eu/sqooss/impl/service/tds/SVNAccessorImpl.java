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
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNRevisionProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.ISVNReporterBaton;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.CommitEntry;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNodeType;

public class SVNAccessorImpl implements SCMAccessor {
    private String url;
    private String projectname;
    private SVNRepository svnRepository = null;
    private Logger logger = null;

    private static List<URI> supportedSchemes;
    
    static {
        // Initialize access methods for all the repo types
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
        
        supportedSchemes = new ArrayList<URI>();
        supportedSchemes.add(URI.create("svn-file://www.sqo-oss.org"));
        supportedSchemes.add(URI.create("svn://www.sqo-oss.org"));
        supportedSchemes.add(URI.create("svn-http://www.sqo-oss.org"));
    }
    
    public SVNAccessorImpl() {
        //Default constructor
    }
    
    public List<URI> getSupportedURLSchemes() {
        return null;
       // return supportedSchemes;
    }

    public void init(URI dataURL, String name) throws AccessorException {
        this.url = convertURI(dataURL);
        this.projectname = name;
        logger = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_TDS);
        if (logger != null) {
            logger.info("Created SCMAccessor for " + url);
        }     
        try {
            connectToRepository();
        } catch (InvalidRepositoryException e) {
            throw new AccessorException(this.getClass(), e.getMessage());
        }
    }
    
    /**Convert form Alitheia URL to SVN URL*/
    private String convertURI(URI uri) {
        String s = uri.toString();
        s = s.replace("svn-file", "file");
        s = s.replace("svn-http", "http");
        return s;
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
            logger.error("Could not create SVN repository connection for " + url +
                e.getMessage());
            svnRepository = null;
            throw new InvalidRepositoryException(url,e.getMessage());
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
    private long resolveDatedProjectRevision( SVNProjectRevision r )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        if ((r == null) || !r.hasDate()) {
            throw new InvalidProjectRevisionException(
                    "Can only resolve revisions with date attached", getClass());
        }

        long revno = -1;
        try {
            revno = svnRepository.getDatedRevision(r.getDate());
        } catch (SVNException e) {
            throw new InvalidRepositoryException(url,e.getMessage());
        }
        if (revno > 0) {
            r.setSVNRevision(revno);
        }
        return revno;
    }
    
    /**
     * Get the Date for a revision number
     */
    private Date resolveRevisionDate(SVNProjectRevision r)
            throws InvalidProjectRevisionException, InvalidRepositoryException {
        if ((r == null) || !r.hasSVNRevision()) {
            throw new InvalidProjectRevisionException(
                    "Can only resolve revisions with SVN version attached.", getClass());
        }

        Date d = null;
        String date = "";
        try {
            date = svnRepository.getRevisionPropertyValue(r.getSVNRevision(),
                    SVNRevisionProperty.DATE);
            SimpleDateFormat dateParser = new SimpleDateFormat("y-M-d'T'H:m:s.S'Z'");
            d = dateParser.parse(date);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(url, e.getMessage());
        } catch (ParseException pe) {
            throw new InvalidProjectRevisionException("Cannot parse date "
                    + date + " for revision " + r.getSVNRevision() + " "
                    + pe.getMessage(), getClass());
        }
        if (d == null) {
            logger.warn("Resolved date is null");
        }
        r.setDate(d);
        return d;
    }

    /**
     * Get latest svn revision as long
     */
    private long getHeadSVNRevision() throws InvalidRepositoryException {
        long endRevision = -1;
        if (svnRepository == null) {
            connectToRepository();
        }
        try {
            endRevision = svnRepository.getLatestRevision();
        } catch (SVNException e) {
            logger.warn("Could not get latest revision of " + url
                    + e.getMessage());
            throw new InvalidRepositoryException(url, e.getMessage());
        }

        return endRevision;
    }
    
    /**
     * Dummy check to see if revision 1 is indeed the first revision. 
     */
    private long getFirstSVNRevision() throws InvalidRepositoryException {
        if (svnRepository == null) {
            connectToRepository();
        }
        try {
            svnRepository.getRevisionPropertyValue(1, SVNProperty.REVISION);
        } catch (SVNException e) {
            logger.warn("Could not get revision 1 from repository " + url + 
                    e.getMessage());
            throw new InvalidRepositoryException(url, e.getMessage());
        }

        return 1;
    }
    
    /** {@inheritDoc}} */
    private boolean resolveRevision(Revision r) 
        throws InvalidProjectRevisionException {

        if ((r == null)) {
            throw new InvalidProjectRevisionException(projectname + 
                    ": Revision to be resolved is null", getClass());
        }

        if (((SVNProjectRevision) r).isResolved()) {
            // No resolution necessary
            return true;
        }

        try {
            if (((SVNProjectRevision) r).hasSVNRevision()) {
                if (((SVNProjectRevision) r).getSVNRevision() > getHeadSVNRevision()) {
                    throw new InvalidProjectRevisionException(
                            ((SVNProjectRevision) r).getSVNRevision() + 
                                " > HEAD", getClass());
                }
                
                if (((SVNProjectRevision) r).getSVNRevision() < getFirstSVNRevision()) {
                    
                }
                
                //Resolve date
                Date d = resolveRevisionDate(((SVNProjectRevision) r));
            
                if (d == null) {
                    ((SVNProjectRevision) r).setResolved(Revision.Status.INVALID);
                    return false;
                }
            } else {
                //Resolve SVN revision number
                long l;

                l = resolveDatedProjectRevision(((SVNProjectRevision) r));

                if (l < 0) {
                    ((SVNProjectRevision) r).setResolved(Revision.Status.INVALID);
                    return false;
                }
            }
        } catch (InvalidRepositoryException e) {
            throw new InvalidProjectRevisionException("Revision " + r + 
                    " of project " + projectname + "refers to invalid project " +
                    "repository " + url, getClass());
        }
        //Resolution OK, mark the revision
        ((SVNProjectRevision)r).setResolved(Revision.Status.RESOLVED);
        return true;
    }
    
    // Interface methods
    /** {@inheritDoc}} */
    public boolean isValidRevision(Revision r) {
        if ((r == null)) {
            return false;
        }
        
        if (! (r instanceof SVNProjectRevision)) {
            return false;
        }

        boolean isValid = false;
        try {
            isValid = resolveRevision(r); 
        } catch (InvalidProjectRevisionException e) {
            isValid = false;
        }
        
        return isValid;
    }

    /** {@inheritDoc}} */
    public Revision getHeadRevision() 
        throws InvalidRepositoryException {
       
        long head = getHeadSVNRevision();
        Revision s = new SVNProjectRevision(head);
        
        if (!isValidRevision(s))
            return null;
        
        return s;
    }
    
    /**{@inheritDoc}}*/
    public Revision getFirstRevision()
        throws InvalidRepositoryException {
        long head = getFirstSVNRevision();
        Revision s = new SVNProjectRevision(head);
        
        if (!isValidRevision(s))
            return null;
        
        return s;
    }
    
    /**{@inheritDoc}*/
    public Revision getNextRevision(Revision r) throws InvalidProjectRevisionException {
        SVNProjectRevision svnr = (SVNProjectRevision)r;
        
        try {
            if (svnr.getSVNRevision() + 1 > getHeadSVNRevision()) {
                throw new InvalidProjectRevisionException(
                        "Cannot get next revision of HEAD revision", 
                        getClass());
            }
        } catch (InvalidRepositoryException e) {
            throw new InvalidProjectRevisionException(e.getMessage(), getClass());
        }
        SVNProjectRevision next = new SVNProjectRevision(svnr.getSVNRevision() + 1); 
        resolveRevision(next);
        return next;
    }
    
    /**{@inheritDoc}*/
    public Revision getPreviousRevision(Revision r) 
        throws InvalidProjectRevisionException {
        SVNProjectRevision svnr = (SVNProjectRevision)r;
        
        try {
            if (svnr.getSVNRevision() - 1 < getFirstSVNRevision()) {
                throw new InvalidProjectRevisionException(
                        "Cannot get previous revision of revision 1", 
                        getClass());
            }
        } catch (InvalidRepositoryException e) {
            throw new InvalidProjectRevisionException(e.getMessage(), getClass());
        }
        
        SVNProjectRevision prev = new SVNProjectRevision(svnr.getSVNRevision() - 1); 
        resolveRevision(prev);
        return prev;
    }
    
    /**{@inheritDoc}*/
    public Revision newRevision(Date d) {
        
        if (d == null) {
            logger.error("Cannot create new revision with null or empty" +
                        " date");
            return null;
        }
         
         SVNProjectRevision r = new SVNProjectRevision(d);
         
         try {
            resolveRevision(r);
        } catch (InvalidProjectRevisionException e) {
            logger.error("Cannot create dated revision " + d.toString() + ":" + e);
            return null;
        } 
        
        return r;
    }
    
    /**{@inheritDoc}*/
    public Revision newRevision(String uniqueId) {
        long revision = -1;
        
        if (uniqueId == null || uniqueId.equals("")) {
            logger.error("Cannot create new revision with null or empty" +
            		" revisionid");
            return null;
        }
        
         try{ 
             revision = Long.parseLong(uniqueId);
         } catch (NumberFormatException nfe) {
             logger.error("Invalid SVN revision id" + uniqueId);
             return null;
         }
         
         SVNProjectRevision r = new SVNProjectRevision(revision);
         
         try {
            resolveRevision(r);
        } catch (InvalidProjectRevisionException e) {
            logger.error("Cannot create revision " + uniqueId + ":" + e);
            return null;
        } 
        
        return r; 
    }
    
    /**{@inheritDoc}*/
    public void getCheckout(String repoPath, Revision rev,
        File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }
        SVNCheckoutEditor.logger = logger;
        SVNCheckoutBaton.logger = logger;

        logger.debug("Checking out from repository " + url + " path <" +
            repoPath + "> rev " + rev.getUniqueId() + " in <" +
            localPath + ">");

        if (!resolveRevision(rev)) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }
        
        SVNProjectRevision svnrev = (SVNProjectRevision)rev;
        
        SVNNodeKind nodeKind;
        try {
            nodeKind = svnRepository.checkPath(repoPath, svnrev.getSVNRevision());
        } catch (SVNException e) {
            throw new FileNotFoundException(repoPath);
        }

        // Handle the various kinds of nodes that repoPath may refer to
        if ((SVNNodeKind.NONE == nodeKind) || (SVNNodeKind.UNKNOWN == nodeKind)) {
            logger.info("Requested path " + repoPath + " does not exist.");
            throw new FileNotFoundException(repoPath);
        }
        if (SVNNodeKind.FILE == nodeKind) {
            getFile(repoPath, rev, new File(localPath, repoPath));
            return;
        }
        // It must be a directory now.
        if (SVNNodeKind.DIR != nodeKind) {
            logger.warn("Node " + repoPath + " has weird type.");
            throw new FileNotFoundException(repoPath);
        }

        ISVNReporterBaton baton = new SVNCheckoutBaton(svnrev.getSVNRevision());
        ISVNEditor editor = new SVNCheckoutEditor(svnrev.getSVNRevision(),localPath);

        try {
            svnRepository.update(svnrev.getSVNRevision(),repoPath,true,baton,editor);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(url,e.getMessage());
        }
    }

    /**{@inheritDoc}*/
    public void updateCheckout(String repoPath, Revision src, Revision dst, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }
        SVNCheckoutEditor.logger = logger;
        SVNCheckoutBaton.logger = logger;

        logger.info("Updating path <" + repoPath + "> from " + src + " to "
                + dst + " in <" + localPath + ">");

        if (!resolveRevision(dst)) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }
        
        if (!resolveRevision(src)) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }
        SVNProjectRevision svndst = (SVNProjectRevision)dst;
        SVNProjectRevision svnsrc = (SVNProjectRevision)src;
        
        SVNNodeKind nodeKind;
        try {
            nodeKind = svnRepository.checkPath(repoPath, svndst.getSVNRevision());
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

        ISVNReporterBaton baton = new SVNCheckoutBaton(svnsrc.getSVNRevision(),
            svndst.getSVNRevision());
        ISVNEditor editor = new SVNCheckoutEditor(svndst.getSVNRevision(),localPath);

        try {
            svnRepository.update(svndst.getSVNRevision(),repoPath,true,baton,editor);
        } catch (SVNException e) {
            e.printStackTrace();
            throw new InvalidRepositoryException(url,e.getMessage());
        }
    }

    /**{@inheritDoc}*/
    public void getFile(String repoPath, Revision revision, OutputStream stream)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        // Connect to the repository if a connection has not yet been created
        if (svnRepository == null) {
            connectToRepository();
        }

        if (!resolveRevision(revision)) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }        
        long revno = ((SVNProjectRevision)revision).getSVNRevision();
        
        try {
            SVNNodeKind nodeKind = svnRepository.checkPath(repoPath, revno);
            logger.debug(projectname + ": Requesting path " + repoPath
                    + ", revision " + revno + ", nodeKind="
                    + nodeKind.toString());
            /* NOTE: Seems like checkPath() sometimes returns a node kind in
             *       small letter (i.e. "dir" instead of "DIR"). Converting it
             *       to upper case solves the "problem", although the actual
             *       reason for such a behavior is not clear.
             */
            // TODO: aboves NOTE should not matter, actually it didn't worked :-/
            //nodeKind = SVNNodeKind.parseKind(
            //        nodeKind.toString().toUpperCase());

            if (SVNNodeKind.NONE == nodeKind) {
                logger.warn(projectname + ": Requested path " + repoPath
                        + " does not exist.");
                throw new FileNotFoundException(repoPath);
            }
            if (SVNNodeKind.DIR == nodeKind) {
                logger.warn(projectname + ": Requested path " + repoPath
                        + " is a directory.");
                throw new FileNotFoundException(repoPath + " (dir)");
            }
            if (SVNNodeKind.UNKNOWN == nodeKind) {
                logger.warn(projectname + ": Requested path " + repoPath
                        + " is of unknown type.");
                throw new FileNotFoundException(repoPath + " (unknown)");
            }

            svnRepository.getFile(repoPath, revno, null, stream);
            stream.close();
        } catch (SVNException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            logger.warn("Failed to close output stream on SVN request." + e);
            // Swallow this exception.
        }
    }

    /**{@inheritDoc}*/
    public void getFile(String repoPath,
            Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        FileOutputStream stream = new FileOutputStream(localPath);
        getFile(repoPath, revision, stream);
        // Stream was closed by other getFile()
    }
    
    /**{@inheritDoc}*/
    public SVNCommitLogImpl getCommitLog(Revision r)
        throws InvalidProjectRevisionException, 
               InvalidRepositoryException {
        Revision r1 = getPreviousRevision(r);
        return getCommitLog("",r1,r);
    }

    /**{@inheritDoc}*/
    public SVNCommitEntryImpl getCommitLog(String repoPath, Revision r1)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        SVNCommitLogImpl l = getCommitLog(repoPath,r1,r1);
        Iterator<CommitEntry> i = l.iterator();
        CommitEntry e = i.next();
        return (SVNCommitEntryImpl)e;
    }

    /**{@inheritDoc}*/
    public SVNCommitLogImpl getCommitLog(Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        return getCommitLog("",r1,r2);
    }

    /**{@inheritDoc}*/
    public SVNCommitLogImpl getCommitLog(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        if (svnRepository == null) {
            connectToRepository();
        }
        
        // Map the project revisions to SVN revision numbers
        long revstart=-1, revend=-1;
        logger.debug("Start revision for log " + r1);
        
        if ((r1 == null) || (!resolveRevision(r1))) {
            throw new InvalidProjectRevisionException("Invalid start revision", getClass());
        }
        revstart = ((SVNProjectRevision)r1).getSVNRevision();

        if (r2 == null) {
            revend = revstart;
        } else {
            if (!resolveRevision(r2)) {
                throw new InvalidProjectRevisionException("Invalid end revision",getClass());
            }
            revend = ((SVNProjectRevision)r2).getSVNRevision();
            logger.debug("End revision for log " + r2);
        }

        ArrayList<SVNLogEntry> l = new ArrayList<SVNLogEntry>();
        SVNCommitLogImpl result = new SVNCommitLogImpl();
        
        String checkoutRoot;
        try {
            checkoutRoot = svnRepository.getRepositoryPath("");
            svnRepository.log(new String[]{repoPath},
                l,revstart, revend, true, true);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(url, e.getMessage());
        }

        
        Iterator<SVNLogEntry> i = l.iterator();
        while(i.hasNext()) {
            SVNLogEntry entry = i.next();
            result.getEntriesReference().add(new SVNCommitEntryImpl(entry, checkoutRoot));   
        }
        
        return result;
    }

    /**{@inheritDoc}*/
    public Diff getDiff( String repoPath, Revision r1, Revision r2 )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }

        //Map the project revisions to SVN revision numbers
        long revstart=-1, revend=-1;
        
        if ((r1 == null) || (!resolveRevision(r1))) {
            throw new InvalidProjectRevisionException("Invalid start revision", getClass());
        }
        revstart = ((SVNProjectRevision)r1).getSVNRevision();
        logger.info("Start revision for diff " + r1);

        if (r2 == null) {
            if (revstart == getHeadSVNRevision()) {
                revend = revstart;
            } else {
                revend = revstart + 1;
            }   
        } else {
            if (!resolveRevision(r2)) {
                throw new InvalidProjectRevisionException("Invalid end revision", getClass());
            }
            revend = ((SVNProjectRevision)r2).getSVNRevision();
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

        File f = null;
        try {
            SVNDiffClient d = new SVNDiffClient(svnRepository.getAuthenticationManager(),null);
            f = File.createTempFile("tds-" + Thread.currentThread().getId(),".diff");
            SVNURL u = svnRepository.getLocation().appendPath(repoPath,true);
            d.doDiff(u,
                SVNRevision.create(revstart),
                SVNRevision.create(revstart),
                SVNRevision.create(revend),
                true,
                false,
                new FileOutputStream(f));
            // Store the diff
            SVNDiffImpl theDiff = new SVNDiffImpl((SVNProjectRevision)r1, (SVNProjectRevision)r2,f);
            // Add status information
            d.doDiffStatus(u,SVNRevision.create(revstart),
                u,SVNRevision.create(revend),
                true,false,new SVNDiffStatusHandler(theDiff));
            f.delete();
            logger.info("Done diff of " + repoPath +
                " to " + f.getAbsolutePath());
            return theDiff;
        } catch (SVNException e) {
            logger.warn(e.getMessage());
            throw new InvalidRepositoryException(url,e.getMessage());
        } catch (IOException e) {
            logger.warn(e.getMessage());
            throw new FileNotFoundException("Could not create temporary file for diff.");
        } finally {
            f.delete();
        }
    }

    /**{@inheritDoc}*/
    public Diff getChange(String repoPath, Revision r)
        throws InvalidProjectRevisionException,
            InvalidRepositoryException,
            FileNotFoundException {
        return getDiff(repoPath, getPreviousRevision(r), r);
    }

    /**{@inheritDoc}*/
    public SCMNodeType getNodeType(String repoPath, Revision r)
            throws InvalidRepositoryException {
        try {
            SVNNodeKind k = svnRepository.checkPath(repoPath, ((SVNProjectRevision)r).getSVNRevision());
            if (k == SVNNodeKind.DIR)
                return SCMNodeType.DIR;
            
            if (k == SVNNodeKind.FILE)
                return SCMNodeType.FILE;
            
            return SCMNodeType.UNKNOWN;
            
        } catch (SVNException e) {
            logger.warn(e.getMessage());
            throw new InvalidRepositoryException(url,e.getMessage());
        }
    }

    /**{@inheritDoc}*/
    public String getSubProjectPath() throws InvalidRepositoryException {
        if (svnRepository == null) {
            connectToRepository();
        }

        try {
            return svnRepository.getRepositoryPath("");
        } catch (SVNException e) {
            logger.warn(e.getMessage());
            throw new InvalidRepositoryException( url, e.getMessage());
        }
    }

    public String toString() {
        return projectname.concat(":").concat(url);
    }
}

// vi: ai nosi sw=4 ts=4 expandtab

