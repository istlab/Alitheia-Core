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

package eu.sqooss.service.tds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * An interface to a source code repository. To be compatible with Alitheia
 * Core, the repository is expected to attach a unique id to each commit (this
 * means maintaining repository wide history or some approximation of it) and be
 * able to traverse revisions forwards and backwards. Other than that, support
 * for the remaining operations should be straight forward with most SCM
 * implementations.
 */
public interface SCMAccessor extends DataAccessor {
   
    /**
     * Construct a new revision out of the revision's date. The revision is
     * returned after the revision has been associated with the underlying 
     * repository. If the association fails (i.e. there is no revision
     * at this timestamp in the repository), null will be returned.
     * 
     * @param d The date to search for a revision.
     * @return A Revision or null if the revision could not be resolved
     */
    public Revision newRevision(Date d);
    
    /**
     * Construct a new revision out of the revision's uniqueId. The revision is
     * returned after the revision has been associated with the underlying 
     * repository. If the association fails (i.e. there is no revision with 
     * this uniqueId in the repository), null will be returned.
     * 
     * @param uniqueId The Id to search for
     * @return A Revision or null if the revision could not be resolved
     */
    public Revision newRevision(String uniqueId);

    /**
     * Get the latest revision of this project.
     * 
     * @return The latest revision or null, if the revision cannot be 
     * retrieved for some reason.
     * @throws InvalidRepositoryException When the underlying repository is
     * not valid in some way (i.e. corrupt or inaccessible) 
     */
    public Revision getHeadRevision()
        throws InvalidRepositoryException;
    
    /**
     * Get the project's first revision. 
     * 
     * @return The first revision or null, if the revision cannot be 
     * retrieved for some reason.
     * @throws InvalidRepositoryException When the underlying repository is
     * not valid in some way (i.e. corrupt or inaccessible) 
     */
    public Revision getFirstRevision() 
        throws InvalidRepositoryException;
    
    /**
     * Return the next revision for the provided revision. 
     * 
     * @param r The revision to get the next revision for
     * @return The next revision
     * @throws InvalidProjectRevisionException When the provided revision is
     * invalid
     */
    public Revision getNextRevision(Revision r)
        throws InvalidProjectRevisionException;
    
    /**
     * Return the previous revision for the provided revision. 
     * @param r The revision to get the previous revision for
     * @return The previous revision. 
     * @throws InvalidProjectRevisionException When the provided revision is
     * invalid
     */
    public Revision getPreviousRevision(Revision r)
        throws InvalidProjectRevisionException;
    
    /**
     * Examines if the provided revision is a valid revision for the underlying
     * repository, namely if the implementing type is correct and the revision
     * falls into the <tt>getFirstRevision() < r < getHeadRevision()</tt> range.
     * 
     * @param r The revision to examine for validity
     * @return True if the provided revision is valid wrt the underlying 
     * repository, false otherwise.
     */
    public boolean isValidRevision(Revision r);
    
