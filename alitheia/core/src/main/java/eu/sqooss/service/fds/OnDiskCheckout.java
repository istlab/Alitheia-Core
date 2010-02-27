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
import java.io.FileNotFoundException;

import eu.sqooss.service.db.ProjectVersion;

/**
 * An <tt>OnDiskCheckout</tt> represents a working copy (checkout) of a project
 * somewhere within the filesystem of the Alitheia system. A checkout
 * has a specific version attached to it. The checkout is supposed to 
 * be a read-only working copy; if the data of a checkout are modified 
 * in some way, the results are unspecified. Other parts
 * of the Alitheia system may access the same checkout concurrently.
 * Use the FDSService to obtain a checkout and remember to release it
 * when done. 
 * <p>
 *      <em>If the checkout is not released after use, then the checkout
 *      cannot be updated, and this means that a new copy of the checkout
 *      data will be created on the disk.</em> 
 * </p>
 * 
 * <p>
 * Typical use of a checkout looks like this:
 *
 * <pre>
 * Checkout c = fds.getCheckout(projectVersion);
 * File r = c.getRoot();
 * // Do stuff in the file system tree under r, but don't change anything!
 * fds.releaseCheckout(c);
 * </pre>
 * </p>
 */
public interface OnDiskCheckout {
    
    /**
     * Get the root within the Alitheia filesystem where the checkout lives,
     * for further manipulation with regular java.io methods.
     *
     * @return File representing the abstract path to the root of the
     *          checkout; all files live beneath this.
     *          
     * @throws FileNotFoundException When the root checkout directory cannot be
     * accessed. 
     * @throws CheckoutException When there is an error accessing the
     * SCM repository or when the checkout's revision does not resolve to 
     * an SCM revision
     */
    public File getRoot() throws FileNotFoundException, 
        CheckoutException;
    
    /**
     * Get the revision at which this checkout was made.
     *
     * @return Revision (resolved to both timestamp and SVN revision
     *          number) of this checkout. Will not change.
     */
    ProjectVersion getProjectVersion();

    /**
     * Get the path in the source code repository that this checkout represents
     * 
     * @return The repository path. 
     */
    String getRepositoryPath();
}
