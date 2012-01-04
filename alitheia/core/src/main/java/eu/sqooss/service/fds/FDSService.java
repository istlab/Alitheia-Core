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

package eu.sqooss.service.fds;

import java.io.File;
import java.io.InputStream;
import java.util.regex.Pattern;

import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.StoredProject;

/**
 * The FDS (Fat Data Service) is the part of the data access layer that
 * combines the DB metadata service with the TDS raw data access service.
 * Its purpose is to deliver high level views of the data and hide the
 * intricacies of raw level SCM data management behind a convenient 
 * interface. Currently, the FDSService is mostly concerned with SCM
 * data. 
 * 
 * The data views it offers are: 
 * 
 * <ul>
 *      <li>{@link eu.sqooss.service.fds.OnDiskCheckout} - An updatable 
 *      on disk checkout that should be used wherever a plug-in would need
 *      to process the contents of files for several versions one after the 
 *      other. OnDiskCheckouts are cacheable and can be shared among clients.
 *      </li>
 *      <li>{@link eu.sqooss.service.fds.InMemoryCheckout} - A very fast
 *      to create in-memory structure that provides an one time access to 
 *      the structure of an on-disk equivalent checkout. To be used in
 *      situations where the client is mostly interested in the file tree
 *      structure rather than the contents of the individual files. 
 *       </li>
 *       <li>{@link eu.sqooss.service.fds.Timeline} - A chronological view of
 *       the project events accross all supported data sources.</li>
 * </ul>
 * 
 * 
 */
public interface FDSService extends AlitheiaCoreService {
    /**
     * Maintains (and caches) a checkout of a given project in a
     * given revision. The checkout must be released after the client is done 
     * using it. As long as a checkout is held, the revisions of
     * files in the checkout will not change, but once the checkout is
     * released by all, it may be updated to some new revision. 
     *
     * @param pv Version to checkout
     * @param path Repository path to checkout
     *
     * @return OnDiskCheckout 
     *
     * @throws CheckoutException if the repository is not valid
     * in some way (usually because of id being wrong),  if the revision 
     * does not make sense with the given repository or when an existing
     * checkout update failed for some reason. 
     */
    OnDiskCheckout getCheckout(ProjectVersion pv, String path)
            throws CheckoutException;

    /**
     * Update an existing checkout to the provided version. 
     * 
     * @param c The checkout to update
     * @param pv The version to 
     * @return True if the update succeeded, false if the checkout is 
     * held by more than one clients
     * @throws CheckoutException When the update failed or if the checkout
     * has been updated by some other client before this method finishes.
     */
    boolean updateCheckout(OnDiskCheckout c, ProjectVersion pv) 
        throws CheckoutException;

    /**
     * Release a previously obtained checkout.
     * 
     * @param co Checkout obtained from previous call to getCheckout()
     * 
     */
    void releaseCheckout(OnDiskCheckout co);
    
    /**
     * Maintains an in-memory representation of a project checkout for a
     * specific revision. 
     * 
     * @return An in-memory representation of the working copy for a specific
     *              revision
     * @throws CheckoutException When a DB or other error occurred
     */
    InMemoryCheckout getInMemoryCheckout(ProjectVersion pv)
        throws CheckoutException;
    /**
     * Maintains an in-memory representation of a project checkout for a
     * specific revision. 
     * 
     * @param pattern A regular expression pattern used to filter files by their path
     * @return An in-memory representation of the working copy for a specific
     *              revision
     * @throws CheckoutException When a DB or other error occurred 
     */
    InMemoryCheckout getInMemoryCheckout(ProjectVersion pv, Pattern pattern)
        throws CheckoutException;
    
    /**
     * This function returns a timeline view (combined metadata and pointers 
     * to actual data) for a given project. 
     *  
     * @param c StoredProject to return the timeline for
     */
    Timeline getTimeline(StoredProject c);
    
    /**
     * Retrieve a file handle corresponding to the selected ProjectFile entry.
     * The file handle lives somewhere in the temporary space where the FDS
     * stores all of its local files.
     * 
     * @param pf ProjectFile entry
     * 
     * @return the file handle or null if there is no such file.
     */
    File getFile(ProjectFile pf);

    /**
     * Retrieve the file contents corresponding to the given ProjectFile entry.
     * Returns null if there is no such file or it is empty.
     * 
     * @param pf ProjectFile entry
     * 
     * @return the file contents or null if none.
     */
    InputStream getFileContents(ProjectFile pf);
}

// vi: ai nosi sw=4 ts=4 expandtab

