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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.tmatesoft.svn.core.SVNDirEntry;
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
import org.tmatesoft.svn.core.wc.ISVNAnnotateHandler;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.AnnotatedLine;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.Diff;
import eu.sqooss.service.tds.DiffFactory;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.PathChangeType;
import eu.sqooss.service.tds.Revision;
import eu.sqooss.service.tds.SCMAccessor;
import eu.sqooss.service.tds.SCMNode;
import eu.sqooss.service.tds.SCMNodeType;
import eu.sqooss.service.util.FileUtils;

public class SVNAccessorImpl extends eu.sqooss.plugins.tds.scm.SCMAccessor {
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
        return supportedSchemes;
    }

    public void init(URI dataURL, String name) throws AccessorException {
        logger = AlitheiaCore.getInstance().getLogManager().createLogger(Logger.NAME_SQOOSS_TDS);

        this.url = convertURI(dataURL);
        this.projectname = name;
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
            svnRepository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
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
        if ((r == null) || r.getDate() == null) {
            throw new InvalidProjectRevisionException(
                    "Can only resolve revisions with a valid date", getClass());
        }

        long revno = -1;
        try {
            revno = svnRepository.getDatedRevision(r.getDate());
        } catch (SVNException e) {
            throw new InvalidRepositoryException(url,e.getMessage());
        }
       
        return revno;
    }
    
    /**
     * Get the Date for a revision number
     */
    private Date resolveRevisionDate(SVNProjectRevision r)
            throws InvalidProjectRevisionException, InvalidRepositoryException {
        if ((r == null) || r.getSVNRevision() == -1) {
            throw new InvalidProjectRevisionException(
                    "Can only resolve revisions with a SVN version.", getClass());
        }

        Date d = null;
        String date = "";
        try {
            date = svnRepository.getRevisionPropertyValue(r.getSVNRevision(),
                    SVNRevisionProperty.DATE).getString();
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
            svnRepository.getRevisionPropertyValue(0, SVNProperty.REVISION);
        } catch (SVNException e) {
            logger.warn("Could not get revision 0 from repository " + url + 
                    e.getMessage());
            throw new InvalidRepositoryException(url, e.getMessage());
        }

        return 0;
    }
    

    /**
     * Resolve all revision fields from the repo. 
     */
    private SVNProjectRevision resolveRevision(Revision r) {

        if ((r == null)) {
            logger.error(projectname + ": Revision to be resolved is null");
            return null;
        }
        
        if (!(r instanceof SVNProjectRevision)) {
            logger.error(projectname + ": " +
            		"Non SVN revision appearing in SVN project?");
            return null;
        }
        
        SVNProjectRevision svnrev = (SVNProjectRevision)r;

        if (svnrev.isResolved()) {
            // No resolution necessary
            return svnrev;
        }

        Date d = null;
        long l = -1;
        try {
            if (svnrev.getSVNRevision() != -1) {
                if (svnrev.getSVNRevision() > getHeadSVNRevision()) {
                    logger.error(String.valueOf(svnrev.getSVNRevision())
                            + " > HEAD");
                    return null;
                }
                
                if (svnrev.getSVNRevision() < getFirstSVNRevision()) {
                    logger.error(String.valueOf(svnrev.getSVNRevision())
                            + " < 0");
                    return null;
                }
                
                //Resolve date
                d = resolveRevisionDate(svnrev);

                if (d == null) {
                    return null;
                }
            } else {
                //Resolve SVN revision number
                l = resolveDatedProjectRevision(svnrev);

                if (l < 0) {
                    return null;
                }
            }
            
            if (svnrev.getSVNRevision() == 0) {
                SVNLogEntry logEntry = new SVNLogEntry(Collections.EMPTY_MAP, 0, 
                        "sqo-oss", d, "Repository Init");
                
                SVNProjectRevision spr = new SVNProjectRevision(logEntry, "");
                return spr;
            }
            
            List<SVNLogEntry> log = Collections.EMPTY_LIST;
            if (svnrev.getSVNRevision() + 1 < getHeadSVNRevision())
                log = getSVNLog("", svnrev.getSVNRevision(), svnrev.getSVNRevision() + 1);
            else 
                log = getSVNLog("", svnrev.getSVNRevision(), -1);
            SVNLogEntry full = log.iterator().next();
            return new SVNProjectRevision(full, "");
        } catch (InvalidRepositoryException e) {
            logger.error("Revision " + r + " of project " + projectname
                    + "refers to invalid project " + "repository " + url, e);
        } catch (InvalidProjectRevisionException e) {
            logger.error("Revision " + r + " is invalid:" + e.getMessage(), e);
        }
        return null;
    }
    

    private List<SVNLogEntry> getSVNLog(String repoPath, long revstart,
            long revend) throws InvalidRepositoryException {
        ArrayList<SVNLogEntry> l = new ArrayList<SVNLogEntry>();
        try {
            svnRepository.log(new String[] { repoPath }, l, revstart, revend,
                    true, true);
        } catch (SVNException e) {
            throw new InvalidRepositoryException(url, e.getMessage());
        }
        return l;
    }
    
    // Interface methods
    /** {@inheritDoc}} */
    public boolean isValidRevision(Revision r) {
        return (resolveRevision((SVNProjectRevision)r) != null?true:false);
    }

    /** {@inheritDoc}} */
    public Revision getHeadRevision() 
        throws InvalidRepositoryException {
       
        long head = getHeadSVNRevision();
        Revision s = new SVNProjectRevision(head);
        return resolveRevision(s);
    }
    
    /** {@inheritDoc}  */
    public Revision getFirstRevision() throws InvalidRepositoryException {
        long first = getFirstSVNRevision();
        SVNProjectRevision s = new SVNProjectRevision(first);
        return resolveRevision(s);
    }
    
    /**{@inheritDoc}*/
    public Revision getNextRevision(Revision r) throws InvalidProjectRevisionException {
        SVNProjectRevision svnr = (SVNProjectRevision)r;
        
        try {
            if (svnr.getSVNRevision() + 1 > getHeadSVNRevision()) {
                throw new InvalidProjectRevisionException(
                        "Cannot get next revision of HEAD", 
                        getClass());
            }
        } catch (InvalidRepositoryException e) {
            throw new InvalidProjectRevisionException(e.getMessage(), getClass());
        }
        SVNProjectRevision next = new SVNProjectRevision(svnr.getSVNRevision() + 1); 
        return resolveRevision(next);
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
        return resolveRevision(prev);
    }

    /** {@inheritDoc} */
    public Revision newRevision(Date d) {

        if (d == null) {
            logger.error("Cannot create new revision with null or empty"
                    + " date");
            return null;
        }

        SVNProjectRevision r = new SVNProjectRevision(d);
        return resolveRevision(r);
    }

    /** {@inheritDoc} */
    public Revision newRevision(String uniqueId) {
        long revision = -1;

        if (uniqueId == null || uniqueId.equals("")) {
            logger.error("Cannot create new revision with null or empty"
                    + " revisionid");
            return null;
        }

        try {
            revision = Long.parseLong(uniqueId);
        } catch (NumberFormatException nfe) {
            logger.error("Invalid SVN revision id" + uniqueId);
            return null;
        }

        SVNProjectRevision r = new SVNProjectRevision(revision);
        return resolveRevision(r);
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
        
        SVNProjectRevision svnrev = resolveRevision(rev);
        
        if (svnrev == null) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }
         
        SVNNodeKind nodeKind;
        try {
            nodeKind = svnRepository.checkPath(repoPath, svnrev.getSVNRevision());
        } catch (SVNException e) {
            throw new FileNotFoundException(repoPath);
        }

        // Handle the various kinds of nodes that repoPath may refer to
        if ((SVNNodeKind.NONE == nodeKind) || (SVNNodeKind.UNKNOWN == nodeKind)) {
            logger.warn("Requested path " + repoPath + " does not exist.");
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

        SVNProjectRevision svndst = resolveRevision(dst);
        SVNProjectRevision svnsrc = resolveRevision(src);
        
        if (svndst == null) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }
        
        if (svnsrc == null) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }
        
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
        
        SVNProjectRevision svnrev = resolveRevision(revision);

        if (svnrev == null) {
            throw new InvalidProjectRevisionException("Cannot resolve revision",
                    getClass());
        }        
        long revno = svnrev.getSVNRevision();
        
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
                logger.warn(projectname + ": Requested path " + repoPath +
                        "@" + revision.getUniqueId() + " does not exist.");
                throw new FileNotFoundException(repoPath);
            }
            if (SVNNodeKind.DIR == nodeKind) {
                logger.warn(projectname + ": Requested path " + repoPath +
                		"@" + revision.getUniqueId() + " is a directory.");
                throw new FileNotFoundException(repoPath + " (dir)");
            }
            if (SVNNodeKind.UNKNOWN == nodeKind) {
                logger.warn(projectname + ": Requested path " + repoPath +
                		"@" + revision.getUniqueId() + " is of unknown type.");
                throw new FileNotFoundException(repoPath + " (unknown)");
            }

            svnRepository.getFile(repoPath, revno, null, stream);
            stream.close();
        } catch (SVNException e) {
            throw new FileNotFoundException(e.getMessage());
        } catch (IOException e) {
            logger.warn("Failed to close output stream on SVN request." + e 
                    + " Revision:" + revision);
            // Swallow this exception.
        }
    }

    /**{@inheritDoc}*/
    public void getFile(String repoPath,
            Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
    	if (!localPath.exists()) {
    		try {
    			localPath.createNewFile();
    		} catch (IOException e) {
    			throw new FileNotFoundException(e.getMessage());
    		}
    	}
        FileOutputStream stream = new FileOutputStream(localPath);
        getFile(repoPath, revision, stream);
        // Stream was closed by other getFile()
    }

    /**{@inheritDoc}*/
    public CommitLog getCommitLog(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {
        if (svnRepository == null) {
            connectToRepository();
        }
        
        // Map the project revisions to SVN revision numbers
        logger.debug("Start revision for log " + r1);
        
        SVNProjectRevision revstart = resolveRevision(r1);
        
        if ((r1 == null) || (revstart == null)) {
            throw new InvalidProjectRevisionException("Invalid start revision", getClass());
        }
        
        SVNProjectRevision revend = null;
        
        if (r2 == null) {
            revend = revstart;
        } else {
            revend = resolveRevision(r2);
            if (revend == null) {
                throw new InvalidProjectRevisionException("Invalid end revision",getClass());
            }
            logger.debug("End revision for log " + r2);
        }

        List<SVNLogEntry> l = getSVNLog(repoPath, revstart.getSVNRevision(), 
                revend.getSVNRevision());
        
        Iterator<SVNLogEntry> i = l.iterator();
        SVNCommitLogImpl result = new SVNCommitLogImpl();
        while (i.hasNext()) {
            SVNLogEntry entry = i.next();
            result.getEntries().add(new SVNProjectRevision(entry, ""));
        }
        
        return result;
    }

    /**{@inheritDoc}*/
    public Diff getDiff(String repoPath, Revision r1, Revision r2 )
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException {
        if (svnRepository == null) {
            connectToRepository();
        }

        //Map the project revisions to SVN revision numbers
        long revstart=-1, revend=-1;
        
        if ((r1 == null) || (resolveRevision(r1) == null)) {
            throw new InvalidProjectRevisionException("Invalid start revision", getClass());
        }
        revstart = ((SVNProjectRevision)r1).getSVNRevision();

        if (r2 == null) {
            if (revstart == getHeadSVNRevision()) {
                revend = revstart;
            } else {
                revend = revstart + 1;
            }   
        } else {
            if (resolveRevision(r2) == null) {
                throw new InvalidProjectRevisionException("Invalid end revision", getClass());
            }
            revend = ((SVNProjectRevision)r2).getSVNRevision();
        }

        logger.debug("Diffing versions " + r1.getUniqueId() + ":" 
        		+ r2.getUniqueId() + " of path " + projectname + ":" 
        		+ repoPath);
        
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
            ByteArrayOutputStream diff = new ByteArrayOutputStream();
            SVNURL u = svnRepository.getLocation().appendPath(repoPath,true);
            d.doDiff(u,
                SVNRevision.create(revstart),
                SVNRevision.create(revstart),
                SVNRevision.create(revend),
                true,
                false,
                diff);
            // Store the diff
            Diff theDiff = DiffFactory.getInstance().doUnifiedDiff((SVNProjectRevision)r1, 
            		(SVNProjectRevision)r2, FileUtils.dirname(repoPath), diff.toString());
           
            return theDiff;
        } catch (SVNException e) {
            logger.warn(e.getMessage());
            throw new InvalidRepositoryException(url,e.getMessage());
        } 
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
    
    public String getName() {
    	return "SVNAccessor";
    }

    /** {@inheritDoc}} */
	public List<SCMNode> listDirectory(SCMNode dir) 
		throws InvalidRepositoryException {
		
		ArrayList<SCMNode> contents = new ArrayList<SCMNode>();
		
		 if (svnRepository == null) {
			 connectToRepository();
		 }
		 
		 if (!getNodeType(dir.getPath(), dir.getRevision()).equals(SCMNodeType.DIR)) {
			 contents.add(dir);
			 return contents;
		 }
		 
		 Collection<SVNDirEntry> svnContents = new Vector<SVNDirEntry>();
		 
		 try {
			svnRepository.getDir(dir.getPath(), 
					Long.parseLong(dir.getRevision().getUniqueId()), 
					false, svnContents);
			
			Iterator<SVNDirEntry> i = svnContents.iterator();
			
			while (i.hasNext()) {
				SVNDirEntry d = i.next();
				
				SCMNode node = new SCMNode(
						dir.getPath() + "/" + d.getName(),
						(d.getKind() == SVNNodeKind.DIR)?SCMNodeType.DIR:SCMNodeType.FILE,
					    dir.getRevision());
				
				contents.add(node);
			}
			
		} catch (NumberFormatException e) {
			logger.warn("Not an SVN revision: " + dir.getRevision().getUniqueId());
		} catch (SVNException e) {
			logger.warn("Error getting dir contents for path " + dir.getPath());
		} 
		 
		return contents;
	}
	
	/** {@inheritDoc}} */
	public SCMNode getNode(String path, Revision r)
			throws InvalidRepositoryException {
		
		 if (svnRepository == null) {
			 connectToRepository();
		 }
		 
		 SCMNodeType t = getNodeType(path, r);
		 
		 if ( !t.equals(SCMNodeType.UNKNOWN)) {
			 return new SCMNode(path, t, r);
		 }
		 
		return null;
	}

	/** {@inheritDoc }*/
	public List<AnnotatedLine> getNodeAnnotations(SCMNode s) {
		if (!s.getType().equals(SCMNodeType.FILE))
			return Collections.emptyList();
		
		List<AnnotatedLine> annotations = new ArrayList<AnnotatedLine>();
		SVNLogClient log = new SVNLogClient(SVNWCUtil.createDefaultAuthenticationManager(), null);
		SVNAnnotator annotator = new SVNAnnotator();
		try {
			log.doAnnotate(SVNURL.parseURIDecoded(url+s.getPath()), 
					SVNRevision.create(Long.parseLong(s.getRevision().getUniqueId())), 
					null, 
					SVNRevision.create(Long.parseLong(s.getRevision().getUniqueId())), 
					annotator);
		} catch (NumberFormatException e) {
			logger.error("Number formating error " + e.getMessage());
			return Collections.emptyList();
		} catch (SVNException e) {
			logger.error("Repository error " + e.getMessage());
			return Collections.emptyList();
		}
		
		return annotator.getAnnotatedFile();
	}
	
	/** Used by the #getNodeAnnotations method to annotate a file */
	private class SVNAnnotator implements ISVNAnnotateHandler {

		private List<AnnotatedLine> annotate = new ArrayList<AnnotatedLine>();
		
		public void handleEOF() {}

		public void handleLine(Date date, long revision, String author,
				String line) throws SVNException {
			AnnotatedLine al = new AnnotatedLine();
			al.developer = String.copyValueOf(author.toCharArray());
			al.line = String.copyValueOf(line.toCharArray());
			al.rev = newRevision(String.valueOf(revision));
			annotate.add(al);
		}

		public void handleLine(Date date, long revision, String author,
				String line, Date mergedDate, long mergedRevision,
				String mergedAuthor, String mergedPath, int lineNumber)
				throws SVNException {
			handleLine(date, revision, author, line);
		}

		public boolean handleRevision(Date date, long revision, String author,
				File contents) throws SVNException { return false;}
		
		public List<AnnotatedLine> getAnnotatedFile() {
			return annotate;
		}
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
