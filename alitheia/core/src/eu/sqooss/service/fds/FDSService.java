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

package eu.sqooss.service.fds;

import java.io.File;
import java.io.InputStream;
import java.util.regex.Pattern;

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.ProjectRevision;

/**
 * The FDS (Fat Data Service) is the part of the raw data access layer that
 * handles caching and coordination of raw project data. It is primarily
 * concerned with handling (and updating) checkouts of project sources, not
 * with access to mail or the BTS.
 *
 * @see Checkout for information on how the lifecycle of a Checkout works.
 */
public interface FDSService {
    /**
     * This maintains (and caches) a checkout of a given project in a
     * given revision; remember to release the checkout when you're done
     * with it. As long as a checkout is held by someone, the revisions of
     * files in the checkout will not change, but once the checkout is
     * released by all, it may be updated to some new revision.
     *
     * @param id    Project ID
     * @param r     Revision (project state) to get
     *
     * @return      Checkout object. Remember to release it later.
     *
     * @throws InvalidRepositoryException if the repository is not valid
     *              in some way (usually because of id being wrong).
     * @throws InvalidProjectRevisionException if the revision does not
     *              make sense with the given repository (for instance,
     *              non-existent SVN number).
     */
    Checkout getCheckout(long id, ProjectRevision r)
        throws InvalidRepositoryException,
               InvalidProjectRevisionException;

    /**
     * Maintains an in-memory representation of a project checkout for a
     * specific revision. 
     * 
     * @param id Stored project id
     * @param r The revision to retrieve the checkout from
     * @return An in-memory representation of the working copy for a specific
     *              revision
     * @throws InvalidRepositoryException if the project repository is not valid
     * @throws InvalidProjectRevisionException if this revision number does not
     *              exist in the project repository
     */
    InMemoryCheckout getInMemoryCheckout(long id, ProjectRevision r)
        throws InvalidRepositoryException,
               InvalidProjectRevisionException;
    /**
     * Maintains an in-memory representation of a project checkout for a
     * specific revision. 
     * 
     * @param id Stored project id
     * @param r The revision to retrieve the checkout from
     * @param pattern A regular expression pattern used to filter files by their path
     * @return An in-memory representation of the working copy for a specific
     *              revision
     * @throws InvalidRepositoryException if the project repository is not valid
     * @throws InvalidProjectRevisionException if this revision number does not
     *              exist in the project repository
     */
    InMemoryCheckout getInMemoryCheckout(long id, ProjectRevision r, Pattern pattern)
        throws InvalidRepositoryException,
               InvalidProjectRevisionException;
    
    /**
     * Release a previously obtained checkout.
     *
     * @param c Checkout obtained from previous call to getCheckout()
     *
     * @throws InvalidRepositoryException if the repository (project ID)
     *              is invalid in some way. If the checkout was obtained
     *              normally, this indicates an inconsistency in the TDS.
     */
    void releaseCheckout(Checkout c)
    	throws InvalidRepositoryException;
    
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

