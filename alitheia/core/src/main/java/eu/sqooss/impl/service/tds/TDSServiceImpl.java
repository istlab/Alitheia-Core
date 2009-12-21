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

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.core.AlitheiaCoreService;
import eu.sqooss.service.db.ClusterNode;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.TDSService;

public class TDSServiceImpl implements TDSService, AlitheiaCoreService {
    private Logger logger = null;
    private ConcurrentHashMap<Long, ProjectDataAccessorImpl> accessorPool;
    private ConcurrentHashMap<ProjectDataAccessorImpl, Integer> accessorClaims;
    
    public TDSServiceImpl() {}

    // Interface methods

    // For now, there is no difference between projectExists and
    // accessorExists; in future there may be when accessor pooling
    // and limiting is implemented. Then it may be that a project
    // exists for the TDS but has no accessor yet.
    /**{@inheritDoc}}*/
    public boolean projectExists( long projectId ) {
        return accessorPool.containsKey(new Long(projectId));
    }

    /**{@inheritDoc}}*/
    public boolean accessorExists( long projectId ) {
        return projectExists(projectId);
    }

    /**{@inheritDoc}}*/
    public ProjectAccessor getAccessor( long projectId ) {
        if (accessorExists(projectId)) {
            ProjectDataAccessorImpl a = accessorPool.get(projectId);
            synchronized (accessorClaims) {
                int claims = accessorClaims.get(a);
                claims++;
                accessorClaims.put(a, claims);
            }
            logger.debug("Retrieving accessor for project " + projectId);
            return a;
        } else {
            logger.warn("Retrieval request for non-existent project " + projectId);
        }

        return null;
    }

    /**{@inheritDoc}}*/
    public void releaseAccessor(ProjectAccessor td) {
        logger.debug("Release accessor for " + td.getName());
        synchronized (accessorClaims) {
            int claims = accessorClaims.get(td);
            if (claims <= 0) {
                logger.error("Request to release not claimed accessor");
            } else {
                claims--;
                accessorClaims.put((ProjectDataAccessorImpl)td, claims);
            }
        }
    }

    /**{@inheritDoc}}*/
    public void addAccessor( long id, String name, String bts, String mail, String scm ) {
        if (accessorExists(id)) {
            logger.warn("Adding duplicate project id " + id + " <" + name + ">");
            // Continue anyway
        }
        ProjectDataAccessorImpl a = new ProjectDataAccessorImpl(id,name,bts,mail,scm);
        accessorPool.putIfAbsent(new Long(id),a);
        accessorClaims.putIfAbsent(a, 1);
        logger.info("Added project <" + name + ">");
    }
    
    /**{@inheritDoc}}*/
    public boolean isURLSupported(String URL) {
        boolean supported = false;
        URI toTest = null;
        
        try {
            toTest = URI.create(URL);
        } catch (IllegalArgumentException iae) {
            return false;
        }
        
        for (String schemes : DataAccessorFactory.getSupportedSchemes()) {
            if (schemes.equals(toTest.getScheme())) {
                supported = true;
                break;
            }
        }
        
        return supported;
    }
    
    /**
     * Tell the TDS which projects are installed.
     */
    private void stuffer() {
        logger.info("TDS is now running the stuffer.");
        DBService db = AlitheiaCore.getInstance().getDBService();
        
        if (db != null && db.startDBSession()) {
            
            for (StoredProject p : ClusterNode.thisNode().getProjects()) {
                addAccessor(p.getId(), p.getName(), p.getBtsUrl(), 
                        p.getMailUrl(), p.getScmUrl());
            }
            db.commitDBSession();
        } 

        logger.info("TDS Stuffer is finished.");
    }

	@Override
	public void shutDown() {
		
	}

	@Override
	public boolean startUp() {
        ProjectDataAccessorImpl.logger = logger;

        //Wake up the DataAccessorFactory
        new DataAccessorFactory(logger);
        DataAccessorFactory.addImplementation(URI.create("bugzilla-xml://www.sqo-oss.org"),
                BugzillaXMLParser.class);
        DataAccessorFactory.addImplementation(URI.create("maildir://www.sqo-oss.org"),
                MailDirAccessor.class);
        DataAccessorFactory.addImplementation(URI.create("svn://www.sqo-oss.org"),
                SVNAccessorImpl.class);
        DataAccessorFactory.addImplementation(URI.create("svn-http://www.sqo-oss.org"),
                SVNAccessorImpl.class);
        DataAccessorFactory.addImplementation(URI.create("svn-file://www.sqo-oss.org"),
                SVNAccessorImpl.class);
        //Init accessor store
        accessorPool = new ConcurrentHashMap<Long,ProjectDataAccessorImpl>();
        accessorClaims = new ConcurrentHashMap<ProjectDataAccessorImpl, Integer>();
       
        logger.info("TDS service created.");
        
		stuffer();
		return true;
	}

	@Override
	public void setInitParams(BundleContext bc, Logger l) {
	    this.logger = l;
	}
}

// vi: ai nosi sw=4 ts=4 expandtab
