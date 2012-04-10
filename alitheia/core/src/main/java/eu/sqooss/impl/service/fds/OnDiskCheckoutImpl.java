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

package eu.sqooss.impl.service.fds;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.locks.ReentrantLock;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.fds.CheckoutException;
import eu.sqooss.service.fds.OnDiskCheckout;
import eu.sqooss.service.tds.InvalidProjectRevisionException;
import eu.sqooss.service.tds.InvalidRepositoryException;
import eu.sqooss.service.tds.SCMAccessor;

/**
 * The CheckoutImpl implements the Checkout interface. It represents a
 * checkout of a specific project at a specific revision somewhere in the
 * filesystem of the Alitheia core system. A CheckoutImpl exposes
 * additional API for updating the checkout itself and handling the
 * reference counting done on it. Most operations on CheckoutImpl
 * are not thread-safe. Locking is done in the FDS which exposes
 * only the Checkout (safe) part of the interface.
 */
class OnDiskCheckoutImpl implements OnDiskCheckout {

    private ReentrantLock updateLock;
    
    private File localRoot;
    private String repoPath;
    private ProjectVersion revision;
    private SCMAccessor scm;
    
    private boolean initCheckout = false;

    OnDiskCheckoutImpl(SCMAccessor accessor, String path,
                       ProjectVersion pv, File root) {
        repoPath = path;
        localRoot = root;
        revision = pv;
        scm = accessor;
        updateLock = new ReentrantLock(true);
    }
    
    void setRevision(ProjectVersion pv) {
        this.revision = pv;
    }
    
    void setAccessor(SCMAccessor scm) {
        this.scm = scm;
    }
    
    void lock() {
        if (!updateLock.isHeldByCurrentThread())
            updateLock.lock();
    }
    
    void unlock(){
        updateLock.unlock();
    }
    
    // Interface methods
    /** {@inheritDoc} */
    public File getRoot() 
        throws FileNotFoundException, CheckoutException {
        
        if (initCheckout == false) {
            lock();
            try {
                scm.getCheckout(repoPath, 
                        scm.newRevision(revision.getRevisionId()), 
                        localRoot);
            } catch (InvalidProjectRevisionException e) {
                throw new CheckoutException("Project version " + revision +
                        " does not map to an SCM revision. Error was:" 
                        + e.getMessage());
            } catch (InvalidRepositoryException e) {
                throw new CheckoutException("Error accessing repository " 
                        + scm.toString() + ". Error was:" + e.getMessage());
            } finally {
                unlock();
            }
            initCheckout = true;
        } 
        
        return localRoot;
    }
    
    /** {@inheritDoc} */
    public ProjectVersion getProjectVersion() {
        DBService dbs = AlitheiaCore.getInstance().getDBService();
        revision = dbs.attachObjectToDBSession(revision);
        return revision;
    }
    
    /** {@inheritDoc} */
    public String getRepositoryPath() {
        return repoPath;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

