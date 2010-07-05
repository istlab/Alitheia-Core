/*
 * This file is part of the Alitheia system, developed by the SQO-OSS
 * consortium as part of the IST FP6 SQO-OSS project, number 033331.
 *
 * Copyright 2008 - 2010 - Organization for Free and Open Source Software,
 *                 Athens, Greece.
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

package eu.sqooss.impl.service.updater;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.cluster.ClusterNodeActionException;
import eu.sqooss.service.cluster.ClusterNodeService;
import eu.sqooss.service.db.ClusterNodeProject;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.logging.Logger;
import eu.sqooss.service.scheduler.SchedulerException;
import eu.sqooss.service.tds.InvalidAccessorException;
import eu.sqooss.service.tds.ProjectAccessor;
import eu.sqooss.service.tds.TDSService;
import eu.sqooss.service.updater.MetadataUpdater;
import eu.sqooss.service.updater.UpdaterService;

public class UpdaterServiceImpl extends HttpServlet implements UpdaterService {

    private static final long serialVersionUID = 1L;
    private Logger logger = null;
    private AlitheiaCore core = null;
    private HttpService httpService = null;
    private BundleContext context;
    private DBService dbs = null;
    
    /*
     * List of updaters indexed by protocol
     */
    private Map<String, List<Class<?>>> updaters;
    
    /*
     * List of updaters indexed by updater stage
     */
    private Map<UpdaterStage, List<Class<?>>> updForStage;
    
    private List<Class<?>> updTargetToUpdater(StoredProject project, UpdateTarget t) {
        List<Class<?>> upds = new ArrayList<Class<?>>();
        TDSService tds = AlitheiaCore.getInstance().getTDSService();
        ProjectAccessor pa = tds.getAccessor(project.getId());
        
        if (t.equals(UpdateTarget.BUGS) || t.equals(UpdateTarget.STAGE1)) {
            try {
                List<URI> schemes = pa.getBTSAccessor().getSupportedURLSchemes();
                for (URI uri : schemes) {
                    if (updaters.containsKey(uri.getScheme()) ||
                            updaters.containsKey(uri.getScheme().substring(
                                    0, uri.getScheme().indexOf(':')))){
                        upds.addAll(updaters.get(uri.getScheme()));
                    }
                }
                
            } catch (InvalidAccessorException e) {
                logger.warn("Project " + project + 
                        " does not include a BTS accessor: " + e.getMessage());
            }
        } else if (t.equals(UpdateTarget.MAIL) || t.equals(UpdateTarget.STAGE1)) {
            
        } else if (t.equals(UpdateTarget.CODE) || t.equals(UpdateTarget.STAGE1)) {
        
        } else if (t.equals(UpdateTarget.STAGE2)) {
            
        }
        
        return upds;
    }
    
    /**
     * Add an update job of the given type for the project. You may not claim
     * ALL as a type of update -- use the individual types.
     * 
     * @param project the project to claim
     * @param t the type of update that is being claimed
     * @return true if the claim succeeds or if an update is already running 
     * for this update target, false otherwise
     */
    private boolean addUpdate(StoredProject project, UpdateTarget t) {
        
        if (t == null) {
            logger.warn("Updater target is null");
            return false;
        }
        
       List<Class<?>> updaters = updTargetToUpdater(project, t);

        if (updaters.isEmpty()) {
            logger.warn("No updater registered for update target:" + t);
            return false;
        }
        
        for (Class<?> updater : updaters) {
            try {
                // Create update job
                MetadataUpdater upd = (MetadataUpdater) updater.newInstance();
                upd.setUpdateParams(project, logger);

                UpdaterJob uj = new UpdaterJob(upd);

                // Add it to the scheduler queue
                core.getScheduler().enqueue(uj);
            } catch (SchedulerException e) {
                logger.error("The Updater failed to update the repository"
                        + " metadata for project " + project.getName()
                        + " Scheduler error: " + e.getMessage());
            } catch (InstantiationException e) {
                logger.error("Failed to add update job: " + e.getMessage());
            } catch (IllegalAccessException e) {
                logger.error("Failed to add update job: " + e.getMessage());
            }
            logger.debug("");
        }
        return true;
    }
    
   /**
     * Produce a string representation of the set of update targets.
     * @param s set to convert to string
     * @return human-readable string representation
     */
    private String explain(Set<UpdateTarget> s) {
        if ((s==null) || s.isEmpty()) {
            return "empty";
        }

        String msg = "";
        for (UpdateTarget u : s) {
            msg = msg + u.toString() + " ";
        }
        return msg.trim();
    }

    /** {@inheritDoc}*/
    public boolean update(StoredProject project, UpdateTarget target) {
        ClusterNodeService cns = null;
        ClusterNodeProject cnp = null;
        
    	if (project == null) {
            logger.info("Bad project name for update.");
            return false;
        }     
    	
    	 /// ClusterNode Checks - Clone to MetricActivatorImpl
    	cns = core.getClusterNodeService();
        if (cns==null) {
            logger.warn("ClusterNodeService reference not found - ClusterNode assignment checks will be ignored");
        } else {            
           
            cnp = ClusterNodeProject.getProjectAssignment(project);
            
            if (cnp==null) {
                // project is not assigned yet to any ClusterNode, assign it here by-default
                try {
                    cns.assignProject(project);
                } catch (ClusterNodeActionException ex){
                    logger.warn("Couldn't assign project " + project.getName() + " to ClusterNode " + cns.getClusterNodeName());
                    return true;
                }
            } else { 
                // project is assigned , check if it is assigned to this Node
                if (!cns.isProjectAssigned(project)){
                    logger.warn("Project " + project.getName() + " is not assigned to this ClusterNode - Ignoring update");
                    // TODO: Clustering - further implementation:
                    //       If needed, forward Update to the appropriate ClusterNode!
                    return true; // report success to avoid errors when adding a new project  
                }                
                // at this point, we are confident the project is assigned to this ClusterNode - Go On...                
            }
        }  
        // Done with ClusterNode Checks
       
    	logger.info("Request to update project:" + project.getName() + " for target: "
                + target);
    
    	addUpdate(project, target);
       
        return true;
    }


    /**
     * This is the standard HTTP request handler. It maps GET parameters onto
     * the method arguments for update(project,target). The response always
     * gets a response code -- SC_OK (200) only if the update was able to
     * start at all.
     *
     * The response codes in HTTP are used as follows:
     * - SC_OK  if the update starts successfully; a fake XML response is
     *          returned describing the success.
     * - SC_BAD_REQUEST (400) if the request is syntactically incorrect, which in
     *          this case means that one of the required parameters "project"
     *          or "target" is missing.
     * - SC_NOT_FOUND (404) if the project does not exist in the database or
     *          is otherwise not found. (This may be confusing with the 404
     *          returned when the updater servlet is not running, so may need
     *          to change this at some point).
     * - SC_NOT_IMPLEMENTED if the update target type is not supported, for
     *          instance because it names a datatype that we do not know about
     *          (valid values are "mail", "code", "bugs" and "all" right now).
     * - SC_CONFLICT if there is already an update running for the given project
     *          and data source; only one can be active at any time.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String p = request.getParameter("project");
        String t = request.getParameter("target");
        String errorMessage;
        dbs.startDBSession();
        if (p == null) {
            errorMessage = "Bad updater request is missing project name.";
            logger.warn(errorMessage);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
            dbs.commitDBSession();
            return;
        }
        if (t == null) {
            errorMessage = "Bad updater request is missing update target.";
            logger.warn(errorMessage);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
            dbs.commitDBSession();
            return;
        }

        StoredProject project = StoredProject.getProjectByName(p);
        if (project == null) {
            //the project was not found, so the job can not continue
            errorMessage = "The project <" + p + "> was not found";
            logger.warn(errorMessage);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, errorMessage);
            dbs.commitDBSession();
            return;
        }

        UpdateTarget target = null;
        try {
            target = UpdateTarget.valueOf(t.toUpperCase());
        } catch (IllegalArgumentException e) {
            errorMessage = "Bad updater request for target <" + t + ">";
            logger.warn(errorMessage);
            response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, errorMessage);
            dbs.commitDBSession();
            return;
        }

        logger.info("Updating project " + p + " target " + t);
        if (!update(project, target)) {
            // Something's wrong
            response.sendError(HttpServletResponse.SC_CONFLICT);
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("text/xml;charset=UTF-8");
            response.getWriter().println("<updater>");
            response.getWriter().println("<project-id>" + project.getId() + "</project-id>");
            response.getWriter().println("<status>Jobs scheduled</status>");
            response.getWriter().println("</updater>");
            response.getWriter().flush();
        }
        dbs.commitDBSession();
    }
  
	@Override
	public void setInitParams(BundleContext bc, Logger l) {
        this.context = bc;
        this.logger = l;
	}

	@Override
	public void shutDown() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean startUp() {
	   
        /* Get a reference to the core service*/
        ServiceReference serviceRef = null;
        serviceRef = context.getServiceReference(AlitheiaCore.class.getName());
        core = (AlitheiaCore) context.getService(serviceRef);
        if (logger != null) {
            logger.info("Got a valid reference to the logger");
        } else {
            System.out.println("ERROR: Updater got no logger");
        }

        /* Get a reference to the HTTP service */
        serviceRef = context.getServiceReference("org.osgi.service.http.HttpService");
        if (serviceRef != null) {
            httpService = (HttpService) context.getService(serviceRef);
            try {
                httpService.registerServlet("/updater", (Servlet) this, null, null);
            } catch (ServletException e) {
                logger.error("Cannot register servlet to path /updater");
                return false;
            } catch (NamespaceException e) {
                logger.error("Duplicate registration at path /updater");
                return false;
            }
        } else {
            logger.error("Could not load the HTTP service.");
            return false;
        }
        dbs = core.getDBService();
        
        updaters = new HashMap<String, List<Class<?>>>();
        updForStage = new HashMap<UpdaterService.UpdaterStage, List<Class<?>>>();

        logger.info("Succesfully started updater service");
        return true;
	}

    /** {@inheritDoc} */
    @Override
    public void registerUpdaterService(String[] protocols,
            UpdaterStage[] stages, Class<? extends MetadataUpdater> clazz) {
        
        String prots = "", stgs = "";
        
        for (String proto : protocols) {
            prots += proto + " ";
            if (updaters.get(proto) == null)
                updaters.put(proto, new ArrayList<Class<?>>());
            updaters.get(proto).add(clazz);
        }
        
        for (UpdaterStage us : stages) {
            stgs += us + " ";
            if (updForStage.get(us) == null)
                updForStage.put(us, new ArrayList<Class<?>>());
            updForStage.get(us).add(clazz);
        }
        logger.info("Registering updater class " + clazz.getCanonicalName() + 
                " for protocols (" + prots + ") and stages (" + stgs + ")");
    }

    /** {@inheritDoc} */
    @Override
    public void unregisterUpdaterService(Class<? extends MetadataUpdater> clazz) {
        for (String proto : updaters.keySet()) {
            if (updaters.get(proto).contains(clazz)) {
                updaters.get(proto).remove(clazz);
                if (updaters.get(proto).size() <= 0)
                    updaters.remove(proto);
                break;
            }
        }
        
        for (UpdaterStage us : updForStage.keySet()) {
            if (updForStage.get(us).contains(clazz)) {
                updForStage.get(us).remove(clazz);
                break;
            }
        }
        logger.info("Unregistering updater class " + clazz.getCanonicalName());
    }
}
