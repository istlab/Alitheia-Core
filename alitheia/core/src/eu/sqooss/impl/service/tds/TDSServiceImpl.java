/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2007-2008 by the SQO-OSS consortium members <info@sqo-oss.eu>
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
import java.util.Iterator;
import java.util.Set;

import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;

import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.TDAccessor;
import eu.sqooss.service.tds.TDSService;

public class TDSServiceImpl implements TDSService {
    private Logger logger = null;
    private HashMap<Long, TDAccessorImpl> accessorPool;

    public TDSServiceImpl(Logger l) {
        logger = l;
        // Many other implementation classes need the same logger
        TDAccessorImpl.logger = logger;
        SCMAccessorImpl.logger = logger;
        BTSAccessorImpl.logger = logger;
        MailAccessorImpl.logger = logger;
        CheckoutBaton.logger = logger;
        CheckoutEditor.logger = logger;

        logger.info("TDS service created.");

        // Initialize access methods for all the repo types
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();

        logger.info("SVN repo factories initialized.");

        accessorPool = new HashMap<Long,TDAccessorImpl>();
    }

    // Interface methods

    // For now, there is no difference between projectExists and
    // accessorExists; in future there may be when accessor pooling
    // and limiting is implemented. Then it may be that a project
    // exists for the TDS but has no accessor yet.
    public boolean projectExists( long projectId ) {
        return accessorPool.containsKey(new Long(projectId));
    }

    public boolean accessorExists( long projectId ) {
        return projectExists(projectId);
    }

    public TDAccessor getAccessor( long projectId ) {
        if (accessorExists(projectId)) {
            logger.info("Retrieving accessor for project " + projectId);
            return accessorPool.get(projectId);
        } else {
            logger.info("Retrieval request for non-existent project " + projectId);
        }

        return null;
    }

    public void releaseAccessor( TDAccessor td ) {
        logger.info("Release accessor for " + td.getName());
    }

    public void addAccessor( long id, String name, String bts, String mail, String scm ) {
        if (accessorExists(id)) {
            logger.warn("Adding duplicate project id " + id + " <" + name + ">");
            // Continue anyway
        }
        TDAccessorImpl a = new TDAccessorImpl(id,name,bts,mail,scm);
        accessorPool.put(new Long(id),a);
        logger.info("Added project <" + name + ">");
    }

    public Object selfTest() {
        // Fail if certain required data structures are not initialized
        if (logger == null) {
            return new String("No logger available.");
        }
        if (accessorPool == null) {
            return new String("No accessor pool available.");
        }

        // Add an accessor for testing purposes when none was there.
        Boolean addedAccessor = false;
        final int TEST_PROJECT_ID = 1337;
        if (accessorPool.isEmpty())
        {
            logger.info("Adding bogus project to empty accessor pool.");
            addAccessor(TEST_PROJECT_ID, "KPilot", "", null, "http://cvs.codeyard.net/svn/kpilot/" );
            addedAccessor = true;
        }

        Set<Long> accessorKeys = accessorPool.keySet();
        Iterator<Long> i = accessorKeys.iterator();
        if (!i.hasNext()) {
            // we added an accessor before, so it should be here...
            if (addedAccessor)
            {
                    accessorPool.clear();
            }
            return new String("No projects to check against.");
        }

        // Check consistency of accessor retrieval
        TDAccessorImpl accessor = accessorPool.get(i.next());
        long id = accessor.getId();
        logger.info("Checking project " + id +
            " <" + accessor.getName() + ">");
        if (accessor != getAccessor(id)) {
            if (addedAccessor)
            {
                    accessorPool.clear();
            }
            return new String("Request for project " + i +
                " got someone else.");
        }

        if (addedAccessor)
        {
                accessorPool.clear();
        }
        // Everything is ok
        return null;
    }
}


// vi: ai nosi sw=4 ts=4 expandtab

