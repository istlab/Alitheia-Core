/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007 by the SQO-OSS consortium members <info@sqo-oss.eu>
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

import java.util.HashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

import eu.sqooss.service.logging.LogManager;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.CommitLog;
import eu.sqooss.service.tds.ProjectRevision;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.tds.TDSException;
import eu.sqooss.service.tds.TDSService;

public class TDSServiceImpl implements TDSService {
    private LogManager logService = null;
    private Logger logger = null;
    private HashMap<String, TDAccessorImpl> accessorPool;

    public TDSServiceImpl(BundleContext bc) {
        ServiceReference serviceRef = bc.getServiceReference(LogManager.class.getName());
        logService = (LogManager) bc.getService(serviceRef);
        logger = logService.createLogger(Logger.NAME_SQOOSS_TDS);
        if (logger != null) {
            logger.info("TDS service created.");
        } else {
            System.out.println("# TDS failed to get logger.");
        }

        // Initialize access methods for all the repo types
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();

        logger.info("SVN repo factories initialized.");
        TDAccessorImpl.logger = logger;

        accessorPool = new HashMap<String,TDAccessorImpl>();
    }

    // Interface methods

    // For now, there is no difference between projectExists and
    // accessorExists; in future there may be when accessor pooling
    // and limiting is implemented. Then it may be that a project
    // exists for the TDS but has no accessor yet.
    public boolean projectExists( String projectName ) {
        return accessorPool.containsKey(projectName);
    }

    public boolean accessorExists( String projectName ) {
        return accessorPool.containsKey(projectName);
    }

    public TDAccessor getAccessor( String projectName ) {
        if (accessorExists(projectName)) {
            logger.info("Retrieving accessor for project " + projectName);
            return accessorPool.get(projectName);
        } else {
            logger.info("Retrieval request for non-existent project " + projectName);
        }

        return null;
    }

    public void releaseAccessor( TDAccessor td ) {
        logger.info("Release accessor for " + td.getName());
    }

    public void addAccessor( String name, String bts, String mail, String scm ) {
        TDAccessorImpl a = new TDAccessorImpl(name,bts,mail,scm);
        accessorPool.put(name,a);
        logger.info("Added project <" + name + ">");
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

