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
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.TDSService;

public class TDSServiceImpl implements TDSService, EventHandler {
    private Logger logger = null;
    private ConcurrentHashMap<Long, ProjectDataAccessorImpl> accessorPool;
    private ConcurrentHashMap<ProjectDataAccessorImpl, Integer> accessorClaims;
    
    public TDSServiceImpl(BundleContext bc, Logger l)  {
        logger = l;
        ProjectDataAccessorImpl.logger = logger;

        //Wake up the DataAccessorFactory
        new DataAccessorFactory(l);
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
        //Register an event handler for hibernate events
        final String[] topics = new String[] {
                DBService.EVENT_STARTED
        };
            
        Dictionary<String, String[]> d = new Hashtable<String, String[]>(); 
        d.put(EventConstants.EVENT_TOPIC, topics ); 
        
        bc.registerService(EventHandler.class.getName(), this, d); 
        logger.info("TDS service created.");
        
    }

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

    public Object selfTest() {
        // Fail if certain required data structures are not initialized
        if (logger == null) {
            return new String("No logger available.");
        }
        if (accessorPool == null) {
            return new String("No accessor pool available.");
        }

        // Add an accessor for testing purposes when none was there.
        boolean addedAccessor = false;
        final int TEST_PROJECT_ID = 1337;
        if (accessorPool.isEmpty()) {
            logger.info("Adding bogus project to empty accessor pool.");
            addAccessor(TEST_PROJECT_ID, "KPilot", "", null,
                    "http://cvs.codeyard.net/svn/kpilot/");
            addedAccessor = true;
        }

        Set<Long> accessorKeys = accessorPool.keySet();
        Iterator<Long> i = accessorKeys.iterator();
        if (!i.hasNext()) {
            // we added an accessor before, so it should be here...
            if (addedAccessor) {
                accessorPool.clear();
            }
            return new String("No projects to check against.");
        }

        // Check consistency of accessor retrieval
        ProjectDataAccessorImpl accessor = accessorPool.get(i.next());
        long id = accessor.getId();
        logger.debug("Checking project " + id + " <" + accessor.getName() + ">");
        if (accessor != getAccessor(id)) {
            if (addedAccessor) {
                accessorPool.clear();
            }
            return new String("Request for project " + i + " got someone else.");
        }

        if (addedAccessor) {
            accessorPool.clear();
        }
        // Everything is ok
        return null;
    }
    
    /**
     * Tell the TDS which projects are installed.
     */
    private void stuffer() {
        logger.info("TDS is now running the stuffer.");
        DBService db = AlitheiaCore.getInstance().getDBService();
        
        if (db != null && db.startDBSession()) {
            List<?> l = db.doHQL("from StoredProject");

            if (l.isEmpty()) {
                bogusStuffer();
                // Next for loop is empty as well
            }
            for (Object o : l) {
                StoredProject p = (StoredProject) o;
                addAccessor(p.getId(), p.getName(), p.getBtsUrl(), 
                        p.getMailUrl(), p.getScmUrl());
            }
            db.commitDBSession();
        } else {
            bogusStuffer();
        }

        logger.info("TDS Stuffer is finished.");
    }

    private void bogusStuffer() {
        logger.debug("Stuffing bogus project into TDS");
        // Some dorky default project so the TDS is not empty
        // for the test later.
        addAccessor(1, "KPilot", "", "",
            "http://cvs.codeyard.net/svn/kpilot/" );
    }

    /**
     * Fill in the TDS with project info imediately after starting hibernate
     */
    public void handleEvent(Event e) {
        logger.debug("Caught EVENT type=" + e.getPropertyNames().toString());
        if (e.getTopic() == DBService.EVENT_STARTED) {
            stuffer();           
        }
    }
}

// vi: ai nosi sw=4 ts=4 expandtab