    /**
     * Retrieve a checkout of the complete source tree underneath
     * the given path, relative to the root URL of the project
     * to which this accessor is bound to. The checkout is written
     * to the provided local path.
     *         
     * If <tt>repoPath</tt> refers to a file, it is written as a single
     * file (named after its basename) in <tt>localPath</tt>. 
     * If <tt>repoPath</tt> refers to a directory, the contents of the 
     * directory are placed directly under <tt>localPath</tt>.
     *
     * @param repoPath The repository path to checkout
     * @param revision The revision to checkout
     * @param localPath The directory to write the checkout to. It must exist
     *  or an exception will be thrown.
     * @throws InvalidProjectRevisionException When the provided revision is
     * invalid
     * @throws InvalidRepositoryException When there is an error accessing the 
     * underlying repository
     * @throws FileNotFoundException When the localPath points to a file that 
     * does not exist
     */
    public void getCheckout(String repoPath, Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Update an existing checkout to a new revision.
     * Once you have a checkout, you can change the revision for it
     * by calling {@link #updateCheckout(String, Revision, Revision, File)} 
     * which modifies the files underneath to reflect the new version. 
     * This must be called with the current
     * project revision as src.
     * 
     * @param repoPath The repository path to update
     * @param src The current revision of the checkout 
     * @param dst The revision to update to
     * @param localPath The directory to update. It must exist or an exception
     * will be thrown.
     * 
     * @throws InvalidProjectRevisionException When either of the provided 
     * revisions is invalid
     * @throws InvalidRepositoryException When there is an error accessing the 
     * underlying repository
     * @throws FileNotFoundException When the localPath points to a file that 
     * does not exist
     * 
     */
    public void updateCheckout(String repoPath, Revision src,
        Revision dst, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Retrieve a single file from the source repository, relative
     * to the root URL of the project to which this accessor is
     * attached. The checked-out file is written to the local
     * path  <tt>localPath</tt>.
     *
     * @param repoPath File within this repository to retrieve
     * @param revision Revision to use for the file
     * @param localPath Where to write the results.
     * 
     * @throws InvalidProjectRevisionException When the provided revision is
     * invalid
     * @throws InvalidRepositoryException When there is an error accessing the 
     * underlying repository
     * @throws FileNotFoundException When the localPath points to a file that 
     * does not exist
     * 
     */
    public void getFile(String repoPath, Revision revision, File localPath)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Retrieve a single file from the source repository, relative
     * to the root URL of the project to which this accessor is
     * attached. The checked-out file is written to the given stream.
     *
     * @param repoPath File within this repository to retrieve
     * @param revision Revision to use for the file
     * @param stream   Where to write the results.
     * 
     * @throws InvalidProjectRevisionException When the provided revision is
     * invalid
     * @throws InvalidRepositoryException When there is an error accessing the 
     * underlying repository
     * @throws FileNotFoundException When the localPath points to a file that 
     * does not exist
     */
    public void getFile(String repoPath, Revision revision, OutputStream stream)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Get the commit log entries for the changes between revisions r1 and r2
     * within the subtree identified by the path <tt>repoPath</tt>. The
     * <tt>repoPath</tt> must be relative to the root URL of the project this
     * accessor is attached to. 
     * 
     * @param repoPath The path to get the log for
     * @param r1 The revision to start getting the log from
     * @param r2 The revision up to which to get the log. If null, the commit log
     * r1 is returned.
     * 
     * @return A valid commit log.
     * 
     * @throws InvalidProjectRevisionException
     *             When either of the provided revisions is invalid
     * @throws InvalidRepositoryException
     *             When there is an error accessing the underlying repository
     */
    public CommitLog getCommitLog(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException;

    /**
     * Get the diff between two revisions of a subtree within the source
     * repository. Passing in a null for r2 calculates the diff between
     * r1 and its next revision. 
     * 
     * @param repoPath The path to get the diff for
     * @param r1 The revision to start getting the diff from
     * @param r2 The revision up to which to get the diff
     * 
     * @throws InvalidProjectRevisionException When either of the provided 
     *  revisions is invalid
     * @throws InvalidRepositoryException When there is an error accessing
     *  the underlying repository
     * @throws FileNotFoundException When the temporary required to calculate
     * the diff cannot be created
     */
    public Diff getDiff(String repoPath, Revision r1, Revision r2)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Get the change that led to revision r. In other words get the
     * diff between the previous and the provided revision
     * 
     * @param repoPath The path to get the diff for
     * @param r1 The revision to start getting the diff from
     * @param r2 The revision up to which to get the diff
     * 
     * @throws InvalidProjectRevisionException When the provided revision is
     * invalid
     * @throws InvalidRepositoryException When there is an error accessing the 
     * underlying repository
     * @throws FileNotFoundException When the temporary required to calculate
     * the diff cannot be created
     */
    public Diff getChange(String repoPath, Revision r)
        throws InvalidProjectRevisionException,
               InvalidRepositoryException,
               FileNotFoundException;

    /**
     * Get the type of the node.
     *      
     * @throws InvalidRepositoryException When there is an error accessing the 
     * underlying repository
     */
    public SCMNodeType getNodeType(String repoPath, Revision r)
    	throws InvalidRepositoryException;

    /**
     * Get a list of files in a node in a specific revision. If the 
     * node is a file and not a directory, then the node itself is returned.
     * 
     * @param dir
     * @return 
     * @throws InvalidProjectRevisionException When the provided revision is
     *  invalid
     * @throws InvalidRepositoryException When there is an error accessing
     *  the underlying repository
     */
    public List<SCMNode> listDirectory(SCMNode dir)
    	throws InvalidRepositoryException,
    	InvalidProjectRevisionException ;
    
    /**
     * Returns a node object from the path. If the node does not exist 
     * in the SCM for the specific revision, then <tt>null</tt> is returned.
     * 
     * @param path The path to return a node for
     * @param r The revision to search the node in
     * @return An {@link SCMNode} or null if the node was not found.  
     * @throws InvalidProjectRevisionException When the provided revision is
     *  invalid
     * @throws InvalidRepositoryException When there is an error accessing
     *  the underlying repository
     */
    public SCMNode getNode(String path, Revision r) 
    	throws  InvalidRepositoryException,
    			InvalidProjectRevisionException;
    
    /**
     * Gets the status a the node in the provided revision.  
     * 
     * @return A {@link PathChangeType} value. If the node was not changed
     * in the revision the method will return {@link PathChangeType.UNMODIFIED}
     * @throws InvalidProjectRevisionException When the provided revision is
     *  invalid
     * @throws InvalidRepositoryException When there is an error accessing
     *  the underlying repository
     */
    public PathChangeType getNodeChangeType(SCMNode s) 
    	throws InvalidRepositoryException, 
    		   InvalidProjectRevisionException;
    
    /**
     * Get an annotated version of the contents of the file represented
     * by the provided SCMNode. This is equivalent to <tt>svn blame</tt>,
     * <tt>git blame</tt> and <tt>cvs annotate</tt>. 
     */
    public List<AnnotatedLine> getNodeAnnotations(SCMNode s);
}

// vi: ai nosi sw=4 ts=4 expandtab
