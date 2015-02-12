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

import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectVersion;

/**
 * A checkout represents a working copy (checkout) of a project;
 * the InMemoryCheckout does not reside on disk but represents
 * the checkout as a collection of Java objects which may be read
 * through the usual InputStream methods. A checkout
 * has a specific revision attached to it. On no account may you edit
 * files in a checkout! It is a read only working copy. Other parts
 * of the Alitheia system may access the same checkout concurrently.
 * Use the FDSService to obtain a checkout and remember to release it
 * when done.
 *
 * Typical use of an in-memory checkout looks like this:
 *
 * <code>
 * Checkout c = fds.getInMemoryCheckout(projectVersion);
 * InMemoryDirectory d = c.getRoot();
 * // Do stuff in the file system tree under r, but don't change anything!
 * fds.releaseCheckout(c);
 * </code>
 *
 */
public interface InMemoryCheckout {
    /**
     * Get the revision at which this checkout was made.
     *
     * @return ProjectVersion The DB representation of the version that 
     * was checked out. Will not change.
     */
    ProjectVersion getProjectVersion();
    
    /**
     * Get the root directory of this checkout.
     */
    public InMemoryDirectory getRoot();
    
    /**
     * Get a file inside of the checkout.
     * @param name The filename.
     */
    public ProjectFile getFile(String name);
}

// vi: ai nosi sw=4 ts=4 expandtab