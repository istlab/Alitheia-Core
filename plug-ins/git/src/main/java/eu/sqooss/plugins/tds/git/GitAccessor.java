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
import java.io.OutputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;

import org.osgi.framework.BundleContext;

import eu.sqooss.service.tds.AccessorException;
import eu.sqooss.service.tds.AnnotatedLine;
import eu.sqooss.service.tds.CommitEntry;
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
 *  A TDS accessor translates repository formated data to the Alitheia Core
 *  abstract representations of those. An accessor can be of one of the following
 *  types:
 *  
 *  <ul>
 *      <li>{@link SCMAccessor}, if the underlying data source is a software 
 *          repository</li>
 *      <li>{@link MailAccessor}, if the underlying data source is a mailing 
 *          list archive</li>
 *      <li>{@link BTSAccessor}, if the underlying data source is a bug 
 *          database</li>
 *  </ul>
 *  
 *  This skeleton class implements the <code>SCMAccessor</code> interface, and 
 *  therefore the plug-in abstracts a software repository.
 */ 
public class GitAccessor implements SCMAccessor {
    
	@Override
	public String getName() {
		return null;
	}

	@Override
	public List<URI> getSupportedURLSchemes() {
		return null;
	}

	@Override
	public void init(URI dataURL, String projectName) throws AccessorException {}		
	
    public Revision newRevision(Date d) {return null;}
    
    public Revision newRevision(String uniqueId) {return null;}

    public Revision getHeadRevision()
        throws InvalidRepositoryException {return null;}
    
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

    public CommitLog getCommitLog(Revision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {return null;}
    
    public CommitLog getCommitLog(Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {return null;}

    public CommitLog getCommitLog(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {return null;}

    public CommitEntry getCommitLog(String repoPath, Revision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException {return null;}

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
}

// vi: ai nosi sw=4 ts=4 expandtab
